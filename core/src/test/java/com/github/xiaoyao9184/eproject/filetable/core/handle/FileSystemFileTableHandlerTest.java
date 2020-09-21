package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.FileSystemHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                PropertiesConfiguration.class,
                FileSystemHandlerConfiguration.class }
)
@TestPropertySource(locations = {
        "classpath:filetable.properties",
        "classpath:mapping-instance-localhost.properties"})
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileSystemFileTableHandlerTest {

    @Autowired
    private FileSystemFileTableHandler fileSystemFileTableHandler;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Test
    public void test01CreateByStream() {
        //no need
        assert true;
    }

    @Test
    public void test02CreateByBytes() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = new byte[]{ 0x0D, 0x49 };

        AbstractFileTable a = fileSystemFileTableHandler.create(bytes,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test03CreateByFileWithNewName() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/file.txt")
                .build()
                .toUri();
        File attach_unit = File.createTempFile("attach_unit",null);
        try(
                FileOutputStream os = new FileOutputStream(attach_unit);
        ){
            os.write(new byte[]{0x01,0x0D,0x0D});
        }

        AbstractFileTable a = fileSystemFileTableHandler.create(attach_unit,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test04CreateByMultipartFileWithoutNewName() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/")
                .build()
                .toUri();

        byte[] bytes = new byte[]{ 0x0D, 0x49, 0x0D, 0x49};
        MockMultipartFile file = new MockMultipartFile("MockMultipartFile.txt","MockMultipartFile.txt","",bytes);

        AbstractFileTable a = fileSystemFileTableHandler.create(file,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath() + "MockMultipartFile.txt"));
    }

    @Test
    public void test05CreateDir() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/test_dir/new_dir")
                .build()
                .toUri();
        AbstractFileTable a = fileSystemFileTableHandler.create(uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test05ReadFile() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/file.txt")
                .build()
                .toUri();
        File file = fileSystemFileTableHandler.readFile(uri);

        Assert.assertEquals(
                file.length(),
                3);
    }

    @Test
    public void test06ReadStream() {
        //no need
        assert true;
    }

    @Test
    public void test07ReadBytes() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = fileSystemFileTableHandler.readBytes(uri);

        Assert.assertEquals(
                bytes.length,
                2);
    }

    @Test
    public void test08Read() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        AbstractFileTable a = fileSystemFileTableHandler.read(uri);

        Assert.assertEquals(
                a.getCached_file_size().intValue(),
                4);
        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .build()
                .toUri();
        List<AbstractFileTable> list = fileSystemFileTableHandler.readChild(uri);

        Assert.assertEquals(
                list.size(),
                4);
    }

    @Test
    public void test091Rename() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        boolean result = fileSystemFileTableHandler.rename(uri,"MockMultipartFile.bak");
        Assert.assertTrue(
                result);

        URI uri2 = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/MockMultipartFile.bak")
                .build()
                .toUri();
        result = fileSystemFileTableHandler.rename(uri2,"MockMultipartFile.txt");
        Assert.assertTrue(
                result);
    }

    @Test
    public void test10Delete() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        boolean result = fileSystemFileTableHandler.delete(uri);

        Assert.assertTrue(
                result);

        URI uri2 = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/file.txt")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri2);

        URI uri3 = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/byte.txt")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri3);

        URI uri4 = UriComponentsBuilder.newInstance()
                .path("/test/fs/test_dir/new_dir")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri4);

        URI uri5 = UriComponentsBuilder.newInstance()
                .path("/test/fs/test_dir")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri5);

        URI uri6 = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri6);

        URI uri7 = UriComponentsBuilder.newInstance()
                .path("/test")
                .build()
                .toUri();
        fileSystemFileTableHandler.delete(uri7);
    }
}