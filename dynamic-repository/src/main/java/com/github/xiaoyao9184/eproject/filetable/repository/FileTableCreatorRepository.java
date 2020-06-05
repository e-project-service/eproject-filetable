package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by xy on 2020/4/19.
 */
public interface FileTableCreatorRepository<T extends AbstractFileTable> {

    @Query(value = "SELECT COUNT(*) FROM sysobjects WHERE name='#{#entityName}' and xtype='U'", nativeQuery = true)
    int findTableCount();

    @Transactional
    @Modifying
    @Query(value = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='#{#entityName}' and xtype='U') " +
            "CREATE TABLE \"#{#entityName}\" AS FILETABLE", nativeQuery = true)
    int createTableIfNotExists();

    @Transactional
    @Modifying
    @Query(value = "CREATE TABLE \"#{#entityName}\" AS FILETABLE", nativeQuery = true)
    int createTable();

    @Transactional
    @Modifying
    @Query(value = "IF EXISTS (SELECT * FROM sysobjects WHERE name='#{#entityName}' and xtype='U') " +
            "DROP table \"#{#entityName}\"", nativeQuery = true)
    int dropTableIfExists();

    @Transactional
    @Modifying
    @Query(value = "DROP table \"#{#entityName}\"", nativeQuery = true)
    int dropTable();
}
