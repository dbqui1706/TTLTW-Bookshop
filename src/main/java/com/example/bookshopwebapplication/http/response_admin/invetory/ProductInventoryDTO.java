package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * this để hiển thị thông tin sản phẩm trong màn hình nhập/xuất kho
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryDTO {
    private Long id;
    private String name;
    private float price;
    private float discount;
    private String author;
    private String publisher;
    private String imageName;

    // Thông tin tồn kho
    private int actualQuantity;      // Số lượng thực tế trong kho
    private int availableQuantity;   // Số lượng có thể sử dụng (đã trừ đặt trước)
    private int reservedQuantity;    // Số lượng đã đặt
    private int reorderThreshold;    // Ngưỡng cảnh báo cần nhập thêm

    // Thông tin phụ
    private String categories;       // Danh mục sách, phân tách bằng dấu phẩy
    private Double lastImportPrice;   // Giá nhập kho lần cuối

    // Các trường tính toán
    private String stockStatus;      // Trạng thái tồn kho (out_of_stock, low_stock, medium_stock, high_stock)
    private Double netPrice;          // Giá sau khi trừ chiết khấu

    public ProductInventoryDTO(ResultSet rs) throws SQLException {
        this.setId(rs.getLong("id"));
        this.setName(rs.getString("name"));
        this.setPrice(rs.getFloat("price"));
        this.setDiscount(rs.getFloat("discount"));
        this.setAuthor(rs.getString("author"));
        this.setPublisher(rs.getString("publisher"));
        this.setImageName(rs.getString("imageName"));

        // Xử lý trường hợp NULL cho số lượng
        int actualQuantity = 0;
        rs.getInt("actual_quantity");
        if (!rs.wasNull()) {
            actualQuantity = rs.getInt("actual_quantity");
        }
        this.setActualQuantity(actualQuantity);

        int availableQuantity = 0;
        rs.getInt("available_quantity");
        if (!rs.wasNull()) {
            availableQuantity = rs.getInt("available_quantity");
        }
        this.setAvailableQuantity(availableQuantity);

        int reservedQuantity = 0;
        rs.getInt("reserved_quantity");
        if (!rs.wasNull()) {
            reservedQuantity = rs.getInt("reserved_quantity");
        }
        this.setReservedQuantity(reservedQuantity);

        int reorderThreshold = 5; // Giá trị mặc định
        rs.getInt("reorder_threshold");
        if (!rs.wasNull()) {
            reorderThreshold = rs.getInt("reorder_threshold");
        }
        this.setReorderThreshold(reorderThreshold);

        this.setCategories(rs.getString("categories"));

        double lastImportPrice = 0;
        rs.getFloat("last_import_price");
        if (!rs.wasNull()) {
            lastImportPrice = rs.getFloat("last_import_price");
        }
        this.setLastImportPrice(lastImportPrice);

        // Tính toán trạng thái tồn kho
        this.setStockStatus(calculateStockStatus(actualQuantity, reorderThreshold));

        // Tính giá thực (sau khi trừ chiết khấu)
        double netPrice = this.getPrice() * (1 - this.getDiscount() / 100);
        this.setNetPrice(netPrice);
    }

    /**
     * Tính toán trạng thái tồn kho dựa trên số lượng và ngưỡng cảnh báo
     */
    private String calculateStockStatus(int quantity, int threshold) {
        if (quantity <= 0) {
            return "out_of_stock";  // Hết hàng
        } else if (quantity <= threshold) {
            return "low_stock";     // Sắp hết
        } else if (quantity <= threshold * 3) {
            return "medium_stock";  // Bình thường
        } else {
            return "high_stock";    // Nhiều
        }
    }

    /**
     * Tính toán phần trăm tồn kho (để hiển thị thanh progress)
     *
     * @return Giá trị phần trăm từ 0-100
     */
    public int getStockPercentage() {
        // Giả sử stock trên 200 là 100%
        int maxStock = 200;
        int percentage = (int) (((float) actualQuantity / maxStock) * 100);
        return Math.min(percentage, 100);
    }

    /**
     * Kiểm tra sản phẩm có còn hàng không
     *
     * @return true nếu còn hàng, false nếu hết hàng
     */
    public boolean isInStock() {
        return availableQuantity > 0;
    }

    /**
     * Kiểm tra sản phẩm có đang ở mức thấp không
     *
     * @return true nếu số lượng dưới hoặc bằng ngưỡng cảnh báo
     */
    public boolean isLowStock() {
        return actualQuantity <= reorderThreshold && actualQuantity > 0;
    }

    /**
     * Lấy màu hiển thị cho trạng thái tồn kho
     *
     * @return Tên class CSS tương ứng
     */
    public String getStockStatusColor() {
        if (stockStatus == null) {
            return "bg-secondary";
        }

        switch (stockStatus) {
            case "out_of_stock":
                return "bg-danger";
            case "low_stock":
                return "bg-warning";
            case "medium_stock":
                return "bg-info";
            case "high_stock":
                return "bg-success";
            default:
                return "bg-secondary";
        }
    }

    /**
     * Lấy tên hiển thị cho trạng thái tồn kho
     *
     * @return Tên trạng thái
     */
    public String getStockStatusText() {
        if (stockStatus == null) {
            return "Không xác định";
        }

        switch (stockStatus) {
            case "out_of_stock":
                return "Hết hàng";
            case "low_stock":
                return "Sắp hết";
            case "medium_stock":
                return "Bình thường";
            case "high_stock":
                return "Nhiều";
            default:
                return "Không xác định";
        }
    }
}