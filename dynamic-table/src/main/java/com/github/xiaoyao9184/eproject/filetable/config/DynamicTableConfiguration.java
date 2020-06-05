package com.github.xiaoyao9184.eproject.filetable.config;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProperties;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.table.MixFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.ThreadLocalEntityClassSwitchableProvider;
import com.github.xiaoyao9184.eproject.filetable.table.ThreadLocalTableNameSwitchableProvider;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.AllWithJoinStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyIfManualAvailableStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/6/1.
 */
@Configuration
public class DynamicTableConfiguration {

//    /**
//     * Provide table name through current repository by repository provider
//     * @return Provider
//     */
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Bean
//    public SimpleJpaRepositoryBeanFileTableNameProvider simpleJpaRepositoryBeanFileTableNameProvider(
//            @Lazy FileTableRepositoryProvider fileTableRepositoryProvider
//    ){
//        return new SimpleJpaRepositoryBeanFileTableNameProvider(fileTableRepositoryProvider);
//    }

    /**
     * Provide table name through thread context by manual setting entity class
     * @return Provider
     */
    @Bean
    public ThreadLocalEntityClassSwitchableProvider threadLocalEntitySwitchFileTableNameProvider(){
        return new ThreadLocalEntityClassSwitchableProvider();
    }

    /**
     * Provide table name through thread context by manual setting a string
     * @return Provider
     */
    @Bean
    public ThreadLocalTableNameSwitchableProvider threadLocalTableNameSwitchableProvider(){
        return new ThreadLocalTableNameSwitchableProvider();
    }

    /**
     * Provide table name through mix multiple provider
     * by strategy {@link MixFileTableNameStrategy}
     * and name list {@link TableNameProviders} properties
     * @param providerList provider collection
     * @param strategyList strategy collection
     * @param properties properties
     * @return Provider
     */
    @Primary
    @Bean
    public MixFileTableNameProvider mixFileTableNameProvider(
            List<FileTableNameProvider> providerList,
            List<MixFileTableNameStrategy> strategyList,
            TableNameProperties properties
    ) {
        List<TableNameProviders> tableNameProvidersList = properties
                .getMix().stream()
                .map(TableNameProperties.Mix::getName)
                .collect(Collectors.toList());

        return new MixFileTableNameProvider(
                strategyList,
                tableNameProvidersList,
                providerList);
    }

    /**
     * Highest priority strategy
     * @return strategy
     */
    @Bean
    public ExclusivelyIfManualAvailableStrategy exclusivelyIfManualAvailableStrategy(){
        return new ExclusivelyIfManualAvailableStrategy();
    }

    /**
     * Highest priority strategy
     * @return strategy
     */
    @Bean
    public AllWithJoinStrategy allWithJoinStrategy(){
        return new AllWithJoinStrategy();
    }

}
