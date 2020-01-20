package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.SampleFileTable;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Created by xy on 2019/6/17.
 */
public interface SampleFileTableRepository extends ReactiveCrudRepository<SampleFileTable, String> {

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
            " where [parent_path_locator] = GetPathLocator(:path)")
    Flux<SampleFileTable> getChildByPathLocator(String path);

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
            //todo BUG if remove this param
            //java.lang.IllegalArgumentException: Invalid TDS type: 0
            " where [path_locator].GetLevel() = :level")
    Flux<SampleFileTable> getChildByRootLocator(Integer level);

    default Flux<SampleFileTable> getChildByRootLocator(){
        return getChildByRootLocator(1);
    }


//    @Query(value = "select " +
//            " [file_stream]" +
//            " from sample_filetable" +
//            " where [path_locator] = GetPathLocator(:path)")
//    Flux<ByteBuffer> getFile(String path);
}
