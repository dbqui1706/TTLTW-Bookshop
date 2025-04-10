package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.Role;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.RoleService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "RoleApiServlet", urlPatterns = {
        "/api/roles",
        "/api/roles/*"
})
public class RoleApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RoleService roleService = new RoleService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/roles - Lấy tất cả vai trò
                List<Role> roles = roleService.getAllRoles();
                JsonUtils.out(resp, roles, HttpServletResponse.SC_OK);
            } else {
                // GET /api/roles/{id} - Lấy vai trò theo ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    Long roleId = Long.parseLong(pathParts[1]);
                    Optional<Role> role = roleService.getRoleById(roleId);

                    if (role.isPresent()) {
                        JsonUtils.out(resp, role.get(), HttpServletResponse.SC_OK);
                    } else {
                        JsonUtils.out(
                                resp,
                                new Message(404, "Role không tồn tại"),
                                HttpServletResponse.SC_NOT_FOUND
                        );
                    }
                } else {
                    JsonUtils.out(
                            resp,
                            new Message(400, "URI không hợp lệ"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                }
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
        try {
            // Đọc dữ liệu từ request body
            BufferedReader reader = req.getReader();
            Role Role = gson.fromJson(reader, Role.class);

            // Xác thực dữ liệu
            if (Role.getName() == null || Role.getName().trim().isEmpty()) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Tên vai trò không được để trống"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Kiểm tra tên vai trò đã tồn tại
            if (roleService.isRoleNameExists(Role.getName())) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Tên vai trò đã tồn tại"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Tạo vai trò mới
            Optional<Role> createdRole = roleService.createRole(Role);

            if (createdRole.isPresent()) {
                JsonUtils.out(resp, createdRole.get(), HttpServletResponse.SC_CREATED);
            } else {
                JsonUtils.out(
                        resp,
                        new Message(500, "Không thể tạo vai trò mới"),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
            }
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ, thiếu ID vai trò"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            // Lấy ID vai trò từ URI
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                Long roleId = Long.parseLong(pathParts[1]);

                // Kiểm tra vai trò tồn tại
                if (!roleService.isRoleExists(roleId)) {
                    JsonUtils.out(
                            resp,
                            new Message(404, "Vai trò không tồn tại"),
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                // Đọc dữ liệu cập nhật từ request body
                BufferedReader reader = req.getReader();
                Role Role = gson.fromJson(reader, Role.class);
                Role.setId(roleId); // Đảm bảo ID đúng

                // Xác thực dữ liệu
                if (Role.getName() == null || Role.getName().trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Tên vai trò không được để trống"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                // Kiểm tra tên vai trò đã tồn tại (không phải là vai trò hiện tại)
                if (roleService.isRoleNameExistsExcludeCurrent(Role.getName(), roleId)) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Tên vai trò đã tồn tại"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                // Kiểm tra nếu là vai trò hệ thống, không cho phép thay đổi is_system
                Optional<Role> currentRole = roleService.getRoleById(roleId);
                if (currentRole.isPresent() && currentRole.get().isSystem()) {
                    Role.setSystem(true); // Bảo toàn trạng thái hệ thống
                }

                // Cập nhật vai trò
                Optional<Role> updatedRole = roleService.updateRole(Role);

                if (updatedRole.isPresent()) {
                    JsonUtils.out(resp, updatedRole.get(), HttpServletResponse.SC_OK);
                } else {
                    JsonUtils.out(
                            resp,
                            new Message(500, "Không thể cập nhật vai trò"),
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ, thiếu ID vai trò"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            // Lấy ID vai trò từ URI
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                Long roleId = Long.parseLong(pathParts[1]);

                // Kiểm tra vai trò tồn tại
                Optional<Role> role = roleService.getRoleById(roleId);
                if (!role.isPresent()) {
                    JsonUtils.out(
                            resp,
                            new Message(404, "Vai trò không tồn tại"),
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                // Kiểm tra nếu là vai trò hệ thống, không cho phép xóa
                if (role.get().isSystem()) {
                    JsonUtils.out(
                            resp,
                            new Message(403, "Không thể xóa vai trò hệ thống"),
                            HttpServletResponse.SC_FORBIDDEN
                    );
                    return;
                }

                // Xóa vai trò
                boolean deleted = roleService.deleteRole(roleId);

                if (deleted) {
                    JsonUtils.out(
                            resp,
                            new Message(200, "Xóa vai trò thành công"),
                            HttpServletResponse.SC_OK
                    );
                } else {
                    JsonUtils.out(
                            resp,
                            new Message(500, "Không thể xóa vai trò"),
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
}