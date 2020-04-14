package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.mapping.FileTablePathBuilder;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.entity.DefaultFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.util.URIUtil;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hierynomus.msdtyp.AccessMask.*;
import static com.hierynomus.msfscc.FileAttributes.FILE_ATTRIBUTE_NORMAL;
import static com.hierynomus.mssmb2.SMB2CreateOptions.FILE_RANDOM_ACCESS;
import static java.util.EnumSet.of;

/**
 * Use the smb {@link DiskShare } to implement operations.
 * smb connection settings source by properties {@link BaseFileTableProperties }
 *
 * Created by xy on 2020/1/15.
 */
public class SMBFileTableHandler implements FileTableHandler {

    private static final Logger logger = LoggerFactory.getLogger(SMBFileTableHandler.class);

    @Autowired
    private FileTableNameProvider fileTableNameProvider;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    private SMBClient client;
    private Session session;
    private DiskShare diskShare;

    @PostConstruct
    public void init() throws IOException {
        if(fileTableProperties.getServername() == null){
            logger.error("No share server configuration provided of FileTable");
            return;
        }

        client = new SMBClient();
        Connection connection = client.connect(fileTableProperties.getServername());
        AuthenticationContext ac = new AuthenticationContext(
                fileTableProperties.getUsername(),
                fileTableProperties.getPassword().toCharArray(),
                fileTableProperties.getDomain());
        session = connection.authenticate(ac);
        diskShare = (DiskShare) session.connectShare(fileTableProperties.getInstance());
    }


//    private Session onceSession = null;

    private void onceDiskShare(Consumer<DiskShare> diskShareConsumer) throws IOException {
        try(Connection connection = client.connect(fileTableProperties.getServername())){
            AuthenticationContext ac = new AuthenticationContext(
                    fileTableProperties.getUsername(),
                    fileTableProperties.getPassword().toCharArray(),
                    fileTableProperties.getDomain());
            Session session = connection.authenticate(ac);
            DiskShare ds = (DiskShare) session.connectShare(fileTableProperties.getInstance());
//        if(onceSession == null){
//            onceSession = session.buildNestedSession(diskShare.getSmbPath());
//        }
//        try (Session onceSession = session.buildNestedSession(diskShare.getSmbPath());
//             DiskShare ds = (DiskShare) onceSession.connectShare(attachFileTableProperties.getInstance())){
            diskShareConsumer.accept(ds);
        } catch (IOException e) {
            logger.error("Create once DiskShare error!", e);
        }
    }

    /**
     * smb FileTable path format \\servername\instance-share\database-directory\FileTable-directory
     * smb file path is database-directory\FileTable-directory\{pathName}
     * @param pathName
     * @return
     */
    private Tuple2<String,String> toSmbPathWithParentPath(Tuple2<String,String> pathName){
        String filePath = UriComponentsBuilder.newInstance()
                .path(fileTableProperties.getDatabase())
                .pathSegment(fileTableNameProvider.provide())
                .path(pathName.v1())
                .toUriString();
        filePath = filePath.replace("/", "\\");
        String fileName = filePath + "\\" + pathName.v2();

        return Tuple.tuple(fileName,filePath);
    }

    private void makeDirectoryRecursion(URI uri) throws FileNotFoundException {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        //check parent path
        if(!diskShare.folderExists(path_parentPath.v2())){
            URI pUri = UriComponentsBuilder.fromUriString(path_name.v1()).build().toUri();
            makeDirectoryRecursion(pUri);
        }

        //check path
        if(!diskShare.folderExists(path_parentPath.v1())){
            diskShare.mkdir(path_parentPath.v1());
        }

        //other way
//        StringBuilder sbCurrentDir = new StringBuilder();
//        Stream.of(path_parentPath.v1().split("\\\\"))
//                .forEach(dir -> {
//                    sbCurrentDir.append(dir);
//                    if(!diskShare.folderExists(sbCurrentDir.toString())){
//                        diskShare.mkdir(sbCurrentDir.toString());
//                    }
//                    sbCurrentDir.append("\\");
//                });
    }

    private AbstractFileTable create(InputStream stream, String path, String name, Long size, Date time) throws Exception {
        Tuple2<String,String> path_name = Tuple.tuple(path,name);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        if(!diskShare.folderExists(path_parentPath.v2()) &&
                fileTableProperties.isAutoCreateDirectory()){
            URI pUri = UriComponentsBuilder.fromUriString(path).build().toUri();
            makeDirectoryRecursion(pUri);
        }

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                path_parentPath.v1(),
                of(GENERIC_ALL),
                of(FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OVERWRITE_IF,
                of(FILE_RANDOM_ACCESS));

        Long copySize;
        try(
                InputStream is = stream;
                OutputStream os = f.getOutputStream();
        ){
            copySize = IOUtils.copy(is,os,4096);
        }
        if(size != null && copySize != size.longValue()){
            logger.error("Actual file size {} does not match expected size {} !", copySize, size);
        }

        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .pathSegment(fileTableNameProvider.provide())
                .path(path)
                .pathSegment(name)
                .and()
                .build()
                .toWinString();

        AbstractFileTable a = new DefaultFileTable();
        a.setName(name);
        a.setFile_namespace_path(fileNamespacePath);
        a.setCached_file_size(copySize);
        a.setFile_type(FilenameUtils.getExtension(name));
        a.setCreation_time(time);
        a.setLast_access_time(time);
        a.setIs_directory(false);
        a.setIs_readonly(null);
        a.setIs_archive(null);
        a.setIs_hidden(null);
        a.setIs_system(null);
        a.setIs_offline(null);
        a.setIs_temporary(null);
        return a;
    }

    @Override
    public AbstractFileTable create(MultipartFile multipartFile, URI uri) throws Exception {
        String path = uri.toString();
        String name = multipartFile.getOriginalFilename();
        if(!uri.toString().endsWith("/")){
            Tuple2<String,String> pn = separatePathAndNameFromURI(uri);
            if(pn.v2.contains(".")){
                // uri is full path contain file name
                logger.info("File {} create with new name {}.", multipartFile.getOriginalFilename(), pn.v2);
                path = pn.v1();
                name = pn.v2();
            }
        }else{
            path = path.substring(0, path.length() - 1);
        }

        return create(
                multipartFile.getInputStream(),
                path,
                name,
                multipartFile.getSize(),
                new Date());
    }

    @Override
    public AbstractFileTable create(File file, URI uri) throws Exception {
        String path = uri.toString();
        String name = file.getName();
        if(!uri.toString().endsWith("/")){
            Tuple2<String,String> pn = separatePathAndNameFromURI(uri);
            if(pn.v2.contains(".")){
                // uri is full path contain file name
                logger.info("File {} create with new name {}.", file.getName(), pn.v2);
                path = pn.v1();
                name = pn.v2();
            }
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        return create(
                fileInputStream,
                path,
                name,
                file.length(),
                new Date());
    }

    @Override
    public AbstractFileTable create(InputStream stream, URI uri) throws Exception {
        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);

        return create(
                stream,
                pn.v1,
                pn.v2,
                0L,
                new Date());
    }

    @Override
    public AbstractFileTable create(byte[] bytes, URI uri) throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        return create(byteInputStream, uri);
    }

    @Override
    public AbstractFileTable create(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        if(!diskShare.folderExists(path_parentPath.v2()) &&
                fileTableProperties.isAutoCreateDirectory()){
            URI pUri = UriComponentsBuilder.fromUriString(path_name.v1()).build().toUri();
            makeDirectoryRecursion(pUri);
        }

        Date time = new Date();
        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .pathSegment(fileTableNameProvider.provide())
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();

        AbstractFileTable a = new DefaultFileTable();
        a.setName(path_name.v2());
        a.setFile_namespace_path(fileNamespacePath);
        a.setFile_type(null);
        a.setCached_file_size(null);
        a.setCreation_time(time);
        a.setLast_access_time(time);
        a.setIs_directory(true);
        a.setIs_readonly(null);
        a.setIs_archive(null);
        a.setIs_hidden(null);
        a.setIs_system(null);
        a.setIs_offline(null);
        a.setIs_temporary(null);
        return a;
    }

    @Override
    public File readFile(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                path_parentPath.v1(),
                of(GENERIC_ALL),
                of(FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN_IF,
                of(FILE_RANDOM_ACCESS));

        File temp = File.createTempFile("attach_filetable", null);

        try(
                FileOutputStream os = new FileOutputStream(temp);
                InputStream is = f.getInputStream();
        ){
            IOUtils.copy(is,os);
        }

        return temp;
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                path_parentPath.v1(),
                of(GENERIC_ALL),
                of(FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN_IF,
                of(FILE_RANDOM_ACCESS));

        return f.getInputStream();
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        try(
                InputStream stream = readStream(uri);
        ){
            return IOUtils.toByteArray(stream);
        }
    }


    @Override
    public AbstractFileTable read(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        FileAllInformation file = diskShare.getFileInformation(path_parentPath.v1());

        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .pathSegment(fileTableNameProvider.provide())
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();

        AbstractFileTable a = new DefaultFileTable();
        a.setName(path_name.v2());
        a.setFile_namespace_path(fileNamespacePath);
        a.setFile_type(null);
        a.setCached_file_size(file.getStandardInformation().getEndOfFile());
        a.setCreation_time(file.getBasicInformation().getCreationTime().toDate());
        a.setLast_access_time(file.getBasicInformation().getLastAccessTime().toDate());
        a.setIs_directory(file.getStandardInformation().isDirectory());
        a.setIs_readonly(null);
        a.setIs_archive(null);
        a.setIs_hidden(null);
        a.setIs_system(null);
        a.setIs_offline(null);
        a.setIs_temporary(null);
        return a;
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        if(diskShare.folderExists(path_parentPath.v1())){
            diskShare.rmdir(path_parentPath.v1(),false);
        }else if(diskShare.fileExists(path_parentPath.v1())){
//            onceDiskShare(ds -> ds.rm(fileName));
            diskShare.rm(path_parentPath.v1());
        }
        return true;
    }

    @Override
    public List<AbstractFileTable> readChild(URI uri) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        List<FileIdBothDirectoryInformation> fis = diskShare.list(path_parentPath.v1());

        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .pathSegment(fileTableNameProvider.provide())
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();
        return fis.stream()
                .filter(fi -> !fi.getFileName().equals("."))
                .filter(fi -> !fi.getFileName().equals(".."))
                .map(fi -> {
                    long directoryMark = FileAttributes.FILE_ATTRIBUTE_DIRECTORY.getValue() & fi.getFileAttributes();
                    boolean isDirectory = directoryMark > 0;
                    AbstractFileTable a = new DefaultFileTable();
                    a.setName(fi.getFileName());
                    a.setFile_namespace_path(fileNamespacePath + "\\" + fi.getFileName());
                    a.setFile_type(null);
                    a.setCached_file_size(fi.getEndOfFile());
                    a.setCreation_time(fi.getCreationTime().toDate());
                    a.setLast_access_time(fi.getLastAccessTime().toDate());
                    a.setIs_directory(isDirectory);
                    a.setIs_readonly(null);
                    a.setIs_archive(null);
                    a.setIs_hidden(null);
                    a.setIs_system(null);
                    a.setIs_offline(null);
                    a.setIs_temporary(null);
                    return a;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean rename(URI uri, String name) throws Exception {
        Tuple2<String,String> path_name = URIUtil.separatePathAndNameFromURI(uri);
        Tuple2<String,String> path_parentPath = toSmbPathWithParentPath(path_name);

        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);
        URI uriNew = UriComponentsBuilder.fromPath(pathName.v1())
                .pathSegment(name)
                .build()
                .toUri();
        String filePathNew = UriComponentsBuilder.newInstance()
                .path(fileTableProperties.getDatabase())
                .pathSegment(fileTableNameProvider.provide())
                .path(uriNew.getPath())
                .toUriString();
        String fileNameNew = filePathNew.replace("/", "\\");

        try(com.hierynomus.smbj.share.File f = diskShare.openFile(
                path_parentPath.v1(),
                of(DELETE,GENERIC_WRITE),
                of(FILE_ATTRIBUTE_NORMAL),
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN,
                null)){
            f.rename(fileNameNew);
            return true;
        }catch (Exception ex){
            logger.error("Rename file error!", ex);
            return false;
        }
    }

    @Override
    public List<AbstractFileTable> search(URI baseUri, String search) throws Exception {
        //TODO search in smb
        throw new UnsupportedOperationException("Smb cant search files");
    }
}
