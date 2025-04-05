package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.OrderShipping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OderShippingDao extends AbstractDao<OrderShipping> {
    public OderShippingDao() {
        super("order_shipping");
    }
    public Long saveWithConnection(OrderShipping orderShipping, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO order_shipping (order_id, receiver_name, receiver_email, receiver_phone, ");
        builderSQL.append("address_line1, address_line2, city, district, ward, postal_code, shipping_notes, ");
        builderSQL.append("tracking_number, shipping_carrier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insertWithConnection(conn, builderSQL.toString(), orderShipping.getOrderId(),
                orderShipping.getReceiverName(), orderShipping.getReceiverEmail(),
                orderShipping.getReceiverPhone(), orderShipping.getAddressLine1(),
                orderShipping.getAddressLine2(), orderShipping.getCity(),
                orderShipping.getDistrict(), orderShipping.getWard(),
                orderShipping.getPostalCode(), orderShipping.getShippingNotes(),
                orderShipping.getTrackingNumber(), orderShipping.getShippingCarrier());
    }

    public Long save(OrderShipping orderShipping) {
        clearSQL();
        builderSQL.append("INSERT INTO order_shipping (order_id, receiver_name, receiver_email, receiver_phone, ");
        builderSQL.append("address_line1, address_line2, city, district, ward, postal_code, shipping_notes, ");
        builderSQL.append("tracking_number, shipping_carrier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), orderShipping.getOrderId(),
                orderShipping.getReceiverName(), orderShipping.getReceiverEmail(),
                orderShipping.getReceiverPhone(), orderShipping.getAddressLine1(),
                orderShipping.getAddressLine2(), orderShipping.getCity(),
                orderShipping.getDistrict(), orderShipping.getWard(),
                orderShipping.getPostalCode(), orderShipping.getShippingNotes(),
                orderShipping.getTrackingNumber(), orderShipping.getShippingCarrier());
    }

    @Override
    public OrderShipping mapResultSetToEntity(ResultSet rs) throws SQLException {
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
            System.out.println("Error mapping ResultSet to OrderShipping entity");
            e.printStackTrace();
        }
        return null;
    }
}
