package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
public class InventoryHistoryItem {
    private Long id;
    private Timestamp createdAt;
    private Long productId;
    private String productName;
    private String productImage;
    private String actionType;
    private int previousQuantity;
    private int quantityChange;
    private int currentQuantity;
    private Long referenceId;
    private String referenceType;
    private String reason;
    private String createdByName;

    public InventoryHistoryItem(ResultSet rs) throws SQLException {
        this.setId(rs.getLong("id"));
        this.setCreatedAt(rs.getTimestamp("createdAt"));
        this.setProductId(rs.getLong("productId"));
        this.setProductName(rs.getString("productName"));
        this.setProductImage(rs.getString("productImage"));
        this.setActionType(rs.getString("actionType"));
        this.setPreviousQuantity(rs.getInt("previousQuantity"));
        this.setQuantityChange(rs.getInt("quantityChange"));
        this.setCurrentQuantity(rs.getInt("currentQuantity"));
        this.setReferenceId(rs.getLong("referenceId"));
        this.setReferenceType(rs.getString("referenceType"));
        this.setReason(rs.getString("reason"));
        this.setCreatedByName(rs.getString("createdByName"));
    }
}
