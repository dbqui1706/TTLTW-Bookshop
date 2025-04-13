package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.http.response_admin.orders.OrderItemDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemDTOMapper implements IRowMapper<OrderItemDTO> {
    @Override
    public OrderItemDTO mapRow(ResultSet resultSet) throws SQLException {
        return OrderItemDTO.builder()
                .id(resultSet.getLong("id"))
                .productId(resultSet.getLong("product_id"))
                .productName(resultSet.getString("product_name"))
                .productImage(resultSet.getString("product_image"))
                .basePrice(resultSet.getDouble("base_price"))
                .discountPercent(resultSet.getDouble("discount_percent"))
                .price(resultSet.getDouble("price"))
                .quantity(resultSet.getInt("quantity"))
                .subtotal(resultSet.getDouble("subtotal"))
                .build();
    }
}
