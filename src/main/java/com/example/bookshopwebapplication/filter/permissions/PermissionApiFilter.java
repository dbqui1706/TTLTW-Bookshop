package com.example.bookshopwebapplication.filter.permissions;

import com.example.bookshopwebapplication.dao.UserSessionDao;
import com.example.bookshopwebapplication.entities.UserSession;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.StringUtils;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Filter kiểm tra quyền truy cập vào các API quản lý phân quyền
 * Bảo vệ các API endpoint liên quan đến:
 * - Quản lý vai trò (roles)
 * - Quản lý quyền (permissions)
 * - Quản lý phân quyền người dùng (user-roles, role-permissions, user-permissions)
 */
@WebFilter("/api/admin/*")
public class PermissionApiFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(PermissionApiFilter.class.getName());

    // Map lưu trữ ánh xạ đường dẫn đến permission code
    private final Map<String, Set<String>> urlPermissionMap = new HashMap<>();

    // Danh sách đường dẫn được phép truy cập mà không cần kiểm tra quyền
    private final Set<String> publicPaths = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Thêm các đường dẫn public không cần kiểm tra quyền
        publicPaths.addAll(Arrays.asList(
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/api/auth/confirm-email",
                "/api/auth/login",
                "/api/auth/register",
                "/api/products",
                "/api/product",
                "/api/categories",
                "/api/category/publishers",
                "/api/delivery-methods",
                "/api/payment-methods",
                "/api/coupons",
                "/api/payment/vnpay-callback"
        ));

        // Đọc file cấu hình permission
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("permissions.properties");
            if (inputStream == null) {
                throw new ServletException("Không tìm thấy file cấu hình permissions.properties");
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            // Duyệt qua tất cả các thuộc tính trong file và tạo map ánh xạ
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                // Xử lý trường hợp một URL có thể yêu cầu nhiều quyền (phân tách bằng dấu phẩy)
                Set<String> permissionSet = new HashSet<>(Arrays.asList(value.split(",")));
                urlPermissionMap.put(key, permissionSet);
            }

            LOGGER.info("Đã nạp " + urlPermissionMap.size() + " cấu hình phân quyền API");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nạp file cấu hình phân quyền: " + e.getMessage());
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Cấu hình CORS cho API
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Cho phép pre-flight request
        if (httpRequest.getMethod().equals("OPTIONS")) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String url = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Kiểm tra nếu đường dẫn nằm trong danh sách public
        if (isPublicPath(url)) {
            chain.doFilter(request, response);
            return;
        }

        // Lấy token từ request
        String token = StringUtils.extractToken(httpRequest);
        if (token == null) {
            sendUnauthorizedResponse(httpResponse, "Bạn chưa đăng nhập");
            return;
        }

        // Xác thực token và lấy thông tin phiên
        UserSessionDao sessionDAO = new UserSessionDao();
        UserSession userSession = sessionDAO.getUserIdFromSession(token);
        if (userSession == null) {
            sendUnauthorizedResponse(httpResponse, "Phiên làm việc hết hạn");
            return;
        }

        // Tìm quyền yêu cầu dựa trên URL pattern matching
        Set<String> requiredPermissions = findRequiredPermissions(url);
        if (requiredPermissions.isEmpty()) {
            // Nếu không có ánh xạ quyền, cho phép truy cập (hoặc có thể deny mặc định)
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra quyền
        PermissionService permissionService = PermissionService.getInstance();
        boolean hasPermission = false;

        // Người dùng cần có ít nhất một trong các quyền yêu cầu
        for (String permission : requiredPermissions) {
            if (permissionService.hasPermission(userSession.getUserId(), permission)) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            sendForbiddenResponse(httpResponse, "Bạn không có quyền thực hiện hành động này");
            return;
        }

        // Ghi log hành động (có thể lưu vào DB để audit)
        LOGGER.info("User ID: " + userSession.getUserId() + " accessed: " + url);

        chain.doFilter(request, response);
    }

    /**
     * Kiểm tra xem URL có thuộc danh sách public path không
     */
    private boolean isPublicPath(String url) {
        return publicPaths.contains(url) || publicPaths.stream().anyMatch(url::startsWith);
    }

    /**
     * Tìm các quyền yêu cầu dựa trên URL pattern
     */
    private Set<String> findRequiredPermissions(String url) {
        // Tìm chính xác trước
        if (urlPermissionMap.containsKey(url)) {
            return urlPermissionMap.get(url);
        }

        // Tìm theo pattern (/*) nếu không tìm thấy chính xác
        for (Map.Entry<String, Set<String>> entry : urlPermissionMap.entrySet()) {
            String pattern = entry.getKey();
            if (pattern.endsWith("/*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (url.startsWith(prefix)) {
                    return entry.getValue();
                }
            }
        }

        return Collections.emptySet();
    }

    /**
     * Gửi response HTTP 401 Unauthorized
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> responseBody = Map.of(
                "success", false,
                "message", message
        );
        response.getWriter().write(new Gson().toJson(responseBody));
    }

    /**
     * Gửi response HTTP 403 Forbidden
     */
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        Map<String, Object> responseBody = Map.of(
                "success", false,
                "message", message
        );
        response.getWriter().write(new Gson().toJson(responseBody));
    }

    @Override
    public void destroy() {
        urlPermissionMap.clear();
        publicPaths.clear();
    }
}