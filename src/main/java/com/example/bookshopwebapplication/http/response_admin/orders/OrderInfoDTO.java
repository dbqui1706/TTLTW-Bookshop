package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDTO {
    private Long id;
    private String orderCode;
    private String status;
    private String statusText;
    private Timestamp orderDate;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private String note;
    private Boolean isVerified;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
}
