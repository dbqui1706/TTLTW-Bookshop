package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "Address-Info",
        urlPatterns = {
                "/addresses-info",
                "/addresses-info/*"
        }
)
public class Address extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/client2/address-info.jsp").forward(req, resp);
    }
}
