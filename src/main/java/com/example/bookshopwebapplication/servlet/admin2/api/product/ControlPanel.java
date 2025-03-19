package com.example.bookshopwebapplication.servlet.admin2.api.product;

import com.example.bookshopwebapplication.dto.CategoryDto;
import com.example.bookshopwebapplication.dto.ProductDto;
import com.example.bookshopwebapplication.http.request.product.AddRequest;
import com.example.bookshopwebapplication.service.CategoryService;
import com.example.bookshopwebapplication.service.ProductService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.MultiPart;
import com.example.bookshopwebapplication.utils.Protector;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "ControlPanel", urlPatterns = {
        "/admin2/api/product/add",
        "/admin2/api/product/update",
        "/admin2/api/product/delete",
        "/admin2/api/product/export",
})
public class ControlPanel extends HttpServlet {
    // Từ điển ánh xạ tên cột với vị trí và thuộc tính
    public static final Map<String, Map<String, Object>> COLUMN_MAPPINGS = new HashMap<String, Map<String, Object>>() {{
        put("ID", Map.of("index", 0, "field", "id", "required", false, "type", "long"));
        put("Tên sản phẩm", Map.of("index", 1, "field", "name", "required", true, "type", "string"));
        put("Giá gốc", Map.of("index", 2, "field", "price", "required", true, "type", "double"));
        put("Giảm giá (%)", Map.of("index", 3, "field", "discount", "required", true, "type", "double"));
        put("Giá khuyến mãi", Map.of("index", 4, "field", "discountedPrice", "required", false, "type", "double", "calculated", true));
        put("Tồn kho", Map.of("index", 5, "field", "quantity", "required", true, "type", "int"));
        put("Trạng thái", Map.of("index", 6, "field", "stockStatus", "required", false, "type", "string", "calculated", true));
        put("Tác giả", Map.of("index", 7, "field", "author", "required", true, "type", "string"));
        put("NXB", Map.of("index", 8, "field", "publisher", "required", true, "type", "string"));
        put("Năm XB", Map.of("index", 9, "field", "yearPublishing", "required", true, "type", "int"));
        put("Số trang", Map.of("index", 10, "field", "pages", "required", true, "type", "int"));
        put("Lượt mua", Map.of("index", 11, "field", "totalBuy", "required", false, "type", "int"));
        put("Thể loại", Map.of("index", 12, "field", "categoryName", "required", true, "type", "string"));
    }};

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin2/api/product/add":
                addProduct(req, resp);
                break;
            case "/admin2/api/product/update":
                updateProduct(req, resp);
                break;

            default:
                JsonUtils.out(
                        resp,
                        "ERROR",
                        HttpServletResponse.SC_NOT_FOUND
                );
        }
    }

    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            return Timestamp.valueOf(stringDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);

        if (!isMultipart) return;

        AddRequest product = MultiPart.get(
                req,
                AddRequest.class
        );
        Long id = Long.parseLong(req.getParameter("productId"));

        ProductDto productDto = new ProductDto();
        productDto.setId(id);
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
        productDto.setCreatedAt(convertStringToTimestamp(product.getCreatedAt()));
        productDto.setStartAt(convertStringToTimestamp(product.getStartsAt()));
        productDto.setEndsAt(convertStringToTimestamp(product.getEndsAt()));
        productDto.setShop(product.getShop());

        Optional<ProductDto> savedProduct = productService.update(productDto);
        if (!savedProduct.isPresent()) {
            System.out.println("[ERROR] Update product failed");
            JsonUtils.out(
                    resp,
                    "ERROR",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            return;
        }

        // Update the product category
        Long productId = savedProduct.get().getId();
        Long categoryId = product.getCategoryId();

        Protector.of(() -> productService.updateProductCategory(productId, categoryId))
                .done(
                        e -> {
                            System.out.println("[INFO] Update product category successfully");
                            JsonUtils.out(
                                    resp,
                                    "SUCCESS",
                                    HttpServletResponse.SC_OK
                            );
                        }
                ).fail(
                        e -> {
                            System.out.println("[ERROR] Update product category failed");
                            e.printStackTrace();
                            JsonUtils.out(
                                    resp,
                                    "ERROR",
                                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                            );
                        }
                );
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {

        JsonUtils.out(
                resp,
                "ERROR",
                HttpServletResponse.SC_NOT_FOUND
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Thiết lập header
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        switch (uri) {
            case "/admin2/api/product/export":
                exportProduct(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private void exportProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy danh sách sản phẩm
            List<ProductDto> products = productService.getAll();
            if (products.isEmpty()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                JsonUtils.out(response, "Không có sản phẩm nào", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Tạo workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Danh sách sản phẩm");

            // Tạo font cho header
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            // Tạo style cho header
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Tạo hàng header
            Row headerRow = sheet.createRow(0);
            String[] columns = {
                    "ID", "Tên sản phẩm", "Giá gốc", "Giảm giá (%)", "Giá khuyến mãi",
                    "Tồn kho", "Tác giả", "NXB", "Năm XB", "Số trang",
                    "Lượt mua", "Thể loại", "ID thể loại", "Hình ảnh", "Mô tả", "Ngày tạo",
                    "Ngày cập nhật", "Ngày bắt đầu KM", "Ngày kết thúc KM", "Giao dịch",
            };

            // Điền thông tin header
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Tạo format số
            XSSFCellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            // Tạo style cho trạng thái
            XSSFCellStyle outOfStockStyle = workbook.createCellStyle();
            outOfStockStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            outOfStockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle lowStockStyle = workbook.createCellStyle();
            lowStockStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            lowStockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle inStockStyle = workbook.createCellStyle();
            inStockStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            inStockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Điền dữ liệu sản phẩm
            int rowNum = 1;
            for (ProductDto product : products) {
                Row row = sheet.createRow(rowNum++);

                // ID sản phẩm
                row.createCell(0).setCellValue(product.getId());

                // Tên sản phẩm
                row.createCell(1).setCellValue(product.getName());

                // Giá gốc
                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(product.getPrice());
                priceCell.setCellStyle(numberStyle);

                // Giảm giá
                row.createCell(3).setCellValue(product.getDiscount());

                // Giá khuyến mãi
                Cell discountedPriceCell = row.createCell(4);
                double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100);
                discountedPriceCell.setCellValue(discountedPrice);
                discountedPriceCell.setCellStyle(numberStyle);

                // Tồn kho
                row.createCell(5).setCellValue(product.getQuantity());

                // Tác giả
                row.createCell(6).setCellValue(product.getAuthor());

                // NXB
                row.createCell(7).setCellValue(product.getPublisher());

                // Năm XB
                row.createCell(8).setCellValue(product.getYearPublishing());

                // Số trang
                row.createCell(9).setCellValue(product.getPages());

                // Lượt mua
                row.createCell(10).setCellValue(product.getTotalBuy());

                // Thể loại
                String categoryName = "Chưa phân loại";
                Optional<CategoryDto> category = categoryService.getByProductId(product.getId());
                if (category.isPresent()) {
                    categoryName = category.get().getName();
                }
                row.createCell(11).setCellValue(categoryName);

                // ID thể loại
                row.createCell(12).setCellValue(category.get().getId());

                // Hình ảnh
                row.createCell(13).setCellValue(product.getImageName());

                // Mô tả
                row.createCell(14).setCellValue(product.getDescription());

                // Ngày tạo
                row.createCell(15).setCellValue(product.getCreatedAt().toString());

                // Ngày cập nhật
                row.createCell(16).setCellValue(
                        product.getUpdatedAt() == null ? "" : product.getUpdatedAt().toString()
                );

                // Ngày bắt đầu KM
                row.createCell(17).setCellValue(
                        product.getStartAt() == null ? "" : product.getStartAt().toString()
                );

                // Ngày kết thúc KM
                row.createCell(18).setCellValue(
                        product.getEndsAt() == null ? "" : product.getEndsAt().toString()
                );

                // Giao dịch
                row.createCell(19).setCellValue(product.getShop());
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Thiết lập response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=products.xlsx");

            // Ghi workbook vào OutputStream
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi - trả về JSON error
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JsonUtils.out(response, "Lỗi xuất file Excel: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
