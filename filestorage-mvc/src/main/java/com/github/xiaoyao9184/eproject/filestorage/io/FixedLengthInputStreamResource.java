package com.github.xiaoyao9184.eproject.filestorage.io;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fix InputStreamResource InputStream has already been read
 * Created by xy on 2020/2/14.
 */
public class FixedLengthInputStreamResource extends InputStreamResource {
    private final long contentLength;

    public FixedLengthInputStreamResource(InputStream inputStream, long contentLength) {
        super(inputStream);
        this.contentLength = contentLength;
    }

    @Override
    public long contentLength() throws IOException {
        return contentLength;
    }
}
