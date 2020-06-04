package com.github.xiaoyao9184.eproject.filetable.autoconfigure;

import com.github.xiaoyao9184.eproject.filetable.model.TableNameProperties;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Created by xy on 2020/6/1.
 */
@ConfigurationProperties(
        prefix = "eproject.filetable.tablename"
)
public class FileTableNameProperties extends TableNameProperties {

    @PostConstruct
    public void init(){
        if(super.size() == 0){
            super.addAll(Arrays.asList(
                    new TableName(TableNameProviders.zone),
                    new TableName(TableNameProviders.client_id),
                    new TableName(TableNameProviders.username),
                    new TableName(TableNameProviders.name)
            ));
        }
    }

}
