package com.example.bookshopwebapplication.servlet.admin2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CategoryManager", urlPatterns = "/admin2/category-manager")
public class CategoryManager extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/admin2/product/category.jsp").forward(request, response);
    }
}
