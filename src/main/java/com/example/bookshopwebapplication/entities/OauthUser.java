package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@AllArgsConstructor
public class OauthUser {
    private Long id;
    private String providerID;
    private String provider;
    private Long userID;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String email;
    private String fullName;
    private final String role = "CUSTOMER";
    public OauthUser() {

    }
}
