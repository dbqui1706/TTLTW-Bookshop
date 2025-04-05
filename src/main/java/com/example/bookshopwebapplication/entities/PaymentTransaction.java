package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    private Long id;
    private Long orderId;
    private Long paymentMethodId;
    private Double amount;
    private String transactionCode;
    private String paymentProviderRef;
    private String status;
    private Timestamp paymentDate;
    private String note;
    private Long createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
