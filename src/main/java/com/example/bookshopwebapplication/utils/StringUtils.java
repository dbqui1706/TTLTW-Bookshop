package com.example.bookshopwebapplication.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class StringUtils {
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = UUID.randomUUID().toString();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static String extractToken(HttpServletRequest request) {
        // 1. Kiểm tra Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Skip "Bearer "
            return token;
        }

        // 2. Kiểm tra request parameter
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam;
        }
        return null;
    }
}
