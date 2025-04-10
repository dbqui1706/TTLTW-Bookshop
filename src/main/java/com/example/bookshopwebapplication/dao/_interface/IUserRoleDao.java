package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.Role;

import java.util.List;

public interface IUserRoleDao {
    /**
     * Find all roles for a user
     * @param userId User ID
     * @return List of roles
     */
    List<Role> findRolesByUserId(Long userId);

    /**
     * Add a role to a user
     * @param userId User ID
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    boolean addRoleToUser(Long userId, Long roleId);

    /**
     * Remove a role from a user
     * @param userId User ID
     * @param roleId Role ID
     * @return true if successful, false otherwise
     */
    boolean removeRoleFromUser(Long userId, Long roleId);

    /**
     * Remove all roles from a user
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    boolean removeAllRolesFromUser(Long userId);

    /**
     * Set roles for a user (replaces all existing roles)
     * @param userId User ID
     * @param roleIds List of role IDs
     * @return true if successful, false otherwise
     */
    boolean setRolesForUser(Long userId, List<Long> roleIds);

    /**
     * Check if a user-role relationship exists
     * @param userId User ID
     * @param roleId Role ID
     * @return true if exists, false otherwise
     */
    boolean existsUserRole(Long userId, Long roleId);
}