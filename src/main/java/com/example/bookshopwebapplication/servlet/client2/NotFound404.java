package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
@WebServlet(
        name = "NotFound404",
        urlPatterns = {
                "/404",
                "/not-found",
                "/not-found/*"
        }
)
public class NotFound404 extends HttpServlet {
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        req.getRequestDispatcher("/WEB-INF/views/client2/404.jsp").forward(req, resp);
    }
}
