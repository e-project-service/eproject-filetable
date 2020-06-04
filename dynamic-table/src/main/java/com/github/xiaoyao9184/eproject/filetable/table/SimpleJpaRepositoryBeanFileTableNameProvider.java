package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import org.springframework.aop.framework.Advised;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Table;
import java.lang.reflect.Field;

/**
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
        return getSimpleJpaRepositoryTableName((AbstractFileTableRepository) databaseFileTableHandlerRepository);
    }

    @Override
    public String name() {
        return TableNameProviders.jpa_repository.name();
    }


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
