package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO {
    private Double subtotal;
    private Double discount;
    private Double shipping;
    private Double tax;
    private Double total;
}