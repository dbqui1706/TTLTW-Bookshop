package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.http.response.user.UserFullDetail;
import com.example.bookshopwebapplication.http.response_admin.DataTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAdminDao extends AbstractDao<Object> {

    public UserAdminDao() {
        super(null);
    }

    @Override
    public Object mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
