package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.Order2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Order2Mapper implements IRowMapper<Order2> {
    @Override
    public Order2 mapRow(ResultSet rs) throws SQLException {
        try {
        Order2 order = Order2.builder()
                .id(rs.getLong("id"))
                .orderCode(rs.getString("order_code"))
                .userId(rs.getLong("user_id"))
                .status(rs.getString("status"))
                .deliveryMethodId(rs.getLong("delivery_method_id"))
                .paymentMethodId(rs.getLong("payment_method_id"))
                .subtotal(rs.getBigDecimal("subtotal"))
                .deliveryPrice(rs.getBigDecimal("delivery_price"))
                .discountAmount(rs.getBigDecimal("discount_amount"))
                .taxAmount(rs.getBigDecimal("tax_amount"))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .couponCode(rs.getString("coupon_code"))
                .isVerified(rs.getBoolean("is_verified"))
                .note(rs.getString("note"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at") != null
                        ? rs.getTimestamp("updated_at") : null)
                .build();
        return order;
    } catch (Exception e) {
        System.out.println("Error MAPPER ResultSet to Order2 entity: " + e.getMessage());
        e.printStackTrace();
    }
        return null;
    }
}
