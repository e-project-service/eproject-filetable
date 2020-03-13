package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

/**
 * Created by xy on 2020/1/15.
 */
public interface AbstractFileTableRepository<T extends AbstractFileTable,ID extends Serializable>
        extends JpaRepository<T,ID>, FileTableRepository<T> {

}
