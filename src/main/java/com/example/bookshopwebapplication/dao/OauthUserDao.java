package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IOauthUserDao;
import com.example.bookshopwebapplication.dao.mapper.OauthUserMapper;
import com.example.bookshopwebapplication.entities.OauthUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class OauthUserDao extends AbstractDao<OauthUser> implements IOauthUserDao {

    public OauthUserDao() {
        super("oauth_user");
    }

    public Long save(OauthUser oauthUser) {
        clearSQL();
        // Step 1: Save to table user.
        String query1 = "INSERT INTO user (username, password, fullname, email," +
                " phoneNumber, gender, address, role, is_email_verified ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String email = oauthUser.getProvider().equals("GOOGLE") ? oauthUser.getEmail() : "";

        Long userID = insert(query1, "", "", oauthUser.getFullName(),
                email, "", 0, "", oauthUser.getRole(), 1
        );

        // Step 2: Save to table oauth_user;
        builderSQL.append(
                "INSERT INTO oauth_user (provider, provider_id, user_id) "
                        + "VALUES(?, ?, ?)"
        );
        return insert(builderSQL.toString(),
                oauthUser.getProvider(), oauthUser.getProviderID(), userID
        );
    }

    public void update(OauthUser oauthUser) {
        clearSQL();
        builderSQL.append(
                "UPDATE oauth_user SET provider = ?, provider_id = ?, user_id = ?, " +
                        "updatedAt = ? WHERE id = ?"
        );
        update(builderSQL.toString(), oauthUser.getProvider(), oauthUser.getProviderID(),
                oauthUser.getUserID(), new Timestamp(System.currentTimeMillis()),
                oauthUser.getId());
    }

    // Xóa một CartItem khỏi cơ sở dữ liệu dựa trên id.
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM oauth_user WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    public Optional<OauthUser> findByID(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM oauth_user WHERE id = ?");
        return Optional.ofNullable(query(builderSQL.toString(), new OauthUserMapper(), id).get(0));
    }

    public Optional<OauthUser> findByUserID(Long userID) {
        clearSQL();
        builderSQL.append("SELECT * FROM oauth_user WHERE user_id = ?");
        return Optional.ofNullable(query(builderSQL.toString(), new OauthUserMapper(), userID).get(0));
    }

    public Optional<OauthUser> getByProviderFBID(String providerID) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM oauth_user WHERE provider_id = ? AND provider = 'FACEBOOK' "
        );
        List<OauthUser> users = query(builderSQL.toString(), new OauthUserMapper(), providerID);
        return users.size() > 0 ? Optional.of(users.get(0)) : Optional.empty();
    }

    @Override
    public OauthUser mapResultSetToEntity(ResultSet resultSet) {
        try{
            OauthUser oauthUser = new OauthUser();
            oauthUser.setId(resultSet.getLong("id"));
            oauthUser.setProvider(resultSet.getString("provider"));
            oauthUser.setProviderID(resultSet.getString("provider_id"));
            oauthUser.setUserID(resultSet.getLong("user_id"));
            oauthUser.setCreatedAt(resultSet.getTimestamp("createdAt"));
            oauthUser.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
            return oauthUser;
        }catch (Exception e){
            return new OauthUser();
        }
    }
}
