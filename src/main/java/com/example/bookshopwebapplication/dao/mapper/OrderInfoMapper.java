package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderInfo;

import java.sql.ResultSet;

public class OrderInfoMapper implements IRowMapper<OrderInfo> {
    @Override
    public OrderInfo mapRow(ResultSet resultSet) {
        try {
            return new OrderInfo(
                    resultSet.getLong("id"),
                    resultSet.getLong("orderId"),
                    resultSet.getString("receiver"),
                    resultSet.getString("address_receiver"),
                    resultSet.getString("email_receiver"),
                    resultSet.getString("phone_receiver"),
                    resultSet.getString("city"),
                    resultSet.getString("district"),
                    resultSet.getString("ward"),
                    resultSet.getDouble("total_price"),
                    resultSet.getTimestamp("createdAt"),
                    resultSet.getTimestamp("updatedAt")
            );
        } catch (Exception e) {
            System.out.println("OrderInfoMapper.mapRow: " + e.getMessage());
        }
        return null;
    }
}
