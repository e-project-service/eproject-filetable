package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.MssqlConfig;
import com.github.xiaoyao9184.eproject.filetable.entity.SampleFileTable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by xy on 2020/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
        MssqlConfig.class }
)
@ComponentScan("com.github.xiaoyao9184.eproject.filetable")
public class SampleFileTableRepositoryTest {

    @Autowired
    private SampleFileTableRepository sampleFileTableRepository;

    @Value("${eproject.filetable.share.rootPath}")
    private String fileTableRootPath;

    private String tableName;

    @Before
    public void getDirNameOfEntity(){
        tableName = SampleFileTable.class.getAnnotation(Table.class)
                .name();
    }

    @Test
    @Transactional
    public void getChildByPath(){
        List<SampleFileTable> files = sampleFileTableRepository.getChildByPath(
                fileTableRootPath + "\\" + tableName + "\\new_dir");
        Assert.assertTrue("files not exist",
                files.size() > 0);
    }

    @Test
    @Transactional
    public void getChildByRoot(){
        List<SampleFileTable> files = sampleFileTableRepository.getChildByRoot();
        Assert.assertTrue("files not exist",
                files.size() > 0);
    }

}
