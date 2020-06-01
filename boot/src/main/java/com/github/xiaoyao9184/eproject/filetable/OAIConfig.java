package com.github.xiaoyao9184.eproject.filetable;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
@EnableSwagger2
public class OAIConfig {

    public static final String securitySchemaOAuth2 = "oauth2schema";

    @Value("${project.oai-ui.client.authorizeUrl}")
    private String authorizeUrl;

    @Value("${project.oai-ui.client.clientId}")
    private String clientId;

    @Value("${project.oai-ui.client.clientSecret}")
    private String clientSecret;

    @Value("${project.oai-ui.client.realm}")
    private String realm;

    @Value("${project.oai-ui.client.scopeSeparator}")
    private String scopeSeparator;

    /**
     * OAI Docket
     * @return Docket
     */
    @Bean(name = "oaiDocket")
    public Docket docket() {
        // @formatter:off
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("eProject-FileTable")
                .select()
                    .apis(withClassAnnotation(Api.class))
                    .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .securitySchemes(newArrayList(securitySchema()))
                .securityContexts(newArrayList(securityContext()));
        // @formatter:on
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("eProject-FileTable API")
                .description("eProject FileTable")
                .version("0.1")
                .contact(new Contact("xy", "", "xiaoyao9184@gmail.com"))
                .build();
    }

    /**
     * App OAI Security
     * @return SecurityConfiguration
     */
    @Bean(name = "oaiSecurity")
    public SecurityConfiguration security() {
        return new SecurityConfiguration(
                clientId,
                clientSecret,
                realm,
                "app",
                "api_value",
                ApiKeyVehicle.HEADER,
                "api_key",
                scopeSeparator
        );
    }

    private OAuth securitySchema() {
        List<AuthorizationScope> authorizationScopeList
                = defaultScope();

        LoginEndpoint loginEndpoint = new LoginEndpoint(authorizeUrl);
        GrantType grantType = new ImplicitGrant(loginEndpoint, "access_token");
        return new OAuth(securitySchemaOAuth2, authorizationScopeList, newArrayList(grantType));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<AuthorizationScope> authorizationScopeList
                = defaultScope();

        return newArrayList(
                new SecurityReference(
                        securitySchemaOAuth2,
                        authorizationScopeList.toArray(new AuthorizationScope[]{})));
    }

    private List<AuthorizationScope> defaultScope() {
        return newArrayList(
                new AuthorizationScope("filetable.write", "write"),
                new AuthorizationScope("filetable.read", "read"));
    }

}
