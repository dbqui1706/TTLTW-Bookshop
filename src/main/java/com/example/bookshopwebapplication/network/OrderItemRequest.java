package com.example.bookshopwebapplication.network;

import java.util.StringJoiner;

public record OrderItemRequest(long productId, double price, double discount, int quantity) {

    @Override
    public String toString() {
        //Nối nhiều chuỗi thành 1 chuỗi duy nhất
        return new StringJoiner(", ", OrderItemRequest.class.getSimpleName() + "[", "]")
                .add("productId=" + productId)
                .add("price=" + price)
                .add("discount=" + discount)
                .add("quantity=" + quantity)
                .toString();
    }
}
