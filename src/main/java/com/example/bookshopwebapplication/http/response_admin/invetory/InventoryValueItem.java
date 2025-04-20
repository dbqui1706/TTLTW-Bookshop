package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@AllArgsConstructor
public class InventoryValueItem {
    private Long productId;
    private String productName;
    private String productAuthor;
    private String productImage;
    private double unitPrice;
    private int inventoryQuantity;
    private double inventoryValue;
    private String categoryName;

    public InventoryValueItem(ResultSet rs) throws SQLException {
        this.setProductId(rs.getLong("product_id"));
        this.setProductName(rs.getString("product_name"));
        this.setProductAuthor(rs.getString("product_author"));
        this.setProductImage(rs.getString("product_image"));
        this.setUnitPrice(rs.getDouble("unit_price"));
        this.setInventoryQuantity(rs.getInt("inventory_quantity"));
        this.setInventoryValue(rs.getDouble("inventory_value"));
        this.setCategoryName(rs.getString("category_name"));
    }
}
