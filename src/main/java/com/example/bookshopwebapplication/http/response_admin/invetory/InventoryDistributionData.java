package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDistributionData {
    private String category;
    private int productCount;
    private int totalQuantity;
    private double totalValue;
    private double perByQuantity;

    public InventoryDistributionData(ResultSet rs) throws SQLException {
        this.setCategory(rs.getString("category_name"));
        this.setProductCount(rs.getInt("product_count"));
        this.setTotalQuantity(rs.getInt("total_quantity"));
        this.setTotalValue(rs.getDouble("total_value"));
        this.setPerByQuantity(rs.getDouble("percentage_by_quantity"));
    }
}
