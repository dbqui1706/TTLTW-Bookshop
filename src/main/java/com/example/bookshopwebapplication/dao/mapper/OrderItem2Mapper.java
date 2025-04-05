package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderItem2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItem2Mapper implements IRowMapper<OrderItem2> {
    @Override
    public OrderItem2 mapRow(ResultSet resultSet) throws SQLException {
        return OrderItem2.builder().build();
    }
}
