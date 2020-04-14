package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
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
import com.hierynomus.smbj.share.File;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.io.IOException;
import java.sql.SQLException;
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
        MssqlConfig.class }
)
@TestPropertySource(locations = {
        "classpath:filetable.properties"})
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class TestFileTableRepositoryTest {

    @Autowired
    private TestFileTableRepository testFileTableRepository;

    @Value("${project.filetable.rootPath}")
    private String fileTableRootPath;

    @Value("${project.filetable.servername}")
    private String servername;

    @Value("${project.filetable.username}")
    private String username;

    @Value("${project.filetable.password}")
    private String password;

    @Value("${project.filetable.domain}")
    private String domain;

    @Value("${project.filetable.instance}")
    private String instance;

    @Value("${project.filetable.database}")
    private String database;

    private String tableName;

    @Before
    public void getDirNameOfEntity(){
        tableName = TestFileTable.class.getAnnotation(Table.class)
                .name();
    }

    public void createBak(){

        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(servername)) {
            AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domain);
            Session session = connection.authenticate(ac);

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare(instance)) {
                Set<FileAttributes> fileAttributes = new HashSet<>();
                fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
                Set<SMB2CreateOptions> createOptions = new HashSet<>();
                createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);

                if(share.fileExists(database + "\\" + tableName + "\\test.bak")){
                    return;
                }
                File f = share.openFile(  database + "\\" + tableName + "\\test.bak",
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
    @Transactional
    public void getChildByRootLocator(){
        List<TestFileTable> files = testFileTableRepository.getChildByRootLocator();
        Assert.assertTrue("files not exist",
                files.size() > 0);
    }

    @Test
    @Transactional
    public void getChildByPathLocator(){
        List<TestFileTable> files = testFileTableRepository.getChildByPathLocator(
                fileTableRootPath + "\\" + tableName + "\\new_dir");
        Assert.assertTrue("files not exist",
                files.size() > 0);
    }

    @Test
    @Transactional
    public void getByPath() throws SQLException {
        TestFileTable fileTable = testFileTableRepository.getByPath(
                "\\" + tableName + "\\new_text.txt");

        Assert.assertEquals("files not exist",
                fileTable.getName(),
                "new_text.txt");
    }

    @Test
    @Transactional
    public void getBytesByPath() throws SQLException {
        byte[] bytes = testFileTableRepository.getBytesByPath(
                "\\" + tableName + "\\new_text.txt");

        Assert.assertTrue("files not exist",
                bytes.length > 0);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void deleteByPath() throws SQLException {
        createBak();
        Integer result = testFileTableRepository.deleteByPath(
                "\\" + tableName + "\\test.bak");

        Assert.assertTrue("files not exist",
                result > 0);
    }

}
