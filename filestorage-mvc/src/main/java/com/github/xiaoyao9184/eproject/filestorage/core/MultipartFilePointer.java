package com.github.xiaoyao9184.eproject.filestorage.core;

import com.google.common.net.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by xy on 2020/3/13.
 */
@SuppressWarnings("UnstableApiUsage")
public class MultipartFilePointer implements FilePointer {

    private URI uri;
    private MultipartFile multipartFile;

    private Instant lastModified;
    private String tag;
    private MediaType mediaTypeOrNull;

    public MultipartFilePointer(URI uri, MultipartFile multipartFile) {
        this.uri = uri;
        this.multipartFile = multipartFile;

        this.lastModified = Instant.now();
        this.tag = UUID.randomUUID().toString();
        this.mediaTypeOrNull = Optional.ofNullable(multipartFile)
                .map(MultipartFile::getContentType)
                .map(MediaType::parse)
                .orElse(null);
    }

    @Override
    public InputStream open() throws Exception {
        return multipartFile.getInputStream();
    }

    @Override
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public String getOriginalName() {
        return multipartFile.getOriginalFilename();
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
        return lastModified;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
