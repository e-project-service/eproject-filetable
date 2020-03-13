package com.github.xiaoyao9184.eproject.filetable.core.mapping;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by xy on 2020/3/13.
 */
public class FileTableLocalhostMappingBuilderTest {

    @Test
    public void testMappingServer(){
        String url = FileTableLocalhostMappingBuilder.newInstance()
                .location(BaseFileTableProperties.MappingLocation.SERVER)
                .server("127.0.0.1")
                .instance("MSSQLSERVER")
                .database("unit_test_table")
                .root("X:/")
                .build()
                .toUriString();

        assertEquals("",
                url,
                "file:///X:/MSSQLSERVER/unit_test_table");
    }

    @Test
    public void testMappingInstance(){
        String url = FileTableLocalhostMappingBuilder.newInstance()
                .location(BaseFileTableProperties.MappingLocation.INSTANCE)
                .server("127.0.0.1")
                .instance("MSSQLSERVER")
                .database("unit_test_table")
                .root("X:/")
                .build()
                .toUriString();

        assertEquals("",
                url,
                "file:///X:/unit_test_table");
    }

    @Test
    public void testMappingDatabase(){
        String url = FileTableLocalhostMappingBuilder.newInstance()
                .location(BaseFileTableProperties.MappingLocation.DATABASE)
                .server("127.0.0.1")
                .instance("MSSQLSERVER")
                .database("unit_test_table")
                .root("X:/")
                .build()
                .toUriString();

        assertEquals("",
                url,
                "file:///X:");
    }
}