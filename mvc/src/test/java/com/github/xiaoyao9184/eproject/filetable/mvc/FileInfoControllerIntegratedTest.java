package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filetable.*;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
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
                FileTableIntegratedConfiguration.class,
                PropertiesConfiguration.class,
                FileSystemHandlerConfiguration.class,
                DataBaseHandlerConfiguration.class,
                MssqlConfig.class,
                BootMvcConfiguration.class }
)
@TestPropertySource(locations = {
        "classpath:filetable-db.properties",
        "classpath:filetable-operator-file-first.properties"
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileInfoControllerIntegratedTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BaseFileTableProperties baseFileTableProperties;

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


        //disable AutoCreateDirectory
        baseFileTableProperties.setAutoCreateDirectory(false);

        ResponseEntity<FileInfo> res_not_exists = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/file-infos/not_exists/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertEquals(res_not_exists.getStatusCodeValue(), HttpStatus.INTERNAL_SERVER_ERROR.value());

        //enable AutoCreateDirectory
        baseFileTableProperties.setAutoCreateDirectory(true);

        ResponseEntity<FileInfo> res_test_dir_new_txt = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertEquals(res_test_dir_new_txt.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res_test_dir_new_txt.getBody());
        Assert.assertEquals(res_test_dir_new_txt.getBody().getPath(),
                "/" + TestFileTable.TABLE_NAME + "/test_dir/new_txt.txt");
        Assert.assertEquals(res_test_dir_new_txt.getBody().getSize().intValue(), 32);

        //add deep file with auto create dir
        ResponseEntity<FileInfo> res_test_dir_1_2_new_txt = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/1/2/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertEquals(res_test_dir_1_2_new_txt.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res_test_dir_1_2_new_txt.getBody());
        Assert.assertEquals(res_test_dir_1_2_new_txt.getBody().getPath(),
                "/" + TestFileTable.TABLE_NAME + "/test_dir/1/2/new_txt.txt");
        Assert.assertEquals(res_test_dir_1_2_new_txt.getBody().getSize().intValue(), 32);

        //add dir
        ResponseEntity<FileInfo> res_test_dir_3 = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/3",
                null,
                FileInfo.class);

        Assert.assertEquals(res_test_dir_3.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertNotNull(res_test_dir_3.getBody());
        Assert.assertEquals(res_test_dir_3.getBody().getPath(),
                "/" + TestFileTable.TABLE_NAME + "/test_dir/3");
        Assert.assertNull(res_test_dir_3.getBody().getSize());
    }

    @Test
    public void test_02_get() {
        ResponseEntity<FileInfo> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/new_txt.txt",
                FileInfo.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().getPath(),
                "/" + TestFileTable.TABLE_NAME + "/test_dir/new_txt.txt");
        Assert.assertEquals(res1.getBody().getSize().intValue(), 32);
    }

    @Test
    public void test_03_get_child() {
        ResponseEntity<List> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/",
                List.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().size(), 3);

        ResponseEntity<List> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/test_dir/1/",
                List.class);

        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res2.getBody());
        Assert.assertEquals(res2.getBody().size(), 1);
    }

    @Test
    public void test_04_delete() {
        ResponseEntity<Void> res_new_txt = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_new_txt.getBody());
        Assert.assertEquals(res_new_txt.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //
        ResponseEntity<Void> res_test_dir_1_2_new_txt = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/1/2/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir_1_2_new_txt.getBody());
        Assert.assertEquals(res_test_dir_1_2_new_txt.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //
        ResponseEntity<Void> res_test_dir_1_2 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/1/2",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir_1_2.getBody());
        Assert.assertEquals(res_test_dir_1_2.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //
        ResponseEntity<Void> res_test_dir_1 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/1",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir_1.getBody());
        Assert.assertEquals(res_test_dir_1.getStatusCodeValue(), HttpStatus.ACCEPTED.value());


        ResponseEntity<Void> res_test_dir_3_new_txt = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/3/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir_3_new_txt.getBody());
        Assert.assertEquals(res_test_dir_3_new_txt.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //delete dir
        ResponseEntity<Void> res_test_dir_3 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir/3",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir_3.getBody());
        Assert.assertEquals(res_test_dir_3.getStatusCodeValue(), HttpStatus.ACCEPTED.value());

        //delete dir
        ResponseEntity<Void> res_test_dir = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/test_dir",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res_test_dir.getBody());
        Assert.assertEquals(res_test_dir.getStatusCodeValue(), HttpStatus.ACCEPTED.value());
    }

}