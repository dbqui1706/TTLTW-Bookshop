package com.example.bookshopwebapplication.http.request.user;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}
