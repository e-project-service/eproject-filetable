package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.FileSystemHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
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
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                PropertiesConfiguration.class,
                FileSystemHandlerConfiguration.class }
)
@TestPropertySource(locations = "classpath:filetable.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SMBFileTableHandlerTest {

    public static final String FILETABLE_TABLE_NAME = "default_filetable";

    @Autowired
    private SMBFileTableHandler smbFileTableHandler;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Test
    public void test00CreateBak(){
        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(fileTableProperties.getServername())) {
            AuthenticationContext ac = new AuthenticationContext(
                    fileTableProperties.getUsername(),
                    fileTableProperties.getPassword().toCharArray(),
                    fileTableProperties.getDomain());
            Session session = connection.authenticate(ac);

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare(fileTableProperties.getInstance())) {
                Set<FileAttributes> fileAttributes = new HashSet<>();
                fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
                Set<SMB2CreateOptions> createOptions = new HashSet<>();
                createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);

                if(!share.folderExists(fileTableProperties.getDatabase() + "\\" +
                        FILETABLE_TABLE_NAME + "\\test")){
                    share.mkdir(fileTableProperties.getDatabase() + "\\" +
                            FILETABLE_TABLE_NAME + "\\test");
                }
                if(!share.folderExists(fileTableProperties.getDatabase() + "\\" +
                        FILETABLE_TABLE_NAME + "\\test\\smb")){
                    share.mkdir(fileTableProperties.getDatabase() + "\\" +
                            FILETABLE_TABLE_NAME + "\\test\\smb");
                }
                if(!share.fileExists(fileTableProperties.getDatabase() + "\\" +
                        FILETABLE_TABLE_NAME + "\\test\\smb\\test.bak")){
                    com.hierynomus.smbj.share.File f = share.openFile(
                            fileTableProperties.getDatabase() + "\\" +
                                    FILETABLE_TABLE_NAME + "\\test\\smb\\test.bak",
                            new HashSet<>(Collections.singletonList(AccessMask.GENERIC_ALL)),
                            fileAttributes,
                            SMB2ShareAccess.ALL,
                            SMB2CreateDisposition.FILE_CREATE,
                            createOptions);
                    f.write(new byte[]{0x0D},0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01CreateByStream() {
        //no need
        assert true;
    }

    @Test
    public void test02CreateByBytes() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/byte.txt")
                .build()
                .toUri();
        byte[] bytes = new byte[]{ 0x0D, 0x49 };

        AbstractFileTable a = smbFileTableHandler.create(bytes,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test03CreateByFileWithNewName() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/file.txt")
                .build()
                .toUri();
        File attach_unit = File.createTempFile("attach_unit",null);
        try(
                FileOutputStream os = new FileOutputStream(attach_unit);
        ){
            os.write(new byte[]{0x01,0x0D,0x0D});
        }

        AbstractFileTable a = smbFileTableHandler.create(attach_unit,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test04CreateByMultipartFileWithoutNewName() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/")
                .build()
                .toUri();

        byte[] bytes = new byte[]{ 0x0D, 0x49, 0x0D, 0x49 };
        MockMultipartFile file = new MockMultipartFile("MockMultipartFile.txt","MockMultipartFile.txt","",bytes);

        AbstractFileTable a = smbFileTableHandler.create(file,uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath() + "MockMultipartFile.txt"));
    }

    @Test
    public void test05CreateDir() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/test_dir/new_dir")
                .build()
                .toUri();
        AbstractFileTable a = smbFileTableHandler.create(uri);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
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
        AbstractFileTable a = smbFileTableHandler.read(uri);

        Assert.assertEquals(
                a.getCached_file_size().intValue(),
                4);

        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.getPath()));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .build()
                .toUri();
        List<AbstractFileTable> list = smbFileTableHandler.readChild(uri);

        Assert.assertEquals(
                list.size(),
                4);
    }

    @Test
    public void test091Rename() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/MockMultipartFile.txt")
                .build()
                .toUri();
        boolean result = smbFileTableHandler.rename(uri,"MockMultipartFile.bak");
        Assert.assertTrue(
                result);

        URI uri2 = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/MockMultipartFile.bak")
                .build()
                .toUri();
        result = smbFileTableHandler.rename(uri2,"MockMultipartFile.txt");
        Assert.assertTrue(
                result);
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
        result = smbFileTableHandler.delete(uri2);

        Assert.assertTrue(
                result);

        URI uri3 = UriComponentsBuilder.newInstance()
                .path("/test/smb")
                .path("/byte.txt")
                .build()
                .toUri();
        result = smbFileTableHandler.delete(uri3);

        Assert.assertTrue(
                result);
    }

    @Test
    public void test11DeleteDir() throws Exception {
        URI uri4 = UriComponentsBuilder.newInstance()
                .path("/test/smb/test_dir/new_dir")
                .build()
                .toUri();
        boolean result = smbFileTableHandler.delete(uri4);

        Assert.assertTrue(
                result);

        URI uri5 = UriComponentsBuilder.newInstance()
                .path("/test/smb/test_dir")
                .build()
                .toUri();
        result = smbFileTableHandler.delete(uri5);

        Assert.assertTrue(
                result);

        //TODO smb rm not work delay until the end of the java program
        //TODO , not smb session, not connection
//        Thread.sleep(10000);
//        URI uri6 = UriComponentsBuilder.newInstance()
//                .path("/test/smb")
//                .build()
//                .toUri();
//        smbFileTableHandler.delete(uri6);
    }

    //TODO smb rm not work delay until the end of the java program
    //TODO , not smb session, not connection
//    @Test
//    public void test99RemoveBak() throws Exception {
//        SMBClient client = new SMBClient();
//
//        try (Connection connection = client.connect(fileTableProperties.getServername())) {
//            AuthenticationContext ac = new AuthenticationContext(
//                    fileTableProperties.getUsername(),
//                    fileTableProperties.getPassword().toCharArray(),
//                    fileTableProperties.getDomain());
//            Session session = connection.authenticate(ac);
//
//            // Connect to Share
//            try (DiskShare share = (DiskShare) session.connectShare(fileTableProperties.getInstance())) {
//                Set<FileAttributes> fileAttributes = new HashSet<>();
//                fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
//                Set<SMB2CreateOptions> createOptions = new HashSet<>();
//                createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);
//
//                if(share.folderExists(fileTableProperties.getDatabase() + "\\" +
//                        FILETABLE_TABLE_NAME + "\\test\\smb")){
//                    share.rmdir(fileTableProperties.getDatabase() + "\\" +
//                            FILETABLE_TABLE_NAME + "\\test\\smb",true);
//                }
//                if(share.folderExists(fileTableProperties.getDatabase() + "\\" +
//                        FILETABLE_TABLE_NAME + "\\test")){
//                    share.rmdir(fileTableProperties.getDatabase() + "\\" +
//                            FILETABLE_TABLE_NAME + "\\test",true);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}