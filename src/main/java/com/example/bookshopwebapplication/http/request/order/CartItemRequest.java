package com.example.bookshopwebapplication.http.request.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Float discount;
    private String name;
    private String image;
}