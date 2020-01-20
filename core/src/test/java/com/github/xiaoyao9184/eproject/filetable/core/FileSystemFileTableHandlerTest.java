package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.FileSystemHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
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
@TestPropertySource(locations = "classpath:fs.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable.core")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileSystemFileTableHandlerTest {

    @Autowired
    private FileSystemFileTableHandler fileSystemFileTableHandler;

    @Test
    public void test01CreateByStream() {
        //no need
        assert true;
    }

    @Test
    public void test02CreateByBytes() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = new byte[]{ 0x0D, 0x49 };

        FileInfo a = fileSystemFileTableHandler.create(bytes,uri);

        Assert.assertEquals(
                a.getPath(),
                "/" + TestFileTable.TABLE_NAME + uri.toString());
    }

    @Test
    public void test03CreateByFileWithNewName() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/file.txt")
                .build()
                .toUri();
        File tempFile = File.createTempFile("tempFile",null);
        try(
                FileOutputStream os = new FileOutputStream(tempFile);
        ){
            os.write(new byte[]{0x01,0x0D,0x0D});
        }

        FileInfo a = fileSystemFileTableHandler.create(tempFile,uri);

        Assert.assertEquals(
                a.getPath(),
                "/" + TestFileTable.TABLE_NAME + uri.toString());
    }

    @Test
    public void test04CreateByMultipartFileWithoutNewName() throws Exception {
         URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/")
                .build()
                .toUri();

        byte[] bytes = new byte[]{ 0x0D, 0x49, 0x0D, 0x49};
        MockMultipartFile file = new MockMultipartFile("MockMultipartFile.txt","MockMultipartFile.txt","",bytes);

        FileInfo a = fileSystemFileTableHandler.create(file,uri);

        Assert.assertEquals(
                a.getPath(),
                "/" + TestFileTable.TABLE_NAME + uri.toString() + "MockMultipartFile.txt");
    }

    @Test
    public void test05CreateDir() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .path("/test_dir/new_dir")
                .build()
                .toUri();
        FileInfo a = fileSystemFileTableHandler.create(uri);

        Assert.assertEquals(
                a.getPath(),
                "/" + TestFileTable.TABLE_NAME + uri.toString());
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
        FileInfo a = fileSystemFileTableHandler.read(uri);

        Assert.assertEquals(
                a.getSize().intValue(),
                4);
        Assert.assertEquals(
                a.getPath(),
                "/test/fs/MockMultipartFile.txt");

        Assert.assertTrue(
                a.getParentPath().contains("/"));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/fs")
                .build()
                .toUri();
        List<FileInfo> list = fileSystemFileTableHandler.readChild(uri);

        Assert.assertEquals(
                list.size(),
                4);
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
    }
}