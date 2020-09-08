package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.Operator.*;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.*;

/**
 * Use configuration
 * (now is from properties { @link BaseFileTableProperties#getOperatorMethods() } )
 * to determine the actual performer of an operation
 *
 * Created by xy on 2020/1/15.
 */
public class ConfigurationFileTableHandler implements FileTableHandler {

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Autowired
    private MappingProperties mappingProperties;

    @Autowired
    private DatabaseFileTableHandler databaseFileTableHandler;

    //TODO when not on windows platform will not Autowired fileSystemFileTableHandler
    @Autowired
    private FileSystemFileTableHandler fileSystemFileTableHandler;

    @Autowired
    private SMBFileTableHandler smbFileTableHandler;

    private Map<BaseFileTableProperties.OperatorMethod, FileTableHandler> handlerMap;

    @PostConstruct
    public void init(){
        handlerMap = new HashMap<>();
        handlerMap.put(FILE,fileSystemFileTableHandler);
    }

    @Override
    public AbstractFileTable create(MultipartFile multipartFile, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(multipartFile,uri);
        //TODO when not on windows platform will not Autowired fileSystemFileTableHandler
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.create(multipartFile,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(multipartFile,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public AbstractFileTable create(File file, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(file,uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.create(file,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(file,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public AbstractFileTable create(byte[] bytes, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(bytes,uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.create(bytes,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(bytes,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public AbstractFileTable create(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.create(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public AbstractFileTable create(InputStream stream, URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(CREATE, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.create(stream,uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.create(stream,uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.create(stream,uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public File readFile(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readFile(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.readFile(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readFile(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readBytes(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.readBytes(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readBytes(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(READ_STREAM, FILE);
        if(om == DATABASE){
            return databaseFileTableHandler.readStream(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.readStream(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readStream(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public AbstractFileTable read(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(READ, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.read(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.read(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.read(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public boolean delete(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(DELETE, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.delete(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.delete(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.delete(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public List<AbstractFileTable> readChild(URI uri) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(READ, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.readChild(uri);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.readChild(uri);
        }else if(om == FILE){
            return fileSystemFileTableHandler.readChild(uri);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public boolean rename(URI uri, String name) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(RENAME, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.rename(uri, name);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.rename(uri, name);
        }else if(om == FILE){
            return fileSystemFileTableHandler.rename(uri, name);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }

    @Override
    public List<AbstractFileTable> search(URI baseUri, String search) throws Exception {
        BaseFileTableProperties.OperatorMethod om = fileTableProperties.getOperatorMethods()
                .getOrDefault(SEARCH, DATABASE);
        if(om == DATABASE){
            return databaseFileTableHandler.search(baseUri, search);
        }else if(om == FILE && mappingProperties.getDevice() == null){
            return smbFileTableHandler.search(baseUri, search);
        }
        throw new UnsupportedOperationException("Configuration error!");
    }
}
