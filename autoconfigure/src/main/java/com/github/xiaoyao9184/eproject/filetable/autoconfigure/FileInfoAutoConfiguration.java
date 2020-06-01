package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableConverter;
import com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableStorage;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * If class path include {@link FileInfoFileTableStorage} and {@link FileInfoFileTableConverter}
 * Use {@link FileInfo} to implement {@link FileStorage} and {@link FileInfoStorage}.
 *
 * Created by xy on 2020/6/1.
 */
@Configuration
@ConditionalOnClass(name = {
        "com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableStorage",
        "com.github.xiaoyao9184.eproject.filetable.core.FileInfoFileTableConverter"
})
public class FileInfoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(FileInfoAutoConfiguration.class);

    public FileInfoAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", FileInfoAutoConfiguration.class.getName());
        }
    }


    /**
     * Enable URL {@link FileStorage} for filestorage-mvc
     * Enable {@link FileInfo} {@link FileInfoStorage} for filetable-mvc
     * @return FileStorage with FileInfoStorage
     */
    @Bean
    public FileInfoFileTableStorage fileInfoFileTableStorage(){
        return new FileInfoFileTableStorage();
    }

    /**
     * Converter {@link AbstractFileTable} for to {@link FileInfo}
     * @return {@link FileInfoFileTableStorage} Converter
     */
    @Bean
    public FileInfoFileTableConverter fileInfoFileTableConverter(){
        return new FileInfoFileTableConverter();
    }

}
