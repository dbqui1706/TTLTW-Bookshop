package com.example.bookshopwebapplication.dao.mapper;
import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OauthUser;

import java.sql.ResultSet;

public class OauthUserMapper implements IRowMapper<OauthUser> {

    @Override
    public OauthUser mapRow(ResultSet resultSet) {
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
