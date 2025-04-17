package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    private int productId;
    private String productName;
    private String author;
    private String image;
    private String categories;
    private int stock;
    private int reserved;
    private int available;
    private int threshold;
    private Timestamp lastUpdated;
    private int storageDays;
    private int daysSinceLastSale;
    private String salesStatus;
    private String status;
    private double price;
    private double inventoryValue;

    public InventoryItem(ResultSet rs) throws SQLException {
        this.productId = rs.getInt("product_id");
        this.productName = rs.getString("product_name");
        this.author = rs.getString("product_author");
        this.image = rs.getString("product_image");
        this.categories = rs.getString("categories");
        this.stock = rs.getInt("stock_quantity");
        this.reserved = rs.getInt("reserved_quantity");
        this.available = rs.getInt("available_quantity");
        this.threshold = rs.getInt("threshold");
        this.lastUpdated = rs.getTimestamp("last_updated");
        this.storageDays = rs.getInt("days_in_inventory");
        this.daysSinceLastSale = rs.getInt("days_since_last_sale");
        this.salesStatus = rs.getString("sales_status");
        this.status = rs.getString("stock_status");
        this.price = rs.getDouble("price");
        this.inventoryValue = rs.getDouble("inventory_value");
    }
}
