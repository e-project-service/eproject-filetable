package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
