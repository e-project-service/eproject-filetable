package com.github.xiaoyao9184.eproject.filetable.util;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xy on 2020/3/13.
 */
public class URIUtil {

    public static Tuple2<String,String> separatePathAndNameFromURI(URI uri){
        String[] urls = uri.getPath().split("/");
        List<String> list = new ArrayList<>(Arrays.asList(urls));
        String name = list.remove(urls.length - 1);
        String path = String.join("/", list);

        return Tuple.tuple(path,name);
    }

}
