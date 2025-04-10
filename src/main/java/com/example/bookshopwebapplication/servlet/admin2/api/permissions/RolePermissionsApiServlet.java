package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.Permission;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.RolePermissionService;
import com.example.bookshopwebapplication.service.RoleService;
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

@WebServlet(name = "RolePermissionsApiServlet", urlPatterns = {
        "/api/roles-permissions",
        "/api/roles-permissions/add",
        "/api/roles-permissions/remove",
        "/api/roles-permissions/update",
        "/api/roles-permissions/remove-all",
})
public class RolePermissionsApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RoleService roleService = new RoleService();
    private final PermissionService permissionService = new PermissionService();
    private final RolePermissionService rolePermissionService = new RolePermissionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("RolePermissionsApiServlet.doGet called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
            // GET /api/roles-permissions - Lấy danh sách quyền của vai trò
            if (requestURI.equals("/api/roles-permissions")) {
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

                handleGetPermissionsByRoleId(resp, roleId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in RolePermissionsApiServlet.doGet: " + e.getMessage());
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
        System.out.println("RolePermissionsApiServlet.doPost called - URI: " + req.getRequestURI());

        String requestURI = req.getRequestURI();

        try {
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

            // POST /api/roles-permissions/add - Thêm một quyền vào vai trò
            if (requestURI.equals("/api/roles-permissions/add")) {
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

                handleAddPermissionToRole(resp, roleId, permissionId);
                return;
            }

            // POST /api/roles-permissions/remove - Xóa một quyền khỏi vai trò
            if (requestURI.equals("/api/roles-permissions/remove")) {
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

                handleRemovePermissionFromRole(resp, roleId, permissionId);
                return;
            }

            // POST /api/roles-permissions/update - Cập nhật danh sách quyền cho vai trò
            if (requestURI.equals("/api/roles-permissions/update")) {
                handleSetPermissionsForRole(req, resp, roleId);
                return;
            }

            // POST /api/roles-permissions/remove-all - Xóa tất cả quyền của vai trò
            if (requestURI.equals("/api/roles-permissions/remove-all")) {
                handleRemoveAllPermissionsFromRole(resp, roleId);
                return;
            }

            // URI không khớp
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

        } catch (Exception e) {
            System.out.println("Exception in RolePermissionsApiServlet.doPost: " + e.getMessage());
            e.printStackTrace();
            JsonUtils.out(
                    resp,
                    new Message(500, "Lỗi server: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    // Các phương thức xử lý

    private void handleGetPermissionsByRoleId(HttpServletResponse resp, Long roleId) throws IOException {
        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Lấy danh sách quyền của vai trò
        List<Permission> permissions = rolePermissionService.getPermissionsByRoleId(roleId);
        JsonUtils.out(resp, permissions, HttpServletResponse.SC_OK);
    }

    private void handleSetPermissionsForRole(HttpServletRequest req, HttpServletResponse resp, Long roleId) throws IOException {
        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Đọc danh sách permission IDs từ request body
        BufferedReader reader = req.getReader();
        Type listType = new TypeToken<List<Long>>() {
        }.getType();
        List<Long> permissionIds = gson.fromJson(reader, listType);

        if (permissionIds == null || permissionIds.isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Danh sách quyền không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra tất cả quyền tồn tại
        List<Long> nonExistentPermissions = permissionIds.stream()
                .filter(id -> !permissionService.isPermissionExists(id))
                .toList();

        if (!nonExistentPermissions.isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Một số quyền không tồn tại: " + nonExistentPermissions),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Thực hiện gán quyền cho vai trò
        boolean success = rolePermissionService.setPermissionsForRole(roleId, permissionIds);

        if (success) {
            JsonUtils.out(
                    resp,
                    new Message(200, "Gán quyền cho vai trò thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể gán quyền cho vai trò"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleAddPermissionToRole(HttpServletResponse resp, Long roleId, Long permissionId) throws IOException {
        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
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

        // Gán quyền cho vai trò
        boolean success = rolePermissionService.addPermissionToRole(roleId, permissionId);

        if (success) {
            JsonUtils.out(
                    resp,
                    new Message(200, "Gán quyền cho vai trò thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể gán quyền cho vai trò"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleRemovePermissionFromRole(HttpServletResponse resp, Long roleId, Long permissionId) throws IOException {
        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
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

        // Xóa quyền khỏi vai trò
        boolean success = rolePermissionService.removePermissionFromRole(roleId, permissionId);

        if (success) {
            JsonUtils.out(
                    resp,
                    new Message(200, "Xóa quyền khỏi vai trò thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể xóa quyền khỏi vai trò"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleRemoveAllPermissionsFromRole(HttpServletResponse resp, Long roleId) throws IOException {
        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(roleId)) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xóa tất cả quyền của vai trò
        boolean success = rolePermissionService.removeAllPermissionsFromRole(roleId);

        if (success) {
            JsonUtils.out(
                    resp,
                    new Message(200, "Xóa tất cả quyền của vai trò thành công"),
                    HttpServletResponse.SC_OK
            );
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể xóa tất cả quyền của vai trò"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}