package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Blob;
import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
public interface FileTableRepository<T extends AbstractFileTable> {

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
            ",[path_locator].ToString() as locator" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from \"#{#entityName}\"" +
            " where [parent_path_locator] is null", nativeQuery = true)
    List<T> getChildByRootLocator();

    /**
     * @param fullFileNamespacePath virtual network name (VNN) or the computer name.
     * @return
     */
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
            ",[path_locator].ToString() as locator" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from \"#{#entityName}\"" +
            " where [parent_path_locator] = GetPathLocator(:path)", nativeQuery = true)
    List<T> getChildByPathLocator(@Param("path") String fullFileNamespacePath);

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
            ",[path_locator].ToString() as locator" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from \"#{#entityName}\"" +
            " where [name] like ('%' + :name + '%')", nativeQuery = true)
    List<T> findByNameContains(@Param("name") String name);

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
            ",[path_locator].ToString() as locator" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from \"#{#entityName}\"" +
            " where [file_stream].GetFileNamespacePath() like (:namespacePath + '%')" +
            " and [name] like ('%' + :name + '%')", nativeQuery = true)
    List<T> findByFileNamespacePathStartsWithAndNameContains(@Param("namespacePath") String namespacePath, @Param("name") String name);

    //TODO fix mapping
    @Query(value = "select " +
            " file_stream" +
            " from \"#{#entityName}\"" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    Blob getBlobByPath(@Param("path") String path);

    @Query(value = "select " +
            " file_stream" +
            " from \"#{#entityName}\"" +
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
            ",[path_locator].ToString() as locator" +
            ",[file_stream].GetFileNamespacePath() as file_namespace_path" +
            ",[file_stream].PathName() as path_name" +
            " from \"#{#entityName}\"" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    T getByPath(@Param("path") String fileNamespacePath);

    /**
     * use CONVERT because ToString() return nvarchar and cant to String
     *
     * @param fileNamespacePath
     * @return
     */
    @Query(value = "select " +
            " CONVERT(varchar(max), [path_locator].ToString()) as locator" +
            " from \"#{#entityName}\"" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    String getPathLocatorStringByPath(@Param("path") String fileNamespacePath);

    @Query(value = "delete " +
            " from \"#{#entityName}\"" +
            " where [file_stream].GetFileNamespacePath() = :path", nativeQuery = true)
    @Modifying
    Integer deleteByPath(@Param("path") String fileNamespacePath);

    @Query(value = "update \"#{#entityName}\"" +
            " set name = :name" +
            " where [path_locator] = GetPathLocator(:path)", nativeQuery = true)
    @Modifying
    Integer updateNameByPathLocator(@Param("path") String pathLocator, @Param("name") String name);

    /**
     * @param pathLocator hierarchyid string
     * @param name
     * @return
     */
    @Query(value = "insert into \"#{#entityName}\"" +
            " ([name],[file_stream],[path_locator]) " +
            " values (:name, 0x0, :path)", nativeQuery = true)
    @Modifying
    Integer insertFileByNameAndPathLocator(@Param("path") String pathLocator, @Param("name") String name);

    /**
     * @param pathLocator hierarchyid string
     * @param name
     * @return
     */
    @Query(value = "insert into \"#{#entityName}\"" +
            " ([name],[is_directory],[path_locator]) " +
            " values (:name, 1, :path)", nativeQuery = true)
    @Modifying
    Integer insertDirectoryByNameAndPathLocator(@Param("path") String pathLocator, @Param("name") String name);

}
