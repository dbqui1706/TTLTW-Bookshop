package com.example.bookshopwebapplication.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialPermission {
    private Long userId;
    private Long permissionId;
    private boolean isGranted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Thông tin bổ sung cho hiển thị
    private String permissionName;
    private String permissionCode;
    private String permissionModule;
}
