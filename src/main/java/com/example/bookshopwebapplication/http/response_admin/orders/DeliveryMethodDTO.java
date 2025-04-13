package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String icon;
}
