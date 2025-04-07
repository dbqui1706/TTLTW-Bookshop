package com.example.bookshopwebapplication.http.response.order;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageResponse {
    private List<OrderDTO> orders;
    private int totalOrders;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private Map<String, Integer> orderStatusCounts; // Đếm số lượng đơn hàng theo từng loại trạng thái
}