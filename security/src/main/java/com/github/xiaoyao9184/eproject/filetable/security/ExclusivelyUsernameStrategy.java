package com.github.xiaoyao9184.eproject.filetable.security;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyIfInWhiteListStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Exclusive if {@link SecurityContextNameFileTableNameProvider} result is in the whitelist
 * and use {@link SecurityContextClientIdFileTableNameProvider} same time
 * Created by xy on 2020/6/1.
 */
public class ExclusivelyUsernameStrategy extends ExclusivelyIfInWhiteListStrategy implements MixFileTableNameStrategy {

    public ExclusivelyUsernameStrategy(){
        super();
    }

    public ExclusivelyUsernameStrategy(Collection<String> whitelist){
        super(whitelist);
    }

    @Override
    public boolean should(Stream<FileTableNameProvider> providers) {
        //FIX stream has already been operated upon or closed
        List<FileTableNameProvider> list = providers
                .filter(provider -> this.predicate(provider) ||
                        provider instanceof SecurityContextClientIdFileTableNameProvider)
                .collect(Collectors.toList());
        boolean isWithClient = list.stream()
                .filter(provider -> provider instanceof SecurityContextClientIdFileTableNameProvider)
                .map(FileTableNameProvider::provide)
                .findFirst()
                .isPresent();
        if(isWithClient){
            return super.should(list.stream());
        }
        return false;
    }

    @Override
    public boolean predicate(FileTableNameProvider provider) {
        return provider instanceof SecurityContextNameFileTableNameProvider;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
