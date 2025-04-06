package com.example.bookshopwebapplication.http.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderCode;
    private Long userId;
    private String status;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private Boolean isVerified;
    private String note;
    private Timestamp createdAt;
    private DeliveryMethodResponse deliveryMethod;
    private PaymentMethodResponse paymentMethod;
    private List<OrderItemResponse> items;
    private OrderShippingResponse shipping;
    private PaymentTransactionResponse transaction;
    private PriceSummary priceSummary;
    private String paymentUrl;
    private Boolean requirePayment;
}
