package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.dto.ProductDto;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Product Table", urlPatterns = {
        "/admin2/api/product/table",
})
public class Table extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        switch (requestURI) {
            case "/admin2/api/product/table":
                getProducts(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void getProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Thiết lập header
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Nhận các tham số lọc từ request
            String categoryParam = request.getParameter("category");
            String stockParam = request.getParameter("stock");
            String sortParam = request.getParameter("sortOption");
            String searchParam = request.getParameter("search");
            String pageParam = request.getParameter("page");
            String limitParam = request.getParameter("limit");

            // Xử lý các giá trị mặc định nếu tham số không được cung cấp
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            int limit = (limitParam != null) ? Integer.parseInt(limitParam) : 10;
            int offset = (page - 1) * limit;

            // Xử lý lọc theo thể loại
            Long categoryId = null;
            if (categoryParam != null && !categoryParam.isEmpty()) {
                categoryId = Long.parseLong(categoryParam);
            }

            // Xây dựng các điều kiện lọc
            List<String> filters = new ArrayList<>();

            // Lọc theo thể loại
            if (categoryId != null) {
                filters.add("EXISTS (SELECT 1 FROM product_category pc WHERE pc.productId = p.id AND pc.categoryId = " + categoryId + ")");
            }

            // Lọc theo tồn kho
            if (stockParam != null) {
                switch (stockParam) {
                    case "AVAILABLE":
                        filters.add("p.quantity > 10");
                        break;
                    case "ALMOST_OUT_OF_STOCK":
                        filters.add("p.quantity > 0 AND p.quantity <= 10");
                        break;
                    case "OUT_OF_STOCK":
                        filters.add("p.quantity = 0");
                        break;
                }
            }

            // Lọc theo tìm kiếm
            if (searchParam != null && !searchParam.isEmpty()) {
                filters.add("(p.name LIKE '%" + searchParam + "%' OR p.author LIKE '%" + searchParam + "%' OR p.publisher LIKE '%" + searchParam + "%')");
            }

            // Xây dựng chuỗi điều kiện lọc
            String filterQuery = filters.isEmpty() ? "" : productService.createFiltersQuery(filters);

            // Xác định cột và thứ tự sắp xếp
            String[] orderByAndSort = getOrderByAndSort(sortParam);
            String orderBy = orderByAndSort[0];
            String sort = orderByAndSort[1];

            // Đếm tổng số sản phẩm theo bộ lọc
            int totalProducts;
            if (!filterQuery.isEmpty()) {
                totalProducts = productService.countByFilter(filterQuery);
            } else {
                totalProducts = productService.count();
            }

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalProducts / limit);

            // Lấy danh sách sản phẩm
            List<ProductDto> products;
            if (!filterQuery.isEmpty()) {
                products = productService.getOrderedPartByFilters(limit, offset, orderBy, sort, filterQuery);
            } else {
                products = productService.getOrderedPart(limit, offset, orderBy, sort);
            }

            // Chuyển đổi sản phẩm thành định dạng phù hợp để trả về client
            List<Map<String, Object>> productList = new ArrayList<>();
            for (ProductDto product : products) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("name", product.getName());
                productMap.put("price", product.getPrice());
                productMap.put("discount", product.getDiscount());
                productMap.put("quantity", product.getQuantity());
                productMap.put("author", product.getAuthor());
                productMap.put("publisher", product.getPublisher());
                productMap.put("yearPublishing", product.getYearPublishing());
                productMap.put("pages", product.getPages());
                productMap.put("imageName", product.getImageName());
                productMap.put("totalBuy", product.getTotalBuy());
                productMap.put("createdAt", product.getCreatedAt());
                productMap.put("updatedAt", product.getUpdatedAt());
                productMap.put("startAt", product.getStartAt());
                productMap.put("endsAt", product.getEndsAt());
                productMap.put("description", product.getDescription());
                productMap.put("shop", product.getShop());
                // Lấy thông tin thể loại của sản phẩm
                categoryService.getByProductId(product.getId()).ifPresent(category -> {
                    productMap.put("categoryId", category.getId());
                    productMap.put("categoryName", category.getName());
                });

                // Tính toán giá sau khuyến mãi
                double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100);
                productMap.put("discountedPrice", discountedPrice);

                // Xác định trạng thái tồn kho
                String stockStatus;
                if (product.getQuantity() <= 0) {
                    stockStatus = "OUT_OF_STOCK";
                } else if (product.getQuantity() <= 10) {
                    stockStatus = "ALMOST_OUT_OF_STOCK";
                } else {
                    stockStatus = "AVAILABLE";
                }
                productMap.put("stockStatus", stockStatus);

                productList.add(productMap);
            }

            // Tạo đối tượng phản hồi
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("products", productList);
            responseData.put("totalProducts", totalProducts);
            responseData.put("totalPages", totalPages);
            responseData.put("currentPage", page);

            // Gửi phản hồi JSON
            JsonUtils.out(response, responseData, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            // Xử lý lỗi
            JsonUtils.out(response, "Lỗi truy vấn dữ liệu", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private String[] getOrderByAndSort(String sortParam) {
        String orderBy = "id";
        String sort = "DESC";

        if (sortParam != null) {
            switch (sortParam) {
                case "PRICE_ASC":
                    orderBy = "price";
                    sort = "ASC";
                    break;
                case "PRICE_DESC":
                    orderBy = "price";
                    sort = "DESC";
                    break;
                case "NAME_ASC":
                    orderBy = "name";
                    sort = "ASC";
                    break;
                case "NAME_DESC":
                    orderBy = "name";
                    sort = "DESC";
                    break;
                case "POPULARITY_ASC":
                    orderBy = "totalBuy";
                    sort = "DESC";
                    break;
                case "CREATED_AT_ASC":
                    orderBy = "createdAt";
                    sort = "ASC";
                    break;
                case "CREATED_AT_DESC":
                    orderBy = "createdAt";
                    sort = "DESC";
                    break;
            }
        }
        return new String[]{orderBy, sort};
    }
}
