package com.github.xiaoyao9184.eproject.filetable.core.filestorage;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filetable.core.context.FileTableContext;
import com.github.xiaoyao9184.eproject.filetable.core.convert.FileTableConverter;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/3/13.
 */
public abstract class FileTableConvertibleStorage<FILE_INFO>
        implements FileStorage<URI>, FileInfoStorage<URI,FILE_INFO> {

    private static final Logger logger = LoggerFactory.getLogger(FileTableConvertibleStorage.class);

    //TODO remove without FileTable
    @Autowired
    private FileTableService fileTableService;

    @Autowired
    private FileTableConverter<FILE_INFO> converter;

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
        return filePointer
                .map(p -> {
                    try {
                        return fileTableService.create(p.open(),uri) != null;
                    } catch (Exception e) {
                        logger.error("Cant storage file!", e);
                    }
                    return false;
                })
                .orElseGet(() -> {
                    try {
                        return fileTableService.create(uri) != null;
                    } catch (Exception e) {
                        logger.error("Cant storage empty file!", e);
                    }
                    return false;
                });
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
    public List<FILE_INFO> searchInfo(URI uri, String search) {
        try {
            FileTableContext context = FileTableContext.create()
                    .withBase(uri);

            return fileTableService.search(uri, search)
                    .stream()
                    .map(file -> converter.convert(file,context))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Cant search filetable file!", e);
        }
        return Collections.emptyList();
    }

    @Override
    public FILE_INFO storageInfo(Optional<FilePointer> filePointer, URI uri) {
        FileTableContext context = FileTableContext.create()
                .withURL(uri);
        return filePointer
                .map(p -> {
                    try {
                        AbstractFileTable aft = fileTableService.create(p.open(),uri);
                        return converter.convert(aft,context);
                    } catch (Exception e) {
                        logger.error("Cant create filetable file!", e);
                    }
                    return null;
                })
                .orElseGet(() -> {
                    try {
                        AbstractFileTable aft = fileTableService.create(uri);
                        return converter.convert(aft,context);
                    } catch (Exception e) {
                        logger.error("Cant storage empty filetable file!", e);
                    }
                    return null;
                });
    }

    @Override
    public List<FILE_INFO> listInfo(URI uri) {
        try {
            FileTableContext context = FileTableContext.create()
                    .withBase(uri);
            return fileTableService.readChild(uri)
                    .stream()
                    .map(file -> converter.convert(file,context))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FILE_INFO findInfo(URI uri) {
        try {
            FileTableContext context = FileTableContext.create()
                    .withURL(uri);
            AbstractFileTable aft = fileTableService.read(uri);
            return converter.convert(aft,context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
