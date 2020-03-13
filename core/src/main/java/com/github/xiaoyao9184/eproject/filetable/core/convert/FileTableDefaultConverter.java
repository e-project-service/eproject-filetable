package com.github.xiaoyao9184.eproject.filetable.core.convert;

import com.github.xiaoyao9184.eproject.filetable.core.context.FileTableContext;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.entity.DefaultFileTable;
import org.springframework.beans.BeanUtils;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTableDefaultConverter implements FileTableConverter<DefaultFileTable> {

    @Override
    public DefaultFileTable convert(AbstractFileTable aft, FileTableContext context) {
        DefaultFileTable defaultFileTable = new DefaultFileTable();
        BeanUtils.copyProperties(aft,defaultFileTable);
        return defaultFileTable;
    }
}
