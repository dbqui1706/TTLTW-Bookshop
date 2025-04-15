package com.example.bookshopwebapplication.http.response_admin.orders;

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
public class DeliveryMethodDTO {
    private Long id;
    private String name;
    private String description;
    private String estimatedDays;
    private Double price;
    private String icon;

    public DeliveryMethodDTO (ResultSet rs) throws SQLException {
        this.setId(rs.getLong("id"));
        this.setName(rs.getString("name"));
        this.setDescription(rs.getString("description"));
        this.setEstimatedDays(rs.getString("estimated_days"));
        this.setPrice(rs.getDouble("price"));
        this.setIcon(rs.getString("icon"));
    }
}
