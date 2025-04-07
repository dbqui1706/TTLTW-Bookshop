package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.UserSessionMapper;
import com.example.bookshopwebapplication.entities.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        builderSQL.append("INSERT INTO user_session (user_id, session_token, ip_address, device_info, expire_time) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?)");

        return insert(builderSQL.toString(),
                userSession.getUserId(), userSession.getSessionToken(), userSession.getIpAddress(),
                userSession.getDeviceInfo(), userSession.getExpireTime());
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

    public UserSession getUserIdFromSession(String sessionId) {
        clearSQL();
        // Xây dựng câu truy vấn SQL để lấy user_id từ bảng user_session nếu session_token còn hạn
        builderSQL.append("SELECT * FROM user_session WHERE session_token = ? AND is_active = 1 AND expire_time > NOW()");
        List<UserSession> userSessions = query(builderSQL.toString(), new UserSessionMapper(), sessionId);

        return userSessions.get(0);
    }
}
