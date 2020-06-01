package com.github.xiaoyao9184.eproject.filetable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter implements Ordered {

    @Autowired
    private ServerProperties server;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ErrorController errorController;

    @Autowired
    private OAuth2SsoProperties sso;

    @Override
    public int getOrder() {
        if (this.sso.getFilterOrder() != null) {
            return this.sso.getFilterOrder();
        }
        if (ClassUtils.isPresent("org.springframework.boot.actuate.autoconfigure.ManagementServerProperties", null)) {
            // If > BASIC_AUTH_ORDER then the existing rules for the actuator
            // endpoints will take precedence. This value is < BASIC_AUTH_ORDER.
            return SecurityProperties.ACCESS_OVERRIDE_ORDER - 5;
        }
        return SecurityProperties.ACCESS_OVERRIDE_ORDER;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        List<String> ignored = securityProperties.getIgnored();
        if (this.errorController != null) {
            ignored.add(this.errorController.getErrorPath());
        }
        String[] paths = this.server.getPathsArray(ignored);
        List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
        if (!ObjectUtils.isEmpty(paths)) {
            for (String pattern : paths) {
                matchers.add(new AntPathRequestMatcher(pattern, null));
            }
        }
        if (!matchers.isEmpty()) {
            web.ignoring().requestMatchers(new OrRequestMatcher(matchers));
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                    .expressionHandler(new OAuth2WebSecurityExpressionHandler())
                    .antMatchers(
                            "/swagger-ui.html",
                            "/swagger-resources",
                            "/swagger-resources/configuration/ui",
                            "/swagger-resources/configuration/security",
                            "/v2/api-docs")
                    .access("hasAuthority('DEV') or hasRole('DEV') or #oauth2?.hasScope('uaa.admin')")
                    .anyRequest().authenticated()
                    .and()
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable().and()
                .httpBasic();
        // @formatter:on
    }
}
