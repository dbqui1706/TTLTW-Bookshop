package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(
        name = "OrderInfo",
        urlPatterns = {
                "/order-info",
                "/order-info/*",
                "/order-detail"
        }
)
public class OrderInfo extends HttpServlet {
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        String url = req.getRequestURI();
        if (url.startsWith("/order-detail")) {
            req.getRequestDispatcher("/WEB-INF/views/client2/order-detail.jsp").forward(req, resp);
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/client2/order-info.jsp").forward(req, resp);
    }
}
