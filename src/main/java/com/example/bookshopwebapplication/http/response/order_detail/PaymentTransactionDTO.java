package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

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
    private String statusText; // Text hiển thị của trạng thái
    private java.sql.Timestamp paymentDate;
    private String note;
}