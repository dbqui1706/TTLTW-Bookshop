package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.PaymentTransaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentTransactionDao extends AbstractDao<PaymentTransaction> {

    public PaymentTransactionDao() {
        super("payment_transaction");
    }

    @Override
    public PaymentTransaction mapResultSetToEntity(ResultSet resultSet) throws SQLException {
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
            System.out.println("Error mapping result set to entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Long save(PaymentTransaction transaction) {
        clearSQL();
        builderSQL.append("INSERT INTO payment_transaction (order_id, payment_method_id, amount, ");
        builderSQL.append("transaction_code, payment_provider_ref, status, payment_date, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), transaction.getOrderId(), transaction.getPaymentMethodId(),
                transaction.getAmount(), transaction.getTransactionCode(), transaction.getPaymentProviderRef(),
                transaction.getStatus(), transaction.getPaymentDate(), transaction.getNote());
    }
}
