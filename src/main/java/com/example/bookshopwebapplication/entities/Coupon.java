package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Coupon {
    private Long id;
    private String code;
    private String description;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscount;
    private Timestamp startDate;
    private Timestamp endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
