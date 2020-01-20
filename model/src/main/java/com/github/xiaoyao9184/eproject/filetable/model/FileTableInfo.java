package com.github.xiaoyao9184.eproject.filetable.model;

/**
 * Created by xy on 2020/1/15.
 */
public class FileTableInfo extends FileInfo {

    private String serverName;

    private String instance;

    private String database;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
