package com.example.bookshopwebapplication.servlet.admin2.api.user;

import com.example.bookshopwebapplication.http.response.user.UserFullDetail;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet( urlPatterns = {
        "/admin2/api/users",
})
public class UserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin2/api/users":
                getUsers(req, resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getUsers(HttpServletRequest req, HttpServletResponse resp) {
        // Các tham số phân trang và lọc
        int page = Integer.parseInt(req.getParameter("page") != null ? req.getParameter("page") : "1");
        int limit = Integer.parseInt(req.getParameter("limit") != null ? req.getParameter("limit") : "10");
        String search = req.getParameter("search"); // Tìm kiếm theo tên, email, số điện thoại, địa chỉ, id
        String role = req.getParameter("role"); // Tất cả, "ADMIN", "EMPLOYEE", "CUSTOMER"
        String status = req.getParameter("status"); // "Đang hoạt động", "Không hoạt động", "Bị khóa"
        String sort = req.getParameter("sort"); // "Tên A-Z", "Tên Z-A", "Ngày tạo mới nhất", "Ngày tạo cũ nhất", "Đăng Nhập gần đây", role (A-Z)

        try {
            // Lấy toàn bộ dữ liệu chi tiết của tất cả người dùng
            Map<String, Object> result = userService.getAllUserDetails(
                    page, limit, search, role, status, sort
            );
            JsonUtils.out(
                    resp,
                    result,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi truy vấn dữ liệu"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
