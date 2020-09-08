package com.github.xiaoyao9184.eproject.filetable.mapping;

import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Mpr;
import com.sun.jna.platform.win32.Winnetwk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sun.jna.platform.win32.WinBase.DRIVE_NO_ROOT_DIR;
import static com.sun.jna.platform.win32.WinError.NO_ERROR;
import static com.sun.jna.platform.win32.Winnetwk.ConnectFlag.CONNECT_UPDATE_PROFILE;
import static com.sun.jna.platform.win32.Winnetwk.RESOURCETYPE.RESOURCETYPE_DISK;

/**
 * Created by xy on 2020/9/6.
 */
public class LocalMappingManager {

    private static final Logger logger = LoggerFactory.getLogger(LocalMappingManager.class);

    private Mpr INSTANCE;

    private List<String> devices;


    public LocalMappingManager(){
        INSTANCE = Mpr.INSTANCE;
        this.devices = new ArrayList<>();
    }

    public boolean mapping(String localName, String remoteName, String username, String password){
        Winnetwk.NETRESOURCE lpNetResource = new Winnetwk.NETRESOURCE();
//        lpNetResource.dwScope = 0;
        lpNetResource.dwType = RESOURCETYPE_DISK;
//        lpNetResource.dwDisplayType = NETRESOURCE.RESOURCEDISPLAYTYPE_SHARE;
//        lpNetResource.dwUsage = NETRESOURCE.RESOURCEUSAGE_CONNECTABLE;
        lpNetResource.lpLocalName = localName;
        lpNetResource.lpRemoteName = remoteName;
//        lpNetResource.lpComment = null;
//        lpNetResource.lpProvider = null;


        int result = INSTANCE.WNetAddConnection3(null,lpNetResource,password,username, CONNECT_UPDATE_PROFILE);

        logger.debug("Mounting Windows Share {} -> {} result code {}.",
                remoteName, localName, result);

        if(NO_ERROR == result){
            this.devices.add(localName);
            return true;
        }else{
            logger.warn("Mounting Windows Share error {} -> {} result code {}!",
                    remoteName, localName, result);
            return false;
        }
    }

    public boolean unmapping(String localName){
        int result = INSTANCE.WNetCancelConnection2(localName,CONNECT_UPDATE_PROFILE,true);

        logger.debug("UnMounting Windows Share {} result code {}.",
                localName, result);

        if(NO_ERROR == result){
            this.devices.remove(localName);
            return true;
        }else{
            logger.warn("Mounting Windows Share error {} result code {}!",
                    localName, result);
            return false;
        }
    }

    public boolean mapping(char deviceLetter, String remoteName, String username, String password){
        return this.mapping(deviceLetter + ":", remoteName, username, password);
    }

    public boolean unmapping(char deviceLetter){
        return this.unmapping(deviceLetter + ":");
    }




    public char getAvailableDeviceLetter() {
        char z = 'Z';

        while(isDeviceExist(z)){
            z--;
            if(z <= 'C'){
                throw new RuntimeException("No idle available device letter");
            }
        }
        return z;
    }

    public boolean isDeviceExist(char deviceLetter){
        String deviceRoot = deviceLetter + ":";
        if(!this.devices.contains(deviceRoot)){
            int type = Kernel32Util.getDriveType(deviceRoot);
            return DRIVE_NO_ROOT_DIR != type;
        }
        return true;
    }
}
