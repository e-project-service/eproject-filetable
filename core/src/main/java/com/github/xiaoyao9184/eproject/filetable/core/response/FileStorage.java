package com.github.xiaoyao9184.eproject.filetable.core.response;

import java.util.Optional;

/**
 * Created by xy on 2020/2/14.
 */
public interface FileStorage<ID> {
    Optional<FilePointer> findFile(ID id);
}
