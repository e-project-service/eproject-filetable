package com.github.xiaoyao9184.eproject.filetable.model;

/**
 * Created by xy on 2020/9/6.
 */
public class MappingProperties {

    private Boolean enable = true;
    private Character device;
    private MappingLocation location = MappingLocation.DATABASE;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Character getDevice() {
        return device;
    }

    public void setDevice(Character device) {
        this.device = device;
    }

    public MappingLocation getLocation() {
        return location;
    }

    public void setLocation(MappingLocation location) {
        this.location = location;
    }

    public enum MappingLocation {

        /**
         * Windows cant mapping server only use ComputerName
         * See <a href="https://docs.microsoft.com/en-us/previous-versions/windows/it-pro/windows-server-2012-r2-and-2012/gg651155(v=ws.11)">Net Use</a>
         */
        @Deprecated
        SERVER,

        /**
         * Currently this project does not support managing multiple instances, multiple databases,
         * This project can still run with mapping instance level directory to the local.
         */
        INSTANCE,

        /**
         * Recommend
         */
        DATABASE,

        /**
         * NOT Recommend
         */
        TABLE
    }

}
