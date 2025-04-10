package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    private Long id;
    private String name;
    private String description;
    private boolean isSystem;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
