package com.github.xiaoyao9184.eproject.filetable.info.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by xy on 2020/6/6.
 */
@Entity
@Table(name="not.server_info")
public class ServerInfo {

    @Id
    @Column(name="server_name")
    private String serverName;

    @Column(name="service_name")
    private String serviceName;

    @Column(name="version")
    private String version;

    @Column(name="root_path")
    private String rootPath;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
