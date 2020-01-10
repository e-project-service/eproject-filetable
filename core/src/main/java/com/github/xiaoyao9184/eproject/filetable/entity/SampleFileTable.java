package com.github.xiaoyao9184.eproject.filetable.entity;

import io.r2dbc.spi.Clob;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;


/**
 * Created by xy on 2019/6/17.
 */
public class SampleFileTable implements Persistable<String> {

    private String stream_id;

    private String name;

    private String file_type;

    private Long cached_file_size;

    private OffsetDateTime creation_time;

    private OffsetDateTime last_write_time;

    private OffsetDateTime last_access_time;

    private Boolean is_directory;

    private Boolean is_offline;

    private Boolean is_hidden;

    private Boolean is_readonly;

    private Boolean is_archive;

    private Boolean is_system;

    private Boolean is_temporary;

    private String root;

    private Integer level;

    private String file_namespace_path;

    private String path_name;

    @Override
    public String getId() {
        return stream_id;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public void setFile_namespace_path(Clob clob) {
        this.file_namespace_path = clob.toString();
    }


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

    public OffsetDateTime getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(OffsetDateTime creation_time) {
        this.creation_time = creation_time;
    }

    public OffsetDateTime getLast_write_time() {
        return last_write_time;
    }

    public void setLast_write_time(OffsetDateTime last_write_time) {
        this.last_write_time = last_write_time;
    }

    public OffsetDateTime getLast_access_time() {
        return last_access_time;
    }

    public void setLast_access_time(OffsetDateTime last_access_time) {
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
