package com.example.bookshopwebapplication.http.response.product;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ProductDetailDto {
    private Long id;
    private String name;
    private float price;
    private float discount;
    private short quantity;
    private short totalBuy;
    private String author;
    private short pages;
    private String publisher;
    private int yearPublishing;
    private String description;
    private String imageName;
    private boolean shop;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp startsAt;
    private Timestamp endsAt;

    // Thông tin đánh giá
    private int totalProductReviews;
    private double averageRatingScore;
    private String categoryBreadcrumb;

    // Getters and setters
    // ...
}