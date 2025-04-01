package com.example.bookshopwebapplication.http.request.cart;

import lombok.Data;

@Data
public class UpdateCart {
    private Long cartItemId;
    private int quantity;
}
