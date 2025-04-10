package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.PermissionMapper;
import com.example.bookshopwebapplication.entities.Permission;

import java.awt.font.TextHitInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PermissionDao extends AbstractDao<Permission> {
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
        }finally {
            close(conn, null, null);
        }
    }
}
