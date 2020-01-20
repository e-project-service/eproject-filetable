package com.github.xiaoyao9184.eproject.filetable.model;

import java.util.Map;

/**
 * Created by xy on 2020/1/15.
 */
public class BaseFileTableProperties {


    //if mapping smb to location
    /**
     * include table name
     * @see com.github.xiaoyao9184.eproject.filetable.core.FileSystemFileTableHandler
     */
    private String mappingPath;

    private String rootPath;
    private String servername;
    private String instance;
    private String database;
    private String username;
    private String password;
    private String domain = "WORKGROUP";

    private Map<Operator,OperatorMethod> operatorMethods;


    public String getMappingPath() {
        return mappingPath;
    }

    public void setMappingPath(String mappingPath) {
        this.mappingPath = mappingPath;
    }

    public String getRootPath() {
        if(rootPath == null){
            return "\\" + servername +
                    "\\" + instance +
                    "\\" + database;
        }
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Map<Operator, OperatorMethod> getOperatorMethods() {
        return operatorMethods;
    }

    public void setOperatorMethods(Map<Operator, OperatorMethod> operatorMethods) {
        this.operatorMethods = operatorMethods;
    }

    public enum Operator {
        CREATE,
        READ_STREAM,
        READ,

        DELETE
    }

    public enum OperatorMethod {
        DATABASE,
        FILE
    }
}
