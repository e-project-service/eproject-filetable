package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
public interface AbstractFileTableRepository<T extends AbstractFileTable>
        extends Repository<T,String> {

    @Query(value = "select " +
            " stream_id" +
            ",name" +
            ",file_type" +
            ",cached_file_size" +
            ",creation_time" +
            ",last_write_time" +
            ",last_access_time" +
            ",is_directory" +
            ",is_offline" +
            ",is_hidden" +
            ",is_readonly" +
            ",is_archive" +
            ",is_system" +
            ",is_temporary" +

            ",FileTableRootPath() as root" +
            ",[path_locator].GetLevel() as level" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from #{#entityName}" +
            " where [parent_path_locator] is null", nativeQuery = true)
    List<T> getChildByRootLocator();

    @Query(value = "select " +
            " stream_id" +
            ",name" +
            ",file_type" +
            ",cached_file_size" +
            ",creation_time" +
            ",last_write_time" +
            ",last_access_time" +
            ",is_directory" +
            ",is_offline" +
            ",is_hidden" +
            ",is_readonly" +
            ",is_archive" +
            ",is_system" +
            ",is_temporary" +

            ",FileTableRootPath() as root" +
            ",[path_locator].GetLevel() as level" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from #{#entityName}" +
            " where [parent_path_locator] = GetPathLocator(:path)", nativeQuery = true)
    List<T> getChildByPathLocator(@Param("path") String pathLocator);

    //TODO fix mapping
    @Query(value = "select " +
            " file_stream" +
            " from #{#entityName}" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    Blob getBlobByPath(@Param("path") String path);

    @Query(value = "select " +
            " file_stream" +
            " from #{#entityName}" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    byte[] getBytesByPath(@Param("path") String fileNamespacePath);

    @Query(value = "select " +
            " stream_id" +
            ",name" +
            ",file_type" +
            ",cached_file_size" +
            ",creation_time" +
            ",last_write_time" +
            ",last_access_time" +
            ",is_directory" +
            ",is_offline" +
            ",is_hidden" +
            ",is_readonly" +
            ",is_archive" +
            ",is_system" +
            ",is_temporary" +

            ",FileTableRootPath() as root" +
            ",[path_locator].GetLevel() as level" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from #{#entityName}" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    T getByPath(@Param("path") String fileNamespacePath);

    @Query(value = "delete " +
            " from #{#entityName}" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    @Modifying
    Integer deleteByPath(@Param("path") String fileNamespacePath);
}
