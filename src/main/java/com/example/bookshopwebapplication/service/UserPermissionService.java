package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.UserPermissionDao;
import com.example.bookshopwebapplication.dao._interface.IUserPermissionDao;
import com.example.bookshopwebapplication.entities.SpecialPermission;

import java.util.List;

/**
 * Service class for handling user special permissions business logic
 */
public class UserPermissionService {
    private final IUserPermissionDao userPermissionDao;
    private final PermissionService permissionService;

    public UserPermissionService() {
        this.userPermissionDao = new UserPermissionDao();
        this.permissionService = new PermissionService();
    }

    /**
     * Get all special permissions for a user
     * @param userId User ID
     * @return List of special permissions
     */
    public List<SpecialPermission> getSpecialPermissionsByUserId(Long userId) {
        return userPermissionDao.findSpecialPermissionsByUserId(userId);
    }

    /**
     * Add a special permission for a user
     * @param specialPermission Special permission data
     * @return true if successful, false otherwise
     */
    public boolean addSpecialPermission(SpecialPermission specialPermission) {
        boolean result = userPermissionDao.addSpecialPermission(specialPermission);
        if (result) {
            // Clear permission cache for this user since special permissions changed
            permissionService.clearUserCache(specialPermission.getUserId());
        }
        return result;
    }

    /**
     * Remove a special permission from a user
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    public boolean removeSpecialPermission(Long userId, Long permissionId) {
        boolean result = userPermissionDao.removeSpecialPermission(userId, permissionId);
        if (result) {
            // Clear permission cache for this user since special permissions changed
            permissionService.clearUserCache(userId);
        }
        return result;
    }

    /**
     * Check if a special permission exists for a user
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if exists, false otherwise
     */
    public boolean isSpecialPermissionExists(Long userId, Long permissionId) {
        return userPermissionDao.existsSpecialPermission(userId, permissionId);
    }

    /**
     * Get a special permission for a user by permission ID
     * @param userId User ID
     * @param permissionId Permission ID
     * @return Special permission if exists, null otherwise
     */
    public SpecialPermission getSpecialPermission(Long userId, Long permissionId) {
        List<SpecialPermission> specialPermissions = getSpecialPermissionsByUserId(userId);
        for (SpecialPermission permission : specialPermissions) {
            if (permission.getPermissionId().equals(permissionId)) {
                return permission;
            }
        }
        return null;
    }

    /**
     * Check if a user has a granted special permission
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if granted, false if denied or doesn't exist
     */
    public boolean hasGrantedSpecialPermission(Long userId, Long permissionId) {
        SpecialPermission specialPermission = getSpecialPermission(userId, permissionId);
        return specialPermission != null && specialPermission.isGranted();
    }

    /**
     * Check if a user has a denied special permission
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if denied, false if granted or doesn't exist
     */
    public boolean hasDeniedSpecialPermission(Long userId, Long permissionId) {
        SpecialPermission specialPermission = getSpecialPermission(userId, permissionId);
        return specialPermission != null && !specialPermission.isGranted();
    }
}