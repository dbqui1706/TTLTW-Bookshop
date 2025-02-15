package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.entities.UserKeys;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserKeysMapper implements IRowMapper<UserKeys> {


    @Override
    public UserKeys mapRow(ResultSet resultSet) {
        try{
            UserKeys userKeys = new UserKeys();
            userKeys.setId(resultSet.getLong("id"));
            userKeys.setUserId(resultSet.getLong("userId"));
            userKeys.setPublicKey(resultSet.getString("public_key"));
            userKeys.setIsActive(resultSet.getInt("status"));
            userKeys.setCreatedAt(resultSet.getTimestamp("createdAt"));
            userKeys.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
            return userKeys;
        } catch (SQLException e) {
            System.out.println("Error in UserKeysMapper.mapRow: " + e.getMessage());
            return null;
        }
    }
}
