package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserSession {
    private Long id;
    private Long userId;
    private String sessionToken;
    private String ipAddress;
    private String deviceInfo;
    private Timestamp startTime;
    private Timestamp expireTime;
    private Timestamp listActivity;
    private boolean isActive;
}
