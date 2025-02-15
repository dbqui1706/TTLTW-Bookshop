package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;

@Data
@ToString
@NoArgsConstructor
public class UserKeys {
    private Long id;
    private Long userId;
    private String publicKey;
    private int isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;


}
