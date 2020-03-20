package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.core.context.FileTableContext;
import com.github.xiaoyao9184.eproject.filetable.core.convert.FileTableConverter;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.model.FileTableInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xy on 2020/3/13.
 */
public class FileInfoFileTableConverter implements FileTableConverter<FileInfo> {

    @Autowired
    BaseFileTableProperties fileTableProperties;

    @Override
    public FileInfo convert(AbstractFileTable aft, FileTableContext context) {
        int index = aft.getFile_namespace_path().lastIndexOf("\\");
        String pp = aft.getFile_namespace_path().substring(0,index)
                .replace("\\","/");

        String p = aft.getFile_namespace_path()
                .replace("\\","/");

        FileTableInfo info = new FileTableInfo();
        info.setName(aft.getName());
        info.setPath(p);
        info.setParentPath(pp);

        info.setSize(aft.getCached_file_size());
        info.setCreationTime(aft.getCreation_time());

        info.setServerName(fileTableProperties.getServername());
        info.setInstance(fileTableProperties.getInstance());
        info.setDatabase(fileTableProperties.getDatabase());

        return info;
    }
}
