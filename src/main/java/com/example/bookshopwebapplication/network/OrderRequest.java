package com.example.bookshopwebapplication.network;

import java.util.List;
import java.util.StringJoiner;

public record OrderRequest(long cartId, long userId,
                           List<OrderItemRequest> orderItems) {

    @Override
    public String toString() {
        //Nối nhiều chuỗi thành 1 chuỗi duy nhất
        return new StringJoiner(", ", OrderRequest.class.getSimpleName() + "[", "]")
                .add("cartId=" + cartId)
                .add("userId=" + userId)
                .add("orderItems=" + orderItems)
                .toString();
    }
}
