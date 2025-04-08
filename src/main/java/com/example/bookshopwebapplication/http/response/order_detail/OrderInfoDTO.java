package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private Long id;
    private String orderCode;
    private String status;
    private String statusText; // Text hiển thị của trạng thái
    private java.sql.Timestamp orderDate;
    private String formattedOrderDate; // Định dạng ngày giờ để hiển thị
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private String note;
    private boolean isVerified;
}
