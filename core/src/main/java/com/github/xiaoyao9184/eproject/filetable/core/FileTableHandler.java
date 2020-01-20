package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
public interface FileTableHandler {

    default Tuple2<String,String> separatePathAndNameFromURI(URI uri){
        String[] urls = uri.getPath().split("/");
        List<String> list = new ArrayList<>(Arrays.asList(urls));
        String name = list.remove(urls.length - 1);
        String path = String.join("/", list);

        return Tuple.tuple(path,name);
    }

    /**
     * 创建文件
     * @param multipartFile SpringMVC文件
     * @param uri
     * @return FileTableInfo
     * @throws Exception
     */
    FileInfo create(MultipartFile multipartFile, URI uri) throws Exception;

    /**
     * 创建文件
     * @param file 文件
     * @param uri
     * @return FileTableInfo
     * @throws Exception
     */
    FileInfo create(File file, URI uri) throws Exception;

    /**
     * 创建文件
     * @param stream 流
     * @param uri
     * @return FileTableInfo
     * @throws Exception
     */
    FileInfo create(InputStream stream, URI uri) throws Exception;

    /**
     * 创建文件
     * @param bytes 字节数组
     * @param uri
     * @return FileTableInfo
     * @throws Exception
     */
    FileInfo create(byte[] bytes, URI uri) throws Exception;

    /**
     * 创建文件夹
     * @param uri
     * @return
     * @throws Exception
     */
    FileInfo create(URI uri) throws Exception;

    /**
     * 获取文件
     * @param uri
     * @return File
     * @throws Exception
     */
    File readFile(URI uri) throws Exception;

    /**
     * 获取读入流
     * @param uri
     * @return Input Stream
     * @throws Exception
     */
    InputStream readStream(URI uri) throws Exception;

    /**
     * 获取字节数组
     * @param uri
     * @return Byte Array
     * @throws Exception
     */
    byte[] readBytes(URI uri) throws Exception;


    /**
     * 获取文件
     * @param uri
     * @return FileTableInfo
     * @throws Exception
     */
    FileInfo read(URI uri) throws Exception;

    /**
     * 删除
     * @param uri
     * @return 成功/失败
     * @throws Exception
     */
    boolean delete(URI uri) throws Exception;

    /**
     * 获取子级文件
     * @param uri
     * @return
     * @throws Exception
     */
    List<FileInfo> readChild(URI uri) throws Exception;
}
