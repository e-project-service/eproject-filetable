package com.github.xiaoyao9184.eproject.filestorage.config;

import com.github.xiaoyao9184.eproject.filestorage.accept.MimeApiPathExtensionAdapterContentNegotiationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by xy on 2020/3/13.
 */
@Configuration
public class MimeApiConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MimeApiConfiguration.class);

    public MimeApiConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", MimeApiConfiguration.class.getName());
        }
    }

    public MimeApiPathExtensionAdapterContentNegotiationStrategy mimeApiPathExtensionAdapterContentNegotiationStrategy(){
        return new MimeApiPathExtensionAdapterContentNegotiationStrategy();
    }

    @Bean
    public BeanPostProcessor contentNegotiationManagerPostProcessor(

    ) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
                if(ContentNegotiationManager.class.isInstance(bean)) {

                    Field field = ReflectionUtils.findField(ContentNegotiationManager.class,"strategies");
                    field.setAccessible(true);
                    try {
                        List<ContentNegotiationStrategy> listStrategy = (List<ContentNegotiationStrategy>) field.get(bean);
                        listStrategy.add(0, mimeApiPathExtensionAdapterContentNegotiationStrategy());
                    } catch (IllegalAccessException e) {
                        logger.error("Cant add MimeApiPathExtensionAdapterContentNegotiationStrategy to ContentNegotiationManager",e);
                    }
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
    }
}
