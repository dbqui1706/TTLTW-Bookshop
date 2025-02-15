package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderHash;
import com.example.bookshopwebapplication.entities.OrderInfo;

import java.sql.ResultSet;

public class OrderHashMapper implements IRowMapper<OrderHash> {
    @Override
    public OrderHash mapRow(ResultSet resultSet) {
        try {
            return new OrderHash(
                    resultSet.getLong("id"),
                    resultSet.getLong("orderId"),
                    resultSet.getLong("user_id"),
                    resultSet.getString("data_hash"),
                    resultSet.getString("publicKey"),
                    resultSet.getTimestamp("createdAt"),
                    resultSet.getTimestamp("updatedAt")
            );
        } catch (Exception e) {
            System.out.println("OrderHashMapper.mapRow: " + e.getMessage());
        }
        return null;
    }
}
