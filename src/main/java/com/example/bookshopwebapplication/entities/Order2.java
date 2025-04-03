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
    private BigDecimal subtotal;
    private BigDecimal deliveryPrice;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String couponCode;
    private Boolean isVerified;
    private String note;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
