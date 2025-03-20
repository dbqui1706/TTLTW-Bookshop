package com.example.bookshopwebapplication.http.response.user;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoginHistory {
    private long id;
    private Timestamp loginTime;
    private String ipAddress;
    private String deviceInfo;
    private String browserInfo;
    private String loginStatus;
}
