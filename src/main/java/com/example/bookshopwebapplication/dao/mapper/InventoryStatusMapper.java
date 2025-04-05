package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.InventoryStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryStatusMapper implements IRowMapper<InventoryStatus> {
    @Override
    public InventoryStatus mapRow(ResultSet rs) throws SQLException {
        return InventoryStatus.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .actualQuantity(rs.getInt("actual_quantity"))
                .availableQuantity(rs.getInt("available_quantity"))
                .reservedQuantity(rs.getInt("reserved_quantity"))
                .reorderThreshold(rs.getInt("reorder_threshold"))
                .lastUpdated(rs.getTimestamp("last_updated"))
                .build();
    }
}
