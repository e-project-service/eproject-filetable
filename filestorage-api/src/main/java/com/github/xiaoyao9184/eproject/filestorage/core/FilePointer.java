package com.github.xiaoyao9184.eproject.filestorage.core;


import com.google.common.net.MediaType;

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

    Instant getLastModified();

    default boolean matchesEtag(String requestEtag) {
        return getEtag().equals(requestEtag);
    }

    default boolean modifiedAfter(Instant clientTime) {
        return !clientTime.isBefore(getLastModified());
    }

}
