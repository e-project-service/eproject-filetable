package com.github.xiaoyao9184.eproject.filestorage.core;

import java.util.Optional;

/**
 * Created by xy on 2020/2/14.
 */
public interface FileStorage<ID> {
    Optional<FilePointer> findFile(ID id);

    boolean storageFile(Optional<FilePointer> filePointer, ID id);

    boolean rename(ID id, String name);

    boolean delete(ID id);
}
