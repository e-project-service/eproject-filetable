package com.github.xiaoyao9184.eproject.filetable.core.hibernate;

import com.github.xiaoyao9184.eproject.filetable.core.BaseDynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.expander.DynamicFileTableRepositoryExpander;
import com.github.xiaoyao9184.eproject.filetable.core.interceptor.DynamicFileTableRepositoryInitInterceptor;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.entity.DefaultFileTable;
import com.rits.cloning.Cloner;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.hibernate.jpa.internal.metamodel.MetamodelImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.ReflectionUtils;

import javax.persistence.metamodel.Metamodel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * Created by xy on 2020/4/18.
 */
public class HibernateDynamicFileTableRepositoryManager extends BaseDynamicFileTableRepositoryManager {

    private LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    public HibernateDynamicFileTableRepositoryManager(
            Collection<DynamicFileTableRepositoryInitInterceptor> dynamicFileTableRepositoryInitInterceptorCollection,
            DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander,
            LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean,
            JpaRepositoryFactory jpaRepositoryFactory) {
        super(dynamicFileTableRepositoryInitInterceptorCollection,
                dynamicFileTableRepositoryExpander,
                jpaRepositoryFactory);
        this.localContainerEntityManagerFactoryBean = localContainerEntityManagerFactoryBean;
    }

    public HibernateDynamicFileTableRepositoryManager(
            Collection<DynamicFileTableRepositoryInitInterceptor> dynamicFileTableRepositoryInitInterceptorCollection,
            DynamicFileTableRepositoryExpander dynamicFileTableRepositoryExpander,
            LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean,
            ApplicationContext applicationContext) {
        super(dynamicFileTableRepositoryInitInterceptorCollection,
                dynamicFileTableRepositoryExpander,
                applicationContext);
        this.localContainerEntityManagerFactoryBean = localContainerEntityManagerFactoryBean;
    }

    @Override
    public void initEntity(Class<? extends AbstractFileTable> entity) throws Exception {
        EntityManagerFactoryImpl emf = (EntityManagerFactoryImpl) this.localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();

        //metamodel
        Metamodel metamodel = emf.getMetamodel();
        addEntityType(metamodel,entity,entity.getSimpleName());
        //SingleTableEntityPersister
        SessionFactoryImplementor sessionFactoryImplementor = emf.getSessionFactory();
        addSingleTableEntityPersister(sessionFactoryImplementor,entity,apply(entity.getSimpleName()));

    }

    private EntityTypeImpl copyEntityType(
            EntityTypeImpl entityType,
            Class<?> entityClass, String jpaEntityName) throws IllegalAccessException {
        Cloner cloner = new Cloner();
        EntityTypeImpl newEt = cloner.deepClone(entityType);

        Field f_javaType = ReflectionUtils.findField(EntityTypeImpl.class,"javaType");
        f_javaType.setAccessible(true);
        f_javaType.set(newEt,entityClass);

        Field f_typeName = ReflectionUtils.findField(EntityTypeImpl.class,"typeName");
        f_typeName.setAccessible(true);
        f_typeName.set(newEt,entityClass.getName());

        Field f_jpaEntityName = ReflectionUtils.findField(EntityTypeImpl.class,"jpaEntityName");
        f_jpaEntityName.setAccessible(true);
        f_jpaEntityName.set(newEt,jpaEntityName);

        return newEt;
    }

    private void addEntityType(Metamodel metamodel, Class<?> entityClass, String jpaEntityName) throws IllegalAccessException {
        Field f_entities = ReflectionUtils.findField(MetamodelImpl.class,"entities");
        f_entities.setAccessible(true);
        Map<Class, EntityTypeImpl> entities = (Map<Class, EntityTypeImpl>) f_entities.get(metamodel);

        Field f_entityTypesByEntityName = ReflectionUtils.findField(MetamodelImpl.class,"entityTypesByEntityName");
        f_entityTypesByEntityName.setAccessible(true);
        Map<String, EntityTypeImpl> entityTypesByEntityName = (Map<String, EntityTypeImpl>) f_entityTypesByEntityName.get(metamodel);


        EntityTypeImpl baseEntityType = entities.get(DefaultFileTable.class);
        EntityTypeImpl newEntityType = copyEntityType(baseEntityType, entityClass, jpaEntityName);

        entities = new HashMap<>(entities);
        entities.put(entityClass,newEntityType);
        f_entities.set(metamodel,entities);

        entityTypesByEntityName = new HashMap<>(entityTypesByEntityName);
        entityTypesByEntityName.put(entityClass.getName(),newEntityType);
        f_entityTypesByEntityName.set(metamodel,entityTypesByEntityName);
    }

    private SingleTableEntityPersister copySingleTableEntityPersister(
            SingleTableEntityPersister singleTableEntityPersister,
            Class<?> entityClass, String tableName) throws IllegalAccessException {
        Cloner cloner = new Cloner();
        SingleTableEntityPersister newEt = cloner.shallowClone(singleTableEntityPersister);


        Field f_subclassClosure = ReflectionUtils.findField(SingleTableEntityPersister.class,"subclassClosure");
        f_subclassClosure.setAccessible(true);
        f_subclassClosure.set(newEt,new String[]{entityClass.getName()});

        Field f_subclassesByDiscriminatorValue = ReflectionUtils.findField(SingleTableEntityPersister.class,"subclassesByDiscriminatorValue");
        f_subclassesByDiscriminatorValue.setAccessible(true);
        f_subclassesByDiscriminatorValue.set(newEt, Collections.singletonMap(tableName,entityClass.getName()));

        Field f_discriminatorValue = ReflectionUtils.findField(SingleTableEntityPersister.class,"discriminatorValue");
        f_discriminatorValue.setAccessible(true);
        f_discriminatorValue.set(newEt,tableName);

        Field f_discriminatorSQLValue = ReflectionUtils.findField(SingleTableEntityPersister.class,"discriminatorSQLValue");
        f_discriminatorSQLValue.setAccessible(true);
        f_discriminatorSQLValue.set(newEt,tableName);

        Field f_fullDiscriminatorValues = ReflectionUtils.findField(SingleTableEntityPersister.class,"fullDiscriminatorValues");
        f_fullDiscriminatorValues.setAccessible(true);
        f_fullDiscriminatorValues.set(newEt,new String[]{tableName});

        return newEt;
    }

    private void addSingleTableEntityPersister(SessionFactoryImplementor sessionFactoryImplementor, Class<?> entityClass, String jpaEntityName) throws IllegalAccessException {
        SessionFactoryImpl sf = (SessionFactoryImpl) sessionFactoryImplementor;

        Field f_entityPersisters = ReflectionUtils.findField(SessionFactoryImpl.class,"entityPersisters");
        f_entityPersisters.setAccessible(true);
        Map<String, SingleTableEntityPersister> entityPersisters = (Map<String, SingleTableEntityPersister>) f_entityPersisters.get(sf);

        Field f_classMetadata = ReflectionUtils.findField(SessionFactoryImpl.class,"classMetadata");
        f_classMetadata.setAccessible(true);
        Map<String, ClassMetadata> classMetadata = (Map<String, ClassMetadata>) f_classMetadata.get(sf);


        SingleTableEntityPersister baseSingleTableEntityPersister = entityPersisters.get(DefaultFileTable.class.getName());
        SingleTableEntityPersister newSingleTableEntityPersister =
                copySingleTableEntityPersister(baseSingleTableEntityPersister, entityClass, jpaEntityName);

        entityPersisters = new HashMap<>(entityPersisters);
        entityPersisters.put(entityClass.getName(),newSingleTableEntityPersister);

        classMetadata = new HashMap<>(classMetadata);
        classMetadata.put(entityClass.getName(),newSingleTableEntityPersister);

        f_entityPersisters.set(sf,entityPersisters);
        f_classMetadata.set(sf,classMetadata);
    }

}
