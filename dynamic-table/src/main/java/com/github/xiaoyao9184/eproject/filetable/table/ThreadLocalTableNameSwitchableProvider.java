package com.github.xiaoyao9184.eproject.filetable.table;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.name;

/**
 * The table name provided by setting the table name on thread
 * Created by xy on 2020/1/15.
 */
public class ThreadLocalTableNameSwitchableProvider
        extends ThreadLocalFileTableNameProvider<String> {

    @Override
    public String name() {
        return name.name();
    }
}
