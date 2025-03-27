package com.example.bookshopwebapplication.http.request.user;

import lombok.Data;

@Data
public class RegisterDTO {
    private String fullname;
    private String email;
    private String phone;
    private String password;
    private int gender;
    private String role = "CUSTOMER";
}
