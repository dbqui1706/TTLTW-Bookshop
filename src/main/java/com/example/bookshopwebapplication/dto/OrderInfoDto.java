package com.example.bookshopwebapplication.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@ToString
@NoArgsConstructor
public class OrderInfoDto {
    private Long id;
    private OrderDto order;
    private String receiver;
    private String addressReceiver;
    private String emailReceiver;
    private String phone;
    private String city;
    private String district;
    private String ward;
    private Double totalPrice;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public OrderInfoDto(Long id,
                        OrderDto order,
                        String receiver,
                        String addressReceiver,
                        String emailReceiver,
                        String phone,
                        String city,
                        String district,
                        String ward,
                        Double totalPrice,
                        Timestamp createdAt,
                        Timestamp updatedAt) {
        this.id = id;
        this.order = order;
        this.receiver = receiver;
        this.addressReceiver = addressReceiver;
        this.emailReceiver = emailReceiver;
        this.phone = phone;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
