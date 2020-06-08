package com.github.xiaoyao9184.eproject.filetable.info.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by xy on 2020/6/6.
 */
@Entity
@Table(name="sys.database_filestream_options")
public class DatabaseFileStreamOptions {

    @Id
    @Column(name="database_id")
    private String databaseId;

    @Column(name="database_name")
    private String databaseName;

    @Column(name="non_transacted_access")
    private String nonTransactedAccess;

    @Column(name="non_transacted_access_desc")
    private String nonTransactedAccessDesc;

    @Column(name="directory_name")
    private String directoryName;

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getNonTransactedAccess() {
        return nonTransactedAccess;
    }

    public void setNonTransactedAccess(String nonTransactedAccess) {
        this.nonTransactedAccess = nonTransactedAccess;
    }

    public String getNonTransactedAccessDesc() {
        return nonTransactedAccessDesc;
    }

    public void setNonTransactedAccessDesc(String nonTransactedAccessDesc) {
        this.nonTransactedAccessDesc = nonTransactedAccessDesc;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
