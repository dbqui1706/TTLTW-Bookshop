package com.example.bookshopwebapplication.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static final int EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 giờ (giây)

    public static void saveSession(String sessionId, HttpServletRequest request, HttpServletResponse response) {
        // Lấy origin từ header
        String origin = request.getHeader("Origin");
        boolean isSecure = origin != null && origin.startsWith("https");

        // Đặt domain cho cookie
        // Đối với localhost, không cần thiết lập domain hoặc có thể dùng "localhost"
        // String domain = getDomainFromOrigin(origin);

        // Phương pháp 1: Sử dụng Cookie API (không hỗ trợ SameSite)
        Cookie sessionCookie = new Cookie("SESSION_ID", sessionId);
        sessionCookie.setMaxAge(EXPIRATION_TIME);
        sessionCookie.setPath("/"); // Cho phép cookie hoạt động trên toàn bộ website
        // Không đặt domain cho localhost
        sessionCookie.setHttpOnly(true); // Bảo vệ cookie khỏi XSS
        sessionCookie.setSecure(isSecure); // Chỉ gửi qua HTTPS nếu origin là HTTPS

        // Thêm cookie vào response
        response.addCookie(sessionCookie);

        // Phương pháp 2: Sử dụng Set-Cookie header trực tiếp (hỗ trợ SameSite)
        // Cần thiết cho Chrome và các trình duyệt hiện đại
        String cookieValue = String.format("SESSION_ID=%s; Max-Age=%d; Path=/; HttpOnly",
                sessionId, EXPIRATION_TIME);

        // Thêm Secure nếu cần
        if (isSecure) {
            cookieValue += "; Secure";
        }

        // Thêm SameSite
        // SameSite=None cần đi kèm với Secure
        // SameSite=Lax là giá trị mặc định và phù hợp cho most cases
        if (origin != null && !origin.contains("localhost")) {
            // Chỉ set SameSite=None cho không phải localhost
            if (isSecure) {
                cookieValue += "; SameSite=None";
            } else {
                cookieValue += "; SameSite=Lax";
            }
        } else {
            // Cho localhost, SameSite=Lax là đủ
            cookieValue += "; SameSite=Lax";
        }

        // Thiết lập header
        response.addHeader("Set-Cookie", cookieValue);

    }

    public static String getSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static String getDomainFromOrigin(String origin) {
        if (origin == null) return null;

        try {
            // Loại bỏ protocol
            String domain = origin;
            if (domain.startsWith("http://")) {
                domain = domain.substring(7);
            } else if (domain.startsWith("https://")) {
                domain = domain.substring(8);
            }

            // Loại bỏ port và path
            int portIndex = domain.indexOf(':');
            if (portIndex > 0) {
                domain = domain.substring(0, portIndex);
            }

            // Nếu là localhost hoặc IP, trả về nguyên
            if (domain.equals("localhost") || domain.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                return domain;
            }

            return domain;
        } catch (Exception e) {
            System.out.println("Error extracting domain from origin: " + e.getMessage());
            return null;
        }
    }
}