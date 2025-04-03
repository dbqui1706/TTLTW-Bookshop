package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.PaymentTransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentTransactionMapper implements IRowMapper<PaymentTransaction> {
    @Override
    public PaymentTransaction mapRow(ResultSet resultSet) throws SQLException {
        try {
            return PaymentTransaction.builder()
                    .id(resultSet.getLong("id"))
                    .orderId(resultSet.getLong("order_id"))
                    .paymentMethodId(resultSet.getLong("payment_method_id"))
                    .amount(resultSet.getBigDecimal("amount"))
                    .transactionCode(resultSet.getString("transaction_code"))
                    .paymentProviderRef(resultSet.getString("payment_provider_ref"))
                    .status(resultSet.getString("status"))
                    .paymentDate(resultSet.getTimestamp("payment_date"))
                    .note(resultSet.getString("note"))
                    .createdBy(resultSet.getLong("created_by"))
                    .createdAt(resultSet.getTimestamp("created_at"))
                    .updatedAt(resultSet.getTimestamp("updated_at"))
                    .build();

        } catch (Exception e) {
            System.out.println("Error MAPPER result set to entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
