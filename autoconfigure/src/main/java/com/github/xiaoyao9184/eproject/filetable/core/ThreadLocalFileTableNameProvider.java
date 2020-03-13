package com.github.xiaoyao9184.eproject.filetable.core;

/**
 * Created by xy on 2020/1/15.
 */
public class ThreadLocalFileTableNameProvider implements FileTableNameProvider {

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
}
