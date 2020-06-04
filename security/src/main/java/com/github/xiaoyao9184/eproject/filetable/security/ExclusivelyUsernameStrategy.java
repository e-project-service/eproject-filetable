package com.github.xiaoyao9184.eproject.filetable.security;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyIfInWhiteListStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;

import java.util.Collection;
import java.util.stream.Stream;

/**
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
        boolean isWithClient = providers
                .filter(provider -> provider instanceof SecurityContextClientIdFileTableNameProvider)
                .map(FileTableNameProvider::provide)
                .findFirst()
                .isPresent();
        if(isWithClient){
            return super.should(providers);
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
