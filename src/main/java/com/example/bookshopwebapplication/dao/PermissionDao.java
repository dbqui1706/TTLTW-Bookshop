package com.example.bookshopwebapplication.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PermissionDao extends AbstractDao<Object> {

    /**
     * Constructor nhận vào tên bảng của entity
     *
     * @param tableName Tên bảng
     */
    public PermissionDao(String tableName) {
        super(tableName);
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
    public Object mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
