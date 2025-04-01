package com.example.bookshopwebapplication.http.response.cart;

import lombok.Data;

@Data
public class CartResponse {
    private long productId;
    private int quantity;
    private String productName;
    private String productImage;
    private double productPrice;
    private double totalPrice;
}
