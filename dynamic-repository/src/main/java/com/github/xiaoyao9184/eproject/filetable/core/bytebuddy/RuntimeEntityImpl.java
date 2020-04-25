package com.github.xiaoyao9184.eproject.filetable.core.bytebuddy;

import javax.persistence.Entity;
import java.lang.annotation.Annotation;

/**
 * Created by xy on 2020/4/18.
 */
public class RuntimeEntityImpl implements Entity {

    public RuntimeEntityImpl(String name){
        this.name = name;
    }

    private String name;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Entity.class;
    }

    @Override
    public String name() {
        return name;
    }
}
