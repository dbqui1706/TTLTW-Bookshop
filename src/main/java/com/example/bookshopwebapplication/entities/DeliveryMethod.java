package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DeliveryMethod {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String estimatedDays;
    private String icon;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
