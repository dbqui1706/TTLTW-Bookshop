package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.Permission;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
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
import java.util.Set;

@WebServlet(name = "PermissionApiServlet", urlPatterns = {
        "/api/permissions",
        "/api/permissions/*",
        "/api/permissions/modules"
})
public class PermissionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final PermissionService permissionService = new PermissionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String pathInfo = req.getPathInfo();

        try {
            if (requestURI.endsWith("/api/permissions/modules")) {
                // GET /api/permissions/modules - Lấy danh sách các module
                Set<String> modules = permissionService.getAllModules();
                JsonUtils.out(resp, modules, HttpServletResponse.SC_OK);
            } else if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/permissions - Lấy tất cả quyền
                String moduleParam = req.getParameter("module");
                List<Permission> permissions;

                if (moduleParam != null && !moduleParam.isEmpty()) {
                    // Lấy quyền theo module nếu có tham số module
                    permissions = permissionService.getPermissionsByModule(moduleParam);
                } else {
                    // Lấy tất cả quyền
                    permissions = permissionService.getAllPermissions();
                }

                JsonUtils.out(resp, permissions, HttpServletResponse.SC_OK);
            } else {
                // GET /api/permissions/{id} - Lấy quyền theo ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length > 1) {
                    Long permissionId = Long.parseLong(pathParts[1]);
                    Optional<Permission> permission = permissionService.getPermissionById(permissionId);

                    if (permission.isPresent()) {
                        JsonUtils.out(resp, permission.get(), HttpServletResponse.SC_OK);
                    } else {
                        JsonUtils.out(
                                resp,
                                new Message(404, "Quyền không tồn tại"),
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
            Permission Permission = gson.fromJson(reader, Permission.class);

            // Xác thực dữ liệu
            if (Permission.getName() == null || Permission.getName().trim().isEmpty()) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Tên quyền không được để trống"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            if (Permission.getCode() == null || Permission.getCode().trim().isEmpty()) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Mã quyền không được để trống"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            if (Permission.getModule() == null || Permission.getModule().trim().isEmpty()) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Module không được để trống"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Kiểm tra mã quyền đã tồn tại
            if (permissionService.isPermissionCodeExists(Permission.getCode())) {
                JsonUtils.out(
                        resp,
                        new Message(400, "Mã quyền đã tồn tại"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Tạo quyền mới
            Optional<Permission> createdPermission = permissionService.createPermission(Permission);

            if (createdPermission.isPresent()) {
                JsonUtils.out(resp, createdPermission.get(), HttpServletResponse.SC_CREATED);
            } else {
                JsonUtils.out(
                        resp,
                        new Message(500, "Không thể tạo quyền mới"),
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
                    new Message(400, "URI không hợp lệ, thiếu ID quyền"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            // Lấy ID quyền từ URI
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                Long permissionId = Long.parseLong(pathParts[1]);

                // Kiểm tra quyền tồn tại
                if (!permissionService.isPermissionExists(permissionId)) {
                    JsonUtils.out(
                            resp,
                            new Message(404, "Quyền không tồn tại"),
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                // Đọc dữ liệu cập nhật từ request body
                BufferedReader reader = req.getReader();
                Permission Permission = gson.fromJson(reader, Permission.class);
                Permission.setId(permissionId); // Đảm bảo ID đúng

                // Xác thực dữ liệu
                if (Permission.getName() == null || Permission.getName().trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Tên quyền không được để trống"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                if (Permission.getCode() == null || Permission.getCode().trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Mã quyền không được để trống"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                if (Permission.getModule() == null || Permission.getModule().trim().isEmpty()) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Module không được để trống"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                // Kiểm tra mã quyền đã tồn tại (không phải là quyền hiện tại)
                if (permissionService.isPermissionCodeExistsExcludeCurrent(Permission.getCode(), permissionId)) {
                    JsonUtils.out(
                            resp,
                            new Message(400, "Mã quyền đã tồn tại"),
                            HttpServletResponse.SC_BAD_REQUEST
                    );
                    return;
                }

                // Kiểm tra nếu là quyền hệ thống, không cho phép thay đổi is_system
                Optional<Permission> currentPermission = permissionService.getPermissionById(permissionId);
                if (currentPermission.isPresent() && currentPermission.get().getIsSystem()) {
                    Permission.setIsSystem(true); // Bảo toàn trạng thái hệ thống
                }

                // Cập nhật quyền
                Optional<Permission> updatedPermission = permissionService.updatePermission(Permission);

                if (updatedPermission.isPresent()) {
                    JsonUtils.out(resp, updatedPermission.get(), HttpServletResponse.SC_OK);
                } else {
                    JsonUtils.out(
                            resp,
                            new Message(500, "Không thể cập nhật quyền"),
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
                    new Message(400, "URI không hợp lệ, thiếu ID quyền"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            // Lấy ID quyền từ URI
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                Long permissionId = Long.parseLong(pathParts[1]);

                // Kiểm tra quyền tồn tại
                Optional<Permission> permission = permissionService.getPermissionById(permissionId);
                if (!permission.isPresent()) {
                    JsonUtils.out(
                            resp,
                            new Message(404, "Quyền không tồn tại"),
                            HttpServletResponse.SC_NOT_FOUND
                    );
                    return;
                }

                // Kiểm tra nếu là quyền hệ thống, không cho phép xóa
                if (permission.get().getIsSystem()) {
                    JsonUtils.out(
                            resp,
                            new Message(403, "Không thể xóa quyền hệ thống"),
                            HttpServletResponse.SC_FORBIDDEN
                    );
                    return;
                }

                // Xóa quyền
                boolean deleted = permissionService.deletePermission(permissionId);

                if (deleted) {
                    JsonUtils.out(
                            resp,
                            new Message(200, "Xóa quyền thành công"),
                            HttpServletResponse.SC_OK
                    );
                } else {
                    JsonUtils.out(
                            resp,
                            new Message(500, "Không thể xóa quyền"),
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