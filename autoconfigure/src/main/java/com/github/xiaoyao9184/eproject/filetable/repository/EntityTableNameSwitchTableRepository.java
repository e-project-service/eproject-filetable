package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.core.SimpleJpaRepositoryBeanTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.TableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/1/15.
 */
public class EntityTableNameSwitchTableRepository implements AbstractFileTableRepository<AbstractFileTable> {
    private TableNameProvider tableNameProvider;

    private Map<String,AbstractFileTableRepository<AbstractFileTable>> fileTableRepository;

    public EntityTableNameSwitchTableRepository(
            TableNameProvider tableNameProvider,
            List<AbstractFileTableRepository> fileTableRepository
    ){
        this.tableNameProvider = tableNameProvider;
        //noinspection unchecked
        this.fileTableRepository = fileTableRepository
                .stream()
                .filter(r -> SimpleJpaRepository.class.equals(AopUtils.getTargetClass(r)))
                .collect(Collectors.toMap(
                        SimpleJpaRepositoryBeanTableNameProvider::getSimpleJpaRepositoryTableName,
                        r -> r
                ));
    }

    @Override
    public List<AbstractFileTable> getChildByRootLocator(){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.getChildByRootLocator();
    }

    @Override
    public List<AbstractFileTable> getChildByPathLocator(String pathLocator){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.getChildByPathLocator(pathLocator);
    }

    @Override
    public Blob getBlobByPath(String path){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.getBlobByPath(path);
    }

    @Override
    public byte[] getBytesByPath(String fileNamespacePath){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.getBytesByPath(fileNamespacePath);
    }

    @Override
    public AbstractFileTable getByPath(String fileNamespacePath){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.getByPath(fileNamespacePath);
    }

    @Override
    public Integer deleteByPath(String fileNamespacePath){
        String name = tableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable> repository = fileTableRepository.get(name);
        return repository.deleteByPath(fileNamespacePath);
    }
}
