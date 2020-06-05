package com.github.xiaoyao9184.eproject.filetable.core.provider;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.FileTableRepository;
import org.springframework.aop.framework.Advised;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Table;
import java.lang.reflect.Field;

/**
 * The table name provided by the {@link FileTableRepository} obtained by {@link FileTableRepositoryProvider}
 * Note that {@link FileTableRepositoryProvider} must provide an real {@link SimpleJpaRepository}
 * Created by xy on 2020/1/15.
 */
public class SimpleJpaRepositoryBeanFileTableNameProvider implements FileTableNameProvider, Named {

    private FileTableRepositoryProvider repositoryProvider;

    public SimpleJpaRepositoryBeanFileTableNameProvider(FileTableRepositoryProvider repositoryProvider){
        this.repositoryProvider = repositoryProvider;
    }

    @Override
    public String provide() {
        Object databaseFileTableHandlerRepository = repositoryProvider.provide();
        if(!(databaseFileTableHandlerRepository instanceof SimpleJpaRepository)){
            // is not SimpleJpaRepository maybe is virtual repository
            return null;
        }
        return getSimpleJpaRepositoryTableName((AbstractFileTableRepository) databaseFileTableHandlerRepository);
    }

    @Override
    public String name() {
        return TableNameProviders.jpa_repository.name();
    }


    /**
     * Get the table name through {@link JpaMetamodelEntityInformation} in {@link SimpleJpaRepository}
     * @param abstractFileTableRepository repository
     * @return table name on {@link Table}
     */
    public static String getSimpleJpaRepositoryTableName(AbstractFileTableRepository abstractFileTableRepository) {
        try{
            SimpleJpaRepository jpaRepository = (SimpleJpaRepository) ((Advised)abstractFileTableRepository).getTargetSource().getTarget();
            Field fieldJpaMetamodelEntityInformation = ReflectionUtils.findField(SimpleJpaRepository.class,"entityInformation");
            fieldJpaMetamodelEntityInformation.setAccessible(true);
            JpaMetamodelEntityInformation entityInformation = (JpaMetamodelEntityInformation) ReflectionUtils.getField(fieldJpaMetamodelEntityInformation,jpaRepository);

            Field fieldDomainClass = ReflectionUtils.findField(JpaMetamodelEntityInformation.class,"domainClass");
            fieldDomainClass.setAccessible(true);
            Class<?> entityClass = (Class) ReflectionUtils.getField(fieldDomainClass,entityInformation);
            return entityClass.getAnnotation(Table.class)
                    .name();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
