package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "Product Table", urlPatterns = {
        "/admin2/api/product/table",
})
public class Table extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

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
        // Lấy các tham số lọc từ request
        String categoryParam = request.getParameter("category");
        String stockParam = request.getParameter("stock");
        String sortOptionParam = request.getParameter("sortOption");
        String searchParam = request.getParameter("search");
        String pageParam = request.getParameter("page");
        String limitParam = request.getParameter("limit");

        // Chuyển đổi tham số sang kiểu dữ liệu phù hợp với xử lý sau này
        Long categoryId = null;
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryParam);
            } catch (NumberFormatException e) {
                // Bỏ qua nếu categoryId không phải số
            }
        }

        // Xử lý tham số stock
        String stock = null;
        if (stockParam != null && !stockParam.isEmpty()) {
            stock = stockParam;
        }

        // Xử lý tham số sắp xếp
        String sortOption = null;
        if (sortOptionParam != null && !sortOptionParam.isEmpty()) {
            sortOption = sortOptionParam;
        }

        // Xử lý tham số tìm kiếm
        String search = null;
        if (searchParam != null && !searchParam.trim().isEmpty()) {
            search = searchParam.trim();
        }

        // Xử lý phân trang
        int page = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định nếu không phải số
            }
        }

        int limit = 10;
        if (limitParam != null && !limitParam.isEmpty()) {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit < 1) {
                    limit = 10;
                }
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định nếu không phải số
            }
        }

        int offset = (page - 1) * limit;
        try {
            // Thực hiện truy vấn
            Map<String, Object> result = productService.getProductsWithFilters(
                    categoryId, stock, sortOption, search, offset, limit);

            // Trả về dữ liệu dưới dạng JSON
            JsonUtils.out(response, result, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    "Lỗi truy vấn dữ liệu",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
