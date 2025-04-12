package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleDao {
    /**
     * Find all roles
     * @return List of all roles
     */
    List<Role> findAll();

    /**
     * Find a role by ID
     * @param id Role ID
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findById(Long id);

    /**
     * Find a role by name
     * @param name Role name
     * @return Optional containing the role if found, empty otherwise
     */
    Optional<Role> findByName(String name);

    /**
     * Save a new role
     * @param role Role data
     * @return Generated ID
     */
    Long save(Role role);

    /**
     * Update an existing role
     * @param role Role data
     * @return true if update was successful, false otherwise
     */
    boolean update(Role role);

    /**
     * Delete a role by ID
     * @param id Role ID
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(Long id);

    /**
     * Check if a role with the given ID exists
     * @param id Role ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Check if a role with the given name exists
     * @param name Role name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a role with the given name exists, excluding the role with the given ID
     * @param name Role name
     * @param id Role ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByNameExcludingId(String name, Long id);

    /**
     * Lấy tổng số vai trò
     * @return Tổng số vai trò
     */
    int countAll();

    /**
     * Lấy danh sách vai trò có phân trang, sắp xếp và tìm kiếm
     * @param start Vị trí bắt đầu
     * @param length Số lượng bản ghi cần lấy
     * @param orderColumn Cột sắp xếp
     * @param orderDirection Hướng sắp xếp (ASC/DESC)
     * @param searchValue Giá trị tìm kiếm
     * @return Danh sách vai trò đã lọc, sắp xếp và phân trang
     */
    List<Role> findWithPaginationAndSearch(int start, int length, String orderColumn,
                                           String orderDirection, String searchValue);

    /**
     * Đếm số vai trò phù hợp với điều kiện tìm kiếm
     * @param searchValue Giá trị tìm kiếm
     * @return Số vai trò phù hợp
     */
    int countWithSearch(String searchValue);
}