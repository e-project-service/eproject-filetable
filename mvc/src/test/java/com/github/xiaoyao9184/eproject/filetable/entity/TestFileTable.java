package com.github.xiaoyao9184.eproject.filetable.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by xy on 2020/3/20.
 */
@Entity(name = "test_filetable")
@Table(name="test_filetable")
public class TestFileTable extends AbstractFileTable {

    public static final String TABLE_NAME = "test_filetable";
}
