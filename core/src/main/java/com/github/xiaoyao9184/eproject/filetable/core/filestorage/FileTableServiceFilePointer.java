package com.github.xiaoyao9184.eproject.filetable.core.filestorage;

import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

/**
 * use FileTableService
 * Created by xy on 2020/2/14.
 */
public class FileTableServiceFilePointer implements FilePointer {

    private URI uri;
    private FileTableService fileTableService;

    private AbstractFileTable abstractFileTable;
    private String tag;
    private MediaType mediaTypeOrNull;

    public FileTableServiceFilePointer(URI uri, FileTableService fileTableService) throws Exception {
        this.uri = uri;
        this.fileTableService = fileTableService;

        this.abstractFileTable = fileTableService.read(uri);

        this.tag = abstractFileTable.getLast_write_time().toInstant().toString();

        Path path = new File(abstractFileTable.getName()).toPath();
        final String contentType = Files.probeContentType(path);
        this.mediaTypeOrNull = contentType != null ?
                MediaType.parse(contentType) :
                null;
    }

    @Override
    public InputStream open() throws Exception {
        return fileTableService.readStream(uri);
    }

    @Override
    public long getSize() {
        return abstractFileTable.getCached_file_size();
    }

    @Override
    public String getOriginalName() {
        return abstractFileTable.getName();
    }

    @Override
    public String getEtag() {
        return "\"" + tag + "\"";
    }

    @Override
    public Optional<MediaType> getMediaType() {
        return Optional.ofNullable(mediaTypeOrNull);
    }

    @Override
    public Instant getLastModified() {
        return abstractFileTable.getLast_write_time().toInstant();
    }

}
