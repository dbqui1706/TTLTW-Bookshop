package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.RolePermissionDao;
import com.example.bookshopwebapplication.dao._interface.IRolePermissionDao;
import com.example.bookshopwebapplication.entities.Permission;

import java.util.List;

/**
 * Service class for handling role-permission relationship business logic
 */
public class RolePermissionService {
    private final IRolePermissionDao rolePermissionDao;
    private final PermissionService permissionService;

    public RolePermissionService() {
        this.rolePermissionDao = new RolePermissionDao();
        this.permissionService = new PermissionService();
    }

    /**
     * Get all permissions assigned to a role
     * @param roleId Role ID
     * @return List of permissions
     */
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return rolePermissionDao.findPermissionsByRoleId(roleId);
    }

    /**
     * Add a permission to a role
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    public boolean addPermissionToRole(Long roleId, Long permissionId) {
        boolean result = rolePermissionDao.addPermissionToRole(roleId, permissionId);
        if (result) {
            // Clear all permission caches since role permissions changed
            permissionService.clearAllCache();
        }
        return result;
    }

    /**
     * Remove a permission from a role
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    public boolean removePermissionFromRole(Long roleId, Long permissionId) {
        boolean result = rolePermissionDao.removePermissionFromRole(roleId, permissionId);
        if (result) {
            // Clear all permission caches since role permissions changed
            permissionService.clearAllCache();
        }
        return result;
    }

    /**
     * Remove all permissions from a role
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    public boolean removeAllPermissionsFromRole(Long roleId) {
        boolean result = rolePermissionDao.removeAllPermissionsFromRole(roleId);
        if (result) {
            // Clear all permission caches since role permissions changed
            permissionService.clearAllCache();
        }
        return result;
    }

    /**
     * Set the permissions for a role (replace all existing permissions)
     * @param roleId Role ID
     * @param permissionIds List of permission IDs
     * @return true if successful, false otherwise
     */
    public boolean setPermissionsForRole(Long roleId, List<Long> permissionIds) {
        boolean result = rolePermissionDao.setPermissionsForRole(roleId, permissionIds);
        if (result) {
            // Clear all permission caches since role permissions changed
            permissionService.clearAllCache();
        }
        return result;
    }

    /**
     * Check if a role already has a specific permission
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if the role has the permission, false otherwise
     */
    public boolean hasRolePermission(Long roleId, Long permissionId) {
        return rolePermissionDao.existsRolePermission(roleId, permissionId);
    }
}