package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringRunner.class)
public class FileInfoControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FileStorage<URI> infoFileStorage;

    @MockBean
    private FileInfoStorage<URI,FileInfo> infoFileInfoStorage;

    @InjectMocks
    private FileInfoController fileInfoController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

//        Cant use configurer because buildContentNegotiationManager is protected
//        ContentNegotiationConfigurer configurer = new ContentNegotiationConfigurer(null);
//        configurer.favorPathExtension(false).
//                favorParameter(false).
//                ignoreAcceptHeader(false).
//                useJaf(false).
//                defaultContentType(MediaType.APPLICATION_JSON);
//        configurer.buildContentNegotiationManager()
        ContentNegotiationManagerFactoryBean bean = new ContentNegotiationManagerFactoryBean();
        bean.setFavorPathExtension(false);
        bean.setFavorParameter(false);
        bean.setIgnoreAcceptHeader(false);
        bean.setUseJaf(false);
        bean.setDefaultContentType(MediaType.APPLICATION_JSON);
        bean.afterPropertiesSet();

        //no need @SpringBootTest
        mockMvc = MockMvcBuilders
                .standaloneSetup(fileInfoController)
                .setContentNegotiationManager(bean.getObject())
                .build();
    }

    @Test
    public void test_add() throws Exception {
        Path tempFile = Files.createTempFile("upload-test-file", ".txt");
        Files.write(tempFile, "some test content...\nline1\nline2".getBytes());

        File file = tempFile.toFile();
        //to upload in-memory bytes use ByteArrayResource instead
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        when(infoFileInfoStorage.storageInfo(any(),eq(URI.create("/new_dir/new_txt.txt"))))
                .thenReturn(fi);

        MockMultipartFile upload = new MockMultipartFile("file",fileSystemResource.getInputStream());
        this.mockMvc.perform(
                MockMvcRequestBuilders.fileUpload("/v1/file-infos/new_dir/new_txt.txt")
                        .file(upload)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.path",is("new_dir/new_txt.txt")));

    }

    @Test
    public void test_delete() throws Exception {
        when(infoFileStorage.delete(eq(URI.create("/new_dir/new_txt.txt"))))
                .thenReturn(true);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/v1/file-infos/new_dir/new_txt.txt")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_get() throws Exception {
        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        when(infoFileInfoStorage.findInfo(eq(URI.create("/new_dir/new_txt.txt"))))
                .thenReturn(fi);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/file-infos/new_dir/new_txt.txt")
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path",is("new_dir/new_txt.txt")));

        List<FileInfo> fis = new ArrayList<>();
        fis.add(fi);
        when(infoFileInfoStorage.listInfo(argThat(new ArgumentMatcher<URI>() {
            @Override
            public boolean matches(Object o) {
                return ((URI)o).getPath().equals("");
            }
        })))
                .thenReturn(fis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/file-infos/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)));

    }

}