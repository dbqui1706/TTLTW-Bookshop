package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private String transactionCode;
    private String paymentProviderRef;
    private String status;
    private BigDecimal amount;
    private Timestamp paymentDate;
}