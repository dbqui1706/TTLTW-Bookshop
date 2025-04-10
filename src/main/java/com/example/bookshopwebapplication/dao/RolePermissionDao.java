package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IRolePermissionDao;
import com.example.bookshopwebapplication.dao.mapper.PermissionMapper;
import com.example.bookshopwebapplication.entities.Permission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class RolePermissionDao extends AbstractDao<Object> implements IRolePermissionDao {
    public RolePermissionDao() {
        super("role_permissions");
    }

    @Override
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        String sql = "SELECT p.* FROM permissions p " +
                "JOIN role_permissions rp ON p.id = rp.permission_id " +
                "WHERE rp.role_id = ?";
        List<Permission> permissions = new LinkedList<>();
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, roleId);
            ResultSet rs = stmt.executeQuery();
            PermissionMapper permissionMapper = new PermissionMapper();
            while (rs.next()) {
                Permission permission = permissionMapper.mapRow(rs);
                permissions.add(permission);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching permissions for role ID: " + roleId, e);
        }
        return permissions;
    }

    @Override
    public boolean addPermissionToRole(Long roleId, Long permissionId) {
        // Kiểm tra xem đã tồn tại chưa
        if (existsRolePermission(roleId, permissionId)) {
            return true; // Đã tồn tại, không cần thêm mới
        }

        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        try {
            insertNoGenerateKey(sql, roleId, permissionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removePermissionFromRole(Long roleId, Long permissionId) {
        String sql = "DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?";
        try {
            delete(sql, roleId, permissionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAllPermissionsFromRole(Long roleId) {
        String sql = "DELETE FROM role_permissions WHERE role_id = ?";
        try {
            delete(sql, roleId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setPermissionsForRole(Long roleId, List<Long> permissionIds) {
        try {
            executeTransaction(connection -> {
                // Xóa tất cả quyền hiện tại của vai trò
                String deleteSql = "DELETE FROM role_permissions WHERE role_id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                deleteStmt.setLong(1, roleId);
                deleteStmt.executeUpdate();
                deleteStmt.close();

                // Thêm lại các quyền mới
                String insertSql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);

                for (Long permissionId : permissionIds) {
                    insertStmt.setLong(1, roleId);
                    insertStmt.setLong(2, permissionId);
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                insertStmt.close();
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsRolePermission(Long roleId, Long permissionId) {
        String sql = "SELECT COUNT(*) FROM role_permissions WHERE role_id = ? AND permission_id = ?";
        return count(sql, roleId, permissionId) > 0;
    }

    @Override
    public Object mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException("Not supported in RolePermissionDao");
    }
}
