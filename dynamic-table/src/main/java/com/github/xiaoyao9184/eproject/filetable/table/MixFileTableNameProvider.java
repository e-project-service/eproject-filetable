package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;
import org.springframework.core.Ordered;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * If use manual
 * Created by xy on 2020/6/1.
 */
public class MixFileTableNameProvider implements FileTableNameProvider {

    private final List<MixFileTableNameStrategy> strategys;
    private final List<FileTableNameProvider> providers;

    public MixFileTableNameProvider(
            List<MixFileTableNameStrategy> strategyList,
            List<TableNameProviders> nameList,
            List<FileTableNameProvider> providerList) {
        this.strategys = strategyList;
        this.strategys.sort(Comparator.comparingInt(Ordered::getOrder));
        Map<String,FileTableNameProvider> typeProviderMap = providerList.stream()
                .collect(Collectors.toMap(this::getNamedNameOrClassName, Function.identity()));
        this.providers = nameList.stream()
                .map(nameType -> typeProviderMap.get(nameType.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String provide() {
        return strategys.stream()
                .filter(strategy -> strategy.should(this.providers.stream()))
                .map(strategy -> strategy.apply(this.providers.stream()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provider provide failed!"));
    }


    private String getNamedNameOrClassName(FileTableNameProvider provider){
        return Optional.of(provider)
                .filter(p -> p instanceof Named)
                .map(p -> ((Named) p).name())
                .orElse(provider.getClass().getName());
    }

}
