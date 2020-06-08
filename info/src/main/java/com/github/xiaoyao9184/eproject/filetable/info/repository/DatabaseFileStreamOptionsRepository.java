package com.github.xiaoyao9184.eproject.filetable.info.repository;

import com.github.xiaoyao9184.eproject.filetable.info.entity.DatabaseFileStreamOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by xy on 2020/6/6.
 */
public interface DatabaseFileStreamOptionsRepository extends JpaRepository<DatabaseFileStreamOptions,String> {

    @Query(value = "select " +
            "database_id, DB_NAME(database_id) as database_name, non_transacted_access, non_transacted_access_desc, directory_name " +
            "from sys.database_filestream_options " +
            "where (:databaseName is null or DB_NAME(database_id) = :databaseName) ", nativeQuery = true)
    List<DatabaseFileStreamOptions> findByDatabaseName(
            @Param("databaseName") String databaseName);
}
