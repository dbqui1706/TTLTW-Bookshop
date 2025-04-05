package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order2 {
    private Long id;
    private String orderCode;
    private Long userId;
    private String status;
    private Long deliveryMethodId;
    private Long paymentMethodId;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private Boolean isVerified;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
