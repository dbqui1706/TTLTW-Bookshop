package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.Permission;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IPermissionDao {
    /**
     * Find all permissions
     *
     * @return List of all permissions
     */
    List<Permission> findAll();

    /**
     * Find permissions by module
     *
     * @param module Module name
     * @return List of permissions for the given module
     */
    List<Permission> findByModule(String module);

    /**
     * Find a permission by ID
     *
     * @param id Permission ID
     * @return Optional containing the permission if found, empty otherwise
     */
    Optional<Permission> findById(Long id);

    /**
     * Find a permission by code
     *
     * @param code Permission code
     * @return Optional containing the permission if found, empty otherwise
     */
    Optional<Permission> findByCode(String code);

    /**
     * Find all module names
     *
     * @return Set of module names
     */
    Set<String> findAllModules();

    /**
     * Save a new permission
     *
     * @param permission Permission data
     * @return Generated ID
     */
    Long save(Permission permission);

    /**
     * Update an existing permission
     *
     * @param permission Permission data
     * @return true if update was successful, false otherwise
     */
    boolean update(Permission permission);

    /**
     * Delete a permission by ID
     *
     * @param id Permission ID
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(Long id);

    /**
     * Check if a permission with the given ID exists
     *
     * @param id Permission ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Check if a permission with the given code exists
     *
     * @param code Permission code
     * @return true if exists, false otherwise
     */
    boolean existsByCode(String code);

    /**
     * Check if a permission with the given code exists, excluding the permission with the given ID
     *
     * @param code Permission code
     * @param id   Permission ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByCodeExcludingId(String code, Long id);


    /**
     * Lấy tổng số quyền
     *
     * @return Tổng số quyền
     */
    public int countAll();

    /**
     * Đếm số quyền phù hợp với điều kiện tìm kiếm
     *
     * @param searchValue Giá trị tìm kiếm
     * @param module      Module lọc (có thể null)
     * @return Số lượng quyền thỏa mãn
     */
    public int countWithSearch(String searchValue, String module);

    /**
     * Lấy danh sách quyền có phân trang, sắp xếp và tìm kiếm
     *
     * @param start          Vị trí bắt đầu
     * @param length         Số lượng bản ghi
     * @param orderColumn    Cột sắp xếp
     * @param orderDirection Hướng sắp xếp
     * @param searchValue    Giá trị tìm kiếm
     * @param module         Module lọc (có thể null)
     * @return Danh sách quyền đã lọc, sắp xếp và phân trang
     */
    public List<Permission> findWithPaginationAndSearch(int start, int length, String orderColumn,
                                                        String orderDirection, String searchValue, String module);
}