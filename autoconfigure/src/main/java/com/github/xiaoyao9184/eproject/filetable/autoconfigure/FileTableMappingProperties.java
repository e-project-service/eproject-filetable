package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.model.MappingProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xy on 2020/9/6.
 */
@ConfigurationProperties(
        prefix = "eproject.filetable.mapping"
)
public class FileTableMappingProperties extends MappingProperties {

}
