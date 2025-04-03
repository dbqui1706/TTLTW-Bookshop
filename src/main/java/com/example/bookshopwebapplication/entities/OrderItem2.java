package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem2 {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal basePrice;
    private BigDecimal discountPercent;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}