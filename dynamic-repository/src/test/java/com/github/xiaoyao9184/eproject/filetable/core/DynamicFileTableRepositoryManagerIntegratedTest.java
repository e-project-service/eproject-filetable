package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.AutoCreateRepositoryConfiguration;
import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.config.FileTableDynamicConfiguration;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.repository.TestFileTableRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static com.github.xiaoyao9184.eproject.filetable.util.UUIDUtil.uuidToHierarchyIdNodeString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by xy on 2020/4/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                FileTableDynamicConfiguration.class,
                AutoCreateRepositoryConfiguration.class,
                MssqlConfig.class }
)
@TestPropertySource(locations = "classpath:filetable-db.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class DynamicFileTableRepositoryManagerIntegratedTest extends AbstractTransactionalJUnit4SpringContextTests {


    @Autowired
    EntityManager entityManager;

    @Autowired
    DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager;

    @Autowired
    TestFileTableRepository testFileTableRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void init(){
        //already
//        TestTransaction.start();

        entityManager.createNativeQuery(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='unit_test' and xtype='U') " +
                "CREATE TABLE unit_test AS FILETABLE").executeUpdate();
        entityManager.createNativeQuery(
                "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='unit_test2' and xtype='U') " +
                "CREATE TABLE unit_test2 AS FILETABLE").executeUpdate();
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Autowired
    ApplicationContext context;

    @Test
    public void test_create() throws IllegalAccessException {
        TestTransaction.start();

        testFileTableRepository.getChildByRootLocator();

        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed("UnitTest");

        Assert.assertNotNull(repository);

        //insert data
        String pathLocator = "/" +uuidToHierarchyIdNodeString(UUID.randomUUID());
        repository.insertFileByNameAndPathLocator(pathLocator,"111");

        //find data
        List<AbstractFileTable> list = repository.getChildByRootLocator();
        assertNotNull(list);
        assertEquals(list.size(),1);

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    public void test_create_2() throws IllegalAccessException {
        TestTransaction.start();

        testFileTableRepository.getChildByRootLocator();

        AbstractFileTableRepository<AbstractFileTable,String> repository =
                dynamicFileTableRepositoryManager.getAndInitIfNeed("UnitTest");

        Assert.assertNotNull(repository);

        //insert data
        String pathLocator = "/" +uuidToHierarchyIdNodeString(UUID.randomUUID());
        repository.insertFileByNameAndPathLocator(pathLocator,"111");

        //find data
        List<AbstractFileTable> list = repository.getChildByRootLocator();
        assertNotNull(list);
        assertEquals(list.size(),1);



        AbstractFileTableRepository<AbstractFileTable,String> repository2 =
                dynamicFileTableRepositoryManager.getAndInitIfNeed("UnitTest2");

        Assert.assertNotNull(repository2);

        //insert data
        String pathLocator2 = "/" +uuidToHierarchyIdNodeString(UUID.randomUUID());
        repository2.insertFileByNameAndPathLocator(pathLocator2,"111");

        //find data
        List<AbstractFileTable> list2 = repository2.getChildByRootLocator();
        assertNotNull(list2);
        assertEquals(list2.size(),1);



        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @After
    public void uninit(){
        TestTransaction.start();
        entityManager.createNativeQuery("DROP table unit_test").executeUpdate();
        entityManager.createNativeQuery("DROP table unit_test2").executeUpdate();
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

}
