package com.github.xiaoyao9184.eproject.filetable.core.data;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by xy on 2020/4/25.
 */
public class RepositoryBeanDefinitionBuilderVisible {

    private Class<?> cRepositoryBeanDefinitionBuilder;
    private Object o;

    public RepositoryBeanDefinitionBuilderVisible(
            BeanDefinitionRegistry registry,
            RepositoryConfigurationExtension extension,
            ResourceLoader resourceLoader,
            Environment environment) {
        try {
            cRepositoryBeanDefinitionBuilder = Class.forName("org.springframework.data.repository.config.RepositoryBeanDefinitionBuilder");
            Constructor<?> constructor = cRepositoryBeanDefinitionBuilder.getDeclaredConstructor(
                    BeanDefinitionRegistry.class,
                    RepositoryConfigurationExtension.class,
                    ResourceLoader.class,
                    Environment.class
            );
            constructor.setAccessible(true);

            o = constructor.newInstance(registry, extension, resourceLoader, environment);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BeanDefinitionBuilder build(RepositoryConfiguration<?> configuration) {
        Method m = ReflectionUtils.findMethod(cRepositoryBeanDefinitionBuilder,"build", RepositoryConfiguration.class);
        m.setAccessible(true);
        try {
            return (BeanDefinitionBuilder) m.invoke(o,configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
