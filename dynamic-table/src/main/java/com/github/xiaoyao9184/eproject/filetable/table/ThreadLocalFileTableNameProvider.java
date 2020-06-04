package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.name;

/**
 * Created by xy on 2020/1/15.
 */
public class ThreadLocalFileTableNameProvider implements FileTableNameProvider, Named {

    @Override
    public String provide() {
        return tableNameThreadLocal.get() == null ?
                "" :
                tableNameThreadLocal.get();
    }


    private ThreadLocal<String> tableNameThreadLocal = new ThreadLocal<>();

    public void set(String tableName){
        tableNameThreadLocal.set(tableName);
    }

    @Override
    public String name() {
        return name.name();
    }
}
