package com.github.xiaoyao9184.eproject.filetable.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                FileTableNameProviderTest.Configuration.class }
)
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class FileTableNameProviderTest {

    public static class Configuration {
        @Bean
        public FileTableNameProvider tableNameProvider(){
            return new FileTableNameProvider() {
                @Override
                public String provide() {
                    return "test_filetable";
                }
            };
        }

    }

    @Autowired
    private FileTableNameProvider provider;

    @Test
    public void test(){
        Assert.assertTrue("table name not same",
                provider.provide().endsWith("test_filetable"));

    }
}