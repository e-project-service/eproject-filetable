package com.github.xiaoyao9184.eproject.filetable.config;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryCreatorExpander;
import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryExpander;
import com.github.xiaoyao9184.eproject.filetable.core.hibernate.HibernateDynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryCreatorInterceptor;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryInitInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import java.util.Collections;

/**
 * Created by xy on 2020/4/19.
 */
@Configuration
public class FileTableDynamicConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FileTableDynamicConfiguration.class);

    public FileTableDynamicConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", FileTableDynamicConfiguration.class.getName());
        }
    }


    @Bean
    public DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander(){
        return new DynamicFileTableRepositoryCreatorExpander();
    }

    @Bean
    public DynamicFileTableRepositoryInitInterceptor dynamicFileTableRepositoryInitInterceptor(){
        return new DynamicFileTableRepositoryCreatorInterceptor();
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager(
            ApplicationContext applicationContext,
            LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ){
        return new HibernateDynamicFileTableRepositoryManager(
                Collections.singletonList(dynamicFileTableRepositoryInitInterceptor()),
                dynamicFileTableRepositoryExpander(),
                localContainerEntityManagerFactoryBean,
                applicationContext
        );
    }

}
