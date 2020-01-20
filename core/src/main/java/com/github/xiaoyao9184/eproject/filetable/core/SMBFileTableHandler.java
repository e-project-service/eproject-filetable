package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.model.FileTableInfo;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xy on 2020/1/15.
 */
public class SMBFileTableHandler implements FileTableHandler {

    private static final Logger logger = LoggerFactory.getLogger(SMBFileTableHandler.class);

    @Autowired
    private TableNameProvider tableNameProvider;

    @Autowired
    private BaseFileTableProperties attachFileTableProperties;

    private SMBClient client;
    private Session session;
    private DiskShare diskShare;

    @PostConstruct
    public void init() throws IOException {
        if(attachFileTableProperties.getServername() == null){
            logger.error("No share server configuration provided of FileTable");
            return;
        }

        client = new SMBClient();
        Connection connection = client.connect(attachFileTableProperties.getServername());
        AuthenticationContext ac = new AuthenticationContext(
                attachFileTableProperties.getUsername(),
                attachFileTableProperties.getPassword().toCharArray(),
                attachFileTableProperties.getDomain());
        session = connection.authenticate(ac);
        diskShare = (DiskShare) session.connectShare(attachFileTableProperties.getInstance());
    }


//    private Session onceSession = null;

    private void onceDiskShare(Consumer<DiskShare> diskShareConsumer) throws IOException {
        try(Connection connection = client.connect(attachFileTableProperties.getServername())){
            AuthenticationContext ac = new AuthenticationContext(
                    attachFileTableProperties.getUsername(),
                    attachFileTableProperties.getPassword().toCharArray(),
                    attachFileTableProperties.getDomain());
            Session session = connection.authenticate(ac);
            DiskShare ds = (DiskShare) session.connectShare(attachFileTableProperties.getInstance());
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
     * FileTable full path format
     * \\servername\instance-share\database-directory\FileTable-directory
     *
     * @param stream
     * @param path
     * @param name
     * @param size
     * @param time
     * @return
     * @throws Exception
     */
    private FileInfo create(InputStream stream, String path, String name, Long size, Date time) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(path)
                .toUriString();
        String fileName = filePath.replace("/", "\\")
                + "\\"
                + name;


        Set<FileAttributes> fileAttributes = new HashSet<>();
        fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
        Set<SMB2CreateOptions> createOptions = new HashSet<>();
        createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                fileName,
                new HashSet<>(Collections.singletonList(AccessMask.GENERIC_ALL)),
                fileAttributes,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OVERWRITE_IF,
                createOptions);

        int copySize = size.intValue();
        try(
                InputStream is = stream;
                OutputStream os = f.getOutputStream();
        ){
            copySize = IOUtils.copy(is,os);
            if(copySize != size.intValue()){
                logger.error("Actual file size {} does not match expected size {} !", copySize, size);
            }
        }

        String id = UriComponentsBuilder.newInstance()
                .path(path)
                .pathSegment(name)
                .toUriString();


        FileTableInfo info = new FileTableInfo();
        info.setName(name);
        info.setPath(id);
        info.setParentPath(filePath);

        info.setSize((long) copySize);
        info.setCreationTime(time);

        info.setServerName(attachFileTableProperties.getServername());
        info.setInstance(attachFileTableProperties.getInstance());
        info.setDatabase(attachFileTableProperties.getDatabase());
        return info;
    }

    @Override
    public FileInfo create(MultipartFile multipartFile, URI uri) throws Exception {
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
        }

        return create(
                multipartFile.getInputStream(),
                path,
                name,
                multipartFile.getSize(),
                new Date());
    }

    @Override
    public FileInfo create(File file, URI uri) throws Exception {
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
    public FileInfo create(InputStream stream, URI uri) throws Exception {
        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);

        return create(
                stream,
                pn.v1,
                pn.v2,
                0L,
                new Date());
    }

    @Override
    public FileInfo create(byte[] bytes, URI uri) throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        return create(byteInputStream, uri);
    }

    @Override
    public FileInfo create(URI uri) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        StringBuilder sbCurrentDir = new StringBuilder();
        Stream.of(fileName.split("\\\\"))
                .forEach(dir -> {
                    sbCurrentDir.append(dir);
                    if(!diskShare.folderExists(sbCurrentDir.toString())){
                        diskShare.mkdir(sbCurrentDir.toString());
                    }
                    sbCurrentDir.append("\\");
                });

        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);

        FileTableInfo info = new FileTableInfo();
        info.setName(pn.v2());
        info.setPath(uri.getPath());
        info.setParentPath(pn.v1());

        info.setServerName(attachFileTableProperties.getServername());
        info.setInstance(attachFileTableProperties.getInstance());
        info.setDatabase(attachFileTableProperties.getDatabase());
        return info;
    }

    @Override
    public File readFile(URI uri) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        Set<FileAttributes> fileAttributes = new HashSet<>();
        fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
        Set<SMB2CreateOptions> createOptions = new HashSet<>();
        createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                fileName,
                new HashSet<>(Collections.singletonList(AccessMask.GENERIC_ALL)),
                fileAttributes,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN_IF,
                createOptions);

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
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        Set<FileAttributes> fileAttributes = new HashSet<>();
        fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
        Set<SMB2CreateOptions> createOptions = new HashSet<>();
        createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);

        com.hierynomus.smbj.share.File f = diskShare.openFile(
                fileName,
                new HashSet<>(Collections.singletonList(AccessMask.GENERIC_ALL)),
                fileAttributes,
                SMB2ShareAccess.ALL,
                SMB2CreateDisposition.FILE_OPEN_IF,
                createOptions);

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
    public FileInfo read(URI uri) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        FileAllInformation file = diskShare.getFileInformation(fileName);

        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);
        String location = UriComponentsBuilder.newInstance()
                .pathSegment(tableNameProvider.provide())
                .path(pn.v1())
                .toUriString()
                .replace("/","\\");
        String path = UriComponentsBuilder.newInstance()
                .path(pn.v1())
                .pathSegment(pn.v2())
                .toUriString();

        FileTableInfo info = new FileTableInfo();
        info.setName(pn.v2());
        info.setPath(path);
        info.setParentPath(location);

        info.setSize(file.getStandardInformation().getEndOfFile());
        info.setCreationTime(file.getBasicInformation().getCreationTime().toDate());

        info.setServerName(attachFileTableProperties.getServername());
        info.setInstance(attachFileTableProperties.getInstance());
        info.setDatabase(attachFileTableProperties.getDatabase());
        return info;
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        if(diskShare.folderExists(fileName)){
            diskShare.rmdir(fileName,false);
        }else if(diskShare.fileExists(fileName)){
//            onceDiskShare(ds -> ds.rm(fileName));
            diskShare.rm(fileName);
        }
        return true;
    }

    @Override
    public List<FileInfo> readChild(URI uri) throws Exception {
        //smb file path is database-directory\FileTable-directory\{file}
        String filePath = UriComponentsBuilder.newInstance()
                .path(attachFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(uri.getPath())
                .toUriString();
        String fileName = filePath.replace("/", "\\");

        List<FileIdBothDirectoryInformation> fis = diskShare.list(fileName);

        String location = UriComponentsBuilder.newInstance()
                .pathSegment(tableNameProvider.provide())
                .path(uri.toString())
                .toUriString()
                .replace("/","\\");

        return fis.stream()
                .filter(fi -> !fi.getFileName().equals("."))
                .filter(fi -> !fi.getFileName().equals(".."))
                .map(fi -> {
                    FileTableInfo info = new FileTableInfo();
                    info.setName(fi.getFileName());
                    info.setPath(uri.toString() + "/" + fi.getFileName());
                    info.setParentPath(location);

                    info.setSize(fi.getEndOfFile());
                    info.setCreationTime(fi.getCreationTime().toDate());

                    info.setServerName(attachFileTableProperties.getServername());
                    info.setInstance(attachFileTableProperties.getInstance());
                    info.setDatabase(attachFileTableProperties.getDatabase());
                    return info;
                })
                .collect(Collectors.toList());
    }
}
