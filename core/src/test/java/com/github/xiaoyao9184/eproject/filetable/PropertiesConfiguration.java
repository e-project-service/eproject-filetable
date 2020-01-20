package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.TableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by xy on 2020/1/15.
 */
@EnableConfigurationProperties({ PropertiesConfiguration.AttachFileTableProperties.class })
public class PropertiesConfiguration {

    @ConfigurationProperties(
            prefix = "project.attach.filetable"
    )
    public static class AttachFileTableProperties extends BaseFileTableProperties {

    }

    @Bean
    public TableNameProvider tableNameProvider(){
        return new TableNameProvider() {
            @Override
            public String provide() {
                return "test_filetable";
            }
        };
    }

}
