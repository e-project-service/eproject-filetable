package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProviderSetter;

import java.util.stream.Stream;

/**
 * Created by xy on 2020/6/1.
 */
public class ExclusivelyIfManualAvailableStrategy implements MixFileTableNameStrategy {
    @Override
    public boolean should(Stream<FileTableNameProvider> providers) {
        return providers
                .filter(FileTableNameProviderSetter::isManual)
                .anyMatch(provider -> provider.provide() != null);
    }

    @Override
    public String apply(Stream<FileTableNameProvider> providers) {
        return providers
                .filter(FileTableNameProviderSetter::isManual)
                .map(FileTableNameProvider::provide)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Strategy apply failed!"));
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
