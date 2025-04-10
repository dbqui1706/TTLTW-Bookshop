package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.RoleDao;
import com.example.bookshopwebapplication.dao._interface.IRoleDao;
import com.example.bookshopwebapplication.entities.Role;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling role-related business logic
 */
public class RoleService {
    private final IRoleDao roleDao;

    public RoleService() {
        this.roleDao = new RoleDao();
    }

    /**
     * Get all roles in the system
     * @return List of all roles
     */
    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    /**
     * Get a role by its ID
     * @param id Role ID
     * @return Optional containing the role if found, empty otherwise
     */
    public Optional<Role> getRoleById(Long id) {
        return roleDao.findById(id);
    }

    /**
     * Get a role by name
     * @param name Role name
     * @return Optional containing the role if found, empty otherwise
     */
    public Optional<Role> getRoleByName(String name) {
        return roleDao.findByName(name);
    }

    /**
     * Create a new role
     * @param Role Role data
     * @return Optional containing the created role with ID, empty if failed
     */
    public Optional<Role> createRole(Role Role) {
        Long id = roleDao.save(Role);
        if (id != null) {
            Role.setId(id);
            return Optional.of(Role);
        }
        return Optional.empty();
    }

    /**
     * Update an existing role
     * @param Role Role data with updated fields
     * @return Optional containing the updated role, empty if failed
     */
    public Optional<Role> updateRole(Role Role) {
        if (roleDao.update(Role)) {
            return roleDao.findById(Role.getId());
        }
        return Optional.empty();
    }

    /**
     * Delete a role by ID
     * @param id Role ID
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteRole(Long id) {
        return roleDao.delete(id);
    }

    /**
     * Check if a role with the given ID exists
     * @param id Role ID
     * @return true if exists, false otherwise
     */
    public boolean isRoleExists(Long id) {
        return roleDao.existsById(id);
    }

    /**
     * Check if a role with the given name exists
     * @param name Role name
     * @return true if exists, false otherwise
     */
    public boolean isRoleNameExists(String name) {
        return roleDao.existsByName(name);
    }

    /**
     * Check if a role with the given name exists, excluding the role with the given ID
     * @param name Role name
     * @param id Role ID to exclude
     * @return true if exists, false otherwise
     */
    public boolean isRoleNameExistsExcludeCurrent(String name, Long id) {
        return roleDao.existsByNameExcludingId(name, id);
    }
}