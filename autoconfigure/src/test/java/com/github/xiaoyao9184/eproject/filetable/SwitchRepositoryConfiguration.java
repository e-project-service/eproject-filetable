package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.autoconfigure.FileTableAutoConfiguration;
import com.github.xiaoyao9184.eproject.filetable.core.*;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.DefaultFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.EntityClassRoutingRepository;
import com.github.xiaoyao9184.eproject.filetable.table.ThreadLocalEntityClassSwitchableProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
public class SwitchRepositoryConfiguration {

    /**
     * Not create because it not JPA Repository
     * @param fileTableNameProvider
     * @param fileTableRepository
     * @return
     */
    @Bean
    public EntityClassRoutingRepository entityTableNameSwitchTableRepository(
            @Autowired FileTableNameProvider fileTableNameProvider,
            @Autowired List<AbstractFileTableRepository> fileTableRepository
    ){
        return new EntityClassRoutingRepository(fileTableNameProvider,fileTableRepository);
    }

    /**
     * Replace {@link FileTableAutoConfiguration#fileTableRepositoryProvider(DefaultFileTableRepository)}
     * @param entityClassRoutingRepository
     * @return
     */
    @Bean
    public FileTableRepositoryProvider fileTableRepositoryProvider(
            @Autowired EntityClassRoutingRepository entityClassRoutingRepository
    ){
        return () -> entityClassRoutingRepository;
    }

    /**
     * Replace {@link FileTableAutoConfiguration#tableNameProvider()}
     * Switch table name on thread
     * @return
     */
    @Bean
    public ThreadLocalEntityClassSwitchableProvider tableNameProvider(){
        return new ThreadLocalEntityClassSwitchableProvider();
    }

}
