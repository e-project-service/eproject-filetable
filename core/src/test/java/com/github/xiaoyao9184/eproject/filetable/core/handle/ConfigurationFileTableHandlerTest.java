package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.DataBaseHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.FileSystemHandlerConfiguration;
import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.PropertiesConfiguration;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.MappingLocation.DATABASE;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.Operator.*;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.FILE;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                PropertiesConfiguration.class,
                DataBaseHandlerConfiguration.class,
                FileSystemHandlerConfiguration.class,
                MssqlConfig.class }
)
@TestPropertySource(locations = {
        "classpath:filetable.properties",
        "classpath:mapping-instance-localhost.properties",
        "classpath:config-filesystem.properties"})
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationFileTableHandlerTest {

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Test
    public void testUseFileSystem(){
        Assert.assertNotNull(
                fileTableProperties.getMappingPath());

        Assert.assertEquals(
                fileTableProperties.getOperatorMethods().get(CREATE),
                FILE);

        Assert.assertEquals(
                fileTableProperties.getOperatorMethods().get(READ_STREAM),
                FILE);

        Assert.assertEquals(
                fileTableProperties.getOperatorMethods().get(READ),
                DATABASE);

        Assert.assertEquals(
                fileTableProperties.getOperatorMethods().get(DELETE),
                DATABASE);
    }
}