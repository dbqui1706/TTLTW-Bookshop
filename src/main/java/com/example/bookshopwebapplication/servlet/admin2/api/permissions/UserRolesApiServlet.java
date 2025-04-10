package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.Role;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.RoleService;
import com.example.bookshopwebapplication.service.UserRoleService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@WebServlet(name = "UserRolesApiServlet", urlPatterns = {
        "/api/users-roles",
        "/api/users-roles/add",
        "/api/users-roles/remove",
        "/api/users-roles/update",
        "/api/users-roles/remove-all"
})
public class UserRolesApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();
    private final UserRoleService userRoleService = new UserRoleService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("UserRolesApiServlet.doGet called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // GET /api/users-roles - Lấy danh sách vai trò của người dùng
            if (requestURI.equals("/api/users-roles")) {
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

                handleGetRolesByUserId(resp, userId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in UserRolesApiServlet.doGet: " + e.getMessage());
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
        System.out.println("UserRolesApiServlet.doPost called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
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

            // POST /api/users-roles/add - Thêm một vai trò cho người dùng
            if (requestURI.equals("/api/users-roles/add")) {
                // Lấy roleId từ request parameter
                String roleIdParam = req.getParameter("roleId");
                if (roleIdParam == null || roleIdParam.trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Thiếu tham số roleId"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                Long roleId;
                try {
                    roleId = Long.parseLong(roleIdParam);
                } catch (NumberFormatException e) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "roleId không hợp lệ"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                handleAddRoleToUser(resp, userId, roleId);
                return;
            }

            // POST /api/users-roles/remove - Xóa một vai trò khỏi người dùng
            if (requestURI.equals("/api/users-roles/remove")) {
                // Lấy roleId từ request parameter
                String roleIdParam = req.getParameter("roleId");
                if (roleIdParam == null || roleIdParam.trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Thiếu tham số roleId"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                Long roleId;
                try {
                    roleId = Long.parseLong(roleIdParam);
                } catch (NumberFormatException e) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "roleId không hợp lệ"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                handleRemoveRoleFromUser(resp, userId, roleId);
                return;
            }

            // POST /api/users-roles/update - Cập nhật danh sách vai trò cho người dùng
            if (requestURI.equals("/api/users-roles/update")) {
                handleSetRolesForUser(req, resp, userId);
                return;
            }

            // POST /api/users-roles/remove-all - Xóa tất cả vai trò của người dùng
            if (requestURI.equals("/api/users-roles/remove-all")) {
                handleRemoveAllRolesFromUser(resp, userId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in UserRolesApiServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    // Các phương thức xử lý

    private void handleGetRolesByUserId(HttpServletResponse resp, Long userId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Lấy danh sách vai trò của người dùng
        List<Role> roles = userRoleService.getRolesByUserId(userId);
        JsonUtils.out(resp, roles, HttpServletResponse.SC_OK);
    }

    private void handleSetRolesForUser(HttpServletRequest req, HttpServletResponse resp, Long userId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Đọc danh sách role IDs từ request body
        BufferedReader reader = req.getReader();
        Type listType = new TypeToken<List<Long>>() {}.getType();
        List<Long> roleIds = gson.fromJson(reader, listType);

        if (roleIds == null || roleIds.isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Danh sách vai trò không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra tất cả vai trò tồn tại
        List<Long> nonExistentRoles = roleIds.stream()
                .filter(id -> !roleService.isRoleExists(id))
                .toList();

        if (!nonExistentRoles.isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Một số vai trò không tồn tại: " + nonExistentRoles),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Thực hiện gán vai trò cho người dùng
        boolean success = userRoleService.setRolesForUser(userId, roleIds);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            JsonUtils.out(
                    resp,
                    new Message(200, "Gán vai trò cho người dùng thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể gán vai trò cho người dùng"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleAddRoleToUser(HttpServletResponse resp, Long userId, Long roleId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Gán vai trò cho người dùng
        boolean success = userRoleService.addRoleToUser(userId, roleId);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            JsonUtils.out(
                    resp,
                    new Message(200, "Gán vai trò cho người dùng thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể gán vai trò cho người dùng"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleRemoveRoleFromUser(HttpServletResponse resp, Long userId, Long roleId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xóa vai trò khỏi người dùng
        boolean success = userRoleService.removeRoleFromUser(userId, roleId);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            JsonUtils.out(
                    resp,
                    new Message(200, "Xóa vai trò khỏi người dùng thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể xóa vai trò khỏi người dùng"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleRemoveAllRolesFromUser(HttpServletResponse resp, Long userId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xóa tất cả vai trò của người dùng
        boolean success = userRoleService.removeAllRolesFromUser(userId);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            JsonUtils.out(
                    resp,
                    new Message(200, "Xóa tất cả vai trò của người dùng thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể xóa tất cả vai trò của người dùng"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    // Phương thức làm mới cache quyền của người dùng
    private void refreshUserPermissionCache(Long userId) {
        // Sử dụng PermissionService để xóa cache quyền của người dùng
        // Khi người dùng truy cập lần tiếp theo, quyền sẽ được tải lại từ cơ sở dữ liệu
        new PermissionService().clearUserCache(userId);
    }
}