package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.DataBaseHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
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
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

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
                DataBaseHandlerConfiguration.class,
                MssqlConfig.class }
)
@TestPropertySource(locations = "classpath:filetable.properties")
@ComponentScan("om.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseFileTableHandlerTest {

    @Autowired
    private DatabaseFileTableHandler databaseFileTableHandler;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Autowired
    private FileTableNameProvider fileTableNameProvider;

    @Test
    public void test00CreateBak() throws Exception {
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
                        fileTableNameProvider.provide() + "\\test")){
                    share.mkdir(fileTableProperties.getDatabase() + "\\" +
                            fileTableNameProvider.provide() + "\\test");
                }
                if(!share.folderExists(fileTableProperties.getDatabase() + "\\" +
                        fileTableNameProvider.provide() + "\\test\\db")){
                    share.mkdir(fileTableProperties.getDatabase() + "\\" +
                            fileTableNameProvider.provide() + "\\test\\db");
                }
                if(!share.fileExists(fileTableProperties.getDatabase() + "\\" +
                        fileTableNameProvider.provide() + "\\test\\db\\test.bak")){
                    com.hierynomus.smbj.share.File f = share.openFile(
                            fileTableProperties.getDatabase() + "\\" +
                                    fileTableNameProvider.provide() + "\\test\\db\\test.bak",
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

    //TODO
//    @Test
//    public void test06ReadStream() throws Exception {
//        URI uri = UriComponentsBuilder.newInstance()
//                .path("/attach_filetable/new_text.txt")
//                .build()
//                .toUri();
//        databaseFileTableHandler.readStream(uri);
//        assert true;
//    }

//    @Test
//    public void test07ReadBytes() throws Exception {
//        URI uri = UriComponentsBuilder.newInstance()
//                .path("/test/db/test.bak")
//                .build()
//                .toUri();
//        byte[] bytes = databaseFileTableHandler.readBytes(uri);
//
//        Assert.assertEquals(
//                bytes.length,
//                1);
//    }

    @Test
    public void test08Read() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/test.bak")
                .build()
                .toUri();
        AbstractFileTable a = databaseFileTableHandler.read(uri);

        Assert.assertEquals(
                a.getCached_file_size().intValue(),
                1);
        Assert.assertTrue(
                a.getFile_namespace_path().replace("\\","/").endsWith(uri.toString()));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db")
                .build()
                .toUri();
        List<AbstractFileTable> list = databaseFileTableHandler.readChild(uri);

        Assert.assertEquals(
                list.size(),
                1);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test091Rename() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/test.bak")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.rename(uri,"test.rename");
        Assert.assertTrue(
                result);

        URI uri2 = UriComponentsBuilder.newInstance()
                .path("/test/db/test.rename")
                .build()
                .toUri();
        result = databaseFileTableHandler.rename(uri2,"test.bak");
        Assert.assertTrue(
                result);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test092MkFile() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/mk_file")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.makeFile(uri);
        Assert.assertTrue(
                result);

        result = databaseFileTableHandler.delete(uri);
        Assert.assertTrue(
                result);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test093Mkdir() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/mk_dir")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.makeDirectory(uri);
        Assert.assertTrue(
                result);

        result = databaseFileTableHandler.delete(uri);
        Assert.assertTrue(
                result);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test094Mkdirs() throws Exception {
        fileTableProperties.setAutoCreateDirectory(true);

        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/mk_dir/mkdir2/mkdir3")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.mkdirs(uri);
        Assert.assertTrue(
                result);


        result = databaseFileTableHandler.delete(uri);
        Assert.assertTrue(
                result);

        URI mkdir2 = UriComponentsBuilder.newInstance()
                .path("/test/db/mk_dir/mkdir2")
                .build()
                .toUri();
        result = databaseFileTableHandler.delete(mkdir2);
        Assert.assertTrue(
                result);

        URI mk_dir = UriComponentsBuilder.newInstance()
                .path("/test/db/mk_dir")
                .build()
                .toUri();
        result = databaseFileTableHandler.delete(mk_dir);
        Assert.assertTrue(
                result);


        //create root
        URI test2_smb = UriComponentsBuilder.newInstance()
                .path("/test2/db")
                .build()
                .toUri();
        result = databaseFileTableHandler.mkdirs(test2_smb);
        Assert.assertTrue(
                result);

        result = databaseFileTableHandler.delete(test2_smb);
        Assert.assertTrue(
                result);

        URI test2 = UriComponentsBuilder.newInstance()
                .path("/test2")
                .build()
                .toUri();
        result = databaseFileTableHandler.delete(test2);
        Assert.assertTrue(
                result);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test10Delete() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test/db/test.bak")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.delete(uri);

        Assert.assertTrue(
                result);
    }

    @Test
    public void test99RemoveBak() throws Exception {
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

                if(share.folderExists(fileTableProperties.getDatabase() + "\\" +
                        fileTableNameProvider.provide() + "\\test")){
                    share.rmdir(fileTableProperties.getDatabase() + "\\" +
                            fileTableNameProvider.provide() + "\\test",true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}