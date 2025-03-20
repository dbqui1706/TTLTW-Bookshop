package com.example.bookshopwebapplication.http.response.category;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
public class CategoryProduct {
    private Long categoryId;
    private String categoryName;
    private String image;
    private int productCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
