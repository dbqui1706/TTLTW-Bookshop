package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean requiresConfirmation;
    private BigDecimal processingFee;
}
