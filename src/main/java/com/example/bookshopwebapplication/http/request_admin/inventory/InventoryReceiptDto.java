package com.example.bookshopwebapplication.http.request_admin.inventory;

import com.example.bookshopwebapplication.entities.InventoryReceiptItems;

import java.sql.Timestamp;

public class InventoryReceiptDto {
    private Long id;
    private String receiptCode;
    private String receiptType; // import hoặc export
    private Long supplierId;
    private Long customerId;
    private Long orderId;
    private int totalItems;
    private int totalQuantity;
    private String notes;
    private String status;
    private Long createdBy;
    private Long approvedBy;
    private Timestamp createdAt;
    private Timestamp completedAt;
}
