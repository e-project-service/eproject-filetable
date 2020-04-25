package com.github.xiaoyao9184.eproject.filetable.core.interceptor;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;

/**
 * Created by xy on 2020/4/19.
 */
public interface DynamicFileTableRepositoryInitInterceptor {

    void intercept(AbstractFileTableRepository<AbstractFileTable, String> repository);

}
