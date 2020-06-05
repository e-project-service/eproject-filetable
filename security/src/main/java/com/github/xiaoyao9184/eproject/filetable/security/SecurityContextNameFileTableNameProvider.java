package com.github.xiaoyao9184.eproject.filetable.security;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.username;

/**
 * Provide {@link Authentication} name
 * Created by xy on 2020/6/1.
 */
public class SecurityContextNameFileTableNameProvider implements FileTableNameProvider, Named {

    @Override
    public String provide() {
        SecurityContext context = SecurityContextHolder
                .getContext();
        Authentication a = context.getAuthentication();
        return a.getName();
    }

    @Override
    public String name() {
        return username.name();
    }

}
