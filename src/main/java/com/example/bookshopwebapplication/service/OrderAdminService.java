package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.OrderAdminDao;
import com.example.bookshopwebapplication.http.response_admin.orders.OrderListResponse;

import java.util.Map;

public class OrderAdminService {
    private final OrderAdminDao orderAdminDao;

    public OrderAdminService() {
        this.orderAdminDao = new OrderAdminDao();
    }

    /**
     * Lấy danh sách đơn hàng có phân trang
     *
     * @param params Tham số lọc và phân trang
     * @return OrderListResponse Đối tượng chứa danh sách đơn hàng và thông tin phân trang
     */
    public OrderListResponse getOrdersWithPagination(Map<String, String> params) {
        return orderAdminDao.getOrdersWithPagination(params);
    }
}
