package com.example.bookshopwebapplication.http.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderCode;
    private String status;
    private String orderDate;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double totalAmount;
    private List<OrderItemDTO> orderItems;
    private int totalItems;

    // Thông tin thanh toán
    private String paymentMethod;

    // Thông tin giao hàng
    private String receiverName;
    private String receiverPhone;
    private String address;
    private String district;
    private String city;

    // Thông tin vận chuyển
    private String trackingNumber;
    private String shippingCarrier;

    // Thông tin giao dịch
    private String transactionCode;
    private Timestamp paymentDate;
    private String paymentStatus;
}