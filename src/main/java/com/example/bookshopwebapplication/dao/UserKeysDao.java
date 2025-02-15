package com.example.bookshopwebapplication.dao;


import com.example.bookshopwebapplication.dao._interface.IUserKeysDao;
import com.example.bookshopwebapplication.dao.mapper.UserKeysMapper;
import com.example.bookshopwebapplication.entities.UserKeys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserKeysDao extends AbstractDao<UserKeys> implements IUserKeysDao {
    public UserKeysDao() {
        super("user_keys");
    }

    @Override
    public Optional<UserKeys> getByUserId(long userId) {
        clearSQL();
        String sql = "SELECT * FROM user_keys WHERE userId = ? and status = 1 order by createdAt desc LIMIT 1";
        List<UserKeys> userKeys = query(sql, new UserKeysMapper(), userId);
        return userKeys.isEmpty() ? Optional.empty() : Optional.of(userKeys.get(0));
    }

    public Long save(UserKeys userKeys) {
        clearSQL();
        builderSQL.append("INSERT INTO user_keys (userId, public_key) ");
        builderSQL.append("VALUES (?, ?)");
        return insert(builderSQL.toString(), userKeys.getUserId(), userKeys.getPublicKey());
    }

    public void update(UserKeys userKeys) {
        clearSQL();
        builderSQL.append("UPDATE user_keys SET public_key = ?, status = ? ");
        builderSQL.append("WHERE userId = ? and id = ?");
        update(builderSQL.toString(), userKeys.getPublicKey(), userKeys.getIsActive(), userKeys.getUserId(), userKeys.getId());
    }

    public void updateActiveStatus(UserKeys userKeys) {
        clearSQL();
        builderSQL.append("UPDATE user_keys SET status = ? ");
        builderSQL.append("WHERE userId = ? and id = ?");
        update(builderSQL.toString(), userKeys.getIsActive(), userKeys.getUserId(), userKeys.getId());
    }

    @Override
    public UserKeys mapResultSetToEntity(ResultSet resultSet) {
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
