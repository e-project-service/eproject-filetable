package com.github.xiaoyao9184.eproject.filetable.info.repository;

import com.github.xiaoyao9184.eproject.filetable.info.entity.ServerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by xy on 2020/6/6.
 */
public interface ServerInfoRepository extends JpaRepository<ServerInfo,String> {

    @Query(value = "select " +
            "@@SERVERNAME as 'server_name', " +
            "@@SERVICENAME as 'service_name', " +
            "@@VERSION as 'version'," +
            "FileTableRootPath() as 'root_path'", nativeQuery = true)
    ServerInfo getServerInfo();

}
