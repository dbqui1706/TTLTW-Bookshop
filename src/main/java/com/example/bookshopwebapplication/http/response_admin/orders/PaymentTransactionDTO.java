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
public class PaymentTransactionDTO {
    private Long id;
    private Double amount;
    private String transactionCode;
    private String paymentProviderRef;
    private String status;
    private String statusText;
    private Timestamp paymentDate;
    private String note;

    public PaymentTransactionDTO mapRow(ResultSet rs) throws SQLException {
        return PaymentTransactionDTO.builder()
                .id(rs.getLong("id"))
                .amount(rs.getDouble("amount"))
                .transactionCode(rs.getString("transaction_code"))
                .paymentProviderRef(rs.getString("payment_provider_ref"))
                .status(rs.getString("status"))
                .statusText(convertPaymentStatusToVietnamese(rs.getString("status")))
                .paymentDate(rs.getTimestamp("payment_date"))
                .note(rs.getString("note"))
                .build();
    }


    /**
     * Chuyển đổi trạng thái thanh toán từ tiếng Anh sang tiếng Việt
     */
    private String convertPaymentStatusToVietnamese(String status) {
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("pending", "Chờ thanh toán");
        statusMap.put("waiting_payment", "Chờ thanh toán");
        statusMap.put("processing", "Đang xử lý");
        statusMap.put("completed", "Đã thanh toán");
        statusMap.put("failed", "Thất bại");
        statusMap.put("expired", "Hết hạn");
        statusMap.put("refunded", "Đã hoàn tiền");
        statusMap.put("partially_refunded", "Hoàn tiền một phần");

        return statusMap.getOrDefault(status, status);
    }
}
