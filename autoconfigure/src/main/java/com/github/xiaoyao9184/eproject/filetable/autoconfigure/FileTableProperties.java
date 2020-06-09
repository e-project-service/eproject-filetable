package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by xy on 2020/1/15.
 */
@ConfigurationProperties(
        prefix = "eproject.filetable"
)
public class FileTableProperties extends BaseFileTableProperties {

    private List<String> internalHostnames;

    public List<String> getInternalHostnames() {
        return internalHostnames;
    }

    public void setInternalHostnames(List<String> internalHostnames) {
        this.internalHostnames = internalHostnames;
    }


}
