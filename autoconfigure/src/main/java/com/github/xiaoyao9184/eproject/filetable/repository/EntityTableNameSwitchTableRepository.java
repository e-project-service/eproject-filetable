package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.core.SimpleJpaRepositoryBeanFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by xy on 2020/1/15.
 */
public class EntityTableNameSwitchTableRepository implements FileTableRepository<AbstractFileTable> {
    private FileTableNameProvider fileTableNameProvider;

    private Map<String,AbstractFileTableRepository<AbstractFileTable,String>> fileTableRepository;

    public EntityTableNameSwitchTableRepository(
            FileTableNameProvider fileTableNameProvider,
            List<AbstractFileTableRepository> fileTableRepository
    ){
        this.fileTableNameProvider = fileTableNameProvider;
        //noinspection unchecked
        this.fileTableRepository = fileTableRepository
                .stream()
                .filter(r -> SimpleJpaRepository.class.equals(AopUtils.getTargetClass(r)))
                .collect(Collectors.toMap(
                        SimpleJpaRepositoryBeanFileTableNameProvider::getSimpleJpaRepositoryTableName,
                        r -> r
                ));
    }

    @Override
    public List<AbstractFileTable> getChildByRootLocator(){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public List<AbstractFileTable> getChildByPathLocator(String pathLocator){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getChildByPathLocator(pathLocator);
    }

    @Override
    public List<AbstractFileTable> findByNameContains(String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public List<AbstractFileTable> findByFileNamespacePathStartsWithAndNameContains(String namespacePath, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public Blob getBlobByPath(String path){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getBlobByPath(path);
    }

    @Override
    public byte[] getBytesByPath(String fileNamespacePath){
        String name = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(name);
        return repository.getBytesByPath(fileNamespacePath);
    }

    @Override
    public AbstractFileTable getByPath(String fileNamespacePath){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getByPath(fileNamespacePath);
    }

    @Override
    public String getPathLocatorStringByPath(String fileNamespacePath) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.getPathLocatorStringByPath(fileNamespacePath);
    }

    @Override
    public Integer deleteByPath(String fileNamespacePath){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.deleteByPath(fileNamespacePath);
    }

    @Override
    public Integer updateNameByPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.updateNameByPathLocator(pathLocator, name);
    }

    @Override
    public Integer insertFileByNameAndPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.insertFileByNameAndPathLocator(pathLocator, name);
    }

    @Override
    public Integer insertDirectoryByNameAndPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository = fileTableRepository.get(tableName);
        return repository.insertDirectoryByNameAndPathLocator(pathLocator, name);
    }
}
