package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.TableNameProvider;
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
            prefix = "eproject.filetable"
    )
    public static class TestFileTableProperties extends BaseFileTableProperties {

    }

    @Bean
    public TableNameProvider tableNameProvider(){
        return new TableNameProvider() {
            @Override
            public String provide() {
                return TestFileTable.TABLE_NAME;
            }
        };
    }

}
