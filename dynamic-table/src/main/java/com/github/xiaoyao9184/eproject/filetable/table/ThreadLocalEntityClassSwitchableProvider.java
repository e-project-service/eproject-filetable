package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;

import javax.persistence.Table;

/**
 * The table name provided by setting the entity class on thread
 * Created by xy on 2020/1/15.
 */
public class ThreadLocalEntityClassSwitchableProvider
        extends ThreadLocalFileTableNameProvider<Class<?>> {

    @Override
    public void set(Class<?> entityClass){
        super.set(entityClass.getAnnotation(Table.class)
                .name());
    }

    @Override
    public String name() {
        return TableNameProviders.jpa_entity.name();
    }

}
