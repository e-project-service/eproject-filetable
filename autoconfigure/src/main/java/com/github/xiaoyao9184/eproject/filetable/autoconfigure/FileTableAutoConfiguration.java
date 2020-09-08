package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.core.*;
import com.github.xiaoyao9184.eproject.filetable.core.convert.FileTableDefaultConverter;
import com.github.xiaoyao9184.eproject.filetable.core.filestorage.FileTableConvertibleStorage;
import com.github.xiaoyao9184.eproject.filetable.core.filestorage.FileTableStorage;
import com.github.xiaoyao9184.eproject.filetable.core.handle.*;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.DefaultFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import com.github.xiaoyao9184.eproject.filetable.core.provider.DefaultFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.provider.SimpleJpaRepositoryBeanFileTableNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.xiaoyao9184.eproject.filetable.model.Constants.DEFAULT_FILE_TABLE_REPOSITORY;

/**
 * Created by xy on 2020/1/15.
 */
@Configuration
@EnableConfigurationProperties({ FileTableProperties.class, FileTableMappingProperties.class })
public class FileTableAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FileTableAutoConfiguration.class);

    public FileTableAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", FileTableAutoConfiguration.class.getName());
        }
    }

    @Bean
    @ConditionalOnMissingBean(FileTableNameProvider.class)
    public DefaultFileTableNameProvider defaultFileTableNameProvider(){
        return new DefaultFileTableNameProvider();
    }

    /**
     * Default FileTableRepository
     * Both for {@link SimpleJpaRepositoryBeanFileTableNameProvider}
     * @param defaultFileTableRepository
     * @return
     */
    @Bean(DEFAULT_FILE_TABLE_REPOSITORY)
    @ConditionalOnMissingBean
    public AbstractFileTableRepository databaseFileTableHandlerRepository(
            @Autowired DefaultFileTableRepository defaultFileTableRepository
    ){
        return defaultFileTableRepository;
    }

    /**
     *
     * @param defaultFileTableRepository Create by Spring Data JPA
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public FileTableRepositoryProvider fileTableRepositoryProvider(
            @Autowired DefaultFileTableRepository defaultFileTableRepository
    ){
        return () -> defaultFileTableRepository;
    }

    @Bean
    @ConditionalOnMissingBean(name = "fileTableHandler")
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

    @Bean
    public FileTableService fileTableService(){
        return new FileTableService();
    }

    @Bean
    @ConditionalOnMissingBean(FileTableConvertibleStorage.class)
    public FileTableStorage fileTableStorage(){
        return new FileTableStorage(fileTableService());
    }

    @Bean
    @ConditionalOnBean(FileTableConvertibleStorage.class)
    public FileTableDefaultConverter fileTableDefaultConverter(){
        return new FileTableDefaultConverter();
    }


}
