package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String fullAddress; // Địa chỉ đầy đủ ghép từ các thành phần
    private String shippingNotes;
    private String trackingNumber;
    private String shippingCarrier;
}