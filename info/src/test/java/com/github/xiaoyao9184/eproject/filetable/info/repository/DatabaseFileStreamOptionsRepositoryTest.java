package com.github.xiaoyao9184.eproject.filetable.info.repository;

import com.github.xiaoyao9184.eproject.filetable.info.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.info.entity.DatabaseFileStreamOptions;
import com.github.xiaoyao9184.eproject.filetable.info.entity.ServerInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xy on 2020/6/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
        MssqlConfig.class }
)
@TestPropertySource(locations = {
        "classpath:db-mssql.properties"})
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class DatabaseFileStreamOptionsRepositoryTest {

    @Autowired
    private DatabaseFileStreamOptionsRepository fileTableInfoRepository;

    @Test
    @Transactional
    public void findByDatabaseName(){
        List<DatabaseFileStreamOptions> options =
                fileTableInfoRepository.findByDatabaseName("eProjectFileTable");
        Assert.assertNotNull(options.get(0));
        Assert.assertNotNull(options.get(0).getDatabaseId());
        Assert.assertNotNull(options.get(0).getDatabaseName());
        Assert.assertNotNull(options.get(0).getDirectoryName());
        Assert.assertNotNull(options.get(0).getNonTransactedAccess());
        Assert.assertNotNull(options.get(0).getNonTransactedAccessDesc());
    }

}
