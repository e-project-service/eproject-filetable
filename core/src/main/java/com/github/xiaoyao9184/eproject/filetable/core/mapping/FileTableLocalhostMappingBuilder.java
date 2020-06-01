package com.github.xiaoyao9184.eproject.filetable.core.mapping;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTableLocalhostMappingBuilder {

    private FileTableNameProvider fileTableNameProvider;

    public static FileTableLocalhostMappingBuilder newInstance(){
        return new FileTableLocalhostMappingBuilder();
    }

    private BaseFileTableProperties.MappingLocation location;
    private String root;
    private String servername;
    private String instance;
    private String database;

    public FileTableLocalhostMappingBuilder location(BaseFileTableProperties.MappingLocation location){
        this.location = location;
        return this;
    }

    @Deprecated
    public FileTableLocalhostMappingBuilder server(String servername){
        this.servername = servername;
        return this;
    }

    public FileTableLocalhostMappingBuilder instance(String instance){
        this.instance = instance;
        return this;
    }

    public FileTableLocalhostMappingBuilder database(String database){
        this.database = database;
        return this;
    }


    public FileTableLocalhostMappingBuilder root(String root){
        this.root = root;
        return this;
    }

    public FileTableLocalhostMappingBuilder table(FileTableNameProvider fileTableNameProvider) {
        this.fileTableNameProvider = fileTableNameProvider;
        return this;
    }

    public UriComponentsBuilder build(){
        return UriComponentsBuilder.newInstance()
                .scheme("file")
                .host("")
                .path(root)
                .pathSegment(this.location.ordinal() >= 1 ? "" : instance)
                .pathSegment(this.location.ordinal() >= 2 ? "" : database)
                .pathSegment(this.location.ordinal() >= 3 ? "" : fileTableNameProvider.provide());
    }

}
