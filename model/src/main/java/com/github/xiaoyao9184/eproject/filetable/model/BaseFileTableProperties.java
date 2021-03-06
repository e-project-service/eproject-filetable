package com.github.xiaoyao9184.eproject.filetable.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xy on 2020/1/15.
 */
public class BaseFileTableProperties {

    private String rootPath;
    private String servername;
    private String instance;
    private String database;
    private String username;
    private String password;
    private String domain = "WORKGROUP";

    private boolean autoCreateDirectory = false;

    private Map<Operator,OperatorMethod> operatorMethods = new HashMap<>();

    public String getRootPath() {
//        if(rootPath == null){
//            return "\\" + servername +
//                    "\\" + instance +
//                    "\\" + database;
//        }
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

    public boolean isAutoCreateDirectory() {
        return autoCreateDirectory;
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
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

        DELETE,

        RENAME,

        SEARCH
    }

    public enum OperatorMethod {
        DATABASE,
        FILE
    }

    public enum MappingLocation {

        /**
         * Windows cant mapping server only use ComputerName
         * See <a href="https://docs.microsoft.com/en-us/previous-versions/windows/it-pro/windows-server-2012-r2-and-2012/gg651155(v=ws.11)">Net Use</a>
         */
        @Deprecated
        SERVER,

        /**
         * Currently this project does not support managing multiple instances, multiple databases,
         * This project can still run with mapping instance level directory to the local.
         */
        INSTANCE,

        /**
         * Recommend
         */
        DATABASE,

        /**
         * NOT Recommend
         */
        TABLE
    }

}
