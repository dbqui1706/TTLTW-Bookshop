package com.example.bookshopwebapplication.servlet.admin2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "InventoryManager",
        urlPatterns = {
                "/admin/inventory-manager",
                "/admin/inventory-manager/dashboard",
                "/admin/inventory-manager/import-export",
                "/admin/inventory-manager/management",
                "/admin/inventory-manager/report",
        }
)
public class InventoryManager extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin/inventory-manager":
            case "/admin/inventory-manager/dashboard":
                req.getRequestDispatcher("/WEB-INF/views/admin2/inventory/inventory-dashboard.jsp").forward(req, resp);
                break;
            case "/admin/inventory-manager/import-export":
                req.getRequestDispatcher("/WEB-INF/views/admin2/inventory/inventory-import-export.jsp").forward(req, resp);
                break;
            case "/admin/inventory-manager/management":
                req.getRequestDispatcher("/WEB-INF/views/admin2/inventory/inventory-management.jsp").forward(req, resp);
                break;
            case "/admin/inventory-manager/report":
                req.getRequestDispatcher("/WEB-INF/views/admin2/inventory/inventory-reports.jsp").forward(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
