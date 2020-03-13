package com.github.xiaoyao9184.eproject.filestorage.core;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by xy on 2020/3/13.
 */
public interface FileInfoStorage<ID,INFO> {
    List<INFO> searchInfo(ID uri, String search);

    INFO storageInfo(Optional<FilePointer> filePointer, URI uri);

    List<INFO> listInfo(ID uri);

    INFO findInfo(ID uri);
}
