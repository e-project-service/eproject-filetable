package com.github.xiaoyao9184.eproject.filetable.core;

/**
 * Created by xy on 2020/6/1.
 */
public interface FileTableNameProviderSetter<T> {
    void set(T t);

    static boolean isManual(FileTableNameProvider provider){
        return provider instanceof FileTableNameProviderSetter;
    }

    static boolean isManual(Class<? extends FileTableNameProvider> clazz){
        return clazz.isAssignableFrom(FileTableNameProviderSetter.class);
    }
}
