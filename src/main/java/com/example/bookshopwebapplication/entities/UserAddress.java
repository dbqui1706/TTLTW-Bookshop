package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserAddress {
    private Long id;
    private Long userId;
    private String addressType;
    private String recipientName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;

    private Integer provinceCode;
    private Integer districtCode;
    private Integer wardCode;

    private String provinceName;
    private String districtName;
    private String wardName;

    private String postalCode;
    private Boolean isDefault;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}