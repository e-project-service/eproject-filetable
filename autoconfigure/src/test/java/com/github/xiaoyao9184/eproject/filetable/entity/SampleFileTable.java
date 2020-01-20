package com.github.xiaoyao9184.eproject.filetable.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by xy on 2020/1/16.
 */
@Entity(name = "sample_filetable")
@Table(name = "sample_filetable")
public class SampleFileTable extends AbstractFileTable {

    public static String TABLE_NAME = "sample_filetable";
}
