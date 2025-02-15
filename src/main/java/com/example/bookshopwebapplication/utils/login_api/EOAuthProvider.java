package com.example.bookshopwebapplication.utils.login_api;

public enum EOAuthProvider {
    FACEBOOK("FACEBOOK"),
    GOOGLE("GOOGLE");

    private final String providerName;

    EOAuthProvider(String providerName) {
        this.providerName = providerName;
    }

    public String getName() {
        return providerName;
    }
}
