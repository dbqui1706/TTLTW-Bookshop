package com.example.bookshopwebapplication.network;

import java.util.StringJoiner;

public record OrderInfoPost(
        long userId,
        Long cartId,
        String receiver,
        String emailReceiver,
        String addressReceiver,
        String phone,
        OrderNetwork order,
        double totalPrice,
        String city,
        String district,
        String ward,
        String signature,
        String jsonData
) {
    @Override
    public String toString() {
        return new StringJoiner(", ", OrderInfoPost.class.getSimpleName() + "[", "]")
                .add("userId=" + userId)
                .add("cartId=" + cartId)
                .add("receiver=" + receiver)
                .add("emailReceiver=" + emailReceiver)
                .add("addressReceiver=" + addressReceiver)
                .add("phone=" + phone)
                .add("order=" + order)
                .add("totalPrice=" + totalPrice)
                .add("city=" + city)
                .add("district=" + district)
                .add("ward=" + ward)
                .add("signature=" + signature)
                .add("jsonData=" + jsonData)
                .toString();
    }
}
