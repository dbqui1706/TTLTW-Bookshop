package com.example.bookshopwebapplication.utils.login_api;

import com.example.bookshopwebapplication.entities.OauthUser;

import java.io.IOException;

public interface OauthProvider {
    String getAccessToken(String code) throws IOException;
    OauthUser getUserInfo(String accessToken) throws IOException;
}
