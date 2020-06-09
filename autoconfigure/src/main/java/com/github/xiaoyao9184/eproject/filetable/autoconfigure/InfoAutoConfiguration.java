package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.info.entity.ServerInfo;
import com.github.xiaoyao9184.eproject.filetable.info.repository.DatabaseFileStreamOptionsRepository;
import com.github.xiaoyao9184.eproject.filetable.info.repository.ServerInfoRepository;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xy on 2020/6/6.
 */
@Configuration
@EnableConfigurationProperties({ FileTableProperties.class })
@ConditionalOnClass(ServerInfoRepository.class)
@AutoConfigureBefore(FileTableAutoConfiguration.class)
public class InfoAutoConfiguration implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(InfoAutoConfiguration.class);

    private static final Pattern pattern = Pattern.compile("jdbc:sqlserver:\\/\\/(.*?)(\\\\.*?|:)(\\d*);");

    public InfoAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", InfoAutoConfiguration.class.getName());
        }
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    DataSource dataSource;

    @Autowired
    ServerInfoRepository serverInfoRepository;

    @Autowired
    DatabaseFileStreamOptionsRepository databaseFileStreamOptionsRepository;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BaseFileTableProperties) {
            try {
                init((BaseFileTableProperties) bean);
            } catch (SQLException e) {
                logger.warn("Cant auto init BaseFileTableProperties.",e);
            }
        }
        return bean;
    }

    public void init(BaseFileTableProperties fileTableProperties) throws SQLException {
//        String databaseDirectoryName = databaseFileStreamOptionsRepository.findByDatabaseName(databaseName)
//                .stream()
//                .map(DatabaseFileStreamOptions::getDirectoryName)
//                .findFirst()
//                .orElse(databaseName);
        ServerInfo serverInfo = serverInfoRepository.getServerInfo();
        UriComponents shareUri = UriComponentsBuilder.fromUriString(
                serverInfo.getRootPath().replace("\\","/"))
                .build();
        String serverShareName = shareUri.getHost();
        String url = dataSource.getConnection().getMetaData().getURL();
        Matcher m = pattern.matcher(url);
        if(m.find()){
            serverShareName = m.group(1);
        }
        String instanceDirectoryName = shareUri.getPathSegments().get(0);
        String databaseDirectoryName = shareUri.getPathSegments().get(1);

        if(fileTableProperties.getRootPath() == null){
            fileTableProperties.setRootPath(serverInfo.getRootPath());
        }
        if(fileTableProperties.getServername() == null){
            fileTableProperties.setServername(serverShareName);
        }
        if(fileTableProperties.getInstance() == null){
            fileTableProperties.setInstance(instanceDirectoryName);
        }
        if(fileTableProperties.getDatabase() == null){
            fileTableProperties.setDatabase(databaseDirectoryName);
        }
    }

}
