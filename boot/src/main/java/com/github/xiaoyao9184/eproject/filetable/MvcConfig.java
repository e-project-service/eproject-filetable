package com.github.xiaoyao9184.eproject.filetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*","localhost")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Content-Type","Accept","Origin","authorization");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if(env.getActiveProfiles().length == 0 ||
                Arrays.asList(env.getActiveProfiles()).contains("dev")){
            registry.addRedirectViewController("/","/swagger-ui.html");
        }else{
            registry.addRedirectViewController("/","/index.html");
        }
    }


}
