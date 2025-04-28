package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReceiptDTO {
    private Long id;
    private String receiptCode;
    private String receiptType;
    private String supplier;
    private Long customerId;
    private Long orderId;
    private int totalItems;
    private int totalQuantity;
    private double totalValue;
    private String notes;
    private String status;
    private Timestamp createdAt;
    private Timestamp completedAt;
    private Long createdById;
    private String createdByName;

    public InventoryReceiptDTO(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.receiptCode = rs.getString("receiptCode");
        this.receiptType = rs.getString("receiptType");
        this.supplier = rs.getString("supplier");
        this.customerId = rs.getLong("customerId");
        if (rs.wasNull()) this.customerId = null;
        this.orderId = rs.getLong("orderId");
        if (rs.wasNull()) this.orderId = null;
        this.totalItems = rs.getInt("totalItems");
        this.totalQuantity = rs.getInt("totalQuantity");
        this.totalValue = rs.getDouble("totalValue");
        if (rs.wasNull()) this.totalValue = 0.0;
        this.notes = rs.getString("notes");
        this.status = rs.getString("status");
        this.createdAt = rs.getTimestamp("createdAt");
        this.completedAt = rs.getTimestamp("completedAt");
        this.createdById = rs.getLong("createdById");
        this.createdByName = rs.getString("createdByName");
    }
}