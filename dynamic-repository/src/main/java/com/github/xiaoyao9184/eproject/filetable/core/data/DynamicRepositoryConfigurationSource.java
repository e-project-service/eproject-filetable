package com.github.xiaoyao9184.eproject.filetable.core.data;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationSourceSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/4/25.
 */
public class DynamicRepositoryConfigurationSource extends RepositoryConfigurationSourceSupport {

    private List<Class<? extends Repository>> repositoryClassList;

    public DynamicRepositoryConfigurationSource(
            Environment environment, BeanDefinitionRegistry registry) {
        super(environment, registry);
        this.repositoryClassList = new ArrayList<>();
    }

    public void addClass(Class<? extends Repository> repositoryClass){
        repositoryClassList.add(repositoryClass);
    }



    @Override
    public Object getSource() {
        return null;
    }

    @Override
    public Iterable<String> getBasePackages() {
        return null;
    }

    @Override
    public Object getQueryLookupStrategyKey() {
        return null;
    }

    @Override
    public String getRepositoryImplementationPostfix() {
        return null;
    }

    @Override
    public String getNamedQueryLocation() {
        return null;
    }

    @Override
    public String getRepositoryFactoryBeanName() {
        return null;
    }

    @Override
    public String getRepositoryBaseClassName() {
        return null;
    }

    @Override
    public String getAttribute(String s) {
        return null;
    }

    @Override
    public boolean usesExplicitFilters() {
        return false;
    }

    @Override
    public Collection<BeanDefinition> getCandidates(ResourceLoader loader) {
        return repositoryClassList
                .stream()
                .map(rc -> {

                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                    beanDefinition.setBeanClass(rc);
                    return beanDefinition;
                })
                .collect(Collectors.toList());
    }
}
