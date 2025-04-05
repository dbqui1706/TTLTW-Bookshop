package com.example.bookshopwebapplication.http.request.order;

import com.example.bookshopwebapplication.http.response.order.PriceSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long userId;
    private List<CartItemRequest> cartItems;
    private BigDecimal totalAmount;
    private DeliveryAddressRequest deliveryAddress;
    private Long deliveryMethod;
    private String paymentMethod;
    private BigDecimal deliveryPrice;
    private BigDecimal discountPromotionAmount;
    private String couponCode;
    private PriceSummary priceSummary;
}
