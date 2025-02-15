package com.example.bookshopwebapplication.utils.login_api;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class AbstractOauthProvider implements OauthProvider {
    protected Gson gson = new Gson();

    protected String fetchUserInfo(String userInfoUrl) throws IOException {
        URL url = new URL(userInfoUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error fetching user info: " + conn.getResponseCode());
        }
        return response.toString();
    }
}
