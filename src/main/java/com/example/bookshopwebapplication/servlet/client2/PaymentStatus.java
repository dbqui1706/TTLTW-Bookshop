package com.example.bookshopwebapplication.servlet.client2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "Payment-Status",
        urlPatterns = {
                "/payment-status",
                "/payment-status/*"
        }
)
public class PaymentStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/client2/payment-status.jsp").forward(req, resp);
    }
}
