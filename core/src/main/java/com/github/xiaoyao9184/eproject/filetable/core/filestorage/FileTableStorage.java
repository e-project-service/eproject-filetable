package com.github.xiaoyao9184.eproject.filetable.core.filestorage;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * use FileTableService
 * Created by xy on 2020/2/14.
 */
public class FileTableStorage implements FileStorage<URI>, FileInfoStorage<URI, AbstractFileTable> {

    private static final Logger logger = LoggerFactory.getLogger(FileTableStorage.class);

    //    @Autowired
    private FileTableService fileTableService;

    public FileTableStorage(FileTableService fileTableService) {
        this.fileTableService = fileTableService;
    }

    @Override
    public Optional<FilePointer> findFile(URI uri) {
        try {
            FilePointer filePointer = new FileTableServiceFilePointer(uri,fileTableService);
            return Optional.of(filePointer);
        } catch (Exception e) {
            logger.error("Cant find file!", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean storageFile(Optional<FilePointer> filePointer, URI uri) {
        return this.storageInfo(filePointer, uri) != null;
    }

    @Override
    public boolean rename(URI uri, String name) {
        try {
            return fileTableService.rename(uri, name);
        } catch (Exception e) {
            logger.error("Cant rename file!", e);
        }

        return false;
    }

    @Override
    public boolean delete(URI uri) {
        try {
            return fileTableService.delete(uri);
        } catch (Exception e) {
            logger.error("Cant delete file!", e);
        }

        return false;
    }

    @Override
    public List<AbstractFileTable> searchInfo(URI uri, String search) {
        try {
            return fileTableService.search(uri, search);
        } catch (Exception e) {
            logger.error("Cant search filetable file!", e);
        }
        return Collections.emptyList();
    }


    @Override
    public AbstractFileTable storageInfo(Optional<FilePointer> filePointer, URI uri) {
        return filePointer
                .map(p -> {
                    try {
                        return fileTableService.create(p.open(),uri);
                    } catch (Exception e) {
                        logger.error("Cant create filetable file!", e);
                        throw new RuntimeException(e);
                    }
                })
                .orElseGet(() -> {
                    try {
                        return fileTableService.create(uri);
                    } catch (Exception e) {
                        logger.error("Cant storage filetable directory!", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public List<AbstractFileTable> listInfo(URI uri) {
        try {
            return fileTableService.readChild(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AbstractFileTable findInfo(URI uri) {
        try {
            return fileTableService.read(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
