package com.github.xiaoyao9184.eproject.filetable.zone;

/**
 * Zone name holder in thread
 * Created by xy on 2020/6/1.
 */
public class ZoneHolder {

    private static final ThreadLocal<String> THREADLOCAL = new ThreadLocal<>();

    public static String get() {
        return THREADLOCAL.get();
    }

    public static void set(String zone) {
        THREADLOCAL.set(zone);
    }

    public static void clear() {
        THREADLOCAL.remove();
    }
}
