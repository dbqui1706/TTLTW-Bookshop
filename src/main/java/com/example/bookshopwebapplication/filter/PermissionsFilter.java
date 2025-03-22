package com.example.bookshopwebapplication.filter;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.service.PermissionService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@WebFilter(filterName = "PermissionsFilter", urlPatterns = {"/admin2/*"})
public class PermissionsFilter implements Filter{
    private PermissionService permissionService;
    // Bảng ánh xạ URL với quyền cần thiết
    private Map<String, String> urlPermissionMap;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.permissionService = new PermissionService();
        this.urlPermissionMap = new HashMap<>();

        // Đăng ký bảng ánh xạ URL với Permission cần thiết
        urlPermissionMap.put("/admin/products/create", "product.create");
        urlPermissionMap.put("/admin/products/edit", "product.edit");
        urlPermissionMap.put("/admin/products/delete", "product.delete");
        urlPermissionMap.put("/admin/orders/list", "order.view_list");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Bỏ qua các trang công khai
        String requestURI = httpRequest.getRequestURI();
        if (isPublicPage(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra người dùng đã đăng nhập
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin người dùng
        UserDto user = (UserDto) session.getAttribute("currentUser");

        // Kiểm tra quyền
        String requiredPermission = getRequiredPermission(requestURI);
        if (requiredPermission != null) {
            if (!permissionService.hasPermission(user.getId(), requiredPermission)) {
                // Ghi log
//                logAccessDenied(userId, requestURI, requiredPermission);

                // Chuyển hướng đến trang lỗi 403
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getRequiredPermission(String requestURI) {
        // Kiểm tra xem URI có khớp với pattern nào không
        for (Map.Entry<String, String> entry : urlPermissionMap.entrySet()) {
            String pattern = entry.getKey();
            // Có thể dùng regex hoặc kiểm tra đơn giản
            if (requestURI.contains(pattern)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean isPublicPage(String requestURI) {
        return requestURI.endsWith("/signin")
                || requestURI.endsWith("/signout")
                || requestURI.endsWith("/")
                || requestURI.endsWith("/signup")
                || requestURI.endsWith("category")
                || requestURI.contains("/product")
                || requestURI.contains("/assets/");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
