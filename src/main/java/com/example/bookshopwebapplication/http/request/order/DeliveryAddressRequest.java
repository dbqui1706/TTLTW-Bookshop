package com.example.bookshopwebapplication.http.request.order;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressRequest {
    private String recipientName;
    private String phoneNumber;
    private String provinceCode;
    private String provinceName;
    private String districtCode;
    private String districtName;
    private String wardCode;
    private String wardName;
    private String addressLine1;
    private Boolean isDefault;
    private String addressType;
}