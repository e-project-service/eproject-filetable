package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.core.bytebuddy.RuntimeEntityImpl;
import com.github.xiaoyao9184.eproject.filetable.core.bytebuddy.RuntimeTableImpl;
import com.github.xiaoyao9184.eproject.filetable.core.data.DynamicRepositoryConfigurationDelegate;
import com.github.xiaoyao9184.eproject.filetable.core.data.DynamicRepositoryConfigurationSource;
import com.github.xiaoyao9184.eproject.filetable.core.data.RepositoryBeanDefinitionBuilderVisible;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.FileTableRepository;
import com.rits.cloning.Cloner;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.config.NamedQueriesBeanDefinitionBuilder;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.query.ExtensionAwareEvaluationContextProvider;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.lang.model.SourceVersion;
import javax.servlet.FilterRegistration;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * The dynamic filetable repository manager manages the
 * {@link FileTableRepository} created at runtime by using entity name.
 *
 * Not only holds the name and repository map table,
 * but also create and initialize entities and {@link AbstractFileTableRepository} when needed
 *
 * Created entities are all {@link AbstractFileTable} subclasses
 * Initialized entities are implemented by concrete subclasses
 *
 * Create repositories to support extensions provided in subtypes
 * Initialized repositories are implemented by {@link JpaRepositoryFactory}
 *
 * Created by xy on 2020/4/18.
 */
public abstract class DynamicFileTableRepositoryManager {

    private static final Logger logger = LoggerFactory.getLogger(DynamicFileTableRepositoryManager.class);
    private ApplicationContext applicationContext;

    private JpaRepositoryFactory jpaRepositoryFactory;

    private Map<String,AbstractFileTableRepository<AbstractFileTable,String>> fileTableRepositoryMap;

    public DynamicFileTableRepositoryManager(
            JpaRepositoryFactory jpaRepositoryFactory
    ){
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.fileTableRepositoryMap = new HashMap<>();
    }

    public DynamicFileTableRepositoryManager(
            ApplicationContext applicationContext
    ){
        this.applicationContext = applicationContext;
        this.fileTableRepositoryMap = new HashMap<>();
    }

    protected String apply(String name) {
        if (name == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(name.replace('.', '_'));

            for(int i = 1; i < builder.length() - 1; ++i) {
                if (this.isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
                    builder.insert(i++, '_');
                }
            }

            return builder.toString().toLowerCase();
        }
    }

    private boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }

    /**
     * Extended Annotations for entity
     * @param entityName
     * @return
     */
    public abstract List<Annotation> expandEntity(String entityName);

    /**
     * why use table name for {@link javax.persistence.Entity}
     *
     * Query Expression only support \"#{#entityName}\" form Entity name
     * {@link org.springframework.data.jpa.repository.query.ExpressionBasedStringQuery}
     *
     * And FileTableRepository use Native SQL, so Entity name must use table name
     * {@link org.springframework.data.jpa.repository.query.AbstractStringBasedJpaQuery}
     */
    public Class<? extends AbstractFileTable> createEntityClass(String entityName, String tableName){
        List<Annotation> expandEntityAnnotationList = expandEntity(entityName);
        expandEntityAnnotationList = new ArrayList<>(expandEntityAnnotationList);
        if(expandEntityAnnotationList.stream().noneMatch(a -> a instanceof RuntimeEntityImpl)){
            expandEntityAnnotationList.add(0,new RuntimeEntityImpl(tableName));
        }
        if(expandEntityAnnotationList.stream().noneMatch(a -> a instanceof RuntimeTableImpl)){
            expandEntityAnnotationList.add(0,new RuntimeTableImpl(tableName));
        }
        Annotation[] entityAnnotations = new ArrayList<>(expandEntityAnnotationList)
                .toArray(new Annotation[]{});

        String fullName = AbstractFileTable.class.getPackage().getName()
                + "." + entityName;
        return new ByteBuddy()
                .subclass(AbstractFileTable.class)
                .name(fullName)
                .annotateType(entityAnnotations)
                .make()
                .load(AbstractFileTableRepository.class.getClassLoader())
                .getLoaded();
    }

    public Class<? extends AbstractFileTable> createEntityClass(String name){
        String tableName = apply(name);
        //
        if(SourceVersion.isKeyword(name)){
            name = name.toUpperCase();
        }
        return this.createEntityClass(name,tableName);
    }

    /**
     * Extended Interfaces for repository
     * @param entityClass
     * @return
     */
    public abstract List<TypeDefinition> expandRepository(Class<?> entityClass);

    public Class<AbstractFileTableRepository<AbstractFileTable,String>> createRepositoryClass(Class<?> entityClass){
        TypeDescription.Generic t = TypeDescription.Generic.Builder.parameterizedType(
                AbstractFileTableRepository.class,
                entityClass,
                String.class)
                .build();

        List<TypeDefinition> expandRepositoryInterfaceList = expandRepository(entityClass);
        expandRepositoryInterfaceList = new ArrayList<>(expandRepositoryInterfaceList);
        expandRepositoryInterfaceList.add(0,t);
        TypeDefinition[] repositoryInterface = new ArrayList<>(expandRepositoryInterfaceList)
                .toArray(new TypeDefinition[]{});

        String fullName = AbstractFileTableRepository.class.getPackage().getName()
                + "." + entityClass.getSimpleName() + "Repository";
        //noinspection unchecked
        return (Class<AbstractFileTableRepository<AbstractFileTable,String>>) new ByteBuddy()
                .makeInterface(repositoryInterface)
                .name(fullName)
                .make()
                .load(entityClass.getClassLoader())
                .getLoaded();
    }

    public abstract void initEntity(Class<? extends AbstractFileTable> entity) throws Exception;

    public AbstractFileTableRepository<AbstractFileTable,String> initRepository(
            Class<AbstractFileTableRepository<AbstractFileTable,String>> repository) throws ClassNotFoundException {

//        EntityManager entityManager = applicationContext.getBean(EntityManager.class);
//        JpaRepositoryFactoryBean<AbstractFileTableRepository<AbstractFileTable,String>,AbstractFileTable,String>
//                factoryBean = new JpaRepositoryFactoryBean<>(repository);
//        factoryBean.setEntityManager(entityManager);
//        factoryBean.afterPropertiesSet();
//        factoryBean.setBeanClassLoader(repository.getClassLoader());
//        factoryBean.setBeanFactory(applicationContext);
//        AbstractFileTableRepository<AbstractFileTable,String> r = factoryBean.getObject();

        GenericApplicationContext context = new GenericApplicationContext();
        context.setParent(applicationContext);

//        ResourceLoader resourceLoader = new ResourceLoader() {
//            @Override
//            public Resource getResource(String s) {
//                return applicationContext.getResource(s);
//            }
//
//            @Override
//            public ClassLoader getClassLoader() {
//                return repository.getClassLoader();
//            }
//        };
//        context.setResourceLoader(resourceLoader);
        context.setClassLoader(repository.getClassLoader());

        Environment environment = context.getEnvironment();
        BeanDefinitionRegistry registry = context;

//        applicationContext.getBeanNamesForAnnotation(EnableJpaRepositories.class);
//        Class jpaConfigClass = applicationContext.getBeansWithAnnotation(EnableJpaRepositories.class)
//            .values().stream()
//            .findFirst()
//            .map(bean -> {
//                CglibHelper cglibHelper = new CglibHelper(bean);
//                return cglibHelper.getTargetObject().getClass();
//            })
//            .get();

        DynamicRepositoryConfigurationSource configurationSource =
                new DynamicRepositoryConfigurationSource(environment,registry);
        configurationSource.addClass(repository);

        JpaRepositoryConfigExtension extension = new JpaRepositoryConfigExtension();


//        new ByteBuddy()
//                .redefine(RepositoryConfigurationDelegate.class)
//                .

        //use RepositoryConfigurationDelegate
        //use BeanDefinitionBuilder

        DynamicRepositoryConfigurationDelegate delegate = new DynamicRepositoryConfigurationDelegate(
                configurationSource,context,environment);
        List<BeanComponentDefinition> definitions = delegate.registerRepositoriesIn(registry,extension);

        context.refresh();
        AbstractFileTableRepository<AbstractFileTable,String> r = context.getBean(repository);


//        Collection<RepositoryConfigurationSource> configurations =
//                extension.getRepositoryConfigurations(configurationSource, resourceLoader, false);
//
//
//        //use RepositoryBeanDefinitionBuilder
////        RepositoryBeanDefinitionBuilder builder = new RepositoryBeanDefinitionBuilder(registry, extension, this.resourceLoader, this.environment);
//
//        RepositoryBeanDefinitionBuilderVisible builder = new RepositoryBeanDefinitionBuilderVisible(
//                registry,extension,resourceLoader,environment);






        //use BeanDefinition

//        AbstractBeanDefinition definition = (AbstractBeanDefinition) context.getBeanDefinition("defaultFileTableRepository");
//        Cloner cloner = new Cloner();
//        definition = cloner.deepClone(definition);
//        definition.getConstructorArgumentValues().clear();
//        definition.getConstructorArgumentValues().addIndexedArgumentValue(0,repository.getName());
//        definition.setPropertyValues();



        //use JpaRepositoryFactoryBean
//        JpaRepositoryFactoryBean defaultFactoryBean =
//                (JpaRepositoryFactoryBean) applicationContext.getBean("&defaultFileTableRepository");
//        Cloner cloner = new Cloner();
//        JpaRepositoryFactoryBean factoryBean = cloner.deepClone(defaultFactoryBean);
//
//        //TODO set repositoryInterface
//        factoryBean.setBeanClassLoader(repository.getClassLoader());
//
//        AbstractFileTableRepository<AbstractFileTable,String> r =
//                (AbstractFileTableRepository<AbstractFileTable, String>) factoryBean.getObject();

        //use JpaRepositoryFactory
//        AbstractFileTableRepository<AbstractFileTable,String> r = jpaRepositoryFactory.getRepository(repository);


        interceptRepositoryInit(r);
        return r;
    }

    public AbstractFileTableRepository<AbstractFileTable,String> init(String entityName) throws Exception {
        Class<? extends AbstractFileTable> entity = this.createEntityClass(entityName);

        initEntity(entity);

        Class<AbstractFileTableRepository<AbstractFileTable,String>> repository = this.createRepositoryClass(entity);

        return initRepository(repository);
    }

    /**
     *
     * @param repository
     */
    public abstract void interceptRepositoryInit(
            AbstractFileTableRepository<AbstractFileTable,String> repository);

    public AbstractFileTableRepository<AbstractFileTable,String> getAndInitIfNeed(String entityName) {
        if(fileTableRepositoryMap.containsKey(entityName)){
            return fileTableRepositoryMap.get(entityName);
        }else{
            try {
                AbstractFileTableRepository<AbstractFileTable,String> repository = init(entityName);
                fileTableRepositoryMap.put(entityName, repository);
                return repository;
            }catch (Exception e){
                logger.error("Cant init repository with " + entityName + " name.",e);
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isInitialized(String entityName){
        return fileTableRepositoryMap.containsKey(entityName);
    }

    public void addDefault(Map<String, AbstractFileTableRepository<AbstractFileTable,String>> defaultFileTableRepository) {
        fileTableRepositoryMap.putAll(defaultFileTableRepository);
    }
}
