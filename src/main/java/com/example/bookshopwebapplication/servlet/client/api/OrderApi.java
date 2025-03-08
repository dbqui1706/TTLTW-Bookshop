package com.example.bookshopwebapplication.servlet.client.api;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.network.OrderResponse;
import com.example.bookshopwebapplication.service.OrderHashService;
import com.example.bookshopwebapplication.service.OrderItemService;
import com.example.bookshopwebapplication.service.OrderService;
import com.example.bookshopwebapplication.utils.Paging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/orders-api"})
public class OrderApi extends HttpServlet {
    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final OrderHashService orderHashService = new OrderHashService();
    private static final int ORDERS_PER_PAGE = 10;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Đặt header cho response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Kiểm tra đăng nhập
        UserDto user = (UserDto) request.getSession().getAttribute("currentUser");
        if (user == null) {
            // Trả về lỗi nếu chưa đăng nhập
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Vui lòng đăng nhập để xem đơn hàng");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(errorJson));
            return;
        }

        try {
            // Lấy các tham số từ request
            String pageParam = Optional.ofNullable(request.getParameter("page")).orElse("1");
            String statusParam = request.getParameter("status");
            int page = Integer.parseInt(pageParam);
            if (page < 1) {
                page = 1;
            }

            // Tính toán tổng số đơn hàng và trang
            int totalOrders;
            List<OrderDto> orders;

            // Lọc theo trạng thái nếu được chỉ định
            if (statusParam != null && !statusParam.isEmpty() && !statusParam.equals("all")) {
                int status = mapStatusStringToCode(statusParam);
                if (status != -1) {
                    totalOrders = orderService.countByUserIdAndStatus(user.getId(), status);
                    int offset = Paging.offset(page, totalOrders, ORDERS_PER_PAGE);
                    orders = orderService.getOrderedPartByUserIdAndStatus(
                            user.getId(), status, ORDERS_PER_PAGE, offset
                    );
                } else {
                    // Trạng thái không hợp lệ, lấy tất cả
                    totalOrders = orderService.countByUserId(user.getId());
                    int offset = Paging.offset(page, totalOrders, ORDERS_PER_PAGE);
                    orders = orderService.getOrderedPartByUserId(
                            user.getId(), ORDERS_PER_PAGE, offset
                    );
                }
            } else {
                // Lấy tất cả đơn hàng
                totalOrders = orderService.countByUserId(user.getId());
                int offset = Paging.offset(page, totalOrders, ORDERS_PER_PAGE);
                orders = orderService.getOrderedPartByUserId(
                        user.getId(), ORDERS_PER_PAGE, offset
                );
            }

            int totalPages = Paging.totalPages(totalOrders, ORDERS_PER_PAGE);

            // Chuyển đổi danh sách đơn hàng thành OrderResponse
            List<OrderResponse> orderResponses = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

            for (OrderDto order : orders) {
                // Set orderItems for Order
                order.setOrderItems(orderItemService.getByOrderId(order.getId()));

                double totalPrice = orderService.totalPrice(order);

                String verifyStatus = "NONE";
                if (order.getIsVerified() == 1) {
                    verifyStatus = (orderHashService.verifyOrderById(order.getId())) ? "GOOD" : "BAD";
                }

                OrderResponse orderResponse = new OrderResponse(
                        order.getId(),
                        simpleDateFormat.format(order.getCreatedAt()),
                        orderItemService.getProductNamesByOrderId(order.getId()),
                        order.getStatus(),
                        verifyStatus,
                        totalPrice + order.getDeliveryPrice());

                orderResponses.add(orderResponse);
            }

            // Lấy danh sách đơn hàng có vấn đề
//            List<Integer> inDangerList = orderHashService.getUncanceledOrderHaveDangerByUserId(user.getId());

            // Tạo response JSON
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("currentPage", page);
            jsonResponse.addProperty("totalPages", totalPages);
            jsonResponse.addProperty("totalOrders", totalOrders);
            jsonResponse.add("orders", gson.toJsonTree(orderResponses));
//            jsonResponse.add("inDangerList", gson.toJsonTree(inDangerList));

            // Gửi response
            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (Exception e) {
            // Xử lý lỗi
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("error", "Đã xảy ra lỗi: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(errorJson));
        }
    }

    /**
     * Chuyển đổi chuỗi trạng thái thành mã số
     */
    private int mapStatusStringToCode(String status) {
        switch (status) {
            case "new":
                return 0; // Đơn hàng mới
            case "confirmed":
                return 1; // Đã xác nhận
            case "shipping":
                return 2; // Đang vận chuyển
            case "completed":
                return 3; // Hoàn thành
            case "canceled":
                return 4; // Đã hủy
            default:
                return -1; // Không hợp lệ
        }
    }
}
