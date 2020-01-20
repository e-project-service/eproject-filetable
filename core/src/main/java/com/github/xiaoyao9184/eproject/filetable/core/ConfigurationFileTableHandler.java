package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.Operator.*;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.DATABASE;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.FILE;

/**
 * Created by xy on 2020/1/15.
 */
public class ConfigurationFileTableHandler implements FileTableHandler {

    @Autowired
    private BaseFileTableProperties attachFileTableProperties;

    @Autowired
    private DatabaseFileTableHandler databaseFileTableHandler;

    @Autowired
    private FileSystemFileTableHandler fileSystemFileTableHandler;

    @Autowired
    private SMBFileTableHandler smbFileTableHandler;

    @Override
    public FileInfo create(MultipartFile multipartFile, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(multipartFile,uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.create(multipartFile,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(multipartFile,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public FileInfo create(File file, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(file,uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.create(file,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(file,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public FileInfo create(byte[] bytes, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(bytes,uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.create(bytes,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(bytes,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public FileInfo create(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.create(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public FileInfo create(InputStream stream, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(stream,uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.create(stream,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(stream,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public File readFile(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readFile(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.readFile(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readFile(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readBytes(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.readBytes(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readBytes(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readStream(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.readStream(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readStream(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public FileInfo read(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(READ, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.read(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.read(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.read(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(DELETE, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.delete(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.delete(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.delete(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public List<FileInfo> readChild(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = attachFileTableProperties.getOperatorMethods()
                .getOrDefault(READ, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.readChild(uri);
        }else if(om == FILE && attachFileTableProperties.getMappingPath() == null){
            return smbFileTableHandler.readChild(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readChild(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }
}
