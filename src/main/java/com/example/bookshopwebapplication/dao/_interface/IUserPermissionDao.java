package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.SpecialPermission;

import java.util.List;

public interface IUserPermissionDao {
    /**
     * Find all special permissions for a user
     * @param userId User ID
     * @return List of special permissions
     */
    List<SpecialPermission> findSpecialPermissionsByUserId(Long userId);

    /**
     * Add or update a special permission for a user
     * @param specialPermission Special permission data
     * @return true if successful, false otherwise
     */
    boolean addSpecialPermission(SpecialPermission specialPermission);

    /**
     * Remove a special permission from a user
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    boolean removeSpecialPermission(Long userId, Long permissionId);

    /**
     * Check if a special permission exists for a user
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if exists, false otherwise
     */
    boolean existsSpecialPermission(Long userId, Long permissionId);
}
