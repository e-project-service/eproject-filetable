package com.github.xiaoyao9184.eproject.filetable.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xy on 2020/1/16.
 */
@Entity(name="sample_filetable")
@Table(name="sample_filetable")
public class SampleFileTable implements Serializable {

    @Id
    @Column(name="stream_id")
    private String stream_id;

    @Column(name="name")
    private String name;

    @Column(name="file_type")
    private String file_type;

    @Column(name="cached_file_size")
    private Long cached_file_size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_time")
    private Date creation_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_write_time")
    private Date last_write_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_access_time")
    private Date last_access_time;

    @Column(name="is_directory")
    private Boolean is_directory;

    @Column(name="is_offline")
    private Boolean is_offline;

    @Column(name="is_hidden")
    private Boolean is_hidden;

    @Column(name="is_readonly")
    private Boolean is_readonly;

    @Column(name="is_archive")
    private Boolean is_archive;

    @Column(name="is_system")
    private Boolean is_system;

    @Column(name="is_temporary")
    private Boolean is_temporary;

    @Column(name="root")
    private String root;

    @Column(name="level")
    private Integer level;

    @Column(name="file_namespace_path")
    private String file_namespace_path;

    @Column(name="path_name")
    private String path_name;


    public String getStream_id() {
        return stream_id;
    }

    public void setStream_id(String stream_id) {
        this.stream_id = stream_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public Long getCached_file_size() {
        return cached_file_size;
    }

    public void setCached_file_size(Long cached_file_size) {
        this.cached_file_size = cached_file_size;
    }

    public Date getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Date creation_time) {
        this.creation_time = creation_time;
    }

    public Date getLast_write_time() {
        return last_write_time;
    }

    public void setLast_write_time(Date last_write_time) {
        this.last_write_time = last_write_time;
    }

    public Date getLast_access_time() {
        return last_access_time;
    }

    public void setLast_access_time(Date last_access_time) {
        this.last_access_time = last_access_time;
    }

    public Boolean getIs_directory() {
        return is_directory;
    }

    public void setIs_directory(Boolean is_directory) {
        this.is_directory = is_directory;
    }

    public Boolean getIs_offline() {
        return is_offline;
    }

    public void setIs_offline(Boolean is_offline) {
        this.is_offline = is_offline;
    }

    public Boolean getIs_hidden() {
        return is_hidden;
    }

    public void setIs_hidden(Boolean is_hidden) {
        this.is_hidden = is_hidden;
    }

    public Boolean getIs_readonly() {
        return is_readonly;
    }

    public void setIs_readonly(Boolean is_readonly) {
        this.is_readonly = is_readonly;
    }

    public Boolean getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(Boolean is_archive) {
        this.is_archive = is_archive;
    }

    public Boolean getIs_system() {
        return is_system;
    }

    public void setIs_system(Boolean is_system) {
        this.is_system = is_system;
    }

    public Boolean getIs_temporary() {
        return is_temporary;
    }

    public void setIs_temporary(Boolean is_temporary) {
        this.is_temporary = is_temporary;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getFile_namespace_path() {
        return file_namespace_path;
    }

    public void setFile_namespace_path(String file_namespace_path) {
        this.file_namespace_path = file_namespace_path;
    }

    public String getPath_name() {
        return path_name;
    }

    public void setPath_name(String path_name) {
        this.path_name = path_name;
    }
}
