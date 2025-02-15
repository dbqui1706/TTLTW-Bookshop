package com.example.bookshopwebapplication.entities;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class AuditLog {
    private Long id;
    private String ipAddress;
    private String tableName;
    private String action;
    private String level;
    private String beforeData;
    private String afterData;
    private Long modifiedBy;
    private Timestamp modifiedAt;
}
