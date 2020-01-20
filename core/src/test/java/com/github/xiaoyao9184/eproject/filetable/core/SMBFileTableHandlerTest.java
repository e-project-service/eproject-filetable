package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.FileSystemHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
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
@TestPropertySource(locations = "classpath:smb.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable.core")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SMBFileTableHandlerTest {

    @Autowired
    private SMBFileTableHandler smbFileTableHandler;

    @Test
    public void test01CreateByStream() {
        //no need
        assert true;
    }

    @Test
    public void test02CreateByBytes() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = new byte[]{ 0x0D, 0x49 };

        FileInfo result = smbFileTableHandler.create(bytes,uri);

        Assert.assertEquals(
                result.getPath(),
                uri.toString());
    }

    @Test
    public void test03CreateByFileWithNewName() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/file.txt")
                .build()
                .toUri();
        File tempFile = File.createTempFile("tempFile",null);
        try(
                FileOutputStream os = new FileOutputStream(tempFile);
        ){
            os.write(new byte[]{0x01,0x0D,0x0D});
        }

        FileInfo result = smbFileTableHandler.create(tempFile,uri);

        Assert.assertEquals(
                result.getPath(),
                uri.toString());
    }

    @Test
    public void test04CreateByMultipartFileWithoutNewName() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/")
                .build()
                .toUri();

        byte[] bytes = new byte[]{ 0x0D, 0x49, 0x0D, 0x49 };
        MockMultipartFile file = new MockMultipartFile("MockMultipartFile.txt","MockMultipartFile.txt","",bytes);

        FileInfo result = smbFileTableHandler.create(file,uri);

        Assert.assertEquals(
                result.getPath(),
                uri.toString() + "MockMultipartFile.txt");
    }

    @Test
    public void test05CreateDir() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/test_dir/new_dir")
                .build()
                .toUri();
        FileInfo result = smbFileTableHandler.create(uri);

        Assert.assertEquals(
                result.getPath(),
                uri.toString());
    }

    @Test
    public void test05ReadFile() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/file.txt")
                .build()
                .toUri();
        File file = smbFileTableHandler.readFile(uri);

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
                .path("/test/smb")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = smbFileTableHandler.readBytes(uri);

        Assert.assertEquals(
                bytes.length,
                2);
    }

    @Test
    public void test08Read() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        FileInfo a = smbFileTableHandler.read(uri);

        Assert.assertEquals(
                a.getSize().intValue(),
                4);
        Assert.assertEquals(
                a.getPath(),
                "/test/smb/MockMultipartFile.txt");

        Assert.assertTrue(
                a.getParentPath().contains("\\"));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .build()
                .toUri();
        List<FileInfo> list = smbFileTableHandler.readChild(uri);

        Assert.assertEquals(
                list.size(),
                4);
    }

    @Test
    public void test10Delete() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        boolean result = smbFileTableHandler.delete(uri);

        Assert.assertTrue(
                result);

        URI uri2 = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/file.txt")
                .build()
                .toUri();
        smbFileTableHandler.delete(uri2);

        URI uri3 = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/byte.txt")
                .build()
                .toUri();
        smbFileTableHandler.delete(uri3);
    }

    @Test
    public void test11DeleteDir() throws Exception {
        URI uri4 = UriComponentsBuilder.newInstance()
                .path("/test/smb/test_dir/new_dir")
                .build()
                .toUri();
        smbFileTableHandler.delete(uri4);

        URI uri5 = UriComponentsBuilder.newInstance()
                .path("/test/smb/test_dir")
                .build()
                .toUri();
        smbFileTableHandler.delete(uri5);

        //TODO smb rm not work delay until the end of the java program
        //TODO , not smb session, not connection
//        Thread.sleep(10000);
//        URI uri6 = UriComponentsBuilder.newInstance()
//                .path("/test/smb")
//                .build()
//                .toUri();
//        smbFileTableHandler.delete(uri6);
    }
}