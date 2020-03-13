package com.github.xiaoyao9184.eproject.filetable.core.mapping;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTablePathBuilderTest {

    @Test
    public void test(){
        String fileNamespacePath = FileTablePathBuilder.newInstance()
                .uri()
                .path("/11")
                .path("/22")
                .and()
                .build()
                .toWinString();

        assertEquals("",
                fileNamespacePath,
                "\\11\\22");
    }
}