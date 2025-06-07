package com.sct.context;

public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static ThreadLocal<String> threadLocalUsername = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

    public static void setUsername(String username) {
        threadLocalUsername.set(username);
    }

    public static String getUsername() {
        return threadLocalUsername.get();
    }

    public static void removeUsername() {
        threadLocalUsername.remove();
    }

}
