package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.UserRoleDao;
import com.example.bookshopwebapplication.dao._interface.IUserRoleDao;
import com.example.bookshopwebapplication.entities.Role;

import java.util.List;

/**
 * Service class for handling user-role relationship business logic
 */
public class UserRoleService {
    private final IUserRoleDao userRoleDao;
    private final PermissionService permissionService;

    public UserRoleService() {
        this.userRoleDao = new UserRoleDao();
        this.permissionService = new PermissionService();
    }

    /**
     * Get all roles assigned to a user
     * @param userId User ID
     * @return List of roles
     */
    public List<Role> getRolesByUserId(Long userId) {
        return userRoleDao.findRolesByUserId(userId);
    }

    /**
     * Add a role to a user
     * @param userId User ID
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    public boolean addRoleToUser(Long userId, Long roleId) {
        boolean result = userRoleDao.addRoleToUser(userId, roleId);
        if (result) {
            // Clear permission cache for this user since roles changed
            permissionService.clearUserCache(userId);
        }
        return result;
    }

    /**
     * Remove a role from a user
     * @param userId User ID
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        boolean result = userRoleDao.removeRoleFromUser(userId, roleId);
        if (result) {
            // Clear permission cache for this user since roles changed
            permissionService.clearUserCache(userId);
        }
        return result;
    }

    /**
     * Remove all roles from a user
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean removeAllRolesFromUser(Long userId) {
        boolean result = userRoleDao.removeAllRolesFromUser(userId);
        if (result) {
            // Clear permission cache for this user since roles changed
            permissionService.clearUserCache(userId);
        }
        return result;
    }

    /**
     * Set the roles for a user (replace all existing roles)
     * @param userId User ID
     * @param roleIds List of role IDs
     * @return true if successful, false otherwise
     */
    public boolean setRolesForUser(Long userId, List<Long> roleIds) {
        boolean result = userRoleDao.setRolesForUser(userId, roleIds);
        if (result) {
            // Clear permission cache for this user since roles changed
            permissionService.clearUserCache(userId);
        }
        return result;
    }

    /**
     * Check if a user already has a specific role
     * @param userId User ID
     * @param roleId Role ID
     * @return true if the user has the role, false otherwise
     */
    public boolean hasUserRole(Long userId, Long roleId) {
        return userRoleDao.existsUserRole(userId, roleId);
    }
}