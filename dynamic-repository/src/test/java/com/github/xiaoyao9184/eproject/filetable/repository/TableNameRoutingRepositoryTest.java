package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by xy on 2020/4/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = { }
)
public class TableNameRoutingRepositoryTest {

    @MockBean
    DynamicFileTableRepositoryManager dynamicFileTableRepositoryManager;

    @MockBean
    TestFileTableRepository testFileTableRepository;

    @MockBean
    DefaultFileTableRepository defaultFileTableRepository;


    public class TestFileTableNameProvider implements FileTableNameProvider{
        private String name;

        @Override
        public String provide() {
            return name;
        }

        public void set(String name){
            this.name = name;
        }
    }

    @Test
    public void getChildByRootLocator(){
        TestFileTableNameProvider fileTableNameProvider = new TestFileTableNameProvider();

        doReturn(testFileTableRepository)
                .when(dynamicFileTableRepositoryManager).getAndInitIfNeed(eq("test"));
        doReturn(defaultFileTableRepository)
                .when(dynamicFileTableRepositoryManager).getAndInitIfNeed(eq("default"));

        TableNameRoutingRepository repository = new TableNameRoutingRepository(
                fileTableNameProvider,
                dynamicFileTableRepositoryManager,
                Collections.emptyMap()
        );


        doReturn(null)
                .when(testFileTableRepository).getChildByRootLocator();
        doReturn(null)
                .when(defaultFileTableRepository).getChildByRootLocator();

        fileTableNameProvider.set("test");
        repository.getChildByRootLocator();

        fileTableNameProvider.set("default");
        repository.getChildByRootLocator();


        verify(dynamicFileTableRepositoryManager, times(2)).getAndInitIfNeed(any());
        verify(testFileTableRepository, times(1)).getChildByRootLocator();
        verify(defaultFileTableRepository, times(1)).getChildByRootLocator();
    }

}
