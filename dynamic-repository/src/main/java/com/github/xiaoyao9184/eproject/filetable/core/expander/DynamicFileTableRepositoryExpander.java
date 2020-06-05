package com.github.xiaoyao9184.eproject.filetable.core.expander;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Extend the dynamic repository
 * Created by xy on 2020/4/19.
 */
public interface DynamicFileTableRepositoryExpander {

    /**
     * Find extended interface class based on entity class
     * @param entityClass entity class
     * @return interface class
     */
    List<Class> expandRepository(Class<?> entityClass);

    /**
     * Find extended annotation based on entity name
     * @param entityName entity name
     * @return annotation
     */
    List<Annotation> expandEntity(String entityName);

}
