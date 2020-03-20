package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.MultipartFilePointer;
import com.github.xiaoyao9184.eproject.filetable.*;
import com.github.xiaoyao9184.eproject.filetable.entity.TestFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
                BootMvcConfiguration.class }
)
public class FileInfoControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FileStorage<URI> infoFileStorage;

    @MockBean
    private FileInfoStorage<URI,FileInfo> infoFileInfoStorage;

    @Test
    public void test_add() throws IOException {
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

        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        when(infoFileInfoStorage.storageInfo(any(),any()))
                .thenReturn(fi);

        ResponseEntity<FileInfo> res1 = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/file-infos/new_dir/new_txt.txt",
                requestEntity,
                FileInfo.class);

        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.CREATED.value());
        Assert.assertEquals(res1.getBody().getPath(), fi.getPath());
    }

    @Test
    public void test_delete() {
        when(infoFileStorage.delete(URI.create("new_dir/new_txt.txt")))
                .thenReturn(false);

        ResponseEntity<Void> res1 = this.restTemplate.exchange(
                "http://localhost:" + port + "/v1/file-infos/new_dir/new_txt.txt",
                HttpMethod.DELETE,
                null,
                Void.class);

        Assert.assertNull(res1.getBody());
        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.ACCEPTED.value());
    }

    @Test
    public void test_get() {
        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        when(infoFileInfoStorage.findInfo(argThat(new ArgumentMatcher<URI>() {
            @Override
            public boolean matches(Object o) {
                return ((URI)o).getPath().equals("/" + fi.getPath());
            }
        })))
                .thenReturn(fi);

        ResponseEntity<FileInfo> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/new_dir/new_txt.txt",
                FileInfo.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().getPath(), fi.getPath());


        List<FileInfo> fis = new ArrayList<>();
        fis.add(fi);
        when(infoFileInfoStorage.listInfo(argThat(new ArgumentMatcher<URI>() {
            @Override
            public boolean matches(Object o) {
                return ((URI)o).getPath().equals("");
            }
        })))
                .thenReturn(fis);

        ResponseEntity<List> res2 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/",
                List.class);

        Assert.assertEquals(res2.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res2.getBody());
        Assert.assertEquals(res2.getBody().size(), 1);
    }

}