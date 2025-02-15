package com.example.bookshopwebapplication.utils.login_api.providers;

import com.example.bookshopwebapplication.entities.OauthUser;
import com.example.bookshopwebapplication.utils.login_api.AbstractOauthProvider;
import com.example.bookshopwebapplication.utils.login_api.pojo.FBPojo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.ResourceBundle;

public class FacebookOauthProvider extends AbstractOauthProvider {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("fbConfig");

    @Override
    public String getAccessToken(String code) throws IOException {
        String link = String.format(
                bundle.getString("FACEBOOK_LINK_GET_TOKEN"),
                bundle.getString("FACEBOOK_APP_ID"),
                bundle.getString("FACEBOOK_REDIRECT_URL"),
                bundle.getString("FACEBOOK_APP_SECRET"),
                code
        );
        String response = Request.Get(link).execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        String accessToken = jobj.get("access_token").getAsString();  // Ensure no quotes are left in the token
        return accessToken;
    }

    @Override
    public OauthUser getUserInfo(String accessToken) throws IOException {
        String userInfoUrl = bundle.getString("FACEBOOK_LINK_GET_USER_INFO") + accessToken;
        FBPojo fbPojo = gson.fromJson(fetchUserInfo(userInfoUrl), FBPojo.class);

        OauthUser oauthUser = new OauthUser();
        oauthUser.setFullName(fbPojo.getName());
        oauthUser.setProvider("FACEBOOK");
        oauthUser.setProviderID(fbPojo.getId());
        return oauthUser;
    }
}