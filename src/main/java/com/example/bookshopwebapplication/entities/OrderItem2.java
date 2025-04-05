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
    private Double basePrice;
    private Double discountPercent;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}