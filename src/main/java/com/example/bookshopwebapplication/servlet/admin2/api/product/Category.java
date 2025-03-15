package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.dto.CategoryDto;
import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "Category", urlPatterns = {
        "/admin2/api/product/category"
})
public class Category extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy ra tên các danh mục sản phẩm và trả về dữ liệu dưới dạng JSON
        try {
            List<CategoryDto> categoryDto = categoryService.getAll();
            Map<Long, String> categories = new HashMap<>();
            for (CategoryDto category : categoryDto) {
                categories.put(category.getId(), category.getName());
            }

            JsonUtils.out(response, categories, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    "Lỗi truy vấn dữ liệu",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }

    }
}
