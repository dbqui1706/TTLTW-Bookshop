package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShippingResponse {
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
    private String shippingNotes;
    private String trackingNumber;
    private String shippingCarrier;
}