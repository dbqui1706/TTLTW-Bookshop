package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryImport {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Double costPrice;
    private String supplier;
    private Timestamp importDate;
    private String notes;
    private Long createdBy;
    private Timestamp createdAt;
}