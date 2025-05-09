package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(
        name = "AdresssInfo",
        urlPatterns = {
                "/address-info",
                "/address-info/*"
        }
)
public class AddressInfo extends HttpServlet {
    @Override
    protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        req.getRequestDispatcher("/WEB-INF/views/client2/address-info.jsp").forward(req, resp);
    }
}
