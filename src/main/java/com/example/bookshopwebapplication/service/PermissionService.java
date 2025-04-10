package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.PermissionDao;
import com.example.bookshopwebapplication.entities.Permission;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionService {
    private final PermissionDao permissionDao;

    // Cache permissions for performance
    private Map<String, Permission> permissionCache = new ConcurrentHashMap<>();
    private Map<Long, Set<String>> userPermissionCache = new ConcurrentHashMap<>();

    // Singleton instance
    private static PermissionService instance;

    public PermissionService() {
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

    /**
     * Get all permissions in the system
     * @return List of all permissions
     */
    public List<Permission> getAllPermissions() {
        return permissionDao.findAll();
    }

    /**
     * Get permissions by module
     * @param module Module name
     * @return List of permissions for the given module
     */
    public List<Permission> getPermissionsByModule(String module) {
        return permissionDao.findByModule(module);
    }

    /**
     * Get all modules in the system
     * @return Set of module names
     */
    public Set<String> getAllModules() {
        return permissionDao.findAllModules();
    }

    /**
     * Get a permission by its ID
     * @param id Permission ID
     * @return Optional containing the permission if found, empty otherwise
     */
    public Optional<Permission> getPermissionById(Long id) {
        return permissionDao.findById(id);
    }

    /**
     * Get a permission by its code
     * @param code Permission code
     * @return Optional containing the permission if found, empty otherwise
     */
    public Optional<Permission> getPermissionByCode(String code) {
        return permissionDao.findByCode(code);
    }

    /**
     * Create a new permission
     * @param Permission Permission data
     * @return Optional containing the created permission with ID, empty if failed
     */
    public Optional<Permission> createPermission(Permission Permission) {
        Long id = permissionDao.save(Permission);
        if (id != null) {
            Permission.setId(id);
            return Optional.of(Permission);
        }
        return Optional.empty();
    }

    /**
     * Update an existing permission
     * @param Permission Permission data with updated fields
     * @return Optional containing the updated permission, empty if failed
     */
    public Optional<Permission> updatePermission(Permission Permission) {
        if (permissionDao.update(Permission)) {
            // Clear all permission caches since permission details changed
            clearAllCache();
            return permissionDao.findById(Permission.getId());
        }
        return Optional.empty();
    }

    /**
     * Delete a permission by ID
     * @param id Permission ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePermission(Long id) {
        boolean result = permissionDao.delete(id);
        if (result) {
            // Clear all permission caches since permissions changed
            clearAllCache();
        }
        return result;
    }

    /**
     * Check if a permission with the given ID exists
     * @param id Permission ID
     * @return true if exists, false otherwise
     */
    public boolean isPermissionExists(Long id) {
        return permissionDao.existsById(id);
    }

    /**
     * Check if a permission with the given code exists
     * @param code Permission code
     * @return true if exists, false otherwise
     */
    public boolean isPermissionCodeExists(String code) {
        return permissionDao.existsByCode(code);
    }

    /**
     * Check if a permission with the given code exists, excluding the permission with the given ID
     * @param code Permission code
     * @param id Permission ID to exclude
     * @return true if exists, false otherwise
     */
    public boolean isPermissionCodeExistsExcludeCurrent(String code, Long id) {
        return permissionDao.existsByCodeExcludingId(code, id);
    }

    public void clearUserCache(Long userId) {
        userPermissionCache.remove(userId);
    }

    public void clearAllCache() {
        userPermissionCache.clear();
    }
}
