package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.dto.CategoryDto;
import com.example.bookshopwebapplication.http.response.category.CategoryProduct;
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
import java.util.*;

@WebServlet(name = "Category", urlPatterns = {
        "/admin2/api/product/category",
        "/admin2/api/product/category-statistic",
        "/admin2/api/product/product-category",
})
public class Category extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        switch (uri) {
            case "/admin2/api/product/category":
                getCategories(request, response);
                break;
            case "/admin2/api/product/product-category":
                getProductCategories(request, response);
                break;
            case "/admin2/api/product/category-statistic":
                getCategoryStatistic(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getCategoryStatistic(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy tất cả danh mục
            List<CategoryDto> allCategories = categoryService.getAll();

            // Đếm tổng số danh mục
            int total = allCategories.size();

            // Đếm số danh mục đang hoạt động
            int active = 0;

            // Đếm số danh mục không hoạt động
            int inactive = 0;

            // Duyệt qua tất cả danh mục để đếm theo trạng thái
            for (CategoryDto category : allCategories) {
//                if (category.isActive()) {
//                    active++;
//                } else {
//                    inactive++;
//                }
            }

            // Tạo đối tượng kết quả
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("active", active);
            result.put("inactive", inactive);

            // Trả về kết quả dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            new Gson().toJson(result, response.getWriter());

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtils.out(
                    response,
                    "Lỗi truy vấn dữ liệu: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getProductCategories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Lấy các tham số tìm kiếm và phân trang
            String searchQuery = request.getParameter("search");
            String statusFilter = request.getParameter("status");

            int page = 1;
            int limit = 5;

            try {
                if (request.getParameter("page") != null) {
                    page = Integer.parseInt(request.getParameter("page"));
                }
                if (request.getParameter("limit") != null) {
                    limit = Integer.parseInt(request.getParameter("limit"));
                }
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định nếu có lỗi
            }

            // Lấy tất cả danh mục
            List<CategoryDto> allCategories = categoryService.getAll();

            // Lọc danh mục theo tìm kiếm và trạng thái
            List<CategoryDto> filteredCategories = new ArrayList<>();

            // Lấy tổng số danh mục thỏa điều kiện lọc
            int totalCategories = allCategories.size();

            // Tính toán tổng số trang
            int totalPages = (int) Math.ceil((double) totalCategories / limit);

            // Đảm bảo page nằm trong khoảng hợp lệ
            if (page < 1) page = 1;
            if (page > totalPages && totalPages > 0) page = totalPages;

            // Lấy danh mục cho trang hiện tại
            int startIndex = (page - 1) * limit;
            int endIndex = Math.min(startIndex + limit, totalCategories);

            List<CategoryDto> paginatedCategories = allCategories.subList(startIndex, endIndex);

            // Lấy số lượng sản phẩm cho mỗi danh mục
            Map<Long, Integer> productCountByCategory = categoryService.getProductsCountByCategory();

            // Chuyển đổi sang định dạng trả về cho front-end
            List<Map<String, Object>> categories = new ArrayList<>();

            for (CategoryDto category : paginatedCategories) {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("id", category.getId());
                categoryMap.put("name", category.getName());
                categoryMap.put("description", category.getDescription());
                categoryMap.put("imageName", category.getImageName());
                categoryMap.put("productCount", productCountByCategory.getOrDefault(category.getId(), 0));
                categoryMap.put("createdAt", category.getCreatedAt());
                categoryMap.put("updatedAt", category.getUpdatedAt());

                categories.add(categoryMap);
            }

            // Tạo đối tượng kết quả
            Map<String, Object> result = new HashMap<>();
            result.put("categories", categories);
            result.put("currentPage", page);
            result.put("totalPages", totalPages);
            result.put("totalCategories", totalCategories);

            // Trả về kết quả dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JsonUtils.out(
                    response,
                    result,
                    HttpServletResponse.SC_OK
            );

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtils.out(
                    response,
                    "Lỗi truy vấn dữ liệu: " + e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getCategories(HttpServletRequest request, HttpServletResponse response) {
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
