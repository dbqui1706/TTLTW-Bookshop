package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceSummary {
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double totalAmount;
}
