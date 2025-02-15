package com.example.bookshopwebapplication.servlet.admin.order;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.dto.OrderItemDto;
import com.example.bookshopwebapplication.dto.ProductDto;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.service.*;
import com.example.bookshopwebapplication.servlet.admin.order.utils.JQueryDataTablesOrder;
import com.example.bookshopwebapplication.servlet.admin.order.utils.JQueryDataTablesSentParamModel;
import com.example.bookshopwebapplication.servlet.admin.order.utils.Utils;
import com.example.bookshopwebapplication.utils.Protector;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(urlPatterns = {"/admin/orders/filter", "/admin/orders/get-filter"})
public class OrderFilter extends HttpServlet {
    private final OrderService orderService = new OrderService();
    private final OrderHashService orderHashService = new OrderHashService();
    private final UserService userService = new UserService();
    private final OrderItemService orderItemService = new OrderItemService();
    private int status = -1;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getRequestURI().contains("/admin/orders/filter")) {
            int status = Integer.parseInt(request.getParameter("status"));
            this.status = status;
            request.getRequestDispatcher("/WEB-INF/views/admin/order/filter.jsp").forward(request, response);
            return;
        }
        JQueryDataTablesSentParamModel param = Utils.getParam(request);


        // Tham số từ DataTable
        int draw = Integer.parseInt(request.getParameter("draw"));
        int start = Integer.parseInt(request.getParameter("start")); // Bắt đầu từ dòng nào
        int length = Integer.parseInt(request.getParameter("length")); // Số dòng trên mỗi trang
        String searchValue = request.getParameter("search"); // Từ khóa tìm kiếm (nếu có)
        JQueryDataTablesOrder orderParam = param.getOrder().get(0);
        int column = orderParam.getColumn();
        String dir = orderParam.getDir();
        String columnName = param.getColumns().get(column).getData();

        // Tổng số bản ghi
        int totalRecords = orderService.countByTampered(status);

        // Lọc những đơn hàng có trạng thái tương ứng
        List<OrderDto> orders = orderService.getOrderPartServerSideByTampered(
                length, start,
                orderParam.getColumn() + "",
                orderParam.getDir(), searchValue,
                status
        );

        // Định dạng dữ liệu trả về
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            OrderDto order = orders.get(i);
            userService.getById(order.getUser().getId()).ifPresent(order::setUser);
            List<OrderItemDto> items = orderItemService.getByOrderId(order.getId());
            order.setOrderItems(items);
            order.setTotalPrice(calculateTotalPrice(items, order.getDeliveryPrice()));

            Map<String, Object> row = new HashMap<>();
            row.put("id", order.getId());
            row.put("username", generateUser(order.getUser()));
            row.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt() : "");
            row.put("updatedAt", order.getUpdatedAt() != null ? order.getUpdatedAt() : "");
            row.put("productCount", items.size());
            row.put("totalPrice", new String(String
                    .format("%,.0f", order.getTotalPrice()))
                    .replace(",", ".")
                    + "đ");
            row.put("status", formatStatus(order.getStatus()));
            row.put("verify", generateVerifyStatus(order));
            row.put("actions", generateActions(order));
            data.add(row);
        }

        // JSON response
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("draw", draw);
        jsonResponse.put("recordsTotal", totalRecords);
        jsonResponse.put("recordsFiltered", totalRecords);
        jsonResponse.put("data", data);

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(jsonResponse));
    }

    private String generateVerifyStatus(OrderDto order) {
        String verifyStatus = "NONE";
        int isTampered = order.getIsTampered();
        if (isTampered == 1) {
            return "<span class=\"badge bg-danger\">Đã bị thay đổi</span>";
        }
        return "<span class=\"badge bg-success\">Đã xác thực</span>";
//        if (order.getIsVerified() == 1) {
//            verifyStatus = orderHashService.verifyOrderById(order.getId()) ? "GOOD" : "BAD";
//        }
//        if (verifyStatus.equals("GOOD")) {
//            return "<span class=\"badge bg-success\">Đã xác thực</span>";
//        } else if (verifyStatus.equals("BAD")) {
//            return "<span class=\"badge bg-danger\">Đã bị thay đổi</span>";
//        }
//        return "<span class=\"badge bg-warning text-dark\">Không</span>";
    }
    public double calculateTotalPrice(List<OrderItemDto> orderItems, double deliveryPrice) {
        double totalPrice = deliveryPrice;

        for (OrderItemDto orderItem : orderItems) {
            totalPrice += orderItem.getPrice() * orderItem.getQuantity();
        }
        return totalPrice;
    }

    private String formatStatus(int status) {
        switch (status) {
            case 1:
                return "<span class='badge bg-warning text-dark'>Đang giao hàng</span>";
            case 2:
                return "<span class='badge bg-success'>Giao hàng thành công</span>";
            case 3:
                return "<span class='badge bg-danger'>Hủy đơn hàng</span>";
            default:
                return "<span class='badge bg-secondary'>Không xác định</span>";
        }
    }

    private String generateActions(OrderDto order) {
        // Kiểm tra các trạng thái đơn hàng và điều chỉnh CSS class và form tương ứng
        String confirmButtonClass = order.getStatus() == 2 || order.getStatus() == 3 ? "btn-secondary" : "btn-success";
        String cancelButtonClass = order.getStatus() == 2 || order.getStatus() == 3 ? "btn-secondary" : "btn-danger";
        String resetButtonClass = order.getStatus() == 1 ? "btn-secondary" : "btn-warning";

        // Chế độ disabled của các nút khi đơn hàng đã được giao hoặc bị hủy
        String confirmDisabled = order.getStatus() == 2 || order.getStatus() == 3 ? "disabled" : "";
        String cancelDisabled = order.getStatus() == 2 || order.getStatus() == 3 ? "disabled" : "";
        String resetDisabled = order.getStatus() == 1 ? "disabled" : "";

        // Xây dựng HTML cho các nút hành động
        return String.format("""
                            <div>
                                <a class="btn btn-primary me-1" href='/admin/orderManager/detail?id=%d' target="_blank" role="button">
                                    Chi tiết
                                </a>
                                <button class="btn %s me-1 action-btn"
                                        data-action="CONFIRM" data-id="%d" %s
                                        title="Xác nhận đã giao">
                                    <i class="bi bi-check-circle"></i>
                                </button>
                                <button class="btn %s me-1 action-btn"
                                        data-action="CANCEL" data-id="%d" %s
                                        title="Hủy đơn hàng">
                                    <i class="bi bi-x-circle"></i>
                                </button>
                                <button class="btn %s action-btn"
                                        data-action="RESET" data-id="%d" %s
                                        title="Đặt lại trạng thái là đang giao hàng">
                                    <i class="bi bi-arrow-clockwise"></i>
                                </button>
                            </div>
                        """, order.getId(), confirmButtonClass, order.getId(), confirmDisabled,
                cancelButtonClass, order.getId(), cancelDisabled, resetButtonClass, order.getId(), resetDisabled);
    }


    private String generateUser(UserDto userDto) {
        return "<a target=\"_blank\" href='/admin/userManager/detail?id="
                + userDto.getId() + "'>" + userDto.getUsername()
                + "</a> " + "(" + userDto.getFullName() + ")";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long orderId = Protector.of(() -> Long.parseLong(request.getParameter("id"))).get(0L);
        String action = request.getParameter("action");

        // Đặt kiểu phản hồi là JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Tạo phản hồi mặc định
        JsonObject jsonResponse = new JsonObject();
        try {
            boolean isSuccess = false;
            String message = "";

            // Xử lý từng hành động
            switch (action) {
                case "CONFIRM":
                    message = processConfirmAction(orderId);
                    isSuccess = true;
                    break;
                case "CANCEL":
                    message = processCancelAction(orderId);
                    isSuccess = true;
                    break;
                case "RESET":
                    message = processResetAction(orderId);
                    isSuccess = true;
                    break;
                default:
                    message = "Hành động không hợp lệ.";
                    isSuccess = false;
            }

            // Gửi phản hồi
            jsonResponse.addProperty("success", isSuccess);
            jsonResponse.addProperty("message", message);

        } catch (Exception e) {
            // Xử lý lỗi nếu có
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau.");
        } finally {
            // Gửi phản hồi JSON
            out.print(jsonResponse.toString());
            out.flush();
            out.close();
        }
    }

    private String processConfirmAction(long orderId) {
        try {
            Protector.of(() -> orderService.confirm(orderId));
            Protector.of(() -> updateProductQuantities(orderId));
            return String.format("Đã xác nhận giao đơn hàng #%d thành công!", orderId);
        } catch (Exception e) {
            return "Xác nhận đơn hàng thất bại: " + e.getMessage();
        }
    }

    private String processCancelAction(long orderId) {
        try {
            orderService.cancel(orderId);
            return String.format("Đã hủy đơn hàng #%d thành công!", orderId);
        } catch (Exception e) {
            return "Hủy đơn hàng thất bại: " + e.getMessage();
        }
    }

    private String processResetAction(long orderId) {
        try {
            orderService.reset(orderId);
            return String.format("Đã đặt lại trạng thái đơn hàng #%d thành công!", orderId);
        } catch (Exception e) {
            return "Đặt lại trạng thái thất bại: " + e.getMessage();
        }
    }

    private void updateProductQuantities(long orderId) {
        // Cập nhật số lượng sản phẩm sau khi xác nhận đơn hàng
        Optional<OrderDto> orderDto = orderService.getById(orderId);
        List<OrderItemDto> itemDtoList = OrderItemService.getInstance().getByOrderId(orderDto.get().getId());
        for (OrderItemDto orderItemDto : itemDtoList) {
            ProductDto productDto = orderItemDto.getProduct();
            productDto.setQuantity(productDto.getQuantity() - 1);
            productDto.setTotalBuy(productDto.getTotalBuy() + 1);
            ProductService.getInstance().update(productDto);
        }
    }
}
