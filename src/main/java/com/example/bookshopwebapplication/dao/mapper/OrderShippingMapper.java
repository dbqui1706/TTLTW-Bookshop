package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderShipping;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderShippingMapper implements IRowMapper<OrderShipping> {
    @Override
    public OrderShipping mapRow(ResultSet rs) throws SQLException {
        try {
            return OrderShipping.builder()
                    .id(rs.getLong("id"))
                    .orderId(rs.getLong("order_id"))
                    .receiverName(rs.getString("receiver_name"))
                    .receiverEmail(rs.getString("receiver_email"))
                    .receiverPhone(rs.getString("receiver_phone"))
                    .addressLine1(rs.getString("address_line1"))
                    .addressLine2(rs.getString("address_line2"))
                    .city(rs.getString("city"))
                    .district(rs.getString("district"))
                    .ward(rs.getString("ward"))
                    .postalCode(rs.getString("postal_code"))
                    .shippingNotes(rs.getString("shipping_notes"))
                    .trackingNumber(rs.getString("tracking_number"))
                    .shippingCarrier(rs.getString("shipping_carrier"))
                    .createdAt(rs.getTimestamp("created_at"))
                    .updatedAt(rs.getTimestamp("updated_at"))
                    .build();
        } catch (Exception e) {
            System.out.println("Error MAPPER ResultSet to OrderShipping entity");
            e.printStackTrace();
        }
        return null;
    }
}
