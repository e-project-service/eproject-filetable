package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;

import java.sql.Blob;
import java.util.List;
import java.util.Map;

/**
 * Created by xy on 2020/4/18.
 */
public class TableNameRoutingRepository implements FileTableRepository<AbstractFileTable> {
    private DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager;

    private FileTableNameProvider fileTableNameProvider;

    public TableNameRoutingRepository(
            FileTableNameProvider fileTableNameProvider,
            DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager,
            Map<String,AbstractFileTableRepository<AbstractFileTable,String>> defaultFileTableRepositoryMap
    ){
        this.fileTableNameProvider = fileTableNameProvider;
        this.dynamicFileTableRepositoryManager = dynamicFileTableRepositoryManager;
        this.dynamicFileTableRepositoryManager.addDefault(defaultFileTableRepositoryMap);
    }

    @Override
    public List<AbstractFileTable> getChildByRootLocator() {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public List<AbstractFileTable> getChildByPathLocator(String pathLocator){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getChildByPathLocator(pathLocator);
    }

    @Override
    public List<AbstractFileTable> findByNameContains(String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public List<AbstractFileTable> findByFileNamespacePathStartsWithAndNameContains(String namespacePath, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getChildByRootLocator();
    }

    @Override
    public Blob getBlobByPath(String path){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getBlobByPath(path);
    }

    @Override
    public byte[] getBytesByPath(String fileNamespacePath){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getBytesByPath(fileNamespacePath);
    }

    @Override
    public AbstractFileTable getByPath(String fileNamespacePath){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getByPath(fileNamespacePath);
    }

    @Override
    public String getPathLocatorStringByPath(String fileNamespacePath) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.getPathLocatorStringByPath(fileNamespacePath);
    }

    @Override
    public Integer deleteByPath(String fileNamespacePath){
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.deleteByPath(fileNamespacePath);
    }

    @Override
    public Integer updateNameByPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.updateNameByPathLocator(pathLocator, name);
    }

    @Override
    public Integer insertFileByNameAndPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.insertFileByNameAndPathLocator(pathLocator, name);
    }

    @Override
    public Integer insertDirectoryByNameAndPathLocator(String pathLocator, String name) {
        String tableName = fileTableNameProvider.provide();
        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed(tableName);
        return repository.insertDirectoryByNameAndPathLocator(pathLocator, name);
    }
}
