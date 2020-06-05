package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProviderSetter;
import com.github.xiaoyao9184.eproject.filetable.model.Named;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.name;

/**
 * The table name provided by setting the table name on thread
 * Created by xy on 2020/1/15.
 */
public abstract class ThreadLocalFileTableNameProvider<SET_TYPE>
        implements FileTableNameProvider, FileTableNameProviderSetter<SET_TYPE>, Named {

    private ThreadLocal<String> tableNameThreadLocal = new ThreadLocal<>();

    @Override
    public String provide() {
        return tableNameThreadLocal.get();
    }

    public void set(String tableName){
        tableNameThreadLocal.set(tableName);
    }

}
