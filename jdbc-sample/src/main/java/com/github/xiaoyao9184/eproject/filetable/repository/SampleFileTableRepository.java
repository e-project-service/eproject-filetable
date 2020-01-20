package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.SampleFileTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.io.InputStream;
import java.util.List;

/**
 * Created by xy on 2020/1/16.
 */
public interface SampleFileTableRepository extends Repository<SampleFileTable, String> {

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
            " from sample_filetable" +
            " where [parent_path_locator] = GetPathLocator(:path)", nativeQuery = true)
    List<SampleFileTable> getChildByPath(@Param("path") String path);

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
            " from sample_filetable" +
            " where [parent_path_locator] is null", nativeQuery = true)
    List<SampleFileTable> getChildByRoot();


    @Query(value = "select " +
            " [file_stream]" +
            " from sample_filetable" +
            " where [parent_path_locator] = GetPathLocator(?)", nativeQuery = true)
    InputStream getFile(String path);

}
