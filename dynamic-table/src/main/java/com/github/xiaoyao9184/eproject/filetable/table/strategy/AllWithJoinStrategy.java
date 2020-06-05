package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Connect the names provided by all providers join by '_'
 * Created by xy on 2020/6/1.
 */
public class AllWithJoinStrategy implements MixFileTableNameStrategy {

    @Override
    public boolean should(Stream<FileTableNameProvider> providers) {
        return true;
    }

    @Override
    public String apply(Stream<FileTableNameProvider> providers) {
        return providers
                .map(FileTableNameProvider::provide)
                .collect(Collectors.joining("_"));
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
