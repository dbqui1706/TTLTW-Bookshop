package com.example.bookshopwebapplication.dao.mapper._interface;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRowMapper<T> {
    T mapRow(ResultSet resultSet) throws SQLException;
}
