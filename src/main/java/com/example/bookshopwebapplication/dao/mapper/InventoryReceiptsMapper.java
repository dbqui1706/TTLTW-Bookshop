package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.InventoryReceipts;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryReceiptsMapper implements IRowMapper<InventoryReceipts> {
    @Override
    public InventoryReceipts mapRow(ResultSet resultSet) throws SQLException {
        return InventoryReceipts.builder()
                .id(resultSet.getLong("id"))
                .receiptCode(resultSet.getString("receipt_code"))
                .receiptType(resultSet.getString("receipt_type"))
                .supplier(resultSet.getString("supplier"))
                .customerId(resultSet.getLong("customer_id"))
                .orderId(resultSet.getLong("order_id"))
                .totalItems(resultSet.getInt("total_items"))
                .totalQuantity(resultSet.getInt("total_quantity"))
                .notes(resultSet.getString("notes"))
                .status(resultSet.getString("status"))
                .createdBy(resultSet.getLong("created_by"))
                .approvedBy(resultSet.getLong("approved_by"))
                .completedAt(resultSet.getTimestamp("completed_at"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .build();
    }
}
