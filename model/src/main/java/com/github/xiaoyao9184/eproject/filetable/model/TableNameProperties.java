package com.github.xiaoyao9184.eproject.filetable.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy on 2020/6/1.
 */
public class TableNameProperties {

    private List<Mix> mix;

    public List<Mix> getMix() {
        return mix;
    }

    public void setMix(List<Mix> mix) {
        this.mix = mix;
    }

    public static class Mix {

        private TableNameProviders name;

        private List<String> exclusive = new ArrayList<>();

        public Mix(){

        }

        public Mix(TableNameProviders name){
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
        return this.mix
                .stream()
                .filter(tableName -> tableName.getName() == tableNameProviders)
                .findFirst()
                .map(Mix::getExclusive)
                .orElse(null);
    }

}
