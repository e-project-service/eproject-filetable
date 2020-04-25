package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryExpander;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryInitInterceptor;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by xy on 2020/4/18.
 */
public abstract class BaseDynamicFileTableRepositoryManager extends DynamicFileTableRepositoryManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseDynamicFileTableRepositoryManager.class);

    private DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander;
    private Collection<DynamicFileTableRepositoryInitInterceptor> dynamicFileTableRepositoryInitInterceptorCollection;

    public BaseDynamicFileTableRepositoryManager(
            Collection<DynamicFileTableRepositoryInitInterceptor> dynamicFileTableRepositoryInitInterceptorCollection,
            DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander,
            JpaRepositoryFactory jpaRepositoryFactory) {
        super(jpaRepositoryFactory);
        this.dynamicFileTableRepositoryExpander = dynamicFileTableRepositoryExpander;
        this.dynamicFileTableRepositoryInitInterceptorCollection = dynamicFileTableRepositoryInitInterceptorCollection;
    }

    public BaseDynamicFileTableRepositoryManager(
            Collection<DynamicFileTableRepositoryInitInterceptor> dynamicFileTableRepositoryInitInterceptorCollection,
            DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander,
            ApplicationContext applicationContext) {
        super(applicationContext);
        this.dynamicFileTableRepositoryExpander = dynamicFileTableRepositoryExpander;
        this.dynamicFileTableRepositoryInitInterceptorCollection = dynamicFileTableRepositoryInitInterceptorCollection;
    }

    @Override
    public List<Annotation> expandEntity(String entityName) {
        return dynamicFileTableRepositoryExpander.expandEntity(entityName);
    }

    @Override
    public List<TypeDefinition> expandRepository(Class<?> entityClass) {
        List<Class> expandRepositoryInterface = dynamicFileTableRepositoryExpander.expandRepository(entityClass);

        return expandRepositoryInterface.stream()
                .map(clazz -> {
                    TypeDefinition td = null;
                    Type[] types = clazz.getTypeParameters();
                    if(types.length == 0){
                        td = TypeDescription.ForLoadedType.of(clazz);
                        logger.info("{} Repository will support {}.", entityClass.getName(), clazz.getName());
                    }else if(types.length == 1){
                        //generic
                        Type type = types[0];
                        if(type instanceof TypeVariable){
                            TypeVariable tv = (TypeVariable) type;
                            Type[] bounds = tv.getBounds();
                            if(bounds.length == 1){
                                Type bound = bounds[0];
                                if(bound == AbstractFileTable.class){
                                    td = TypeDescription.Generic.Builder.parameterizedType(
                                            clazz,
                                            entityClass)
                                            .build();
                                    logger.info("{} Repository will support {} using generic way.", entityClass.getName(), clazz.getName());
                                }
                            }
                        }
                    }
                    return td;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void interceptRepositoryInit(AbstractFileTableRepository<AbstractFileTable, String> repository) {
        dynamicFileTableRepositoryInitInterceptorCollection.forEach(i -> i.intercept(repository));
    }

}
