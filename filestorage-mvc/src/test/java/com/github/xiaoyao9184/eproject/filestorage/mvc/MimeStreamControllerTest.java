package com.github.xiaoyao9184.eproject.filestorage.mvc;

import com.github.xiaoyao9184.eproject.filestorage.DisableSpringDefaultContentNegotiationConfiguration;
import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        classes = {
                DisableSpringDefaultContentNegotiationConfiguration.class }
)
public class MimeStreamControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private FileStorage<URI> storage;

    private FilePointer filePointer;

    @Before
    public void initFile(){
        filePointer = new FilePointer() {
            @Override
            public InputStream open() throws Exception {
                return new ByteArrayInputStream(new byte[]{0x0D});
            }

            @Override
            public long getSize() {
                return 1;
            }

            @Override
            public String getOriginalName() {
                return "unit_test_file";
            }

            @Override
            public String getEtag() {
                return "null";
            }

            @Override
            public Optional<com.google.common.net.MediaType> getMediaType() {
                return Optional.empty();
            }

            @Override
            public Instant getLastModified() {
                return Instant.now();
            }
        };
    }


    @Test
    public void test_mime_application_octet_stream() {
        when(storage.findFile(URI.create("/new_dir/new_image.bmp")))
                .thenReturn(Optional.of(this.filePointer));

        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/octet-stream/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().length,1);
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    public void test_mime_application() {
        when(storage.findFile(URI.create("/new_dir/new_image.bmp")))
                .thenReturn(Optional.of(this.filePointer));

        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/application/pdf/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().length,1);
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.APPLICATION_PDF);
    }

    @Test
    public void test_mime_image() {
        when(storage.findFile(URI.create("/new_dir/new_image.bmp")))
                .thenReturn(Optional.of(this.filePointer));

        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/image/png/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().length,1);
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.IMAGE_PNG);
    }

    @Test
    public void test_mime_any() {
        when(storage.findFile(URI.create("/new_dir/new_image.bmp")))
                .thenReturn(Optional.of(this.filePointer));

        ResponseEntity<byte[]> res1 = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/v1/mimes/text/html/new_dir/new_image.bmp",
                byte[].class);

        Assert.assertNotNull(res1.getBody());
        Assert.assertEquals(res1.getBody().length,1);
        Assert.assertEquals(res1.getHeaders().getContentType(), MediaType.TEXT_HTML);
    }
}