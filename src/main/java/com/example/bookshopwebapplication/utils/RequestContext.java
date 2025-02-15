package com.example.bookshopwebapplication.utils;

public class RequestContext {
    public static final ThreadLocal<String> ipAddress = new ThreadLocal<>();
    public static final ThreadLocal<Long> userId = new ThreadLocal<>();
    public static final ThreadLocal<String> username = new ThreadLocal<>();

    public static void setIpAddress(String ip) {
        ipAddress.set(ip);
    }

    public static String getIpAddress() {
        return ipAddress.get();
    }

    public static void setUserId(Long id) {
        userId.set(id);
    }

    public static Long getUserId() {
        return userId.get();
    }

    public static void setUsername(String name) {
        username.set(name);
    }

    public static String getUsername() {
        return username.get();
    }

    public static void clear() {
        ipAddress.remove();
        userId.remove();
        username.remove();
    }
}
