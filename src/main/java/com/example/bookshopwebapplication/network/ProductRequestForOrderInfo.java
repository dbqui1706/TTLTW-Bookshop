package com.example.bookshopwebapplication.network;

import java.util.StringJoiner;

public record ProductRequestForOrderInfo(
        long productId,
        String name,
        int quantity,
        double price,
        double discount,
        String image
) {
    @Override
    public String toString() {
        //Nối nhiều chuỗi thành 1 chuỗi duy nhất
        return new StringJoiner(", ", ProductRequestForOrderInfo.class.getSimpleName() + "[", "]")
                .add("productId=" + productId)
                .add("name=" + name)
                .add("quantity=" + quantity)
                .add("price=" + price)
                .add("discount=" + discount)
                .add("image=" + image)
                .toString();
    }
}
