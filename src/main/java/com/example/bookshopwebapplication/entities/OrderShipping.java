package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShipping {
    private Long id;
    private Long orderId;
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
