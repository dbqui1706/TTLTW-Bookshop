package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.entities.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMethodMapper implements com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper<com.example.bookshopwebapplication.entities.PaymentMethod> {
    @Override
    public PaymentMethod mapRow(ResultSet resultSet) throws SQLException {
        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setId(resultSet.getLong("id"));
            paymentMethod.setName(resultSet.getString("name"));
            paymentMethod.setCode(resultSet.getString("code"));
            paymentMethod.setDescription(resultSet.getString("description"));
            paymentMethod.setIcon(resultSet.getString("icon"));
            paymentMethod.setRequireConfirmation(resultSet.getBoolean("requires_confirmation"));
            paymentMethod.setProcessingFee(resultSet.getDouble("processing_fee"));
            paymentMethod.setActive(resultSet.getBoolean("is_active"));
            paymentMethod.setCreatedAt(resultSet.getTimestamp("created_at"));
            paymentMethod.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return paymentMethod;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
