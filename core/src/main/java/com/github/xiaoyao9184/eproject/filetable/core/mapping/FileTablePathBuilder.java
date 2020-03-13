package com.github.xiaoyao9184.eproject.filetable.core.mapping;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTablePathBuilder
        implements Consumer<FileTablePathBuilder.FileTableUriComponentsBuilder> {

    public static FileTablePathBuilder newInstance(){
        return new FileTablePathBuilder();
    }


    private FileTableUriComponentsBuilder fileTableUriComponentsBuilder;

    public FileTableUriComponentsBuilder<FileTablePathBuilder> uri(){
        return FileTableUriComponentsBuilder.newInstance(this);
    }

    public FileTableUriComponentsHolder build() {
        return new FileTableUriComponentsHolder(fileTableUriComponentsBuilder.build());
    }

    @Override
    public void accept(FileTableUriComponentsBuilder fileTableUriComponentsBuilder) {
        this.fileTableUriComponentsBuilder = fileTableUriComponentsBuilder;
    }


    public static class FileTableUriComponentsBuilder<PARENT_BUILDER extends Consumer<FileTableUriComponentsBuilder>>
            extends UriComponentsBuilder {

        private PARENT_BUILDER parentBuilder;

        public static <PARENT_BUILDER extends Consumer<FileTableUriComponentsBuilder>> FileTableUriComponentsBuilder<PARENT_BUILDER> newInstance(PARENT_BUILDER parent_builder){
            return new FileTableUriComponentsBuilder<PARENT_BUILDER>()
                    .enter(parent_builder);
        }

        public FileTableUriComponentsBuilder<PARENT_BUILDER> enter(PARENT_BUILDER parentBuilder){
            this.parentBuilder = parentBuilder;
            return this;
        }

        public PARENT_BUILDER and(){
            parentBuilder.accept(this);
            return parentBuilder;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> uri(URI uri) {
            super.uri(uri);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> uriComponents(UriComponents uriComponents) {
            super.uriComponents(uriComponents);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> scheme(String scheme) {
            super.scheme(scheme);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> schemeSpecificPart(String ssp) {
            super.schemeSpecificPart(ssp);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> userInfo(String userInfo) {
            super.userInfo(userInfo);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> host(String host) {
            super.host(host);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> port(int port) {
            super.port(port);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> port(String port) {
            super.port(port);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> path(String path) {
            super.path(path);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> pathSegment(String... pathSegments) throws IllegalArgumentException {
            super.pathSegment(pathSegments);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> replacePath(String path) {
            super.replacePath(path);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> query(String query) {
            super.query(query);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> replaceQuery(String query) {
            super.replaceQuery(query);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> queryParam(String name, Object... values) {
            super.queryParam(name, values);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> queryParams(MultiValueMap<String, String> params) {
            super.queryParams(params);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> replaceQueryParam(String name, Object... values) {
            super.replaceQueryParam(name, values);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> replaceQueryParams(MultiValueMap<String, String> params) {
            super.replaceQueryParams(params);
            return this;
        }

        @Override
        public FileTableUriComponentsBuilder<PARENT_BUILDER> fragment(String fragment) {
            super.fragment(fragment);
            return this;
        }



    }

    public static class FileTableUriComponentsHolder {

        protected UriComponents that;

        public FileTableUriComponentsHolder(UriComponents that){
            this.that = that;
        }

        public UriComponents show(){
            return that;
        }

        public String toWinString(){
            return that.toUriString().replace("/","\\");
        }

    }

}
