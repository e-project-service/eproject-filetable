package com.github.xiaoyao9184.eproject.filetable.security;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import static com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders.client_id;

/**
 * Provide {@link OAuth2Authentication} client id
 * Created by xy on 2020/6/1.
 */
public class SecurityContextClientIdFileTableNameProvider implements FileTableNameProvider, Named {

    @Override
    public String provide() {
        SecurityContext context = SecurityContextHolder
                .getContext();
        Authentication a = context.getAuthentication();
        if(a instanceof OAuth2Authentication){
            OAuth2Authentication o2a = (OAuth2Authentication) a;
            return o2a.getOAuth2Request().getClientId();
        }
        return null;
    }

    @Override
    public String name() {
        return client_id.name();
    }

}
