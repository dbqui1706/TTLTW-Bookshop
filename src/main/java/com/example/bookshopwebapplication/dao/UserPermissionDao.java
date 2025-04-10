package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IUserPermissionDao;
import com.example.bookshopwebapplication.dao.mapper.SpecialPermissionMapper;
import com.example.bookshopwebapplication.entities.SpecialPermission;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserPermissionDao extends AbstractDao<SpecialPermission> implements IUserPermissionDao {
    public UserPermissionDao() {
        super("user_permissions");
    }


    @Override
    public List<SpecialPermission> findSpecialPermissionsByUserId(Long userId) {
        String sql = "SELECT up.user_id, up.permission_id, up.is_granted, up.created_at, up.updated_at, " +
                "p.name as permission_name, p.code as permission_code, p.module as permission_module " +
                "FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ?";
        return query(sql, new SpecialPermissionMapper(), userId);
    }

    @Override
    public boolean addSpecialPermission(SpecialPermission specialPermission) {
        // Kiểm tra xem đã tồn tại chưa
        if (existsSpecialPermission(specialPermission.getUserId(), specialPermission.getPermissionId())) {
            // Nếu đã tồn tại, cập nhật lại
            String sql = "UPDATE user_permissions SET is_granted = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE user_id = ? AND permission_id = ?";
            update(sql, specialPermission.isGranted() ? 1 : 0,
                    specialPermission.getUserId(), specialPermission.getPermissionId());
            return true;
        }

        // Nếu chưa tồn tại, thêm mới
        String sql = "INSERT INTO user_permissions (user_id, permission_id, is_granted) VALUES (?, ?, ?)";
        try {
            insertNoGenerateKey(sql, specialPermission.getUserId(),
                    specialPermission.getPermissionId(),
                    specialPermission.isGranted() ? 1 : 0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeSpecialPermission(Long userId, Long permissionId) {
        String sql = "DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?";
        try {
            delete(sql, userId, permissionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsSpecialPermission(Long userId, Long permissionId) {
        String sql = "SELECT COUNT(*) FROM user_permissions WHERE user_id = ? AND permission_id = ?";
        return count(sql, userId, permissionId) > 0;
    }

    @Override
    public SpecialPermission mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return SpecialPermission.builder()
                .userId(resultSet.getLong("user_id"))
                .permissionId(resultSet.getLong("permission_id"))
                .isGranted(resultSet.getBoolean("is_granted"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .permissionName(resultSet.getString("permission_name"))
                .permissionCode(resultSet.getString("permission_code"))
                .permissionModule(resultSet.getString("permission_module"))
                .build();
    }
}
