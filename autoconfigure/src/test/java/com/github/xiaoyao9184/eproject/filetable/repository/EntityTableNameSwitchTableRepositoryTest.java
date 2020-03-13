package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.SwitchRepositoryConfiguration;
import com.github.xiaoyao9184.eproject.filetable.autoconfigure.FileTableAutoConfiguration;
import com.github.xiaoyao9184.eproject.filetable.core.ThreadLocalEntitySwitchFileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.SampleFileTable;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Table;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                FileTableAutoConfiguration.class,
                SwitchRepositoryConfiguration.class,
                MssqlConfig.class }
)
@TestPropertySource(locations = "classpath:filetable-db.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class EntityTableNameSwitchTableRepositoryTest {

    @Autowired
    ThreadLocalEntitySwitchFileTableNameProvider tableNameProvider;

    @Autowired
    EntityTableNameSwitchTableRepository databaseFileTableHandlerRepository;

    @Autowired
    SampleFileTableRepository sampleFileTableRepository;

    @Autowired
    TestFileTableRepository testFileTableRepository;

    @Autowired
    private BaseFileTableProperties fileTableProperties;


    public void createBak(String tableName, String path){

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
                                tableName + path,
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

    public void deleteBak(String tableName, String path){
        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(fileTableProperties.getServername())) {
            AuthenticationContext ac = new AuthenticationContext(
                    fileTableProperties.getUsername(),
                    fileTableProperties.getPassword().toCharArray(),
                    fileTableProperties.getDomain());
            Session session = connection.authenticate(ac);

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare(fileTableProperties.getInstance())) {
                share.rm(fileTableProperties.getDatabase() + "\\" +
                        tableName + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    public void getChildByRootLocator(){
        List list1 = sampleFileTableRepository.getChildByRootLocator();
        int c = list1.size();
        Assert.assertTrue(c > 0);

        createBak(SampleFileTable.class.getAnnotation(Table.class).name(),"\\test.bak");
        tableNameProvider.set(SampleFileTable.class);
        list1 = databaseFileTableHandlerRepository.getChildByRootLocator();
        Assert.assertEquals(c + 1, list1.size());

        deleteBak(SampleFileTable.class.getAnnotation(Table.class).name(),"\\test.bak");


        List list2 = testFileTableRepository.getChildByRootLocator();
        c = list2.size();
        Assert.assertTrue(c > 0);

        createBak(TestFileTable.class.getAnnotation(Table.class).name(),"\\test.bak");
        tableNameProvider.set(TestFileTable.class);
        list2 = databaseFileTableHandlerRepository.getChildByRootLocator();
        Assert.assertEquals(c + 1, list2.size());

        deleteBak(TestFileTable.class.getAnnotation(Table.class).name(),"\\test.bak");
    }

}
