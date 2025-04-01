package com.example.bookshopwebapplication.http.request.cart;

import lombok.Data;

@Data
public class AddCartRequest {
    private long userId;
    private long productId;
    private int quantity;
}
