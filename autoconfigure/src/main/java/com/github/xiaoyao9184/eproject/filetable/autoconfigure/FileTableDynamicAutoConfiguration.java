package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.aop.DynamicTableInitAspect;
import com.github.xiaoyao9184.eproject.filetable.config.DynamicRepositoryConfiguration;
import com.github.xiaoyao9184.eproject.filetable.config.DynamicTableConfiguration;
import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProperties;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.EntityClassRoutingRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.TableNameRoutingRepository;
import com.github.xiaoyao9184.eproject.filetable.security.ExclusivelyUsernameStrategy;
import com.github.xiaoyao9184.eproject.filetable.security.SecurityContextClientIdFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.security.SecurityContextNameFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyTypeIfInWhiteListStrategy;
import com.github.xiaoyao9184.eproject.filetable.zone.ZoneFileTableNameProvider;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
public class FileTableDynamicAutoConfiguration {

    @ConditionalOnClass(DynamicRepositoryConfiguration.class)
    @Configuration
    @Import(DynamicRepositoryConfiguration.class)
    public static class DynamicRepositoryAutoConfiguration {

        /**
         * Use
         * Not create because it not JPA Repository
         * @param fileTableNameProvider
         * @param dynamicFileTableRepositoryManager
         * @return
         */
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public TableNameRoutingRepository tableNameSwitchTableRepository(
                @Autowired FileTableNameProvider fileTableNameProvider,
                @Autowired DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager
        ){
            return new TableNameRoutingRepository(
                    fileTableNameProvider,
                    dynamicFileTableRepositoryManager,
                    Collections.emptyMap());
        }

        /**
         *
         * @param entityTableNameRoutingRepository
         * @return
         */
        @Bean
        public FileTableRepositoryProvider fileTableRepositoryProvider(
                @Autowired TableNameRoutingRepository entityTableNameRoutingRepository
        ){
            return () -> entityTableNameRoutingRepository;
        }
    }

    @ConditionalOnClass(DynamicTableConfiguration.class)
    @Configuration
    @EnableConfigurationProperties({ FileTableNameProperties.class })
    @Import(DynamicTableConfiguration.class)
    public static class DynamicTableAutoConfiguration {

        /**
         * Use
         * Not create because it not JPA Repository
         * @param fileTableNameProvider
         * @param fileTableRepository
         * @return
         */
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Bean
        public EntityClassRoutingRepository tableNameSwitchTableRepository(
                @Autowired FileTableNameProvider fileTableNameProvider,
                @Autowired List<AbstractFileTableRepository> fileTableRepository
        ){
            return new EntityClassRoutingRepository(
                    fileTableNameProvider,
                    fileTableRepository);
        }

        /**
         *
         * @param tableNameSwitchTableRepository
         * @return
         */
        @Bean
        public FileTableRepositoryProvider fileTableRepositoryProvider(
                @Autowired EntityClassRoutingRepository tableNameSwitchTableRepository
        ){
            return () -> tableNameSwitchTableRepository;
        }

        @Bean
        public DynamicTableInitAspect dynamicTableInitAspect(){
            return new DynamicTableInitAspect();
        }

        @ConditionalOnClass({
                SecurityContextClientIdFileTableNameProvider.class,
                SecurityContextNameFileTableNameProvider.class
        })
        @Configuration
        public static class SecurityTableNameConfiguration {

            @Bean
            public SecurityContextClientIdFileTableNameProvider securityContextClientIdFileTableNameProvider(){
                return new SecurityContextClientIdFileTableNameProvider();
            }

            @Bean
            public SecurityContextNameFileTableNameProvider securityContextNameFileTableNameProvider(){
                return new SecurityContextNameFileTableNameProvider();
            }

            @Bean
            public ExclusivelyTypeIfInWhiteListStrategy onlyClientIdStrategy(
                    TableNameProperties properties
            ){
                return new ExclusivelyTypeIfInWhiteListStrategy(
                        SecurityContextClientIdFileTableNameProvider.class,
                        0,
                        properties.getExclusive(TableNameProviders.client_id)
                );
            }

            @Bean
            public ExclusivelyUsernameStrategy onlyUsernameStrategy(
                    TableNameProperties properties
            ){
                return new ExclusivelyUsernameStrategy(
                        properties.getExclusive(TableNameProviders.username));
            }

        }

        @ConditionalOnClass({
                ZoneFileTableNameProvider.class
        })
        @Configuration
        public static class ZoneTableNameConfiguration {

            @Bean
            public ZoneFileTableNameProvider zoneFileTableNameProvider(){
                return new ZoneFileTableNameProvider();
            }

            @Bean
            public ExclusivelyTypeIfInWhiteListStrategy onlyZoneStrategy(
                    TableNameProperties properties
            ){
                return new ExclusivelyTypeIfInWhiteListStrategy(
                        ZoneFileTableNameProvider.class,
                        -1,
                        properties.getExclusive(TableNameProviders.zone)
                );
            }

        }
    }

}
