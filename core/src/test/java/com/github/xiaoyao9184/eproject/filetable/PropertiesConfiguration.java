package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by xy on 2020/1/15.
 */
@EnableConfigurationProperties({ PropertiesConfiguration.TestFileTableProperties.class })
public class PropertiesConfiguration {

    @ConfigurationProperties(
            prefix = "project.filetable"
    )
    public static class TestFileTableProperties extends BaseFileTableProperties {

    }

    @Bean
    public FileTableNameProvider tableNameProvider(){
        return new FileTableNameProvider() {
            @Override
            public String provide() {
                return TestFileTable.TABLE_NAME;
            }
        };
    }

}
