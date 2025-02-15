package com.example.bookshopwebapplication.network;

import java.util.List;
import java.util.StringJoiner;

public record OrderNetwork(
        Integer deliveryMethod,
        Double deliveryPrice,
        List<ProductRequestForOrderInfo> orderItems
) {
    @Override
    public String toString(){
        return new StringJoiner(", ", OrderNetwork.class.getSimpleName() + "[", "]")
                .add("deliveryMethod=" + deliveryMethod)
                .add("deliveryPrice=" + deliveryPrice)
                .add("orderItems=" + orderItems)
                .toString();
    }
}

