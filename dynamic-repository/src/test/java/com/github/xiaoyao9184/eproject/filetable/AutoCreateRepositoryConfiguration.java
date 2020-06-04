package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.repository.TableNameRoutingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

/**
 * Created by xy on 2020/4/19.
 */
public class AutoCreateRepositoryConfiguration {

    /**
     * Not create because it not JPA Repository
     * @param fileTableNameProvider
     * @param dynamicFileTableRepositoryManager
     * @return
     */
    @Bean
    public TableNameRoutingRepository tableNameSwitchTableRepository(
            @Autowired FileTableNameProvider fileTableNameProvider,
            @Autowired DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager
            ){
        return new TableNameRoutingRepository(
                fileTableNameProvider,
                dynamicFileTableRepositoryManager,
                Collections.emptyMap());
    }

    /**
     *
     * @param entityTableNameRoutingRepository
     * @return
     */
    @Bean
    public FileTableRepositoryProvider fileTableRepositoryProvider(
            @Autowired TableNameRoutingRepository entityTableNameRoutingRepository
    ){
        return () -> entityTableNameRoutingRepository;
    }

    /**
     *
     * Switch table name on thread
     * @return
     */
    @Bean
    public ThreadLocalFileTableNameProvider tableNameProvider(){
        return new ThreadLocalFileTableNameProvider();
    }

    public class ThreadLocalFileTableNameProvider implements FileTableNameProvider {

        @Override
        public String provide() {
            return tableNameThreadLocal.get() == null ?
                    "" :
                    tableNameThreadLocal.get();
        }


        private ThreadLocal<String> tableNameThreadLocal = new ThreadLocal<>();

        public void set(String tableName){
            tableNameThreadLocal.set(tableName);
        }
    }

}
