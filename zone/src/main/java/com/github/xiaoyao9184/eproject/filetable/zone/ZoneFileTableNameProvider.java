package com.github.xiaoyao9184.eproject.filetable.zone;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.zone;

/**
 * Provide zone name by {@link ZoneHolder}
 * Created by xy on 2020/6/1.
 */
public class ZoneFileTableNameProvider implements FileTableNameProvider, Named {

    @Override
    public String provide() {
        return ZoneHolder.get();
    }

    @Override
    public String name() {
        return zone.name();
    }

}
