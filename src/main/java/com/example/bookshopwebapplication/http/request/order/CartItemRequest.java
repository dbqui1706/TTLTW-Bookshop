package com.example.bookshopwebapplication.http.request.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private Long cartItemId;
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double originalPrice;
    private Double discount;
    private String name;
    private String image;
}