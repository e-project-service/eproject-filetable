package com.github.xiaoyao9184.eproject.filetable.core.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.data.repository.config.*;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy on 2020/4/25.
 */
public class DynamicRepositoryConfigurationDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConfigurationDelegate.class);

    private static final String REPOSITORY_REGISTRATION = "Spring Data {} - Registering repository: {} - Interface: {} - Factory: {}";
    private static final String MULTIPLE_MODULES = "Multiple Spring Data modules found, entering strict repository configuration mode!";

    static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

    private final RepositoryConfigurationSource configurationSource;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private final BeanNameGenerator beanNameGenerator;
    private final boolean inMultiStoreMode;

    /**
     * Creates a new {@link RepositoryConfigurationDelegate} for the given {@link RepositoryConfigurationSource} and
     * {@link ResourceLoader} and {@link Environment}.
     *
     * @param configurationSource must not be {@literal null}.
     * @param resourceLoader must not be {@literal null}.
     * @param environment must not be {@literal null}.
     */
    public DynamicRepositoryConfigurationDelegate(RepositoryConfigurationSource configurationSource,
                                           ResourceLoader resourceLoader, Environment environment) {

        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        RepositoryBeanNameGenerator generator = new RepositoryBeanNameGenerator();
        generator.setBeanClassLoader(resourceLoader.getClassLoader());

        this.beanNameGenerator = generator;
        this.configurationSource = configurationSource;
        this.resourceLoader = resourceLoader;
        this.environment = defaultEnvironment(environment, resourceLoader);
        this.inMultiStoreMode = multipleStoresDetected();
    }

    /**
     * Defaults the environment in case the given one is null. Used as fallback, in case the legacy constructor was
     * invoked.
     *
     * @param environment can be {@literal null}.
     * @param resourceLoader can be {@literal null}.
     * @return
     */
    private static Environment defaultEnvironment(Environment environment, ResourceLoader resourceLoader) {

        if (environment != null) {
            return environment;
        }

        return resourceLoader instanceof EnvironmentCapable ? ((EnvironmentCapable) resourceLoader).getEnvironment()
                : new StandardEnvironment();
    }

    /**
     * Registers the found repositories in the given {@link BeanDefinitionRegistry}.
     *
     * @param registry
     * @param extension
     * @return {@link BeanComponentDefinition}s for all repository bean definitions found.
     */
    public List<BeanComponentDefinition> registerRepositoriesIn(BeanDefinitionRegistry registry,
                                                                RepositoryConfigurationExtension extension) {

        extension.registerBeansForRoot(registry, configurationSource);

        RepositoryBeanDefinitionBuilderVisible builder = new RepositoryBeanDefinitionBuilderVisible(registry, extension, resourceLoader,
                environment);
        List<BeanComponentDefinition> definitions = new ArrayList<BeanComponentDefinition>();

        for (RepositoryConfiguration<? extends RepositoryConfigurationSource> configuration : extension
                .getRepositoryConfigurations(configurationSource, resourceLoader, inMultiStoreMode)) {

            BeanDefinitionBuilder definitionBuilder = builder.build(configuration);

            extension.postProcess(definitionBuilder, configurationSource);

            String enableDefaultTransactions = configurationSource.getAttribute("enableDefaultTransactions");
            if (StringUtils.hasText(enableDefaultTransactions)) {
                definitionBuilder.addPropertyValue("enableDefaultTransactions", enableDefaultTransactions);
            }

            AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(REPOSITORY_REGISTRATION, extension.getModuleName(), beanName,
                        configuration.getRepositoryInterface(), extension.getRepositoryFactoryClassName());
            }

            beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, configuration.getRepositoryInterface());

            registry.registerBeanDefinition(beanName, beanDefinition);
            definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
        }

        return definitions;
    }

    /**
     * Scans {@code repository.support} packages for implementations of {@link RepositoryFactorySupport}. Finding more
     * than a single type is considered a multi-store configuration scenario which will trigger stricter repository
     * scanning.
     *
     * @return
     */
    private boolean multipleStoresDetected() {

        boolean multipleModulesFound = SpringFactoriesLoader
                .loadFactoryNames(RepositoryFactorySupport.class, resourceLoader.getClassLoader()).size() > 1;

        if (multipleModulesFound) {
            LOGGER.info(MULTIPLE_MODULES);
        }

        return multipleModulesFound;
    }
}
