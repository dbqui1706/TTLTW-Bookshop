package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.PermissionDao;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionService {
    private final PermissionDao permissionDao;
    // Cache quyền để hạn chế việc truy cập cơ sở dữ liệu
    private Map<Long, List<String>> userPermissionsCache = new ConcurrentHashMap<>();

    public PermissionService() {
        this.permissionDao = new PermissionDao("permissions");
    }

    public boolean hasPermission(Long userId, String permissionCode) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode);
    }

    public List<String> getUserPermissions(Long userId) {
        // Kiểm tra cache trước
        if (userPermissionsCache.containsKey(userId)) {
            return userPermissionsCache.get(userId);
        }

        // Nếu không có trong cache, lấy từ DB
        try {
            List<String> permissions = permissionDao.getUserPermissionCodes(userId);
            userPermissionsCache.put(userId, permissions);
            return permissions;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Xóa cache quyền của người dùng
     *
     * @param userId ID người dùng
     */
    public void clearPermissionCache(Long userId) {
        userPermissionsCache.remove(userId);
    }

    /**
     * Xóa toàn bộ quyền trong cache
     */
    public void clearAllPermissionCache() {
        userPermissionsCache.clear();
    }
}
