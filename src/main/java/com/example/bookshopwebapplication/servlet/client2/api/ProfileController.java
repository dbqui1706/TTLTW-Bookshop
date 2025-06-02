package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "Profile-Info",
        urlPatterns = {
                "/api/user/profile",
        }
)
public class ProfileController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String userId = req.getParameter("userId");
            if (userId == null || userId.isEmpty()) {
                JsonUtils.out(
                        resp, "User ID is required",
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Simulate fetching user profile data


            JsonUtils.out(
                    resp, null,
                    HttpServletResponse.SC_OK
            );

        } catch (Exception e) {
            JsonUtils.out(
                    resp, e,
                    HttpServletResponse.SC_NOT_IMPLEMENTED
            );
        }
    }
}
