package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReceipts {
    private Long id ;
    private String receiptCode; // Mã phiếu, ví dụ: NK-20250422-001
    private String receiptType; // Loại phiếu: import/export
    private String supplier; // Nhà cung cấp (cho phiếu nhập)
    private Long customerId; // Khách hàng (cho phiếu xuất)
    private Long orderId; // ID đơn hàng liên quan (nếu có)
    private int totalItems; // Tổng số mặt hàng
    private int totalQuantity; // Tổng số lượng
    private String notes; // Ghi chú
    private String status; // Trạng thái: draft, pending, completed, cancelled
    private Long createdBy; // Người tạo
    private Long approvedBy; // Người duyệt
    private Timestamp createdAt; // Thời gian tạo
    private Timestamp updatedAt; // Thời gian cập nhật
    private Timestamp completedAt; // Thời gian hoàn thành
    private List<InventoryReceiptItems> items = new ArrayList<>();
}


/*
*  id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_code VARCHAR(20) NOT NULL,  -- Mã phiếu, ví dụ: NK-20250422-001
    receipt_type ENUM('import', 'export') NOT NULL,  -- Loại phiếu: nhập/xuất
    supplier_id BIGINT NULL,  -- ID nhà cung cấp (cho phiếu nhập)
    customer_id BIGINT NULL,  -- ID khách hàng (cho phiếu xuất)
    order_id BIGINT NULL,  -- ID đơn hàng liên quan (nếu có)
    total_items INT NOT NULL,  -- Tổng số mặt hàng
    total_quantity INT NOT NULL,  -- Tổng số lượng
    notes TEXT NULL,  -- Ghi chú
    status ENUM('draft', 'pending', 'completed', 'cancelled') NOT NULL DEFAULT 'draft',
    created_by BIGINT NOT NULL,  -- Người tạo
    approved_by BIGINT NULL,  -- Người duyệt
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME NULL  -- Thời gian hoàn thành
 */