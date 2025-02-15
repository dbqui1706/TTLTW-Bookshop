package com.example.bookshopwebapplication.utils.login_api.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@Getter
@Setter
public class FBPojo {
    private String id;
    private String name;
    private String email;
    private String phone;
}
