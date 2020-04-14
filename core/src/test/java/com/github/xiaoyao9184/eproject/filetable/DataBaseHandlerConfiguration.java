package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.core.handle.DatabaseFileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.FileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.TestFileTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * Created by xy on 2020/1/15.
 */
public class DataBaseHandlerConfiguration {

    @Bean
    public DatabaseFileTableHandler databaseFileTableHandler(){
        return new DatabaseFileTableHandler();
    }

    @Bean
    public FileTableRepositoryProvider fileTableRepositoryProvider(
            @Autowired TestFileTableRepository testFileTableRepository){
        return new FileTableRepositoryProvider() {
            @Override
            public FileTableRepository provide() {
                return testFileTableRepository;
            }
        };
    }

    @Bean("databaseFileTableHandlerRepository")
    public AbstractFileTableRepository databaseFileTableHandlerRepository(
            @Autowired TestFileTableRepository testFileTableRepository
            ){
        return testFileTableRepository;
    }
}
