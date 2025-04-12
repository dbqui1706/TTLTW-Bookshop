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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "RoleApiServlet", urlPatterns = {
        "/api/admin/roles",
        "/api/admin/roles/create",
        "/api/admin/roles/update",
        "/api/admin/roles/delete",
        "/api/admin/roles/*"
})
public class RoleApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RoleService roleService = new RoleService();
    private final Gson gson = new Gson();
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RoleApiServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        try {
            // GET /api/roles/{id} - Lấy vai trò theo ID
            if (requestURI.matches("/api/admin/roles/\\d+")) {
                Long roleId = extractIdFromUri(requestURI);
                handleGetRoleById(resp, roleId);
                return;
            }

            // GET /api/roles - Lấy tất cả vai trò
            if (requestURI.equals("/api/admin/roles")) {
                handleGetAllRoles(req, resp);
                return;
            }

            // URL không khớp với bất kỳ mẫu nào
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );

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
        String requestURI = req.getRequestURI();

        try {
            // POST /api/roles/create - Tạo vai trò mới
            if (requestURI.equals("/api/admin/roles/create")) {
                handleCreateRole(req, resp);
                return;
            }

            // URL không khớp với mẫu
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        try {
            // PUT /api/roles/update - Cập nhật vai trò
            if (requestURI.equals("/api/admin/roles/update")) {
                handleUpdateRole(req, resp);
                return;
            }

            // URL không khớp với mẫu
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
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
        String requestURI = req.getRequestURI();

        try {
            // DELETE /api/roles/delete - Xóa vai trò
            if (requestURI.equals("/api/admin/roles/delete")) {
                handleDeleteRole(req, resp);
                return;
            }

            // URL không khớp với mẫu
            JsonUtils.out(
                    resp,
                    new Message(400, "URI không hợp lệ"),
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

    // Các phương thức xử lý

    private void handleGetRoleById(HttpServletResponse resp, Long roleId) throws IOException {
        Optional<Role> role = roleService.getRoleById(roleId);

        if (role.isPresent()) {
            JsonUtils.out(resp, role.get(), HttpServletResponse.SC_OK);
        } else {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
        }
    }

    private void handleGetAllRoles(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Lấy các tham số từ DataTable
        int draw = Integer.parseInt(req.getParameter("draw"));
        int start = Integer.parseInt(req.getParameter("start"));
        int length = Integer.parseInt(req.getParameter("length"));

        // Lấy thông tin sắp xếp
        String orderColumnIndex = req.getParameter("order[0][column]");
        String orderColumnName = req.getParameter("columns[" + orderColumnIndex + "][data]");
        String orderDirection = req.getParameter("order[0][dir]");

        // Lấy thông tin tìm kiếm
        String searchValue = req.getParameter("search[value]") != null ?
                req.getParameter("search[value]") : "";

        // Log thông tin request
        LOGGER.debug("DataTable request - draw: {}, start: {}, length: {}", draw, start, length);
        LOGGER.debug("DataTable order - column: {}, direction: {}", orderColumnName, orderDirection);
        LOGGER.debug("DataTable search - value: {}", searchValue);

        // Lấy tổng số bản ghi không có filter
        int totalRecords = roleService.getTotalRolesCount();

        // Lấy tổng số bản ghi có filter
        int totalRecordsFiltered = searchValue.isEmpty() ?
                totalRecords : roleService.getTotalRolesCountWithFilter(searchValue);

        // Lấy dữ liệu đã được phân trang, sắp xếp và lọc từ database
        List<Role> roles = roleService.getRolesByPage(
                start, length, orderColumnName, orderDirection, searchValue);

        // Tạo response cho DataTable
        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", totalRecords);
        response.put("recordsFiltered", totalRecordsFiltered);
        response.put("data", roles);

        // Gửi response
        JsonUtils.out(resp, response, HttpServletResponse.SC_OK);
    }

    private void handleCreateRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc dữ liệu từ request body
        BufferedReader reader = req.getReader();
        Role role = gson.fromJson(reader, Role.class);

        // Xác thực dữ liệu
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên vai trò không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra tên vai trò đã tồn tại
        if (roleService.isRoleNameExists(role.getName())) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên vai trò đã tồn tại"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Tạo vai trò mới
        Optional<Role> createdRole = roleService.createRole(role);

        if (createdRole.isPresent()) {
            JsonUtils.out(resp, createdRole.get(), HttpServletResponse.SC_CREATED);
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể tạo vai trò mới"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleUpdateRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc dữ liệu cập nhật từ request body
        BufferedReader reader = req.getReader();
        Role role = gson.fromJson(reader, Role.class);

        if (role.getId() == null) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu ID vai trò"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra vai trò tồn tại
        if (!roleService.isRoleExists(role.getId())) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Vai trò không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xác thực dữ liệu
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên vai trò không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra tên vai trò đã tồn tại (không phải là vai trò hiện tại)
        if (roleService.isRoleNameExistsExcludeCurrent(role.getName(), role.getId())) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên vai trò đã tồn tại"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra nếu là vai trò hệ thống, không cho phép thay đổi is_system
        Optional<Role> currentRole = roleService.getRoleById(role.getId());
        if (currentRole.isPresent() && currentRole.get().isSystem()) {
            role.setSystem(true); // Bảo toàn trạng thái hệ thống
        }

        // Cập nhật vai trò
        Optional<Role> updatedRole = roleService.updateRole(role);

        if (updatedRole.isPresent()) {
            JsonUtils.out(resp, updatedRole.get(), HttpServletResponse.SC_OK);
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể cập nhật vai trò"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleDeleteRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc ID từ tham số
        String roleIdStr = req.getParameter("id");
        if (roleIdStr == null || roleIdStr.trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu ID vai trò"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            Long roleId = Long.parseLong(roleIdStr);

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
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    // Phương thức tiện ích
    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}