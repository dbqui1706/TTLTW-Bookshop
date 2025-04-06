package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderItem2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItem2Mapper implements IRowMapper<OrderItem2> {
    @Override
    public OrderItem2 mapRow(ResultSet resultSet) throws SQLException {
        return OrderItem2.builder()
                .id(resultSet.getLong("id"))
                .orderId(resultSet.getLong("order_id"))
                .productId(resultSet.getLong("product_id"))
                .productName(resultSet.getString("product_name"))
                .productImage(resultSet.getString("product_image"))
                .basePrice(resultSet.getDouble("base_price"))
                .discountPercent(resultSet.getDouble("discount_percent"))
                .price(resultSet.getDouble("price"))
                .quantity(resultSet.getInt("quantity"))
                .subtotal(resultSet.getDouble("subtotal"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .build();
    }
}
