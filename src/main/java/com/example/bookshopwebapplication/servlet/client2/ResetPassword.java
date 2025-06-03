package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "ResetPassword",
        urlPatterns = {"/reset-password"}
)
public class ResetPassword extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Forward to reset password page
        req.getRequestDispatcher("/WEB-INF/views/client2/reset-password.jsp").forward(req, resp);
    }
}
