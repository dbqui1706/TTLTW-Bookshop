package com.example.bookshopwebapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderHashDto {
    private long id;
    private OrderDto order;
    private UserDto user;
    private String dataHash;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
