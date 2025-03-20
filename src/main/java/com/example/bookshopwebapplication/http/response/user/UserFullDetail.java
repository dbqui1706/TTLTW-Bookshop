package com.example.bookshopwebapplication.http.response.user;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class UserFullDetail {
    private long id;
    private String username;
    private String fullname;
    private String email;
    private String phoneNumber;
    private boolean gender;
    private String address;
    private String role;
    private Timestamp createdAt;

    // Thông tin trạng thái
    private boolean isActive;
    private boolean isLocked;
    private String lockReason;
    private Timestamp lockTime;
    private Timestamp unlockTime;
    private Timestamp lastLoginTime;
    private Timestamp lastActiveTime;
    private int failedLoginCount;
    private Timestamp statusUpdatedAt;

    // Lịch sử đăng nhập
    private List<LoginHistory> loginHistory;
}
