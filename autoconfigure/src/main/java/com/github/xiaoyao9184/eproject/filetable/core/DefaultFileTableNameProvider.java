package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.entity.DefaultFileTable;

import javax.persistence.Table;

/**
 * Created by xy on 2020/1/15.
 */
public class DefaultFileTableNameProvider implements FileTableNameProvider {

    @Override
    public String provide() {
        return DefaultFileTable.class.getAnnotation(Table.class)
                .name();
    }
}
