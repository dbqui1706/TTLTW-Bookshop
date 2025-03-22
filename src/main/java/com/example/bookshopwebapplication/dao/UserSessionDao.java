package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSessionDao extends AbstractDao<UserSession> {
    /**
     * Constructor nhận vào tên bảng của entity
     *
     */
    public UserSessionDao() {
        super("user_session");
    }

    public Long save(UserSession userSession) {
        clearSQL();
        builderSQL.append("INSERT INTO user_session (user_id, session_token, ip_address, device_info) ");
        builderSQL.append("VALUES (?, ?, ?, ?)");

        return insert(builderSQL.toString(),
                userSession.getUserId(), userSession.getSessionToken(), userSession.getIpAddress(),
                userSession.getDeviceInfo());
    }

    @Override
    public UserSession mapResultSetToEntity(ResultSet resultSet) throws SQLException {
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
