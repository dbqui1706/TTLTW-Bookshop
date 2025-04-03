package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.entities.DeliveryMethod;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryMethodMapper implements com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper<com.example.bookshopwebapplication.entities.DeliveryMethod> {
    @Override
    public DeliveryMethod mapRow(ResultSet resultSet) throws SQLException {
        try {
            DeliveryMethod deliveryMethod = new DeliveryMethod();
            deliveryMethod.setId(resultSet.getLong("id"));
            deliveryMethod.setName(resultSet.getString("name"));
            deliveryMethod.setDescription(resultSet.getString("description"));
            deliveryMethod.setPrice(resultSet.getDouble("price"));
            deliveryMethod.setEstimatedDays(resultSet.getString("estimated_days"));
            deliveryMethod.setIcon(resultSet.getString("icon"));
            deliveryMethod.setActive(resultSet.getBoolean("is_active"));
            deliveryMethod.setCreatedAt(resultSet.getTimestamp("created_at"));
            deliveryMethod.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return deliveryMethod;
        }catch (Exception e){
            throw new SQLException(e);
        }
    }
}
