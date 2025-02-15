package com.example.bookshopwebapplication.dto;

import com.example.bookshopwebapplication.entities.OrderItem;
import com.example.bookshopwebapplication.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Data
@ToString
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private Integer status;
    private Integer deliveryMethod;
    private Double deliveryPrice;
    private Integer isVerified;
    private Integer isTampered;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private UserDto user;
    private List<OrderItemDto> orderItems;
    private double totalPrice;
    public OrderDto(Long id,
                 Integer status,
                 Integer deliveryMethod,
                 Double deliveryPrice,
                 Timestamp createdAt,
                 Timestamp updatedAt) {
        this.id = id;
        this.status = status;
        this.deliveryMethod = deliveryMethod;
        this.deliveryPrice = deliveryPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new ArrayList<>();
    }

    public OrderDto(Long id,
                    Integer status,
                    Integer deliveryMethod,
                    Double deliveryPrice,
                    Integer isVerified,
                    Integer isTampered,
                    Timestamp createdAt,
                    Timestamp updatedAt) {
        this.id = id;
        this.status = status;
        this.deliveryMethod = deliveryMethod;
        this.deliveryPrice = deliveryPrice;
        this.isVerified = isVerified;
        this.isTampered = isTampered;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new ArrayList<>();
    }
}
