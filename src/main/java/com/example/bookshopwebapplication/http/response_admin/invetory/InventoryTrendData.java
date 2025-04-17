package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTrendData {
    private String date;
    private Double value;

    public InventoryTrendData(ResultSet rs) throws SQLException {
        this.date = rs.getString("label");
        this.value = rs.getDouble("value");
    }
}
