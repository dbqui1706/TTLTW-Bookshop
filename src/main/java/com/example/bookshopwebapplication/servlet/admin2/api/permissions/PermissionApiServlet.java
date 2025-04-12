package com.example.bookshopwebapplication.servlet.admin2.api.permissions;

import com.example.bookshopwebapplication.entities.Permission;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "PermissionApiServlet", urlPatterns = {
        "/api/admin/permissions",
        "/api/admin/permissions/getAll",
        "/api/admin/permissions/*"
})
public class PermissionApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionApiServlet.class);
    private final PermissionService permissionService = new PermissionService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        try {
            // GET /api/permissions/modules - Lấy danh sách các module
            if (requestURI.equals("/api/admin/permissions/modules")) {
                handleGetModules(resp);
                return;
            }

            // GET /api/permissions/{id} - Lấy quyền theo ID
            if (requestURI.matches("/api/admin/permissions/\\d+")) {
                Long permissionId = extractIdFromUri(requestURI);
                handleGetPermissionById(resp, permissionId);
                return;
            }

            // GET /api/permissions - Lấy tất cả quyền hoặc theo module
            if (requestURI.equals("/api/admin/permissions")) {
                String moduleParam = req.getParameter("module");
                handleGetAllPermissions(req, resp, moduleParam);
                return;
            }
            if (requestURI.equals("/api/admin/permissions/getAll")) {
                handleGetAllPermissions(req, resp);
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

    private void handleGetAllPermissions(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Lấy danh sách quyền từ service
            List<Permission> permissions = permissionService.getAllPermissions();

            // Gửi phản hồi
            JsonUtils.out(resp, permissions, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LOGGER.error("Error fetching all permissions", e);
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
            // POST /api/permissions/create - Tạo quyền mới
            if (requestURI.equals("/api/admin/permissions/create")) {
                handleCreatePermission(req, resp);
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
            // PUT /api/permissions/update - Cập nhật quyền
            if (requestURI.equals("/api/admin/permissions/update")) {
                handleUpdatePermission(req, resp);
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
            // DELETE /api/permissions/delete - Xóa quyền
            if (requestURI.equals("/api/admin/permissions/delete")) {
                handleDeletePermission(req, resp);
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

    private void handleGetModules(HttpServletResponse resp) throws IOException {
        Set<String> modules = permissionService.getAllModules();
        JsonUtils.out(resp, modules, HttpServletResponse.SC_OK);
    }

    private void handleGetPermissionById(HttpServletResponse resp, Long permissionId) throws IOException {
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
    }

    private void handleGetAllPermissions(HttpServletRequest req, HttpServletResponse resp, String moduleParam) throws IOException {
        try {
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

            // Lấy thông tin lọc theo module (nếu có)
            String moduleFilter = req.getParameter("module");

            // Log thông tin request
            LOGGER.debug("DataTable request - draw: {}, start: {}, length: {}", draw, start, length);
            LOGGER.debug("DataTable order - column: {}, direction: {}", orderColumnName, orderDirection);
            LOGGER.debug("DataTable search - value: {}", searchValue);
            LOGGER.debug("Module filter: {}", moduleFilter);

            // Lấy tổng số bản ghi không có filter
            int totalRecords = permissionService.getTotalPermissionsCount();

            // Lấy tổng số bản ghi có filter
            int totalRecordsFiltered = permissionService.getTotalPermissionsCountWithFilter(searchValue, moduleFilter);

            // Lấy dữ liệu đã được phân trang, sắp xếp và lọc từ database
            List<Permission> permissions = permissionService.getPermissionsByPage(
                    start, length, orderColumnName, orderDirection, searchValue, moduleFilter);

            // Tạo response cho DataTable
            Map<String, Object> response = new HashMap<>();
            response.put("draw", draw);
            response.put("recordsTotal", totalRecords);
            response.put("recordsFiltered", totalRecordsFiltered);
            response.put("data", permissions);

            // Gửi response
            JsonUtils.out(resp, response, HttpServletResponse.SC_OK);

        } catch (Exception e) {
            LOGGER.error("Error processing DataTable request", e);

            // Xử lý lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            JsonUtils.out(resp, errorResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleCreatePermission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc dữ liệu từ request body
        BufferedReader reader = req.getReader();
        Permission permission = gson.fromJson(reader, Permission.class);

        // Xác thực dữ liệu
        if (permission.getName() == null || permission.getName().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên quyền không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        if (permission.getCode() == null || permission.getCode().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Mã quyền không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        if (permission.getModule() == null || permission.getModule().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Module không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra mã quyền đã tồn tại
        if (permissionService.isPermissionCodeExists(permission.getCode())) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Mã quyền đã tồn tại"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Tạo quyền mới
        Optional<Permission> createdPermission = permissionService.createPermission(permission);

        if (createdPermission.isPresent()) {
            JsonUtils.out(resp, createdPermission.get(), HttpServletResponse.SC_CREATED);
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể tạo quyền mới"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleUpdatePermission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc dữ liệu cập nhật từ request body
        Permission permission = JsonUtils.get(req, Permission.class);

        if (permission.getId() == null) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu ID quyền"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra quyền tồn tại
        if (!permissionService.isPermissionExists(permission.getId())) {
            JsonUtils.out(
                    resp,
                    new Message(404, "Quyền không tồn tại"),
                    HttpServletResponse.SC_NOT_FOUND
            );
            return;
        }

        // Xác thực dữ liệu
        if (permission.getName() == null || permission.getName().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Tên quyền không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        if (permission.getCode() == null || permission.getCode().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Mã quyền không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        if (permission.getModule() == null || permission.getModule().trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Module không được để trống"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra mã quyền đã tồn tại (không phải là quyền hiện tại)
        if (permissionService.isPermissionCodeExistsExcludeCurrent(permission.getCode(), permission.getId())) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Mã quyền đã tồn tại"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        // Kiểm tra nếu là quyền hệ thống, không cho phép thay đổi is_system
        Optional<Permission> currentPermission = permissionService.getPermissionById(permission.getId());
        if (currentPermission.isPresent() && currentPermission.get().getIsSystem()) {
            permission.setIsSystem(true); // Bảo toàn trạng thái hệ thống
        }

        // Cập nhật quyền
        Optional<Permission> updatedPermission = permissionService.updatePermission(permission);

        if (updatedPermission.isPresent()) {
            JsonUtils.out(resp, updatedPermission.get(), HttpServletResponse.SC_OK);
        } else {
            JsonUtils.out(
                    resp,
                    new Message(500, "Không thể cập nhật quyền"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void handleDeletePermission(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Đọc ID từ tham số
        String permissionIdStr = req.getParameter("id");
        if (permissionIdStr == null || permissionIdStr.trim().isEmpty()) {
            JsonUtils.out(
                    resp,
                    new Message(400, "Thiếu ID quyền"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        try {
            Long permissionId = Long.parseLong(permissionIdStr);

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
        } catch (NumberFormatException e) {
            JsonUtils.out(
                    resp,
                    new Message(400, "ID không hợp lệ"),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}