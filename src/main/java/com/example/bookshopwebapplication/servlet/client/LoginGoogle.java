package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.OauthUser;
import com.example.bookshopwebapplication.service.OauthUserService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.login_api.EOAuthProvider;
import com.example.bookshopwebapplication.utils.login_api.OauthLoginService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

//@WebServlet(urlPatterns = {"/login-gg", "/login-fb"})
public class LoginGoogle extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final OauthUserService oauthUserService = new OauthUserService();
    private final UserService userService = new UserService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code == null || code.isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/views/client/signin.jsp").forward(request, response);
        } else {
            OauthUser oauthUser = request.getRequestURI().equals("/login-gg")
                    ? OauthLoginService.login(EOAuthProvider.GOOGLE.getName(), code).get()
                    : OauthLoginService.login(EOAuthProvider.FACEBOOK.getName(), code).get();

            Optional<OauthUser> insertOauth = oauthUserService.insert(oauthUser);

            if (insertOauth.isPresent()) {
                Optional<UserDto> userFromServer = userService.getById(insertOauth.get().getUserID());
                request.getSession().setAttribute("currentUser", userFromServer.get());
                response.sendRedirect(request.getContextPath() + "/");
            }
        }
    }
}
