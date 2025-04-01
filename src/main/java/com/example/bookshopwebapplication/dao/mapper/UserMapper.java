package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.Category;
import com.example.bookshopwebapplication.entities.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements IRowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet) {
        try {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setFullName(resultSet.getString("fullname"));
            user.setEmail(resultSet.getString("email"));
            user.setPhoneNumber(resultSet.getString("phoneNumber"));
            user.setGender(resultSet.getInt("gender"));
            user.setAddress(resultSet.getString("address"));
            user.setRole(resultSet.getString("role"));
            user.setIsActiveEmail(resultSet.getBoolean("is_email_verified"));
            user.setCreatedAt(resultSet.getTimestamp("created_at"));
            user.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
