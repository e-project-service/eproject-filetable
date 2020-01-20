package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.core.*;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.DefaultTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xy on 2020/1/15.
 */
@Configuration
@EnableConfigurationProperties({ FileTableProperties.class })
public class FileTableAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FileTableAutoConfiguration.class);

    public FileTableAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init AttachFileTableAutoConfiguration");
        }
    }

    @Bean("databaseFileTableHandlerRepository")
    @ConditionalOnMissingBean
    public AbstractFileTableRepository databaseFileTableHandlerRepository(
            @Autowired DefaultTableRepository defaultTableRepository
    ){
        return defaultTableRepository;
    }

    @Bean
    @ConditionalOnMissingBean
    public TableNameProvider tableNameProvider(){
        return new DefaultTableNameProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public FileTableHandler fileTableHandler(){
        return new ConfigurationFileTableHandler();
    }

    @Bean
    public SMBFileTableHandler smbFileTableHandler(){
        return new SMBFileTableHandler();
    }

    @Bean
    public FileSystemFileTableHandler fileSystemFileTableHandler(){
        return new FileSystemFileTableHandler();
    }

    @Bean
    public DatabaseFileTableHandler databaseFileTableHandler(){
        return new DatabaseFileTableHandler();
    }


}
