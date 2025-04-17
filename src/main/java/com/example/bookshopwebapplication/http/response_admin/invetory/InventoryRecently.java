package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRecently {
    private Long id;              // Mã phiếu
    private Long productId;       // ID sản phẩm
    private String productName;  // Tên sản phẩm
    private String productImage; // Ảnh sản phẩm
    private String productAuthor;  // Mã sản phẩm
    private Timestamp date;     // Ngày nhập
    private int quantity;        // Số lượng
    private String createdByName; // Tên người nhập
    private String reason; // Lý do

    public InventoryRecently(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.productId = rs.getLong("product_id");
        this.productName = rs.getString("product_name");
        this.productImage = rs.getString("product_image");
        this.productAuthor = rs.getString("product_author");
        this.date = rs.getTimestamp("date");
        this.quantity = rs.getInt("quantity");
        this.createdByName = rs.getString("created_by_name");

        // Lấy lý do từ bảng inventory_recently nếu có
        try {
            this.reason = rs.getString("reason");
        } catch (SQLException e) {
            this.reason = null; // Nếu không có lý do, gán là null
        }
    }
}
