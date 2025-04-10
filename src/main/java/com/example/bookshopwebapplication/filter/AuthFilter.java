package com.example.bookshopwebapplication.filter;

import com.example.bookshopwebapplication.dao.UserSessionDao;
import com.example.bookshopwebapplication.entities.UserSession;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.StringUtils;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
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
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log thông tin request để debug
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String method = httpRequest.getMethod();

        // Luôn cho phép OPTIONS request đi qua (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("AuthFilter: Allowing OPTIONS request to pass through");
            chain.doFilter(request, response);
            return;
        }

        // Đường dẫn không cần xác thực
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            System.out.println("AuthFilter: Path is excluded from authentication");
            chain.doFilter(request, response);
            return;
        }

        // Lấy token từ request
        String token = StringUtils.extractToken(httpRequest);

        if (token == null) {
            System.out.println("AuthFilter: No token found in request");
            JsonUtils.out(
                    httpResponse,
                    new Gson().toJson(Map.of("error", "Vui lòng đăng nhập")),
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        System.out.println("AuthFilter: Found token: " + token);

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

        // Thêm userId vào request attribute
        request.setAttribute("userId", userSession.getUserId());

        // Cho phép request đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}