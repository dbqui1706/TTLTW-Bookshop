package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.InventoryImport;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryImportMapper implements IRowMapper<InventoryImport> {
    @Override
    public InventoryImport mapRow(ResultSet rs) throws SQLException {
        return InventoryImport.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .costPrice(rs.getDouble("cost_price"))
                .supplier(rs.getString("supplier"))
                .importDate(rs.getTimestamp("import_date"))
                .notes(rs.getString("notes"))
                .createdBy(rs.getLong("created_by"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }
}
