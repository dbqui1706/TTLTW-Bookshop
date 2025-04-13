package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInfoDTO {
    private Long id;
    private String receiverName;
    private String receiverEmail;
    private String receiverPhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String ward;
    private String postalCode;
    private String fullAddress;
    private String shippingNotes;
    private String trackingNumber;
    private String shippingCarrier;

}
