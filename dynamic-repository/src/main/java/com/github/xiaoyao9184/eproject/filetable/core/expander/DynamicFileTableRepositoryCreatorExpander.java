package com.github.xiaoyao9184.eproject.filetable.core.expander;

import com.github.xiaoyao9184.eproject.filetable.repository.FileTableCreatorRepository;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * Extended repository support table creation and drop function {@link FileTableCreatorRepository}
 * Created by xy on 2020/4/19.
 */
public class DynamicFileTableRepositoryCreatorExpander implements DynamicFileTableRepositoryExpander {

    @Override
    public List<Class> expandRepository(Class<?> entityClass) {
        return Collections.singletonList(FileTableCreatorRepository.class);
    }

    @Override
    public List<Annotation> expandEntity(String entityName) {
        return Collections.emptyList();
    }

}
