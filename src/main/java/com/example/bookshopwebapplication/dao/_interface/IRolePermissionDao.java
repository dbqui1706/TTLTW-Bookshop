package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.Permission;

import java.util.List;

public interface IRolePermissionDao {
    /**
     * Find all permissions for a role
     * @param roleId Role ID
     * @return List of permissions
     */
    List<Permission> findPermissionsByRoleId(Long roleId);

    /**
     * Add a permission to a role
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    boolean addPermissionToRole(Long roleId, Long permissionId);

    /**
     * Remove a permission from a role
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    boolean removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * Remove all permissions from a role
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    boolean removeAllPermissionsFromRole(Long roleId);

    /**
     * Set permissions for a role (replaces all existing permissions)
     * @param roleId Role ID
     * @param permissionIds List of permission IDs
     * @return true if successful, false otherwise
     */
    boolean setPermissionsForRole(Long roleId, List<Long> permissionIds);

    /**
     * Check if a role-permission relationship exists
     * @param roleId Role ID
     * @param permissionId Permission ID
     * @return true if exists, false otherwise
     */
    boolean existsRolePermission(Long roleId, Long permissionId);
}