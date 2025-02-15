package com.example.bookshopwebapplication.utils.login_api;

import com.example.bookshopwebapplication.entities.OauthUser;

import java.io.IOException;
import java.util.Optional;

public class OauthLoginService {
    public static Optional<OauthUser> login(String provider, String code) throws IOException {
        OauthProvider oauthProvider = OauthProviderFactory.getOauthProvider(provider);
        String accessToken = oauthProvider.getAccessToken(code);
        OauthUser oauthUser = oauthProvider.getUserInfo(accessToken);
        return Optional.ofNullable(oauthUser);
    }
}

