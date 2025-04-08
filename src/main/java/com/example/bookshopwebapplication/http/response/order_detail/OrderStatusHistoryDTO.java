package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryDTO {
    private Long id;
    private String status;
    private String statusText; // Text hiển thị của trạng thái
    private String note;
    private Long changedBy;
    private Timestamp createdAt;
}
