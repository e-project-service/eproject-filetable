package com.github.xiaoyao9184.eproject.filetable.service;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableServiceFilePointer;
import com.github.xiaoyao9184.eproject.filetable.core.response.FilePointer;
import com.github.xiaoyao9184.eproject.filetable.core.response.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

/**
 * use FileTableService
 * Created by xy on 2020/2/14.
 */
@Service
public class FileTableFileStorageService implements FileStorage<URI> {

    @Autowired
    private FileTableService fileTableService;

    @Override
    public Optional<FilePointer> findFile(URI uri) {
        try {
            FilePointer filePointer = new FileTableServiceFilePointer(uri,fileTableService);
            return Optional.of(filePointer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
