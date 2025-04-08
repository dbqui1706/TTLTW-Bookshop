package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryMethodDTO {
    private Long id;
    private String name;
    private String description;
    private String estimatedDays;
    private Double price;
}