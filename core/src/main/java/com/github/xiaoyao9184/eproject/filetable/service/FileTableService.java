package com.github.xiaoyao9184.eproject.filetable.service;

import com.github.xiaoyao9184.eproject.filetable.core.handle.FileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
@Service
public class FileTableService implements FileTableHandler {


    @Autowired
    @Qualifier("fileTableHandler")
    private FileTableHandler fileTableHandler;


    @Override
    public AbstractFileTable create(MultipartFile multipartFile, URI uri) throws Exception {
        return fileTableHandler.create(multipartFile, uri);
    }

    @Override
    public AbstractFileTable create(File file, URI uri) throws Exception {
        return fileTableHandler.create(file, uri);
    }

    @Override
    public AbstractFileTable create(InputStream stream, URI uri) throws Exception {
        return fileTableHandler.create(stream, uri);
    }

    @Override
    public AbstractFileTable create(byte[] bytes, URI uri) throws Exception {
        return fileTableHandler.create(bytes, uri);
    }

    @Override
    public AbstractFileTable create(URI uri) throws Exception {
        return fileTableHandler.create(uri);
    }

    @Override
    public File readFile(URI uri) throws Exception {
        return fileTableHandler.readFile(uri);
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        return fileTableHandler.readStream(uri);
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        return fileTableHandler.readBytes(uri);
    }

    @Override
    public AbstractFileTable read(URI uri) throws Exception {
        return fileTableHandler.read(uri);
    }

    @Override
    @Transactional
    public boolean delete(URI uri) throws Exception {
        return fileTableHandler.delete(uri);
    }

    @Override
    public List<AbstractFileTable> readChild(URI uri) throws Exception {
        return fileTableHandler.readChild(uri);
    }

    @Override
    @Transactional
    public boolean rename(URI uri, String name) throws Exception {
        return fileTableHandler.rename(uri, name);
    }

    @Override
    public List<AbstractFileTable> search(URI baseUri, String search) throws Exception {
        return fileTableHandler.search(baseUri, search);
    }
}
