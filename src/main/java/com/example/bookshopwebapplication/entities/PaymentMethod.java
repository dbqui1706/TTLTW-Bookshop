package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentMethod {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private Boolean requireConfirmation;
    private Double processingFee;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
