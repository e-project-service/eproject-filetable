package com.github.xiaoyao9184.eproject.filetable.core.interceptor;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.FileTableCreatorRepository;

import javax.persistence.EntityManager;

/**
 * Automatically create a table when initializing the repository
 * Created by xy on 2020/4/19.
 */
public class DynamicFileTableRepositoryCreatorInterceptor implements DynamicFileTableRepositoryInitInterceptor {

    @Override
    public void intercept(AbstractFileTableRepository<AbstractFileTable, String> repository) {
        if(repository instanceof FileTableCreatorRepository){
            FileTableCreatorRepository creatorRepository = (FileTableCreatorRepository) repository;
            creatorRepository.createTableIfNotExists();
        }
    }
}
