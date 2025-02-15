package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderHash {
    private long id;
    private long orderId;
    private long userId;
    private String dataHash;
    private String publicKey;
    private Timestamp createdAt;
    private Timestamp updatedAt;


}
