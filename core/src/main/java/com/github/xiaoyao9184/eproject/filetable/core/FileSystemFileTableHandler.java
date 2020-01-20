package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.model.FileTableInfo;
import com.github.xiaoyao9184.eproject.filetable.model.MappingLocalhostFileTableInfo;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

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
 * Created by xy on 2020/1/15.
 */
public class FileSystemFileTableHandler implements FileTableHandler {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemFileTableHandler.class);

    @Autowired
    private BaseFileTableProperties baseFileTableProperties;

    @Autowired
    private TableNameProvider tableNameProvider;

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
    private FileInfo create(InputStream stream, String path, String name, Long size, Date time) throws Exception {
        //filesystem file path is mapping\database-directory\FileTable-directory\{file}

        URI uri = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
                .path(path)
                .pathSegment(name)
                .build()
                .toUri();

        File file = new File(uri);
        boolean dirOk = file.getParentFile().mkdirs();
        int copySize;
        try(
                InputStream is = stream;
                OutputStream os = new FileOutputStream(file);
        ){
            copySize = IOUtils.copy(is,os);
            if(size != null && copySize != size.intValue()){
                logger.error("Actual file size {} does not match expected size {} !", copySize, size);
            }
        }

        String location = UriComponentsBuilder.newInstance()
                .pathSegment(baseFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(path)
                .toUriString();
        String fullPath = UriComponentsBuilder.newInstance()
                .pathSegment(tableNameProvider.provide())
                .path(path)
                .pathSegment(name)
                .toUriString();

        MappingLocalhostFileTableInfo info = new MappingLocalhostFileTableInfo();
        info.setName(name);
        info.setPath(fullPath);
        info.setParentPath(location);

        info.setSize((long) copySize);
        info.setCreationTime(time);

        info.setServerName(baseFileTableProperties.getServername());
        info.setInstance(baseFileTableProperties.getInstance());
        info.setDatabase(baseFileTableProperties.getDatabase());

        info.setMappingPath(baseFileTableProperties.getMappingPath());

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
                null,
                new Date());
    }

    @Override
    public FileInfo create(byte[] bytes, URI uri) throws Exception {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        return create(byteInputStream,uri);
    }

    @Override
    public FileInfo create(URI uri) throws Exception {
        //filesystem file path is mapping\database-directory\FileTable-directory\{file}

        URI uriDir = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
                .path(uri.getPath())
                .build()
                .toUri();

        Path pathDir = Paths.get(uriDir);
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);
        String location = UriComponentsBuilder.newInstance()
                .pathSegment(baseFileTableProperties.getDatabase())
                .pathSegment(tableNameProvider.provide())
                .path(pn.v1())
                .toUriString();
        String fullPath = UriComponentsBuilder.newInstance()
                .pathSegment(tableNameProvider.provide())
                .path(pn.v1())
                .pathSegment(pn.v2())
                .toUriString();


        MappingLocalhostFileTableInfo info = new MappingLocalhostFileTableInfo();
        info.setName(pn.v2());
        info.setPath(fullPath);
        info.setParentPath(location);

        info.setServerName(baseFileTableProperties.getServername());
        info.setInstance(baseFileTableProperties.getInstance());
        info.setDatabase(baseFileTableProperties.getDatabase());

        info.setMappingPath(baseFileTableProperties.getMappingPath());

        return info;
    }

    @Override
    public File readFile(URI uri) throws Exception {
        URI fileUri = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
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
    public FileInfo read(URI uri) throws Exception {
        URI fileUri = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);
        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        Tuple2<String,String> pn = separatePathAndNameFromURI(uri);
        String location = UriComponentsBuilder.newInstance()
                .path(pn.v1())
                .toUriString();
        String fullPath = UriComponentsBuilder.newInstance()
                .path(pn.v1())
                .pathSegment(pn.v2())
                .toUriString();


        MappingLocalhostFileTableInfo info = new MappingLocalhostFileTableInfo();
        info.setName(file.getName());
        info.setPath(fullPath);
        info.setParentPath(location);

        info.setSize(file.length());
        info.setCreationTime(Date.from(attributes.creationTime().toInstant()));

        info.setServerName(baseFileTableProperties.getServername());
        info.setInstance(baseFileTableProperties.getInstance());
        info.setDatabase(baseFileTableProperties.getDatabase());

        info.setMappingPath(baseFileTableProperties.getMappingPath());

        return info;
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        URI fileUri = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);
        return file.delete();
    }

    @Override
    public List<FileInfo> readChild(URI uri) throws Exception {
        URI fileUri = UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(baseFileTableProperties.getMappingPath())
                .path(uri.getPath())
                .build()
                .toUri();

        File file = new File(fileUri);

        String location = UriComponentsBuilder.newInstance()
                .path(uri.getPath())
                .toUriString();

        return Stream.of(Objects.requireNonNull(file.listFiles()))
                .map(f -> {
                    Date time = null;
                    try {
                        BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        time = Date.from(attributes.creationTime().toInstant());
                    } catch (IOException e) {
                        logger.error("Cant read file attributes!", e);
                    }


                    MappingLocalhostFileTableInfo info = new MappingLocalhostFileTableInfo();
                    info.setName(file.getName());
                    info.setPath(location + "/" + f.getName());
                    info.setParentPath(location);

                    info.setSize(file.length());
                    info.setCreationTime(time);

                    info.setServerName(baseFileTableProperties.getServername());
                    info.setInstance(baseFileTableProperties.getInstance());
                    info.setDatabase(baseFileTableProperties.getDatabase());

                    info.setMappingPath(baseFileTableProperties.getMappingPath());

                    return info;
                })
                .collect(Collectors.toList());
    }
}
