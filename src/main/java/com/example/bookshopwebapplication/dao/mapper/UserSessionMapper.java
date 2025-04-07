package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSessionMapper implements IRowMapper<UserSession> {
    @Override
    public UserSession mapRow(ResultSet resultSet) throws SQLException {
        try {
            UserSession userSession = new UserSession();
            userSession.setId(resultSet.getLong("id"));
            userSession.setUserId(resultSet.getLong("user_id"));
            userSession.setSessionToken(resultSet.getString("session_token"));
            userSession.setIpAddress(resultSet.getString("ip_address"));
            userSession.setDeviceInfo(resultSet.getString("device_info"));
            userSession.setStartTime(resultSet.getTimestamp("start_time"));
            userSession.setExpireTime(resultSet.getTimestamp("expire_time"));
            userSession.setListActivity(resultSet.getTimestamp("last_activity"));
            userSession.setActive(resultSet.getBoolean("is_active"));
            return userSession;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
