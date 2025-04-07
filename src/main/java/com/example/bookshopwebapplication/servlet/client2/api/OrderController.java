package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.http.request.order.OrderCreateRequest;
import com.example.bookshopwebapplication.http.response.api.ApiResponse;
import com.example.bookshopwebapplication.http.response.order.OrderPageResponse;
import com.example.bookshopwebapplication.http.response.order.OrderResponse;
import com.example.bookshopwebapplication.service.OrderService2;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "OrderController",
        urlPatterns = {
                "/api/orders",
                "/api/order/*",
        }
)
public class OrderController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrderService2 orderService2 = new OrderService2();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/orders":
                // Lấy danh sách đơn hàng
                getOrders(req, resp);
                break;
            case "/api/order":
            default:
                break;
        }
    }

    private void getOrders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderPageResponse result = orderService2.getUserOrders(6L, null, null, "newest", 1, 10);
            JsonUtils.out(
                    response,
                    result,
                    HttpServletResponse.SC_OK
            );
        }catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            Long userId = (Long) request.getAttribute("userId");
            OrderCreateRequest orderCreateRequest = JsonUtils.get(request, OrderCreateRequest.class);
            orderCreateRequest.setUserId(userId);
            // Tạo quy trình đặt hàng.
            OrderResponse orderResponse = orderService2.createOrder(orderCreateRequest);

            JsonUtils.out(
                    response,
                    orderResponse,
                    HttpServletResponse.SC_OK
            );
        }catch (Exception e){
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
