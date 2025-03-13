package com.example.bookshopwebapplication.servlet.admin2.api;

import com.example.bookshopwebapplication.entities.Order;
import com.example.bookshopwebapplication.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Dashboard", urlPatterns = {
        "/admin2/api/revenue",
})
public class Dashboard extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        switch (uri) {
            case "/admin2/api/revenue":
                getRevenue(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getRevenue(HttpServletRequest request, HttpServletResponse response) {
        // Xử lý lấy dữ liệu doanh thu cho các đơn hàng đã được giao
        // và trả về dữ liệu dưới dạng JSON


    }
}
