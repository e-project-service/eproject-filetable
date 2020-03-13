package com.github.xiaoyao9184.eproject.filetable.core.convert;

import com.github.xiaoyao9184.eproject.filetable.core.context.FileTableContext;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;

/**
 * Created by xy on 2020/3/13.
 */
public interface FileTableConverter<T> {

    T convert(AbstractFileTable aft, FileTableContext fileTableContext);
}

