package com.skypapaya.security;

/**
 * 保存当前请求的用户ID（从 token 解析出来），使用 ThreadLocal 方便在各处获取
 */
public class CurrentUserHolder {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void set(Long userId) {
        USER_ID.set(userId);
    }

    public static Long get() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}

