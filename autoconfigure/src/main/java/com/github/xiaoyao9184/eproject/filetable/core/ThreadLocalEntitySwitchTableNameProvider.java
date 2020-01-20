package com.github.xiaoyao9184.eproject.filetable.core;

import javax.persistence.Table;

/**
 * Created by xy on 2020/1/15.
 */
public class ThreadLocalEntitySwitchTableNameProvider extends ThreadLocalTableNameProvider {

    public void set(Class<?> entityClass){
        super.set(entityClass.getAnnotation(Table.class)
                .name());
    }

}
