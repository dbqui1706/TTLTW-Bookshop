package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistory {
    private Long id;
    private Long orderId;
    private String status;
    private String note;
    private Long changedBy;
    private Timestamp createdAt;
}