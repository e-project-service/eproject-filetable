package com.github.xiaoyao9184.eproject.filetable.core.response;

import org.springframework.http.MediaType;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

/**
 * Created by xy on 2020/2/14.
 */
public interface FilePointer {
    InputStream open() throws Exception;

    long getSize();

    String getOriginalName();

    String getEtag();

    Optional<MediaType> getMediaType();

    boolean matchesEtag(String requestEtag);

    Instant getLastModified();

    boolean modifiedAfter(Instant isModifiedSince);
}
