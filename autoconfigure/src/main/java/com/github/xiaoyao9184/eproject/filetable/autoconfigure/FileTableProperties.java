package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xy on 2020/1/15.
 */
@ConfigurationProperties(
        prefix = "eproject.filetable"
)
public class FileTableProperties extends BaseFileTableProperties {

}
