package com.example.bookshopwebapplication.http.response.product;

import lombok.Data;

@Data
public class CartProductResponse {
    private long cartItemId;
    private long productId;
    private int quantity;
    private String productName;
    private String productImage;
    private double productPrice;
    private double productDiscount;
}
