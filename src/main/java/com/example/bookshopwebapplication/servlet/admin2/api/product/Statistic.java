package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.http.request.product.ProductStatistic;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "Statistic", urlPatterns = "/admin2/api/product/statistic")
public class Statistic extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Xử lý lấy dữ liệu thống kê sản phẩm và trả về dữ liệu dưới dạng JSON
        // 1. Tổng sản phẩm
        // 2. Sản phẩm còn hàng
        // 3. Sản phẩm sắp hết hàng
        // 4. Sản phẩm hết hàng

        try {
            ProductStatistic productStatistic = productService.getStatistic();
            JsonUtils.out(response, productStatistic, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    new Message(500, "Lỗi truy vấn dữ liệu"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
