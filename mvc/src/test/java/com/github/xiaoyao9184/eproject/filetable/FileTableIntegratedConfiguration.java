package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableConverter;
import com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableStorage;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.core.handle.ConfigurationFileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.core.handle.FileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.DefaultFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.TestFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Created by xy on 2020/1/15.
 */
public class FileTableIntegratedConfiguration {

    @Bean
    public FileTableNameProvider tableNameProvider(){
        return new FileTableNameProvider() {
            @Override
            public String provide() {
                return TestFileTable.TABLE_NAME;
            }
        };
    }

    @Bean("databaseFileTableHandlerRepository")
    public AbstractFileTableRepository databaseFileTableHandlerRepository(
            @Autowired TestFileTableRepository testFileTableRepository
    ){
        return testFileTableRepository;
    }

    @Bean
    public FileTableHandler fileTableHandler(){
        return new ConfigurationFileTableHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public FileTableRepositoryProvider fileTableRepositoryProvider(
            @Autowired TestFileTableRepository testFileTableRepository
    ){
        return () -> testFileTableRepository;
    }

    @Bean
    public FileTableService fileTableService(){
        return new FileTableService();
    }

    @Bean
    public FileInfoFileTableStorage fileInfoFileTableStorage(){
        return new FileInfoFileTableStorage();
    }

    @Bean
    public FileInfoFileTableConverter fileInfoFileTableConverter(){
        return new FileInfoFileTableConverter();
    }

}
