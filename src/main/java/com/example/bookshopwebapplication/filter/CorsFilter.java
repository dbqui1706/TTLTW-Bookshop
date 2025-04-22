package com.example.bookshopwebapplication.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Lấy Origin từ request
        String origin = httpRequest.getHeader("Origin");
        String method = httpRequest.getMethod();

        System.out.println("CorsFilter: Processing " + method + " request to " + httpRequest.getRequestURI());

        // Thiết lập CORS headers
//        if (origin != null) {
//            // Cho phép các origins nhất định
//            if (origin.equals("http://localhost:5500") ||
//                    origin.equals("http://127.0.0.1:5500")) {
//                httpResponse.setHeader("Access-Control-Allow-Origin", origin);
//                System.out.println("CorsFilter: Allowed origin: " + origin);
//            }
//        }
        httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        // Vẫn giữ credentials mặc dù chúng ta dùng token
        // Điều này giúp nếu ứng dụng sử dụng kết hợp cả token và cookies
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Các method được cho phép
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Các header được phép gửi - đặc biệt quan trọng là Authorization cho token
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");

        // Cho phép cache preflight request trong 1 giờ
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Nếu là preflight (OPTIONS) thì trả về 200 OK ngay lập tức
        if ("OPTIONS".equalsIgnoreCase(method)) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Thiết lập UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Cho request đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}