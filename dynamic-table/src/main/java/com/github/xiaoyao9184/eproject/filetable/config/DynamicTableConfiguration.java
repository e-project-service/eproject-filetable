package com.github.xiaoyao9184.eproject.filetable.config;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProperties;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.table.MixFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.SimpleJpaRepositoryBeanFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.ThreadLocalEntitySwitchFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.ThreadLocalFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.AllWithJoinStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyIfManualAvailableStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
public class DynamicTableConfiguration {

    /**
     * Provide table name through current repository by repository provider
     * @return Provider
     */
    @Bean
    public SimpleJpaRepositoryBeanFileTableNameProvider simpleJpaRepositoryBeanFileTableNameProvider(
            @Lazy FileTableRepositoryProvider fileTableRepositoryProvider
    ){
        return new SimpleJpaRepositoryBeanFileTableNameProvider(fileTableRepositoryProvider);
    }

    /**
     * Provide table name through thread context by manual setting a string
     * @return Provider
     */
    @Bean
    public ThreadLocalFileTableNameProvider threadLocalFileTableNameProvider(){
        return new ThreadLocalFileTableNameProvider();
    }

    /**
     * Provide table name through thread context by manual setting entity class
     * @return Provider
     */
    @Bean
    public ThreadLocalEntitySwitchFileTableNameProvider threadLocalEntitySwitchFileTableNameProvider(){
        return new ThreadLocalEntitySwitchFileTableNameProvider();
    }

    /**
     * Provide table name through mix multiple provider
     * by strategy {@link MixFileTableNameStrategy}
     * and name list {@link TableNameProviders} properties
     * @param providerList
     * @param strategyList
     * @param properties
     * @return Provider
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Primary
    @Bean
    public MixFileTableNameProvider mixFileTableNameProvider(
            List<FileTableNameProvider> providerList,
            List<MixFileTableNameStrategy> strategyList,
            TableNameProperties properties
    ) {
        List<TableNameProviders> tableNameProvidersList = properties
                .stream()
                .map(TableNameProperties.TableName::getName)
                .collect(Collectors.toList());

        return new MixFileTableNameProvider(
                strategyList,
                tableNameProvidersList,
                providerList);
    }

    @Bean
    public ExclusivelyIfManualAvailableStrategy exclusivelyIfManualAvailableStrategy(){
        return new ExclusivelyIfManualAvailableStrategy();
    }

    @Bean
    public AllWithJoinStrategy allWithJoinStrategy(){
        return new AllWithJoinStrategy();
    }

}
