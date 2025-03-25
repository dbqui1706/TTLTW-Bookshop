package com.example.bookshopwebapplication.servlet.client2.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductApi", urlPatterns = {
        "/api/products"
})
public class ProductApi extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] categories = req.getParameterValues("categories");
        String[] publishers = req.getParameterValues("publishers");
        double priceFrom = Double.parseDouble(req.getParameter("priceFrom"));
        double priceTo = Double.parseDouble(req.getParameter("priceTo"));
        int rating = Integer.parseInt(req.getParameter("rating"));
        int page = Integer.parseInt(req.getParameter("page"));
        int limit = Integer.parseInt(req.getParameter("limit"));
    }
}
