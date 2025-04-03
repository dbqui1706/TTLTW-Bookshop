package com.example.bookshopwebapplication.http.request.order;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressRequest {
    private String fullname;
    private String phone;
    private String provinceCode;
    private String province;
    private String districtCode;
    private String district;
    private String wardCode;
    private String ward;
    private String address;
    private Boolean isDefault;
    private String addressType;
}