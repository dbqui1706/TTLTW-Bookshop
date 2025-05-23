package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IPermissionDao;
import com.example.bookshopwebapplication.dao.mapper.PermissionMapper;
import com.example.bookshopwebapplication.entities.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PermissionDao extends AbstractDao<Permission> implements IPermissionDao {
    public PermissionDao() {
        super("permissions");
    }

    public List<String> getUserPermissionCodes(Long userId) throws SQLException {
        List<String> permissions = new ArrayList<>();

        // Lấy quyền từ vai trò của người dùng
        String sql = "SELECT DISTINCT p.code FROM permissions p " +
                "JOIN role_permissions rp ON p.id = rp.permission_id " +
                "JOIN user_roles ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                permissions.add(rs.getString("code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, stmt, rs);
        }

        // Kiểm tra và áp dụng quyền đặc biệt
        sql = "SELECT p.code, up.is_granted FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ?";

        PreparedStatement stmt2 = conn.prepareStatement(sql);
        stmt2.setLong(1, userId);
        ResultSet rs2 = stmt2.executeQuery();
        try {
            stmt2.setLong(1, userId);
            while (rs2.next()) {
                String code = rs2.getString("code");
                boolean isGranted = rs2.getBoolean("is_granted");

                if (isGranted) {
                    if (!permissions.contains(code)) {
                        permissions.add(code);
                    }
                } else {
                    permissions.remove(code);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt2, rs2);
        }

        return permissions;
    }

    @Override
    public Permission mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return Permission.builder()
                .id(resultSet.getLong("id"))
                .code(resultSet.getString("code"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .module(resultSet.getString("module"))
                .isSystem(resultSet.getBoolean("is_system"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .build();
    }

    public void loadPermissions(Map<String, Permission> permissionCache) {
        clearSQL();
        builderSQL.append("SELECT * FROM bookshopdb.permissions");
        List<Permission> permissions = query(builderSQL.toString(), new PermissionMapper());
        for (Permission permission : permissions) {
            permissionCache.put(permission.getCode(), permission);
        }
    }

    public void loadPermissions(Long userId, Set<String> permissions) {
        clearSQL();
        // SQL to get all permissions for user (both direct and via roles)
        String sql = """
                    -- Direct user permissions that are granted
                    SELECT p.code FROM bookshopdb.user_permissions up
                    JOIN bookshopdb.permissions p ON up.permission_id = p.id
                    WHERE up.user_id = ? AND up.is_granted = 1
                
                    UNION
                
                    -- Permissions from roles (excluding any explicitly denied)
                    SELECT p.code FROM bookshopdb.role_permissions rp
                    JOIN bookshopdb.user_roles ur ON rp.role_id = ur.role_id
                    JOIN bookshopdb.permissions p ON rp.permission_id = p.id
                    WHERE ur.user_id = ?
                    AND NOT EXISTS (
                        SELECT 1 FROM bookshopdb.user_permissions up
                        WHERE up.user_id = ? AND up.permission_id = p.id AND up.is_granted = 0
                    )
                """;
        builderSQL.append(sql);
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            stmt.setLong(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("code"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading permissions: " + e);
        } finally {
            close(conn, null, null);
        }
    }


    @Override
    public List<Permission> findAll() {
        String sql = "SELECT * FROM permissions";
        return query(sql, new PermissionMapper());
    }

    @Override
    public List<Permission> findByModule(String module) {
        String sql = "SELECT * FROM permissions WHERE module = ?";
        return query(sql, new PermissionMapper(), module);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        return getById(sql, new PermissionMapper(), id);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        String sql = "SELECT * FROM permissions WHERE code = ?";
        List<Permission> permissions = query(sql, new PermissionMapper(), code);
        return permissions.isEmpty() ? Optional.empty() : Optional.of(permissions.get(0));
    }

    @Override
    public Set<String> findAllModules() {
        String sql = "SELECT * FROM permissions";
        List<Permission> permissions = query(sql, new PermissionMapper());
        Set<String> modules = new HashSet<>();
        for (Permission permission : permissions) {
            modules.add(permission.getModule());
        }
        return modules;
    }

    @Override
    public Long save(Permission permission) {
        String sql = "INSERT INTO permissions (name, code, module, description, is_system) VALUES (?, ?, ?, ?, ?)";
        return insert(sql, permission.getName(), permission.getCode(), permission.getModule(),
                permission.getDescription(), permission.getIsSystem() ? 1 : 0);
    }

    @Override
    public boolean update(Permission permission) {
        String sql = "UPDATE permissions SET name = ?, code = ?, module = ?, description = ?, " +
                "is_system = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        update(sql, permission.getName(), permission.getCode(), permission.getModule(),
                permission.getDescription(), permission.getIsSystem() ? 1 : 0, permission.getId());
        return true;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM permissions WHERE id = ?";
        delete(sql, id);
        return true;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM permissions WHERE id = ?";
        return count(sql, id) > 0;
    }

    @Override
    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM permissions WHERE code = ?";
        return count(sql, code) > 0;
    }

    @Override
    public boolean existsByCodeExcludingId(String code, Long id) {
        String sql = "SELECT COUNT(*) FROM permissions WHERE code = ? AND id <> ?";
        return count(sql, code, id) > 0;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM permissions";
        return count(sql);
    }

    @Override
    public int countWithSearch(String searchValue, String module) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM permissions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Thêm điều kiện lọc theo module nếu có
        if (module != null && !module.trim().isEmpty()) {
            sql.append(" AND module = ?");
            params.add(module);
        }

        // Thêm điều kiện tìm kiếm
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR code LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + searchValue + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        return count(sql.toString(), params.toArray());
    }

    @Override
    public List<Permission> findWithPaginationAndSearch(int start, int length, String orderColumn, String orderDirection, String searchValue, String module) {
        List<Permission> permissions = new ArrayList<>();

        // Map tên cột từ DataTable sang tên cột trong DB
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("id", "id");
        columnMap.put("name", "name");
        columnMap.put("code", "code");
        columnMap.put("module", "module");
        columnMap.put("description", "description");
        columnMap.put("isSystem", "is_system");
        columnMap.put("createdAt", "created_at");

        // Đảm bảo tên cột hợp lệ
        String dbColumn = columnMap.getOrDefault(orderColumn, "id");

        // Đảm bảo hướng sắp xếp hợp lệ
        String direction = "ASC".equalsIgnoreCase(orderDirection) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("SELECT * FROM permissions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Thêm điều kiện lọc theo module nếu có
        if (module != null && !module.trim().isEmpty()) {
            sql.append(" AND module = ?");
            params.add(module);
        }

        // Thêm điều kiện tìm kiếm
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR code LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + searchValue + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Thêm sắp xếp
        sql.append(" ORDER BY ").append(dbColumn).append(" ").append(direction);

        // Thêm phân trang
        if (length > 0) {
            sql.append(" LIMIT ").append(start).append(" , ").append(length);
        }
        permissions = query(sql.toString(), new PermissionMapper(), params.toArray());

        return permissions;
    }
}
