package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryMethodResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String estimatedDays;
    private String icon;
}