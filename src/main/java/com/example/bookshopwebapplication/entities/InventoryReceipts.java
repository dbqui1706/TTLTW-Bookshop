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