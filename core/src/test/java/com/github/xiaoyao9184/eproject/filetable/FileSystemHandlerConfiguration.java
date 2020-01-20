package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.FileSystemFileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.core.SMBFileTableHandler;
import org.springframework.context.annotation.Bean;

/**
 * Created by xy on 2020/1/15.
 */
public class FileSystemHandlerConfiguration {

    @Bean
    public SMBFileTableHandler smbFileTableHandler(){
        return new SMBFileTableHandler();
    }

    @Bean
    public FileSystemFileTableHandler fileSystemFileTableHandler(){
        return new FileSystemFileTableHandler();
    }

}
