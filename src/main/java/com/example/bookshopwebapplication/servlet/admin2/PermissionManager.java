package com.example.bookshopwebapplication.servlet.admin2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PermissionServlet", urlPatterns = {
        "/admin/permissions"
})
public class PermissionManager extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin/permissions":
                req.getRequestDispatcher("/WEB-INF/views/admin2/permission/permissions.jsp").forward(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
