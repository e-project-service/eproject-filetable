package com.github.xiaoyao9184.eproject.filetable.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xy on 2020/6/1.
 */
public class TableNameProperties extends ArrayList<TableNameProperties.TableName> {

    public static class TableName {

        private TableNameProviders name;

        private List<String> exclusive = new ArrayList<>();

        public TableName(){

        }

        public TableName(TableNameProviders name){
            this.name = name;
        }

        public TableNameProviders getName() {
            return name;
        }

        public void setName(TableNameProviders name) {
            this.name = name;
        }

        public List<String> getExclusive() {
            return exclusive;
        }

        public void setExclusive(List<String> exclusive) {
            this.exclusive = exclusive;
        }
    }

    public List<String> getExclusive(TableNameProviders tableNameProviders){
        return this
                .stream()
                .filter(tableName -> tableName.getName() == tableNameProviders)
                .findFirst()
                .map(TableNameProperties.TableName::getExclusive)
                .orElse(null);
    }

}
