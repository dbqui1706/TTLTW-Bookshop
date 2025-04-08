package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
}