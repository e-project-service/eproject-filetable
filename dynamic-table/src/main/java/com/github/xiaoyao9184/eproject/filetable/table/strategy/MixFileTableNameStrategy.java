package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import org.springframework.core.Ordered;

import java.util.stream.Stream;

/**
 * Created by xy on 2020/6/1.
 */
public interface MixFileTableNameStrategy extends Ordered {
    boolean should(Stream<FileTableNameProvider> providers);

    String apply(Stream<FileTableNameProvider> providers);
}
