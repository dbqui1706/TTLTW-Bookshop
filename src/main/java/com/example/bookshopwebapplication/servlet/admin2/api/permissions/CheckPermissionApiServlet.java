package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CheckPermissionApiServlet", urlPatterns = {
        "/api/admin/check-permission",
        "/api/admin/check-current-permission"
})
public class CheckPermissionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();
    private final PermissionService permissionService = new PermissionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("CheckPermissionApiServlet.doGet called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // GET /api/check-permission - Kiểm tra quyền của một người dùng cụ thể
            if (requestURI.equals("/api/admin/check-permission")) {
                handleCheckPermissionForUser(req, resp);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in CheckPermissionApiServlet.doGet: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("CheckPermissionApiServlet.doPost called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // POST /api/check-current-permission - Kiểm tra quyền của người dùng hiện tại
            if (requestURI.equals("/api/admin/check-current-permission")) {
                handleCheckPermissionForCurrentUser(req, resp);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in CheckPermissionApiServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    // Các phương thức xử lý

    private void handleCheckPermissionForUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Lấy userId từ request parameter
        String userIdParam = req.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu tham số userId"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdParam);
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "userId không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Lấy mã quyền từ query parameter
        String permissionCode = req.getParameter("permissionCode");

        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu mã quyền"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Kiểm tra quyền
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);

        // Tạo đối tượng kết quả
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", hasPermission);

        JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
    }

    private void handleCheckPermissionForCurrentUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Lấy người dùng hiện tại từ session
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            JsonUtils.out(
                    resp,
                    new Message(401, "Chưa đăng nhập"),
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        UserDto currentUser = (UserDto) session.getAttribute("currentUser");
        Long userId = currentUser.getId();

        // Đọc mã quyền từ request body hoặc parameter
        String permissionCode = req.getParameter("permissionCode");

        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            // Thử đọc từ request body
            BufferedReader reader = req.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            if (jsonObject == null || !jsonObject.has("permissionCode")) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Thiếu mã quyền"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            permissionCode = jsonObject.get("permissionCode").getAsString();
        }

        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Mã quyền không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra quyền
        boolean hasPermission = permissionService.hasPermission(userId, permissionCode);

        // Tạo đối tượng kết quả
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", hasPermission);

        JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
    }
}