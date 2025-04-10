package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.SpecialPermission;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.UserPermissionService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet(name = "UserSpecialPermissionsApiServlet", urlPatterns = {
        "/api/admin/users-special-permissions",
        "/api/admin/users-special-permissions/grant",
        "/api/admin/users-special-permissions/deny",
        "/api/admin/users-special-permissions/remove"
})
public class UserSpecialPermissionsApiServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();
    private final PermissionService permissionService = new PermissionService();
    private final UserPermissionService userPermissionService = new UserPermissionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("UserSpecialPermissionsApiServlet.doGet called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // GET /api/users-special-permissions - Lấy danh sách quyền đặc biệt của người dùng
            if (requestURI.equals("/api/admin/users-special-permissions")) {
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

                handleGetSpecialPermissionsByUserId(resp, userId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in UserSpecialPermissionsApiServlet.doGet: " + e.getMessage());
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
        System.out.println("UserSpecialPermissionsApiServlet.doPost called - URI: " + req.getRequestURI());

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

            // POST /api/users-special-permissions/grant - Cấp quyền đặc biệt cho người dùng
            if (requestURI.equals("/api/admin/users-special-permissions/grant")) {
                handleGrantSpecialPermission(req, resp, userId, true); // grant = true
                return;
            }

            // POST /api/users-special-permissions/deny - Từ chối quyền đặc biệt cho người dùng
            if (requestURI.equals("/api/admin/users-special-permissions/deny")) {
                handleGrantSpecialPermission(req, resp, userId, false); // grant = false
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in UserSpecialPermissionsApiServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("UserSpecialPermissionsApiServlet.doDelete called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // DELETE /api/users-special-permissions/remove - Xóa quyền đặc biệt cho người dùng
            if (requestURI.equals("/api/admin/users-special-permissions/remove")) {
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

                // Lấy permissionId từ request parameter
                String permissionIdParam = req.getParameter("permissionId");
                if (permissionIdParam == null || permissionIdParam.trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Thiếu tham số permissionId"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                Long permissionId;
                try {
                    permissionId = Long.parseLong(permissionIdParam);
                } catch (NumberFormatException e) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "permissionId không hợp lệ"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                handleRemoveSpecialPermission(resp, userId, permissionId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in UserSpecialPermissionsApiServlet.doDelete: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    // Các phương thức xử lý

    private void handleGetSpecialPermissionsByUserId(HttpServletResponse resp, Long userId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Lấy danh sách quyền đặc biệt của người dùng
        List<SpecialPermission> specialPermissions = userPermissionService.getSpecialPermissionsByUserId(userId);
        JsonUtils.out(resp, specialPermissions, HttpServletResponse.SC_OK);
    }

    private void handleGrantSpecialPermission(HttpServletRequest req, HttpServletResponse resp, Long userId, boolean isGrant) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Lấy permissionId từ request parameter hoặc body
        Long permissionId;
        String permissionIdParam = req.getParameter("permissionId");

        if (permissionIdParam != null && !permissionIdParam.trim().isEmpty()) {
            try {
                permissionId = Long.parseLong(permissionIdParam);
            } catch (NumberFormatException e) {
                JsonUtils.out(
                        resp,
                        new Message(400, "permissionId không hợp lệ"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }
        } else {
            // Đọc thông tin quyền đặc biệt từ request body
            BufferedReader reader = req.getReader();
            SpecialPermission specialPermission = gson.fromJson(reader, SpecialPermission.class);

            if (specialPermission == null || specialPermission.getPermissionId() == null) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Thiếu thông tin quyền"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            permissionId = specialPermission.getPermissionId();
        }

        // Kiểm tra quyền tồn tại
        if (!permissionService.isPermissionExists(permissionId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Quyền không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Tạo đối tượng SpecialPermission
        SpecialPermission specialPermission = new SpecialPermission();
        specialPermission.setUserId(userId);
        specialPermission.setPermissionId(permissionId);
        specialPermission.setGranted(isGrant);

        // Xóa quyền đặc biệt hiện tại nếu có (để tránh trùng lặp)
        if (userPermissionService.isSpecialPermissionExists(userId, permissionId)) {
            userPermissionService.removeSpecialPermission(userId, permissionId);
        }

        // Thiết lập quyền đặc biệt cho người dùng
        boolean success = userPermissionService.addSpecialPermission(specialPermission);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            String action = isGrant ? "cấp" : "từ chối";
            JsonUtils.out(
                    resp,
                    new Message(200, "Đã " + action + " quyền đặc biệt thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            String action = isGrant ? "cấp" : "từ chối";
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể " + action + " quyền đặc biệt"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleRemoveSpecialPermission(HttpServletResponse resp, Long userId, Long permissionId) throws IOException {
        // Kiểm tra người dùng tồn tại
        if (!userService.isUserExists(userId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Người dùng không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Kiểm tra quyền tồn tại
        if (!permissionService.isPermissionExists(permissionId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Quyền không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Kiểm tra quyền đặc biệt tồn tại
        if (!userPermissionService.isSpecialPermissionExists(userId, permissionId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Không tìm thấy quyền đặc biệt"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xóa quyền đặc biệt
        boolean success = userPermissionService.removeSpecialPermission(userId, permissionId);

        if (success) {
            // Làm mới cache quyền của người dùng
            refreshUserPermissionCache(userId);

            JsonUtils.out(
                    resp,
                    new Message(200, "Xóa quyền đặc biệt thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể xóa quyền đặc biệt"),
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