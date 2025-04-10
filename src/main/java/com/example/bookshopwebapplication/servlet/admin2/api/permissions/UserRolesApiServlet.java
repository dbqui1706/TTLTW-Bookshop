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
import java.util.stream.Collectors;

@WebServlet(name = "UserRolesApiServlet", urlPatterns = {
        "/api/users/*/roles",
        "/api/users/*/roles/*"
})
public class UserRolesApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();
    private final UserRoleService userRoleService = new UserRoleService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getRequestURI();
        String[] parts = pathInfo.split("/");

        try {
            // Định dạng URI: /api/users/{userId}/roles
            if (parts.length >= 5 && parts[1].equals("api") && parts[2].equals("users") && parts[4].equals("roles")) {
                Long userId = Long.parseLong(parts[3]);

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
            } else {
                JsonUtils.out(
                        resp,
                        new Message(400, "URI không hợp lệ"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
            }
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getRequestURI();
        String[] parts = pathInfo.split("/");

        try {
            // Định dạng URI: /api/users/{userId}/roles
            if (parts.length >= 5 && parts[1].equals("api") && parts[2].equals("users") && parts[4].equals("roles")) {
                Long userId = Long.parseLong(parts[3]);

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
            } else {
                JsonUtils.out(
                        resp,
                        new Message(400, "URI không hợp lệ"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
            }
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)  {
        String pathInfo = req.getRequestURI();
        String[] parts = pathInfo.split("/");

        try {
            // Định dạng URI: /api/users/{userId}/roles/{roleId}
            if (parts.length >= 6 && parts[1].equals("api") && parts[2].equals("users") && parts[4].equals("roles")) {
                Long userId = Long.parseLong(parts[3]);
                Long roleId = Long.parseLong(parts[5]);

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
            } else {
                JsonUtils.out(
                        resp,
                        new Message(400, "URI không hợp lệ"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
            }
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getRequestURI();
        String[] parts = pathInfo.split("/");

        try {
            // Định dạng URI: /api/users/{userId}/roles/{roleId} hoặc /api/users/{userId}/roles
            if (parts.length >= 5 && parts[1].equals("api") && parts[2].equals("users") && parts[4].equals("roles")) {
                Long userId = Long.parseLong(parts[3]);

                // Kiểm tra người dùng tồn tại
                if (!userService.isUserExists(userId)) {
                    JsonUtils.out(
                            resp,
                            new Message(404, "Người dùng không tồn tại"),
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                boolean success;

                if (parts.length >= 6) {
                    // Xóa một vai trò cụ thể khỏi người dùng
                    Long roleId = Long.parseLong(parts[5]);

                    // Kiểm tra vai trò tồn tại
                    if (!roleService.isRoleExists(roleId)) {
                        JsonUtils.out(
                                resp,
                                new Message(404, "Vai trò không tồn tại"),
                                HttpServletResponse.SC_NOT_FOUND
                        );
                        return;
                    }

                    success = userRoleService.removeRoleFromUser(userId, roleId);

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
                } else {
                    // Xóa tất cả vai trò của người dùng
                    success = userRoleService.removeAllRolesFromUser(userId);

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
            } else {
                JsonUtils.out(
                        resp,
                        new Message(400, "URI không hợp lệ"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
            }
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
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