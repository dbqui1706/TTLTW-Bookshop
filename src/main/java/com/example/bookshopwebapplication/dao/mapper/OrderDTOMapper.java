package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.http.response_admin.orders.OrderDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDTOMapper implements IRowMapper<OrderDTO> {
    @Override
    public OrderDTO mapRow(ResultSet resultSet) throws SQLException {
        return OrderDTO.builder()
                .id(resultSet.getLong("id"))
                .orderCode(resultSet.getString("order_code"))
                .userId(resultSet.getLong("user_id"))
                .userName(resultSet.getString("user_name"))
                .userEmail(resultSet.getString("user_email"))
                .userPhone(resultSet.getString("user_phone"))
                .status(resultSet.getString("status"))
                .statusText(getStatusText(resultSet.getString("status")))

                .paymentStatus(resultSet.getString("payment_status"))
                .paymentMethodName(resultSet.getString("payment_method_name"))
                .paymentMethodId(resultSet.getLong("payment_method_id"))

                .subtotal(resultSet.getDouble("subtotal"))
                .deliveryPrice(resultSet.getDouble("delivery_price"))
                .taxAmount(resultSet.getDouble("tax_amount"))
                .totalAmount(resultSet.getDouble("total_amount"))
                .couponCode(resultSet.getString("coupon_code"))
                .isVerified(resultSet.getBoolean("is_verified"))

                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))

                // Thông tin shipping cơ bản
                .receiverName(resultSet.getString("receiver_name"))
                .receiverPhone(resultSet.getString("receiver_phone"))
                .address(resultSet.getString("address"))
                .build();
    }

    /**
     * Lấy text hiển thị cho trạng thái đơn hàng
     *
     * @param status Mã trạng thái
     * @return String Text hiển thị
     */
    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "Chờ xác nhận";
            case "waiting_payment":
                return "Chờ thanh toán";
            case "payment_failed":
                return "Thanh toán thất bại";
            case "processing":
                return "Đang xử lý";
            case "shipping":
                return "Đang giao hàng";
            case "delivered":
                return "Đã giao hàng";
            case "cancelled":
                return "Đã hủy";
            case "refunded":
                return "Đã hoàn tiền";
            default:
                return status;
        }
    }
}
