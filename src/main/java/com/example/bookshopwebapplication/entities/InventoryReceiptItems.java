package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReceiptItems {
    private Long id;
    private Long receiptId; // ID phiếu
    private Long productId; // ID sản phẩm
    private int quantity; // Số lượng
    private Double unitPrice; // Đơn giá (có thể null với xuất kho)
    private String notes; // Ghi chú cho từng sản phẩm
    private String createdAt; // Thời gian tạo
}

//id BIGINT AUTO_INCREMENT PRIMARY KEY,
//receipt_id BIGINT NOT NULL,  -- ID phiếu
//product_id BIGINT NOT NULL,  -- ID sản phẩm
//quantity INT NOT NULL,  -- Số lượng
//unit_price FLOAT NULL,  -- Đơn giá (có thể null với xuất kho)
//notes TEXT NULL,  -- Ghi chú cho từng sản phẩm
//created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,