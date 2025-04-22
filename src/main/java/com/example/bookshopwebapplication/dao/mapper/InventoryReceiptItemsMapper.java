package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.InventoryReceiptItems;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryReceiptItemsMapper implements IRowMapper<InventoryReceiptItems> {
    @Override
    public InventoryReceiptItems mapRow(ResultSet resultSet) throws SQLException {
        return InventoryReceiptItems.builder()
                .id(resultSet.getLong("id"))
                .receiptId(resultSet.getLong("receipt_id"))  // ID của phiếu nhập
                .productId(resultSet.getLong("product_id"))
                .quantity(resultSet.getInt("quantity"))
                .unitPrice(resultSet.getDouble("unit_price"))
                .notes(resultSet.getString("notes"))
                .build();
    }
}
