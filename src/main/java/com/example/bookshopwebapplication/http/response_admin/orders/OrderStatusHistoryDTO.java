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
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryDTO {
    private long id;
    private String status;
    private String statusText;
    private String note;
    private long changedBy;
    private String changedByName;
    private Timestamp createdAt;


    public OrderStatusHistoryDTO mapRow(ResultSet rs) throws SQLException {
        return OrderStatusHistoryDTO.builder()
                .id(rs.getLong("id"))
                .status(rs.getString("status"))
                .statusText(convertStatusToVietnamese(rs.getString("status")))
                .note(rs.getString("note"))
                .changedBy(rs.getLong("changed_by"))
                .changedByName(rs.getString("changed_by_name"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
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
