package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.zone.ZoneResolvingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
@ConditionalOnClass(ZoneResolvingFilter.class)
public class ZoneAutoConfiguration {

    @Bean
    public ZoneResolvingFilter zoneResolvingFilter(
            @Autowired FileTableProperties fileTableProperties
    ){
        ZoneResolvingFilter filter = new ZoneResolvingFilter();
        filter.setDefaultInternalHostnames(new HashSet<>(fileTableProperties.getInternalHostnames()));
        return filter;
    }

}
