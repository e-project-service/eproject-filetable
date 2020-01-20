package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filetable.*;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
@TestPropertySource(locations = "classpath:mixing-localhost.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileInfoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test_01_add() throws IOException {
        Path tempFile = Files.createTempFile("upload-test-file", ".txt");
        Files.write(tempFile, "some test content...\nline1\nline2".getBytes());

        File file = tempFile.toFile();
        //to upload in-memory bytes use ByteArrayResource instead
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        ResponseEntity<FileInfo> res1 = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/files/new_dir/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().getPath(), "/" + TestFileTable.TABLE_NAME + "/new_dir/new_txt.txt");
        Assert.assertEquals(res1.getBody().getSize().intValue(), 32);

        //add deep file with auto create dir
        ResponseEntity<FileInfo> res2 = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/files/new_dir/2/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res2.getBody());
        Assert.assertEquals(res2.getBody().getPath(), "/" + TestFileTable.TABLE_NAME + "/new_dir/2/new_txt.txt");
        Assert.assertEquals(res2.getBody().getSize().intValue(), 32);

        //add dir
        ResponseEntity<FileInfo> res3 = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/files/new_dir/3",
                null,
                FileInfo.class);

        Assert.assertEquals(res3.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res3.getBody());
        Assert.assertEquals(res3.getBody().getPath(), "/" + TestFileTable.TABLE_NAME + "/new_dir/3");
        Assert.assertNull(res3.getBody().getSize());
    }

    @Test
    public void test_02_delete() {
        ResponseEntity<Void> res1 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/files/new_dir/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //
        ResponseEntity<Void> res2 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/files/new_dir/2/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res2.getBody());
        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //
        ResponseEntity<Void> res3 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/files/new_dir/2",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res3.getBody());
        Assert.assertEquals(res3.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //delete dir
        ResponseEntity<Void> res4 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/files/new_dir/3",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res4.getBody());
        Assert.assertEquals(res4.getStatusCodeValue(), HttpStatus.ACCEPTED.value());
    }

    @Test
    public void test_03_get() {
        ResponseEntity<FileInfo> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/files/new_dir/new_image.bmp",
                FileInfo.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().getPath(), "/new_dir/new_image.bmp");
        Assert.assertEquals(res1.getBody().getSize().intValue(), 0);

        ResponseEntity<FileInfo> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/files/new_text.txt",
                FileInfo.class);

        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res2.getBody());
        Assert.assertEquals(res2.getBody().getPath(), "/new_text.txt");
        Assert.assertEquals(res2.getBody().getSize().intValue(), 3);
    }

    @Test
    public void test_04_get_child() {
        ResponseEntity<List> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/files/new_dir/",
                List.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().size(), 1);

        ResponseEntity<List> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/files/",
                List.class);

        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res2.getBody());
        Assert.assertTrue(res2.getBody().size() >= 3);
    }

}