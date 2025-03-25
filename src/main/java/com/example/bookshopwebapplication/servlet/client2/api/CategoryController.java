package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/categories",
        "/api/category/publishers",
})
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/categories":
                getCategories(req, resp);
                break;
            case "/api/category/publishers":
                getPublishers(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private void getPublishers(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Đối tượng các nhà xuất bản
            List<Object> publishers = productService.getPublishersAndCountProduct();
            JsonUtils.out(resp, publishers, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    "Lỗi truy vấn dữ liệu",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getCategories(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Đối tượng các thể loại
            List<Object> categories = categoryService.getCategoriesAndCountProduct();
            JsonUtils.out(resp, categories, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    "Lỗi truy vấn dữ liệu",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
