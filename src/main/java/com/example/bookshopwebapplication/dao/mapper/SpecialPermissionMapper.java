package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.SpecialPermission;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialPermissionMapper implements IRowMapper<SpecialPermission> {
    @Override
    public SpecialPermission mapRow(ResultSet resultSet) throws SQLException {
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
