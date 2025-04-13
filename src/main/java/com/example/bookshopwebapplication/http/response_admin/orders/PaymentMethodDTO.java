package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean requiresConfirmation;
    private Double processingFee;
}