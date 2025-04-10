package com.example.bookshopwebapplication.filter;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@WebFilter("/admin2/*")
public class PermissionFilter implements Filter {
    // Map lưu trữ ánh xạ đường dẫn đến permission code
    private final Map<String, String> urlPermissionMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Đọc file cấu hình permission
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("permissions.properties");
            if (inputStream == null) {
                throw new ServletException("Không tìm thấy file cấu hình permissions.properties");
            }
            // Đọc nội dung file và ánh xạ đường dẫn đến permission code
            // Ví dụ: urlPermissionMap.put("/admin/products/create", "product.create");
            Properties properties = new Properties();
            properties.load(inputStream);

            // Duyệt qua tất cả các thuộc tính trong file
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                urlPermissionMap.put(key, value);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String url = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Lấy token từ request
        String token = StringUtils.extractToken(httpRequest);
        if (token == null) {
            // Nếu không có token, chuyển hướng đến trang home
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
            return;
        }

        // Lấy permission code trực tiếp từ URL
        if (!urlPermissionMap.containsKey(url)) {
            // Nếu không có ánh xạ, cho phép truy cập
            chain.doFilter(request, response);
            return;
        }
        String requiredPermission = urlPermissionMap.get(url);

        // Xác thực token
        UserSessionDao sessionDAO = new UserSessionDao();
        UserSession userSession = sessionDAO.getUserIdFromSession(token);
        if (userSession == null) {
            JsonUtils.out(
                    httpResponse,
                    new Gson().toJson(Map.of(
                            "message", "Phiên làm việc hết hạn",
                            "data", null
                    )),
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        // Kiểm tra quyền
        PermissionService permissionService = PermissionService.getInstance();
        if (!permissionService.hasPermission(userSession.getUserId(), requiredPermission)) {
            // Không có quyền, chuyển hướng đến trang 403
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/error/403");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
