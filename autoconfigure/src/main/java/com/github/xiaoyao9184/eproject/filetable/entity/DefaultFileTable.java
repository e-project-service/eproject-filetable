package com.github.xiaoyao9184.eproject.filetable.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xy on 2020/1/16.
 */
@Entity(name = "default")
@Table(name = "default")
public class DefaultFileTable extends AbstractFileTable {

    public static String TABLE_NAME = "default";
}
