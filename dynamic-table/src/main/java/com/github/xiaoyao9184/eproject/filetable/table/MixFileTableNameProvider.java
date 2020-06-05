package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The table name provided by mix multiple providers,
 * based on name order list and strategies
 * Created by xy on 2020/6/1.
 */
public class MixFileTableNameProvider implements FileTableNameProvider {

    private static final Logger logger = LoggerFactory.getLogger(MixFileTableNameProvider.class);

    private final List<MixFileTableNameStrategy> strategies;
    private final List<FileTableNameProvider> providers;

    /**
     * @param strategyList strategy collection
     * @param nameList provider name ordered collection
     * @param providerList provider collection
     */
    public MixFileTableNameProvider(
            List<MixFileTableNameStrategy> strategyList,
            List<TableNameProviders> nameList,
            List<FileTableNameProvider> providerList) {
        this.strategies = strategyList;
        this.strategies.sort(Comparator.comparingInt(Ordered::getOrder));
        Map<String,FileTableNameProvider> typeProviderMap = providerList.stream()
                .collect(Collectors.toMap(this::getNamedNameOrClassName, Function.identity()));
        this.providers = nameList.stream()
                .map(nameType -> typeProviderMap.get(nameType.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String provide() {
        return strategies.stream()
                .filter(strategy -> strategy.should(this.providers.stream()))
                .map(strategy -> strategy.apply(this.providers.stream()))
                .peek(strategy -> logger.info("Mix use {} strategy", strategy.getClass().getSimpleName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provider provide failed!"));
    }

    /**
     * Get provider name if is {@link Named} or get class name
     * @param provider provider
     * @return provider name
     */
    private String getNamedNameOrClassName(FileTableNameProvider provider){
        return Optional.of(provider)
                .filter(p -> p instanceof Named)
                .map(p -> ((Named) p).name())
                .orElse(provider.getClass().getName());
    }

}
