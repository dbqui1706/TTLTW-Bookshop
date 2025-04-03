package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal basePrice;
    private BigDecimal discountPercent;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}