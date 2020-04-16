package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by xy on 2020/1/15.
 */
@RunWith(SpringRunner.class)
public class FileInfoSearchControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FileInfoStorage<URI,FileInfo> infoFileInfoStorage;

    @InjectMocks
    private FileInfoSearchController fileInfoSearchController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ContentNegotiationManagerFactoryBean bean = new ContentNegotiationManagerFactoryBean();
        bean.setFavorPathExtension(false);
        bean.setFavorParameter(false);
        bean.setIgnoreAcceptHeader(false);
        bean.setUseJaf(false);
        bean.setDefaultContentType(MediaType.APPLICATION_JSON);
        bean.afterPropertiesSet();

        //no need @SpringBootTest
        mockMvc = MockMvcBuilders
                .standaloneSetup(fileInfoSearchController)
                .setContentNegotiationManager(bean.getObject())
                .build();
    }

    @Test
    public void test_search() throws Exception {
        FileInfo fi = new FileInfo();
        fi.setPath("new_dir/new_txt.txt");
        List<FileInfo> fis = new ArrayList<>();
        fis.add(fi);
        when(infoFileInfoStorage.searchInfo(any(),
                eq("new")))
                .thenReturn(fis);

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/v1/file-infos/?")
                        .param("search","new")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)));
    }

}