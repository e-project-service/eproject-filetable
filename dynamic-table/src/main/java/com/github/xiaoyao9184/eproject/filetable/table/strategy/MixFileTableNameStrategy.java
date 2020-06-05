package com.github.xiaoyao9184.eproject.filetable.table.strategy;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import org.springframework.core.Ordered;

import java.util.stream.Stream;

/**
 * Mix provider {@link com.github.xiaoyao9184.eproject.filetable.table.MixFileTableNameProvider}
 * need strategies to accomplish mix
 * Created by xy on 2020/6/1.
 */
public interface MixFileTableNameStrategy extends Ordered {

    /**
     * Determine compliance with this strategy
     * @param providers provider stream
     * @return apply flag
     */
    boolean should(Stream<FileTableNameProvider> providers);

    /**
     * Apply this strategy
     * @param providers provider stream
     * @return table name
     */
    String apply(Stream<FileTableNameProvider> providers);
}
