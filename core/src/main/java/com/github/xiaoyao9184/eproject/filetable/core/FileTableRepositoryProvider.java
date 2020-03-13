package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.repository.FileTableRepository;

/**
 * Created by xy on 2020/3/13.
 */
public interface FileTableRepositoryProvider {
    FileTableRepository provide();
}
