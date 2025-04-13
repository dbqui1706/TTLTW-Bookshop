package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String orderCode;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String status;
    private String statusText;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String paymentStatus;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private Boolean isVerified;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    // Thông tin shipping cơ bản
    private String receiverName;
    private String receiverPhone;
    private String address;
    List<OrderItemDTO> items;
}