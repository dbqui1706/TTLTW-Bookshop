package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatus {
    private Long id;
    private Long productId;
    private Integer actualQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderThreshold;
    private Timestamp lastUpdated;
}
