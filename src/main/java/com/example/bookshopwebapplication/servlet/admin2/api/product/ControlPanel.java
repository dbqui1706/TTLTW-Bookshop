package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.dto.ProductDto;
import com.example.bookshopwebapplication.http.request.product.AddRequest;
import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.MultiPart;
import com.example.bookshopwebapplication.utils.Protector;
import com.google.gson.Gson;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "ControlPanel", urlPatterns = {
        "/admin2/api/product/add",
        "/admin2/api/product/edit",
        "/admin2/api/product/delete",
})
public class ControlPanel extends HttpServlet {
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) return;

        AddRequest product = MultiPart.get(
                req,
                AddRequest.class
        );
        Long categoryId = product.getCategoryId();

        // Transform the AddRequest to ProudctDTO
        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setDiscount(product.getDiscount());
        productDto.setQuantity(product.getQuantity());
        productDto.setTotalBuy(product.getTotalBuy());
        productDto.setAuthor(product.getAuthor());
        productDto.setPages(product.getPages());
        productDto.setPublisher(product.getPublisher());
        productDto.setYearPublishing(product.getYearPublishing());
        productDto.setDescription(product.getDescription());
        productDto.setImageName(product.getImage());
        productDto.setShop(product.getShop());
        productDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        productDto.setStartAt(convertStringToTimestamp(product.getStartsAt()));
        productDto.setEndsAt(convertStringToTimestamp(product.getEndsAt()));
        productDto.setShop(product.getShop());

        // Save the product
        Optional<ProductDto> savedProduct = productService.insert(productDto);
        if (!savedProduct.isPresent()) {
            System.out.println("[ERROR] Save product failed");
            JsonUtils.out(
                    resp,
                    "ERROR",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            return;
        }

        Long productId = savedProduct.get().getId();

        // Save the product category
        Protector.of(() -> productService.insertProductCategory(productId, categoryId))
                .done(e -> {
                    System.out.println("[INFO] Save product category successfully");

                    JsonUtils.out(
                            resp,
                            "SUCCESS",
                            HttpServletResponse.SC_OK
                    );
                })
                .fail(e -> {
                    System.out.println("[ERROR] Save product category failed");
                    e.printStackTrace();
                    JsonUtils.out(
                            resp,
                            "ERROR",
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    );
                });
    }

    public static Timestamp convertStringToTimestamp(String stringDate) {
        if (stringDate.equals("")) {
            return null;
        }
        try {
            // Chuyển đổi từ định dạng "yyyy-MM-ddTHH:mm" sang "yyyy-MM-dd HH:mm:ss"
            if (stringDate.contains("T")) {
                // Thay thế 'T' bằng khoảng trắng
                stringDate = stringDate.replace("T", " ");

                // Thêm ":00" vào cuối nếu không có phần giây
                if (!stringDate.matches(".*:\\d{2}:\\d{2}.*")) {
                    stringDate = stringDate + ":00";
                }
            }

            // Bây giờ chuỗi có định dạng đúng
            return Timestamp.valueOf(stringDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
