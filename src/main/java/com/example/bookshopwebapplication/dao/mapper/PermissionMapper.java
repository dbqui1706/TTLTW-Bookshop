package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.Permission;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionMapper implements IRowMapper<Permission> {
    @Override
    public Permission mapRow(ResultSet resultSet) throws SQLException {
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
}
