package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDTO {
    private Long id;
    private Double amount;
    private String transactionCode;
    private String paymentProviderRef;
    private String status;
    private String statusText;
    private Timestamp paymentDate;
    private String note;
}
