package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.mapping.LocalMappingManager;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.MappingProperties;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.regex.Pattern;

/**
 * Created by xy on 2020/9/6.
 */
@Configuration
@ConditionalOnClass(LocalMappingManager.class)
@ConditionalOnProperty(value = "eproject.filetable.mapping.enable", havingValue = "true", matchIfMissing = true)
@Conditional(MappingAutoConfiguration.ConditionalOnJnaWindows.class)
@AutoConfigureAfter(InfoAutoConfiguration.class)
public class MappingAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MappingAutoConfiguration.class);

    public MappingAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", MappingAutoConfiguration.class.getName());
        }
    }

    @Bean
    public LocalMappingManager localMappingManager(){
        return new LocalMappingManager();
    }

    @Autowired
    private LocalMappingManager localMappingManager;

    @Autowired
    private MappingProperties mappingProperties;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @PostConstruct
    public void init(){
        if(mappingProperties.getDevice() == null){
            char dl = localMappingManager.getAvailableDeviceLetter();
            logger.debug("Assign drive letter automatically {}.", dl);
            mappingProperties.setDevice(dl);
        }
        String localName = mappingProperties.getDevice() + ":";
        String remoteName = UriComponentsBuilder.newInstance()
                .host(fileTableProperties.getServername())
                .pathSegment(fileTableProperties.getInstance())
                .pathSegment(fileTableProperties.getDatabase())
                .toUriString()
                .replace("/", "\\");
        String username = fileTableProperties.getUsername();
        String password = fileTableProperties.getPassword();

        boolean result = localMappingManager.mapping(localName,remoteName,username,password);
        if(!result){
            logger.error("Cant mapping {} to local device {}!", remoteName, localName);
            throw new RuntimeException("Cant mapping remote to local device!");
        }
    }

    @PreDestroy
    public void uninit(){
        String localName = mappingProperties.getDevice() + ":";
        boolean result = localMappingManager.unmapping(localName);
        if(!result){
            logger.error("Cant unmapping local device {}!", localName);
            throw new RuntimeException("Cant unmapping local device!");
        }
    }


    public static class ConditionalOnJnaWindows implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            return Platform.isWindows();
        }
    }

}
