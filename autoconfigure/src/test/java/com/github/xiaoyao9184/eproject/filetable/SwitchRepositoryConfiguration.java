package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.*;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.DefaultTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.EntityTableNameSwitchTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
public class SwitchRepositoryConfiguration {

    @Bean("databaseFileTableHandlerRepository")
    public EntityTableNameSwitchTableRepository databaseFileTableHandlerRepository(
            @Autowired TableNameProvider tableNameProvider,
            @Autowired List<AbstractFileTableRepository> fileTableRepository
    ){
        return new EntityTableNameSwitchTableRepository(tableNameProvider,fileTableRepository);
    }

    @Bean
    public ThreadLocalEntitySwitchTableNameProvider tableNameProvider(){
        return new ThreadLocalEntitySwitchTableNameProvider();
    }

}
