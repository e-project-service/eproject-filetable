package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filetable.BootMvcConfiguration;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
public class FileInfoSearchControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FileInfoStorage<URI,FileInfo> infoFileInfoStorage;

    @Test
    public void test_search() throws IOException {
        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        List<FileInfo> fis = new ArrayList<>();
        fis.add(fi);
        when(infoFileInfoStorage.searchInfo(any(),
                eq("new")))
                .thenReturn(fis);

        ResponseEntity<List> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/file-infos/?search=new",
                List.class);

        Assert.assertEquals(res1.getStatusCodeValue(), HttpStatus.OK.value());
        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().size(), 1);
    }

}