package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.PaymentMethodMapper;
import com.example.bookshopwebapplication.entities.PaymentMethod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PaymentMethodDao extends AbstractDao<PaymentMethod> {
    public PaymentMethodDao() {
        super("payment_method");
    }

    public List<PaymentMethod> getAll(){
        clearSQL();
        builderSQL.append(
                "SELECT * FROM bookshopdb.payment_method WHERE is_active = 1"
        );
        return query(builderSQL.toString(), new PaymentMethodMapper());
    }

    public PaymentMethod findById(Long id) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM bookshopdb.payment_method WHERE id = ? AND is_active = 1"
        );
        List<PaymentMethod> rs = query(builderSQL.toString(), new PaymentMethodMapper(), id);
        return rs.isEmpty() ? null : rs.get(0);
    }

    @Override
    public PaymentMethod mapResultSetToEntity(ResultSet resultSet) throws SQLException {
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

    public Optional<PaymentMethod> findByCode(String paymentMethod) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM bookshopdb.payment_method WHERE code = ? AND is_active = 1"
        );
        List<PaymentMethod> rs = query(builderSQL.toString(), new PaymentMethodMapper(), paymentMethod);
        return Optional.ofNullable(rs.isEmpty() ? null : rs.get(0));
    }
}
