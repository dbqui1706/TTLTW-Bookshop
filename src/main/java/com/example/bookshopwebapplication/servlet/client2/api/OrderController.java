package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.service.OrderService;
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
    private final OrderService orderService = new OrderService();

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
            String status = request.getParameter("status");
            String keyword = request.getParameter("keyword");
            String page = request.getParameter("page");
            String limit = request.getParameter("limit");

            int pageInt = page == null ? 1 : Integer.parseInt(page);
            int limitInt = limit == null ? 10 : Integer.parseInt(limit);



        }catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
