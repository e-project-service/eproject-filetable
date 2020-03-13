package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.handle.ConfigurationFileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.core.handle.FileTableHandler;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by xy on 2020/1/15.
 */
//@SpringBootApplication
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableWebMvc
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class BootMvcConfiguration extends WebMvcConfigurerAdapter
    {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).
                favorParameter(false).
                ignoreAcceptHeader(false).
                useJaf(false).
                defaultContentType(MediaType.APPLICATION_JSON);
    }


    @Bean
    public FileTableHandler fileTableHandler(){
        return new ConfigurationFileTableHandler();
    }

}
