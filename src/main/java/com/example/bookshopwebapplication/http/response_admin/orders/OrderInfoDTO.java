package com.example.bookshopwebapplication.http.response_admin.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDTO {
    private Long id;
    private String orderCode;
    private String status;
    private String statusText;
    private Timestamp createdAt;
    private Double subtotal;
    private Double deliveryPrice;
    private Double discountAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String couponCode;
    private String note;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;

    // Thông tin khách hàng/người nhận
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String customerAddressLine1;
    private String customerAddressLine2;
    private String customerFullAddress;

    public OrderInfoDTO(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.orderCode = rs.getString("code");
        this.status = rs.getString("status");
        this.statusText = convertStatusToVietnamese(rs.getString("status"));
        this.createdAt = rs.getTimestamp("created_at");
        this.note = rs.getString("note");

        // Thông tin tài chính
        this.subtotal = rs.getDouble("subtotal");
        this.deliveryPrice = rs.getDouble("shipping");
        this.discountAmount = rs.getDouble("discount");
        this.totalAmount = rs.getDouble("total");
        this.couponCode = rs.getString("coupon_code");

        // Thông tin người dùng
        this.userId = rs.getLong("user_id");

        // Thông tin người nhận/khách hàng
        this.customerName = rs.getString("customer_name");
        this.customerPhone = rs.getString("customer_phone");
        this.customerEmail = rs.getString("customer_email");
        this.customerAddressLine1 = rs.getString("customer_address_line1");
        this.customerAddressLine2 = rs.getString("customer_address_line2");
        this.customerFullAddress = rs.getString("customer_full_address");
    }

    /**
     * Chuyển đổi trạng thái đơn hàng từ tiếng Anh sang tiếng Việt
     */
    private String convertStatusToVietnamese(String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("pending", "Chờ xác nhận");
        statusMap.put("waiting_payment", "Chờ thanh toán");
        statusMap.put("payment_failed", "Thanh toán thất bại");
        statusMap.put("processing", "Đang xử lý");
        statusMap.put("shipping", "Đang giao hàng");
        statusMap.put("delivered", "Đã giao hàng");
        statusMap.put("cancelled", "Đã hủy");
        statusMap.put("refunded", "Đã hoàn tiền");

        return statusMap.getOrDefault(status, status);
    }
}
