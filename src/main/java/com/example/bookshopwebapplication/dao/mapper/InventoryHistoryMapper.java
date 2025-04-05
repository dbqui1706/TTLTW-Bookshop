package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.InventoryHistory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryHistoryMapper implements IRowMapper<InventoryHistory> {
    @Override
    public InventoryHistory mapRow(ResultSet rs) throws SQLException {
        return InventoryHistory.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .quantityChange(rs.getInt("quantity_change"))
                .previousQuantity(rs.getInt("previous_quantity"))
                .currentQuantity(rs.getInt("current_quantity"))
                .actionType(InventoryHistory.ActionType.fromValue(rs.getString("action_type")))
                .reason(rs.getString("reason"))
                .referenceId(rs.getLong("reference_id"))
                .referenceType(rs.getString("reference_type"))
                .notes(rs.getString("notes"))
                .createdBy(rs.getLong("created_by"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }
}
