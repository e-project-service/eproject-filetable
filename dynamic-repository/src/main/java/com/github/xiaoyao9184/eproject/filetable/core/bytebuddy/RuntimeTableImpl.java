package com.github.xiaoyao9184.eproject.filetable.core.bytebuddy;

import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.lang.annotation.Annotation;

/**
 * Created by xy on 2020/4/18.
 */
public class RuntimeTableImpl implements Table {

    public RuntimeTableImpl(String name) {
        this.name = name;
    }

    public RuntimeTableImpl(String name, String catalog, String schema, UniqueConstraint[] uniqueConstraints, Index[] indexes) {
        this.name = name;
        this.catalog = catalog;
        this.schema = schema;
        this.uniqueConstraints = uniqueConstraints;
        this.indexes = indexes;
    }

    private String name = "";

    private String catalog = "";

    private String schema = "";

    private UniqueConstraint[] uniqueConstraints = {};

    private Index[] indexes = {};

    @Override
    public Class<? extends Annotation> annotationType() {
        return Table.class;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String catalog() {
        return this.catalog;
    }

    @Override
    public String schema() {
        return this.schema;
    }

    @Override
    public UniqueConstraint[] uniqueConstraints() {
        return this.uniqueConstraints;
    }

    @Override
    public Index[] indexes() {
        return this.indexes;
    }
}

