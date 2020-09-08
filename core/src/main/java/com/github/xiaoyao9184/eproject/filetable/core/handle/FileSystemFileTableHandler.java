package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.core.mapping.FileTableLocalhostMappingBuilder;
import com.github.xiaoyao9184.eproject.filetable.core.mapping.FileTablePathBuilder;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.entity.DefaultFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.MappingProperties;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.util.URIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Use the file system {@link File } to implement operations.
 * FileTable file system mapping by properties {@link BaseFileTableProperties }
 *
 * Created by xy on 2020/1/15.
 */
public class FileSystemFileTableHandler implements FileTableHandler {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemFileTableHandler.class);

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Autowired
    private MappingProperties mappingProperties;

    @Autowired
    private FileTableNameProvider fileTableNameProvider;

    private FileTableLocalhostMappingBuilder fileTableLocalhostMappingBuilder;

    @PostConstruct
    public void init(){
        fileTableLocalhostMappingBuilder = FileTableLocalhostMappingBuilder.newInstance()
                .root(mappingProperties.getDevice() + ":/")
                .table(fileTableNameProvider)
                .database(fileTableProperties.getDatabase())
                .instance(fileTableProperties.getInstance())
                .server(fileTableProperties.getServername())
                .location(mappingProperties.getLocation());
    }

    /**
     * mapping-path will replace FileTable full
     *
     * @param stream
     * @param path
     * @param name
     * @param size
     * @param time
     * @return
     * @throws Exception
     */
    private AbstractFileTable create(InputStream stream, String path, String name, Long size, Date time) throws Exception {
        //filesystem file path is mapping\database-directory\FileTable-directory\{file}
        URI uri = fileTableLocalhostMappingBuilder.build()
                .path(path)
                .pathSegment(name)
                .build()
                .toUri();

        File file = new File(uri);

        if(fileTableProperties.isAutoCreateDirectory() && path.length() != 0){
            boolean dirOk = file.getParentFile().mkdirs();
            if(!dirOk){
                logger.error("Cant create parent path!");
            }
        }

        Long copySize;
        try(
                InputStream is = stream;
                OutputStream os = new FileOutputStream(file);
        ){
            copySize = IOUtils.copy(is,os,4096);
        }
        if(size != null && copySize != size.longValue()){
            logger.error("Actual file size {} does not match expected size {} !", copySize, size);
        }

        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
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
        a.setIs_hidden(file.isHidden());
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
            Tuple2<String,String> path_name = separatePathAndNameFromURI(uri);
            if(path_name.v2.contains(".")){
                // uri is full path contain file name
                logger.info("File {} create with new name {}.", multipartFile.getOriginalFilename(), path_name.v2);
                path = path_name.v1();
                name = path_name.v2();
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
    public AbstractFileTable create(File file, URI uri) throws Exception {
        String path = uri.toString();
        String name = file.getName();
        if(!uri.toString().endsWith("/")){
            Tuple2<String,String> path_name = separatePathAndNameFromURI(uri);
            if(path_name.v2.contains(".")){
                // uri is full path contain file name
                logger.info("File {} create with new name {}.", file.getName(), path_name.v2);
                path = path_name.v1();
                name = path_name.v2();
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
        Tuple2<String,String> path_name = separatePathAndNameFromURI(uri);

        return create(
                stream,
                path_name.v1,
                path_name.v2,
                null,
                new Date());
    }

    @Override
    public AbstractFileTable create(byte[] bytes, URI uri) throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        return create(byteInputStream,uri);
    }

    @Override
    public AbstractFileTable create(URI uri) throws Exception {
        //filesystem file path is mapping\database-directory\FileTable-directory\{file}
        URI uriDir = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        Path pathDir = Paths.get(uriDir);
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        Date time = new Date();
        Tuple2<String,String> path_name = separatePathAndNameFromURI(uri);
        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();

        AbstractFileTable a = new DefaultFileTable();
        a.setName(path_name.v2());
        a.setFile_namespace_path(fileNamespacePath);
        a.setCached_file_size(null);
        a.setFile_type(null);
        a.setCreation_time(time);
        a.setLast_access_time(time);
        a.setIs_directory(true);
        a.setIs_readonly(false);
        a.setIs_archive(false);
        a.setIs_hidden(false);
        a.setIs_system(false);
        a.setIs_offline(false);
        a.setIs_temporary(false);
        return a;
    }

    @Override
    public File readFile(URI uri) throws Exception {
        URI fileUri = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        return new File(fileUri);
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        File file = readFile(uri);
        return new FileInputStream(file);
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        try(
                InputStream stream = readStream(uri);
        ){
            byte[] targetArray = new byte[stream.available()];
            stream.read(targetArray);
            stream.close();
            return targetArray;
        }
    }


    @Override
    public AbstractFileTable read(URI uri) throws Exception {
        URI fileUri = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);

        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        Tuple2<String,String> path_name = separatePathAndNameFromURI(uri);
        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();

        AbstractFileTable a = new DefaultFileTable();
        a.setName(path_name.v2());
        a.setFile_namespace_path(fileNamespacePath);
        a.setCached_file_size(file.length());
        a.setFile_type(null);
        a.setCreation_time(Date.from(attributes.creationTime().toInstant()));
        a.setLast_access_time(Date.from(attributes.lastAccessTime().toInstant()));
        a.setIs_directory(attributes.isDirectory());
        a.setIs_readonly(null);
        a.setIs_archive(null);
        a.setIs_hidden(file.isHidden());
        a.setIs_system(null);
        a.setIs_offline(null);
        a.setIs_temporary(null);
        return a;
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        URI fileUri = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);
        return file.delete();
    }

    @Override
    public List<AbstractFileTable> readChild(URI uri) throws Exception {
        URI fileUri = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);

        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .path(uri.getPath())
                .and()
                .build()
                .toWinString();

        return Stream.of(Objects.requireNonNull(file.listFiles()))
                .map(f -> {
                    Date creationTime = null;
                    Date lastAccessTime = null;
                    try {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        creationTime = Date.from(attributes.creationTime().toInstant());
                        lastAccessTime = Date.from(attributes.lastAccessTime().toInstant());
                    } catch (IOException e) {
                        logger.error("Cant read file attributes!", e);
                    }
                    AbstractFileTable a = new DefaultFileTable();
                    a.setName(f.getName());
                    a.setFile_namespace_path(fileNamespacePath + f.getName());
                    a.setCached_file_size(f.length());
                    a.setFile_type(FilenameUtils.getExtension(f.getName()));
                    a.setCreation_time(creationTime);
                    a.setLast_access_time(lastAccessTime);
                    a.setIs_directory(f.isDirectory());
                    a.setIs_readonly(null);
                    a.setIs_archive(null);
                    a.setIs_hidden(f.isHidden());
                    a.setIs_system(null);
                    a.setIs_offline(null);
                    a.setIs_temporary(null);
                    return a;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean rename(URI uri, String name) throws Exception {
        URI fileUri = fileTableLocalhostMappingBuilder.build()
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);

        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);
        URI fileUriNew = fileTableLocalhostMappingBuilder.build()
                .path(pathName.v1())
                .pathSegment(name)
                .build()
                .toUri();
        File fileNew = new File(fileUriNew);

        return file.renameTo(fileNew);
    }

    @Override
    public List<AbstractFileTable> search(URI baseUri, String search) throws Exception {
        throw new UnsupportedOperationException("FileSystem cant search files");
    }
}
