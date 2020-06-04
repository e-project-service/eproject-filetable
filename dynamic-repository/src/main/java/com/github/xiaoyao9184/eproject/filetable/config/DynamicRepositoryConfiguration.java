package com.github.xiaoyao9184.eproject.filetable.config;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryCreatorExpander;
import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryExpander;
import com.github.xiaoyao9184.eproject.filetable.core.hibernate.HibernateDynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryCreatorInterceptor;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryInitInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import java.util.Collections;

/**
 * Created by xy on 2020/4/19.
 */
@Configuration
public class DynamicRepositoryConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DynamicRepositoryConfiguration.class);

    public DynamicRepositoryConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", DynamicRepositoryConfiguration.class.getName());
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
