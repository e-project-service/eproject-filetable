package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
        MssqlConfig.class }
)
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class URITest {

    @Test
    @Transactional
    public void url(){
        URI uri = URI.create("//111/22/3/");
        URI uri2 = URI.create("/111/22/3");


        Assert.assertTrue("URI end not '/'",
                uri.toString().endsWith("/"));
        Assert.assertFalse("URI end with '/'",
                uri2.toString().endsWith("/"));
    }

}
