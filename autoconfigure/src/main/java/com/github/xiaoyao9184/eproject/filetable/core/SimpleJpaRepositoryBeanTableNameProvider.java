package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Table;
import java.lang.reflect.Field;

/**
 * Created by xy on 2020/1/15.
 */
public class SimpleJpaRepositoryBeanTableNameProvider implements TableNameProvider {

    private ApplicationContext applicationContext;

    public SimpleJpaRepositoryBeanTableNameProvider(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public String provide() {
        Object databaseFileTableHandlerRepository = applicationContext.getBean("databaseFileTableHandlerRepository");
        return getSimpleJpaRepositoryTableName((AbstractFileTableRepository) databaseFileTableHandlerRepository);
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
