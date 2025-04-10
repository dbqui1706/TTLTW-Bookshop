package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IUserRoleDao;
import com.example.bookshopwebapplication.dao.mapper.RoleMapper;
import com.example.bookshopwebapplication.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UserRoleDao extends AbstractDao<Object> implements IUserRoleDao {
    public UserRoleDao() {
        super("user_roles");
    }

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        String sql = "SELECT r.* FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";
        List<Role> roles = new LinkedList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            RoleMapper roleMapper = new RoleMapper();
            while (rs.next()) {
                Role role = roleMapper.mapRow(rs);
                roles.add(role);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching roles for user ID: " + userId, e);
        } finally {
            close(conn, stmt, rs);
        }
        return roles;
    }

    @Override
    public boolean addRoleToUser(Long userId, Long roleId) {
        // Kiểm tra xem đã tồn tại chưa
        if (existsUserRole(userId, roleId)) {
            return true; // Đã tồn tại, không cần thêm mới
        }

        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try {
            insertNoGenerateKey(sql, userId, roleId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        String sql = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
        try {
            delete(sql, userId, roleId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeAllRolesFromUser(Long userId) {
        String sql = "DELETE FROM user_roles WHERE user_id = ?";
        try {
            delete(sql, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean setRolesForUser(Long userId, List<Long> roleIds) {
        try {
            executeTransaction(connection -> {
                // Xóa tất cả vai trò hiện tại của người dùng
                String deleteSql = "DELETE FROM user_roles WHERE user_id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                deleteStmt.setLong(1, userId);
                deleteStmt.executeUpdate();
                deleteStmt.close();

                // Thêm lại các vai trò mới
                String insertSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);

                for (Long roleId : roleIds) {
                    insertStmt.setLong(1, userId);
                    insertStmt.setLong(2, roleId);
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
    public boolean existsUserRole(Long userId, Long roleId) {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_id = ?";
        return count(sql, userId, roleId) > 0;
    }

    @Override
    public Object mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException("Not supported in UserRoleDao");
    }
}
