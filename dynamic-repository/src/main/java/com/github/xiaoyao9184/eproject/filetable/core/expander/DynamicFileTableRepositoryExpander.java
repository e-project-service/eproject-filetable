package com.github.xiaoyao9184.eproject.filetable.core.expander;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by xy on 2020/4/19.
 */
public interface DynamicFileTableRepositoryExpander {

    List<Class> expandRepository(Class<?> entityClass);

    List<Annotation> expandEntity(String entityName);

}
