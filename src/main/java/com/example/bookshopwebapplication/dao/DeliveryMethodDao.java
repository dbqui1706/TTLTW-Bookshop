package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.DeliveryMethodMapper;
import com.example.bookshopwebapplication.entities.DeliveryMethod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryMethodDao extends AbstractDao<DeliveryMethod> {

    public DeliveryMethodDao() {
        super("delivery_method");
    }

    public List<DeliveryMethod> getAll() {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM bookshopdb.delivery_method WHERE is_active = 1"
        );
        List<DeliveryMethod> deliveryMethods = query(builderSQL.toString(), new DeliveryMethodMapper());
        return deliveryMethods.isEmpty() ? new ArrayList<>() : deliveryMethods;
    }

    @Override
    public DeliveryMethod mapResultSetToEntity(ResultSet resultSet) throws SQLException {
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
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public Optional<DeliveryMethod> findById(Long deliveryMethod) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM bookshopdb.delivery_method WHERE id = ?"
        );
        try {
            List<DeliveryMethod> deliveryMethods = query(builderSQL.toString(), new DeliveryMethodMapper(), deliveryMethod);
            if (deliveryMethods.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(deliveryMethods.get(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
