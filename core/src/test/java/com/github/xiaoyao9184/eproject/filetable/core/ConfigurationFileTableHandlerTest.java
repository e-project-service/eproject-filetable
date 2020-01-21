package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.*;
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

import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.Operator.*;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.DATABASE;
import static com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties.OperatorMethod.FILE;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                PropertiesConfiguration.class }
)
@TestPropertySource(locations = "classpath:filetable-mixing-remote.properties")
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationFileTableHandlerTest {

    @Autowired
    private BaseFileTableProperties baseFileTableProperties;

    @Test
    public void testProperties(){
        Assert.assertNull(
                baseFileTableProperties.getMappingPath());

        Assert.assertEquals(
                baseFileTableProperties.getOperatorMethods().get(CREATE),
                FILE);

        Assert.assertEquals(
                baseFileTableProperties.getOperatorMethods().get(READ_STREAM),
                FILE);

        Assert.assertEquals(
                baseFileTableProperties.getOperatorMethods().get(READ),
                DATABASE);

        Assert.assertEquals(
                baseFileTableProperties.getOperatorMethods().get(DELETE),
                DATABASE);
    }
}