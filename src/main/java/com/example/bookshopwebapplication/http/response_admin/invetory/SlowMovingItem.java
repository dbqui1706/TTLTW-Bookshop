package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@AllArgsConstructor
public class SlowMovingItem {
    private Long productId;
    private String productName;
    private String author;
    private String thumbnail;
    private float price;
    private Long categoryId;
    private String categoryName;
    private int stockQuantity;
    private int daysInStock;
    private double turnoverRate;
    private double stockValue;
    private String suggestion;

    public SlowMovingItem(ResultSet rs) throws SQLException {
        this.productId = rs.getLong("product_id");
        this.productName = rs.getString("product_name");
        this.author = rs.getString("author");
        this.thumbnail = rs.getString("thumbnail");
        this.price = rs.getFloat("price");
        this.categoryId = rs.getLong("category_id");
        this.categoryName = rs.getString("category_name");
        this.stockQuantity = rs.getInt("stock_quantity");
        this.daysInStock = rs.getInt("days_without_sale");
        this.turnoverRate = rs.getDouble("turnover_rate");
        this.stockValue = rs.getDouble("stock_value");
        this.suggestion = generateSuggestion(this);
    }

    /**
     * Tạo đề xuất dựa trên dữ liệu sản phẩm
     * @param product Thông tin sản phẩm
     * @return Đề xuất xử lý
     */
    private String generateSuggestion(SlowMovingItem product) {
        if (product.getDaysInStock() > 365) {
            return "Xem xét thanh lý";
        } else if (product.getDaysInStock() > 180) {
            return "Giảm giá mạnh";
        } else if (product.getDaysInStock() > 90) {
            return "Khuyến mãi";
        } else if (product.getTurnoverRate() < 0.3) {
            return "Cần xem xét";
        } else {
            return "Theo dõi thêm";
        }
    }
}
