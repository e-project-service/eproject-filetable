package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.DataBaseHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
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
@TestPropertySource(locations = "classpath:filetable-db.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseFileTableHandlerTest {

    @Autowired
    private DatabaseFileTableHandler databaseFileTableHandler;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    public void createBak(){

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

                com.hierynomus.smbj.share.File f = share.openFile(
                        fileTableProperties.getDatabase() + "\\" +
                                TestFileTable.TABLE_NAME + "\\test.bak",
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
    }

    @Test
    public void test06ReadStream() throws Exception {
        //TODO
//        URI uri = UriComponentsBuilder.newInstance()
//                .path("/attach_filetable/new_text.txt")
//                .build()
//                .toUri();
//        databaseFileTableHandler.readStream(uri);
        assert true;
    }

    @Test
    public void test07ReadBytes() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/new_text.txt")
                .build()
                .toUri();
        byte[] bytes = databaseFileTableHandler.readBytes(uri);

        Assert.assertEquals("file size not same",
                bytes.length,
                3);
    }

    @Test
    public void test08Read() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/new_text.txt")
                .build()
                .toUri();
        FileInfo a = databaseFileTableHandler.read(uri);

        Assert.assertEquals("file size not same",
                a.getSize().intValue(),
                3);
        Assert.assertEquals("ID",
                a.getPath(),
                "/new_text.txt");

        Assert.assertTrue("parent path not contains '\\'",
                a.getParentPath().contains("\\"));
    }

    @Test
    public void test09ReadChild() throws Exception {
        URI uri = UriComponentsBuilder.newInstance()
                .path("/new_dir")
                .build()
                .toUri();
        List<FileInfo> list = databaseFileTableHandler.readChild(uri);

        Assert.assertEquals("file number not same",
                list.size(),
                1);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void test10Delete() throws Exception {
        createBak();
        URI uri = UriComponentsBuilder.newInstance()
                .path("/test.bak")
                .build()
                .toUri();
        boolean result = databaseFileTableHandler.delete(uri);

        Assert.assertTrue("delete file error",
                result);
    }
}