package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.dto.ProductDto;
import com.example.bookshopwebapplication.http.response.product.ProductDetailDto;
import com.example.bookshopwebapplication.http.response.reviews.RatingsSummary;
import com.example.bookshopwebapplication.http.response.reviews.ReviewDTO;
import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.service.ProductReviewService;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductController", urlPatterns = {
        "/api/products",
        "/api/product",
        "/api/product-related",
        "/api/product-reviews"
})
public class ProductController extends HttpServlet {
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final ProductReviewService productReviewService = new ProductReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Thiết lập response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        switch (uri) {
            case "/api/products":
                getProducts(request, response);
                break;
            case "/api/product":
                getProduct(request, response);
                break;
            case "/api/product-related":
                getProductRelated(request, response);
                break;
            case "/api/product-reviews":
                getProductReviews(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private void getProductReviews(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long productId = Long.parseLong(request.getParameter("productId"));
            RatingsSummary ratingsSummary = productReviewService.getProductRatings(
                    productId
            );
            String filter = request.getParameter("filter");
            int page = Integer.parseInt(request.getParameter("page"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            List<ReviewDTO> reviews = productReviewService.getProductReviews(
                    productId, filter, page, limit
            );

            // Lấy tổng số đánh giá theo loại lọc
            int total = productReviewService.countProductReviews(productId, filter);

            Map<String, Object> result = Map.of(
                    "ratingsSummary", ratingsSummary,
                    "reviews", reviews,
                    "total", total,
                    "currentPage", page,
                    "totalPages", (int) Math.ceil((double) total / limit),
                    "hasMore", page < (int) Math.ceil((double) total / limit)
            );
            JsonUtils.out(
                    response,
                    result,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getProductRelated(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long categoryId = Long.parseLong(request.getParameter("categoryId"));
            List<ProductDto> relatedProducts = productService.getRandomPartByCategoryId(
                    12, 0, categoryId
            );
            JsonUtils.out(
                    response,
                    relatedProducts,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getProduct(HttpServletRequest request, HttpServletResponse response) {
        Long id = Long.parseLong(request.getParameter("id"));
        try {
            ProductDetailDto productDetail = productService.getProductDetail(id);

            JsonUtils.out(
                    response,
                    productDetail,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void getProducts(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy các tham số lọc từ request
            Map<String, String[]> parameterMap = request.getParameterMap();

            // Xử lý tham số
            String searchTerm = getParameter(parameterMap, "searchTerm");
            String[] categories = getParameterValues(parameterMap, "categories");
            String[] publishers = getParameterValues(parameterMap, "publishers");
            Float priceFrom = getFloatParameter(parameterMap, "priceFrom");
            Float priceTo = getFloatParameter(parameterMap, "priceTo");
            Integer rating = getIntParameter(parameterMap, "rating");
            String[] services = getParameterValues(parameterMap, "services");
            String sortBy = getParameter(parameterMap, "sortBy", "popular");

            // Phân trang
            int page = getIntParameter(parameterMap, "page", 1);
            int limit = getIntParameter(parameterMap, "limit", 12);

            // Gọi service để lấy sản phẩm với filter
            Map<String, Object> result = productService.getFilteredProducts(
                    searchTerm, categories, publishers, priceFrom, priceTo,
                    rating, services, sortBy, page, limit
            );

            JsonUtils.out(
                    response,
                    result,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private String getParameter(Map<String, String[]> parameterMap, String name) {
        String[] values = parameterMap.get(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    private String getParameter(Map<String, String[]> parameterMap, String name, String defaultValue) {
        String value = getParameter(parameterMap, name);
        return value != null ? value : defaultValue;
    }

    private String[] getParameterValues(Map<String, String[]> parameterMap, String name) {
        String value = getParameter(parameterMap, name);
        return value != null ? value.split(",") : null;
    }

    private Integer getIntParameter(Map<String, String[]> parameterMap, String name) {
        String value = getParameter(parameterMap, name);
        return value != null ? Integer.parseInt(value) : null;
    }

    private Integer getIntParameter(Map<String, String[]> parameterMap, String name, int defaultValue) {
        Integer value = getIntParameter(parameterMap, name);
        return value != null ? value : defaultValue;
    }

    private Float getFloatParameter(Map<String, String[]> parameterMap, String name) {
        String value = getParameter(parameterMap, name);
        return value != null ? Float.parseFloat(value) : null;
    }


}
