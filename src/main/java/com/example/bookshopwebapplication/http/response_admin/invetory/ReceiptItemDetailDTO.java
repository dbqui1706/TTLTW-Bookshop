package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DTO chứa thông tin chi tiết của item trong phiếu nhập/xuất kho
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemDetailDTO {
    // Thông tin từ inventory_receipt_items
    private Long id;
    private Long productId;
    private int quantity;
    private Double unitPrice;
    private String notes;

    // Thông tin từ product
    private String productName;
    private String productAuthor;
    private String productPublisher;
    private String productImage;
    private float productPrice;

    // Thông tin từ inventory_status
    private int actualQuantity;

    // Thông tin tính toán
    private Double total;

    public ReceiptItemDetailDTO(ResultSet rs) throws SQLException {
        // Đọc các giá trị cơ bản từ inventory_receipt_items
        this.setId(rs.getLong("id"));
        this.setProductId(rs.getLong("product_id"));
        this.setQuantity(rs.getInt("quantity"));
        this.setUnitPrice(rs.getDouble("unit_price"));

        // Đọc giá trị notes (có thể NULL)
        String notes = rs.getString("notes");
        if (!rs.wasNull()) {
            this.setNotes(notes);
        } else {
            this.setNotes("");
        }

        // Đọc thông tin sản phẩm từ product
        this.setProductName(rs.getString("product_name"));
        this.setProductAuthor(rs.getString("product_author"));
        this.setProductPublisher(rs.getString("product_publisher"));

        // Đọc giá trị product_image (có thể NULL)
        String productImage = rs.getString("product_image");
        if (!rs.wasNull()) {
            this.setProductImage(productImage);
        } else {
            this.setProductImage("");
        }

        this.setProductPrice(rs.getFloat("product_price"));

        // Đọc thông tin tồn kho từ inventory_status (có thể NULL)
        int actualQuantity = rs.getInt("actual_quantity");
        if (!rs.wasNull()) {
            this.setActualQuantity(actualQuantity);
        } else {
            this.setActualQuantity(0);
        }

        // Tính toán tổng tiền
        this.calculateTotal();
    }

    // Phương thức tính tổng tiền của item
    public void calculateTotal() {
        this.total = this.quantity * this.unitPrice;
    }
}