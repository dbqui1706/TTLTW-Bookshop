package com.example.bookshopwebapplication.servlet.admin2.api.order;

import com.example.bookshopwebapplication.http.response_admin.orders.OrderListResponse;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.OrderAdminService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "OrderApiServlet",
        urlPatterns = {
                "/api/admin/orders",
                "/api/admin/orders/*",
        })
public class OrderApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OrderAdminService orderAdminService = new OrderAdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        try {
            // Lấy danh sách đơn hàng có phân trang
            if (requestURI.equals("/api/admin/orders")) {
                handleGetOrders(req, resp);
                return;
            }
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Xử lý request lấy danh sách đơn hàng có phân trang
     */
    private void handleGetOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Thu thập tất cả tham số từ request
        Map<String, String> params = getRequestParameters(req);


        try {
            // Gọi service để lấy dữ liệu
            OrderListResponse result = orderAdminService.getOrdersWithPagination(params);

            // Trả về kết quả
            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Thu thập tất cả tham số từ request
     */
    private Map<String, String> getRequestParameters(HttpServletRequest req) {
        Map<String, String> params = new HashMap<>();

        // Chỉ lấy các tham số cần thiết
        String[] requiredParams = {
                "search", "status", "paymentMethod", "fromDate", "toDate",
                "page", "limit", "sortBy", "sortDir", "draw"
        };

        // Lấy giá trị cho từng tham số
        for (String paramName : requiredParams) {
            String paramValue = req.getParameter(paramName);
            // Chỉ thêm vào map nếu tham số có giá trị
            if (paramValue != null && !paramValue.trim().isEmpty()) {
                params.put(paramName, paramValue);
            }
        }

        // Xử lý tham số từ DataTable nếu cần
        // Đối với DataTable, một số tham số có thể có tên khác

        // Xử lý page và limit từ DataTable (start và length)
        String start = req.getParameter("start");
        String length = req.getParameter("length");
        String draw = req.getParameter("draw");
        params.put("draw", draw);

        if (start != null && !start.isEmpty() && length != null && !length.isEmpty()) {
            int startInt = Integer.parseInt(start);
            int lengthInt = Integer.parseInt(length);

            // Tính trang dựa trên start và length
            if (lengthInt > 0) { // Tránh chia cho 0
                int page = (startInt / lengthInt) + 1;
                params.put("page", String.valueOf(page));
                params.put("limit", length);
            }
        }

        // Xử lý thông tin sắp xếp từ DataTable
        String orderColumnIndex = req.getParameter("order[0][column]");
        String orderDirection = req.getParameter("order[0][dir]");

        if (orderColumnIndex != null && !orderColumnIndex.isEmpty() &&
                orderDirection != null && !orderDirection.isEmpty()) {

            // Lấy tên cột sắp xếp
            String columnName = req.getParameter("columns[" + orderColumnIndex + "][data]");

            if (columnName != null && !columnName.isEmpty()) {
                params.put("sortBy", columnName);
                params.put("sortDir", orderDirection);
            }
        }

        // Xử lý tham số tìm kiếm từ DataTable
        String searchValue = req.getParameter("search[value]");
        if (searchValue != null && !searchValue.isEmpty()) {
            params.put("search", searchValue);
        }

        return params;
    }

    /**
     * Trích xuất ID từ URI
     */
    private Long extractIdFromUri(String uri, String prefix) {
        return Long.parseLong(uri.substring(prefix.length()));
    }
}
