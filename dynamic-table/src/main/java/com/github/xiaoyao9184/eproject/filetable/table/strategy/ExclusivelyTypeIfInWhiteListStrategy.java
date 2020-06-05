package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;

import java.util.Collection;

/**
 * Exclusive if the specified provider provide result in the whitelist
 * Created by xy on 2020/6/1.
 */
public class ExclusivelyTypeIfInWhiteListStrategy extends ExclusivelyIfInWhiteListStrategy implements MixFileTableNameStrategy {

    private final Class<? extends FileTableNameProvider> clazz;
    private final int order;

    public ExclusivelyTypeIfInWhiteListStrategy(Class<? extends FileTableNameProvider> clazz, int order){
        super();
        this.clazz = clazz;
        this.order = order;
    }

    public ExclusivelyTypeIfInWhiteListStrategy(Class<? extends FileTableNameProvider> clazz, int order, Collection<String> whitelist){
        super(whitelist);
        this.clazz = clazz;
        this.order = order;
    }

    @Override
    public boolean predicate(FileTableNameProvider provider) {
        return this.clazz.isInstance(provider);
    }

    @Override
    public int getOrder() {
        return order;
    }
}
