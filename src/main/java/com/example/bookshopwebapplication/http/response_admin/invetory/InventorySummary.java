package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummary {
    private int totalImport;
    private int totalExport;
    private int totalAdjustment;
}
