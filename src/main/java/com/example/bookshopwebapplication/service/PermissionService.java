package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.PermissionDao;
import com.example.bookshopwebapplication.entities.Permission;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionService {
    private final PermissionDao permissionDao;

    // Cache permissions for performance
    private Map<String, Permission> permissionCache = new HashMap<>();
    private Map<Long, Set<String>> userPermissionCache = new HashMap<>();

    // Singleton instance
    private static PermissionService instance;

    private PermissionService() {
        this.permissionDao = new PermissionDao();
        loadPermissions();
    }

    public static synchronized PermissionService getInstance() {
        if (instance == null) {
            instance = new PermissionService();
        }
        return instance;
    }

    /**
     * Load permissions from the database into the cache.
     */
    private void loadPermissions(){
        permissionDao.loadPermissions(permissionCache);
    }

    /**
     * Kiểm tra xem người dùng có quyền cụ thể hay không.
     * @param userId ID của người dùng
     * @param permissionCode Mã quyền cần kiểm tra
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        // Kiểm tra xem người dùng đã có quyền trong cache chưa
        if (!userPermissionCache.containsKey(userId)) {
            loadUserPermissions(userId);
        }

        // Kiểm tra quyền trong cache
        return userPermissionCache.getOrDefault(userId, Collections.emptySet())
                .contains(permissionCode);
    }

    private void loadUserPermissions(Long userId) {
        Set<String> permissions = new HashSet<>();
        permissionDao.loadPermissions(userId, permissions);
        // Cache the permissions
        userPermissionCache.put(userId, permissions);
    }

    public void clearUserCache(Long userId) {
        userPermissionCache.remove(userId);
    }

    public void clearAllCache() {
        userPermissionCache.clear();
    }
}
