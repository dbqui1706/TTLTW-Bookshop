package com.example.bookshopwebapplication.servlet.admin2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "OrderManager",
        urlPatterns = {
                "/admin/order-manager",
                "/admin/order-manager/orders",
                "/admin/order-manager/order-detail",
        }
)
public class OrderManager extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin/order-manager":
            case "/admin/order-manager/orders":
                req.getRequestDispatcher("/WEB-INF/views/admin2/order/orders.jsp").forward(req, resp);
                break;
            case "/admin/order-manager/order-detail":
                req.getRequestDispatcher("/WEB-INF/views/admin2/order/order-detail.jsp").forward(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
