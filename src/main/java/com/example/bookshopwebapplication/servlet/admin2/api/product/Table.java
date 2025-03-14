package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Product Table", urlPatterns = {
        "/admin2/api/product/table",
})
public class Table extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        switch (requestURI) {
            case "/admin2/api/product/table":
                getProductTable(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // Lấy ra danh sách các sản phẩm và trả về dữ liệu dưới dạng JSON
    private void getProductTable(HttpServletRequest request, HttpServletResponse response) {

    }
}
