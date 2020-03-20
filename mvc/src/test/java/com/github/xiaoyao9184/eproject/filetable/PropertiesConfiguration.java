package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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

}
