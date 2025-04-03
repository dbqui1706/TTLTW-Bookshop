package com.example.bookshopwebapplication.utils;

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
}
