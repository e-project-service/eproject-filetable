package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filetable.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
                PropertiesConfiguration.class,
                FileSystemHandlerConfiguration.class,
                DataBaseHandlerConfiguration.class,
                MssqlConfig.class,
                BootMvcConfiguration.class }
)
@TestPropertySource(locations = "classpath:filetable-mixing-localhost.properties")
public class MimeStreamControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_mime_application_octet_stream() {
        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/octet-stream/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<byte[]> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/octet-stream/new_text.txt",
                byte[].class);

        Assert.assertEquals(3, res2.getBody().length);
        Assert.assertEquals(res2.getHeaders().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    public void test_mime_application() {
        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/pdf/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.APPLICATION_PDF);

        ResponseEntity<byte[]> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/pdf/new_text.txt",
                byte[].class);

        Assert.assertEquals(3, res2.getBody().length);
        Assert.assertEquals(res2.getHeaders().getContentType(), MediaType.APPLICATION_PDF);
    }

    @Test
    public void test_mime_image() {
        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/image/png/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.IMAGE_PNG);

        ResponseEntity<byte[]> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/image/png/new_text.txt",
                byte[].class);

        Assert.assertEquals(3, res2.getBody().length);
        Assert.assertEquals(res2.getHeaders().getContentType(), MediaType.IMAGE_PNG);
    }

    @Test
    public void test_mime_any() {
        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/text/html/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.TEXT_HTML);

        ResponseEntity<byte[]> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/text/html/new_text.txt",
                byte[].class);

        Assert.assertEquals(3, res2.getBody().length);
        Assert.assertEquals(res2.getHeaders().getContentType(), MediaType.TEXT_HTML);
    }

}