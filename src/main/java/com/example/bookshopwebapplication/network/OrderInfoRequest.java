package com.example.bookshopwebapplication.network;

import java.util.List;
import java.util.StringJoiner;

public record OrderInfoRequest(
        long userId,
        Long cartId,
        String fullName,
        String address,
        String phone,
        String email,
        List<ProductRequestForOrderInfo> products,
        double totalPrice,
        int deliveryMethod,
        double deliveryFee
) {
    @Override
    public String toString() {
        //Nối nhiều chuỗi thành 1 chuỗi duy nhất
        return new StringJoiner(", ", OrderInfoRequest.class.getSimpleName() + "[", "]")
                .add("userId=" + userId)
                .add("cartId=" + cartId)
                .add("fullName=" + fullName)
                .add("address=" + address)
                .add("phone=" + phone)
                .add("email=" + email)
                .add("products=" + products)
                .add("totalPrice=" + totalPrice)
                .add("deliveryMethod=" + deliveryMethod)
                .add("deliveryFee=" + deliveryFee)
                .toString();
    }

}

