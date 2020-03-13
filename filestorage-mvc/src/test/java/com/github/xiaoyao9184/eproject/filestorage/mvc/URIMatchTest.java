package com.github.xiaoyao9184.eproject.filestorage.mvc;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;

/**
 * Created by xy on 2020/1/15.
 */
public class URIMatchTest {

    @Test
    public void failed_try(){

        AntPathMatcher matcher = new AntPathMatcher();

        //(?<=exp)
        Assert.assertFalse(
                matcher.match("/{path:.*(\\/application\\/octet-stream)}/application/octet-stream",
                        "/v1/new_text.txt/application/octet-stream"));
        Assert.assertFalse(
                matcher.match("/{path:.*(\\/application\\/octet-stream)}/application/octet-stream",
                        "/v1/new_text.txt/application/octet-stream/application/octet-stream"));
        
        //dir
        Assert.assertTrue(
                matcher.match("/v1/{path:.*}/application/octet-stream",
                        "/v1/new_text.txt/application/octet-stream"));
        Assert.assertTrue(
                matcher.match("/v1/{path:.*}/application/{type}",
                        "/v1/new_text.txt/application/octet-stream"));
        Assert.assertTrue(
                matcher.match("/v1/{path:.*}/application/{type}",
                        "/v1/new_text.txt/application/pdf"));
        Assert.assertTrue(
                matcher.match("/v1/{path:.*}/image/{type}",
                        "/v1/new_text.txt/image/png"));


        //deep dir
        Assert.assertFalse(
                matcher.match("/v1/{path:.*}/application/octet-stream",
                        "/v1/new_dir/new_text.txt/application/octet-stream"));
    }

    @Test
    public void success_use_prefix(){
        AntPathMatcher matcher = new AntPathMatcher();

        //deep dir
        Assert.assertTrue(
                matcher.match("/v1/application/octet-stream/{path:.*}/**",
                        "/v1/application/octet-stream/new_dir/new_image.bmp"));
        Assert.assertTrue(
                matcher.match("/v1/{type}/{subType}/{path:.*}/**",
                        "/v1/application/octet-stream/new_dir/new_image.bmp"));
        Assert.assertTrue(
                matcher.match("/v1/attachs/{path}/**",
                        "/v1/attachs/new_dir/new_image.bmp"));

        Assert.assertTrue(
                matcher.match("/v1/application/octet-stream/{path:.*}/**",
                        "/v1/application/octet-stream/new_text.txt"));
        Assert.assertTrue(
                matcher.match("/v1/{type}/{subType}/{path:.*}/**",
                        "/v1/application/octet-stream/new_text.txt"));
        Assert.assertTrue(
                matcher.match("/v1/attachs/{path}/**",
                        "/v1/attachs/new_text.txt"));
    }

}
