package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.core.response.FilePointer;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import org.springframework.http.MediaType;

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


    private FileInfo fileInfo;
    private String tag;
    private MediaType mediaTypeOrNull;

    public FileTableServiceFilePointer(URI uri, FileTableService fileTableService) throws Exception {
        this.uri = uri;
        this.fileTableService = fileTableService;

        this.fileInfo = fileTableService.read(uri);

        this.tag = fileInfo.getCreationTime().toInstant().toString();

        Path path = new File(fileInfo.getName()).toPath();
        final String contentType = Files.probeContentType(path);
        this.mediaTypeOrNull = contentType != null ?
                MediaType.parseMediaType(contentType) :
                null;
    }

    @Override
    public InputStream open() throws Exception {
        return fileTableService.readStream(uri);
    }

    @Override
    public long getSize() {
        return fileInfo.getSize();
    }

    @Override
    public String getOriginalName() {
        return fileInfo.getName();
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
    public boolean matchesEtag(String requestEtag) {
        return getEtag().equals(requestEtag);
    }

    @Override
    public Instant getLastModified() {
        return fileInfo.getCreationTime().toInstant();
    }

    @Override
    public boolean modifiedAfter(Instant clientTime) {
        return !clientTime.isBefore(getLastModified());
    }
}
