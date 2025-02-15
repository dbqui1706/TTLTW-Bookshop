package com.example.bookshopwebapplication.utils.login_api;

import com.example.bookshopwebapplication.utils.login_api.providers.FacebookOauthProvider;
import com.example.bookshopwebapplication.utils.login_api.providers.GoogleOauthProvider;

public class OauthProviderFactory {
    public static OauthProvider getOauthProvider(String provider) {
        switch (provider.toUpperCase()) {
            case "FACEBOOK":
                return new FacebookOauthProvider();
            case "GOOGLE":
                return new GoogleOauthProvider();
            default:
                throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
