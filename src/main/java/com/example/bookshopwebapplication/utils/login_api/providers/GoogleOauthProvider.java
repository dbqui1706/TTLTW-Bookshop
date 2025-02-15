package com.example.bookshopwebapplication.utils.login_api.providers;

import com.example.bookshopwebapplication.entities.OauthUser;
import com.example.bookshopwebapplication.utils.login_api.AbstractOauthProvider;
import com.example.bookshopwebapplication.utils.login_api.pojo.GooglePojo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class GoogleOauthProvider extends AbstractOauthProvider {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("googleConfig");

    @Override
    public String getAccessToken(String code) throws IOException {
        String urlParameters = "code=" + code
                + "&client_id=" + bundle.getString("GOOGLE_CLIENT_ID")
                + "&client_secret=" + bundle.getString("GOOGLE_CLIENT_SECRET")
                + "&redirect_uri=" + bundle.getString("GOOGLE_REDIRECT_URI")
                + "&grant_type=" + bundle.getString("GOOGLE_GRANT_TYPE");

        URL url = new URL(bundle.getString("GOOGLE_LINK_GET_TOKEN"));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        OutputStream os = connection.getOutputStream();
        os.write(urlParameters.getBytes());
        os.flush();
        os.close();

        // Kiểm tra mã phản hồi
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            // Parse JSON từ phản hồi để lấy access token

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
            return jsonObject.get("access_token").getAsString();
        } else {
            throw new RuntimeException("Failed to get access token: " + responseCode);
        }
    }

    @Override
    public OauthUser getUserInfo(String accessToken) throws IOException {
        String userInfoUrl = bundle.getString("GOOGLE_LINK_GET_USER_INFO") + accessToken;
        GooglePojo googlePojo = gson.fromJson(fetchUserInfo(userInfoUrl), GooglePojo.class);

        OauthUser oauthUser = new OauthUser();
        oauthUser.setEmail(googlePojo.getEmail());
        oauthUser.setFullName(googlePojo.getName());
        oauthUser.setProvider("GOOGLE");
        oauthUser.setProviderID(googlePojo.getId());
        return oauthUser;
    }
}
