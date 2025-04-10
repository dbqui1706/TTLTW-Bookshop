package com.example.bookshopwebapplication.entities;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {
    private Long id;
    private String name;
    private String code;
    private String module;
    private String description;
    private Boolean isSystem;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}