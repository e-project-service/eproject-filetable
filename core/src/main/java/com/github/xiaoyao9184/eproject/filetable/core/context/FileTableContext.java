package com.github.xiaoyao9184.eproject.filetable.core.context;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTableContext extends ConcurrentHashMap<String, Object> {


    private String FILE_TABLE_BASE_URI = "base_uri";
    private String FILE_TABLE_URI = "uri";

    private String FILE_TABLE_URI_INCLUDE_FILE_NAME = "uri_include_file_name";


    public FileTableContext withBase(URI uri) {
        put(FILE_TABLE_BASE_URI,uri);
        return this;
    }

    public FileTableContext withURL(URI uri){
        put(FILE_TABLE_URI,uri);
        put(FILE_TABLE_URI_INCLUDE_FILE_NAME,true);
        return this;
    }

    public FileTableContext withParentURL(URI uri){
        put(FILE_TABLE_URI,uri);
        put(FILE_TABLE_URI_INCLUDE_FILE_NAME,false);
        return this;
    }

    public URI getUri(){
        return  (URI) get(FILE_TABLE_URI);
    }

    public boolean isUriIncludeFileName(){
        Boolean b = (Boolean) get(FILE_TABLE_URI_INCLUDE_FILE_NAME);
        if (b != null) {
            return b.booleanValue();
        }
        return contains(FILE_TABLE_URI);
    }

    public static FileTableContext create(){
        FileTableContext context = new FileTableContext();

        return context;
    }

    static ThreadLocal<FileTableContext> contextThreadLocal = new ThreadLocal<>();

    public static FileTableContext createThread(){
        FileTableContext context = create();
        contextThreadLocal.set(context);
        return context;
    }

    public static FileTableContext createOrFromThread() {
        return contextThreadLocal.get() == null ? create() : contextThreadLocal.get();
    }
}

