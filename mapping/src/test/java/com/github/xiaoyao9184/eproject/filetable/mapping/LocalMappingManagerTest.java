package com.github.xiaoyao9184.eproject.filetable.mapping;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xy on 2020/9/6.
 */
public class LocalMappingManagerTest {

    @Test
    public void testMappingAndUnMapping(){
        LocalMappingManager manager = new LocalMappingManager();

        boolean ret = manager.mapping("X:",
                "\\\\db.xy.com\\MSSQLSERVER",
                "administrator","XY@dev1024");

        assert ret;

        ret = manager.unmapping("X:");
        assert ret;
    }


    @Test
    public void testGetAvailableDeviceLetter(){
        LocalMappingManager manager = new LocalMappingManager();
        char letter1 = manager.getAvailableDeviceLetter();

        boolean ret = manager.mapping(letter1,
                "\\\\db.xy.com\\MSSQLSERVER",
                "administrator","Newhero123");
        assert ret;

        char letter2 = manager.getAvailableDeviceLetter();
        assert letter2 != letter1;

        ret = manager.mapping(letter2 + ":",
                "\\\\db.xy.com\\MSSQLSERVER",
                "administrator","Newhero123");
        assert ret;

        char letter3 = manager.getAvailableDeviceLetter();
        assert letter3 != letter2;

        ret = manager.unmapping(letter2 + ":");
        assert ret;

        ret = manager.unmapping(letter1);
        assert ret;
    }
}