package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by xy on 2020/6/1.
 */
public abstract class ExclusivelyIfInWhiteListStrategy implements MixFileTableNameStrategy {

    protected Collection<String> whitelist;

    public ExclusivelyIfInWhiteListStrategy(){
        this.whitelist = new ArrayList<>();
    }

    public ExclusivelyIfInWhiteListStrategy(Collection<String> whitelist){
        this.whitelist = new ArrayList<>(whitelist);
    }

    @Override
    public boolean should(Stream<FileTableNameProvider> providers) {
        return providers
                .filter(this::predicate)
                .map(FileTableNameProvider::provide)
                .anyMatch(name -> whitelist.contains(name));
    }

    @Override
    public String apply(Stream<FileTableNameProvider> providers) {
        return providers
                .filter(this::predicate)
                .map(FileTableNameProvider::provide)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Strategy apply failed!"));
    }


    public void addWhitelist(String item){
        this.whitelist.add(item);
    }

    public abstract boolean predicate(FileTableNameProvider provider);
}
