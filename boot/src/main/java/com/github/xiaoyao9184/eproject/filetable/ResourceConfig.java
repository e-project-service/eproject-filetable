package com.github.xiaoyao9184.eproject.filetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private SecurityProperties security;

    @Autowired
    private ResourceServerProperties resource;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .sessionManagement()
                    .sessionCreationPolicy(security.getSessions())
                    .and()
                .requestMatchers()
                    .antMatchers("/v1/**")
                    .and()
                .authorizeRequests()
                    .antMatchers("/v1/**").authenticated()
                    .anyRequest().authenticated()
                    .and()
                .cors().and()
                .httpBasic();
        // @formatter:on
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(this.resource.getResourceId()).stateless(false);
    }

}
