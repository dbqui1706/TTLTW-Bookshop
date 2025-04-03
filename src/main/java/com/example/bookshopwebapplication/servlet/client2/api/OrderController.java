package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.http.request.order.OrderCreateRequest;
import com.example.bookshopwebapplication.http.response.api.ApiResponse;
import com.example.bookshopwebapplication.http.response.order.OrderResponse;
import com.example.bookshopwebapplication.service.OderService2;
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
    private final OderService2 orderService2 = new OderService2();
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            OrderCreateRequest orderCreateRequest = JsonUtils.get(request, OrderCreateRequest.class);

            // Tạo quy trình đặt hàng.
            OrderResponse orderResponse = orderService2.createOrder(orderCreateRequest);

            JsonUtils.out(
                    response,
                    createSuccessResponse(orderResponse),
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
    private ApiResponse<OrderResponse> createSuccessResponse(OrderResponse data) {
        return new ApiResponse<>(true, "Order created successfully", data);
    }

    private ApiResponse<Void> createErrorResponse(String message, int status) {
        return new ApiResponse<>(false, message, null, status);
    }
}
