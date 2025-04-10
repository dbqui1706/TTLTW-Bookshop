package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper implements IRowMapper<Role> {
    @Override
    public Role mapRow(ResultSet resultSet) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .isSystem(resultSet.getBoolean("is_system"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .build();
    }
}
