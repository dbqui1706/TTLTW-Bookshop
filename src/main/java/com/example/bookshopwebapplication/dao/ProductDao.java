package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IProductDao;
import com.example.bookshopwebapplication.dao.mapper.ProductMapper;
import com.example.bookshopwebapplication.entities.Product;
import com.example.bookshopwebapplication.http.response.product.ProductDetailDto;

import java.sql.*;
import java.util.*;

public class ProductDao extends AbstractDao<Product> implements IProductDao {

    public ProductDao() {
        super("product");
    }

    // Phương thức để lưu một đối tượng Product vào cơ sở dữ liệu
    public Long save(Product product) {
        clearSQL();
        builderSQL.append("INSERT INTO product (name, price, discount, quantity, totalBuy, author, ");
        builderSQL.append("pages, publisher, yearPublishing, description, imageName, shop, createdAt, ");
        builderSQL.append("updatedAt, startsAt, endsAt) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), product.getName(), product.getPrice(), product.getDiscount(),
                product.getQuantity(), product.getTotalBuy(), product.getAuthor(), product.getPages(),
                product.getPublisher(), product.getYearPublishing(), product.getDescription(),
                product.getImageName(), product.getShop(), product.getCreatedAt(), product.getUpdatedAt(),
                product.getStartAt(), product.getEndsAt());
    }

    // Phương thức để cập nhật thông tin một đối tượng Product trong cơ sở dữ liệu
    public void update(Product product) {
        clearSQL();
        builderSQL.append(
                "UPDATE product SET name = ?, price = ?, discount = ?, quantity = ?, " +
                        "totalBuy = ?, author = ?, pages = ?, publisher = ?, " +
                        "yearPublishing = ?, description = ?, imageName = ?, " +
                        "shop = ?, updatedAt = ? " +
                        "WHERE id = ?"
        );
        update(builderSQL.toString(), product.getName(), product.getPrice(), product.getDiscount(),
                product.getQuantity(), product.getTotalBuy(), product.getAuthor(), product.getPages(),
                product.getPublisher(), product.getYearPublishing(), product.getDescription(),
                product.getImageName(), product.getShop(),
                new Timestamp(System.currentTimeMillis()), product.getId());
    }

    // Phương thức để xóa một đối tượng Product khỏi cơ sở dữ liệu theo ID
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM product WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    // Phương thức để lấy một đối tượng Product từ cơ sở dữ liệu theo ID
    public Optional<Product> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM product WHERE id = ?");
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), id);
        return products.isEmpty() ? Optional.empty() : Optional.ofNullable(products.get(0));
    }

    // Phương thức để lấy một phần danh sách Product từ cơ sở dữ liệu với giới hạn và vị trí bắt đầu
    public List<Product> getPart(Integer limit, Integer offset) {
        clearSQL();
        builderSQL.append("SELECT * FROM product LIMIT " + offset + ", " + limit);
        return super.getPart(builderSQL.toString(), new ProductMapper());
    }

    // Phương thức để lấy một phần danh sách Product từ cơ sở dữ liệu với sắp xếp theo các thuộc tính được chỉ định
    public List<Product> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        clearSQL();
        builderSQL.append("SELECT * FROM product ORDER BY " + orderBy + " " + sort);
        builderSQL.append(" LIMIT " + offset + ", " + limit + "");
        return super.getOrderedPart(builderSQL.toString(), new ProductMapper());
    }

    public List<Product> getAll() {
        clearSQL();
        builderSQL.append("SELECT * FROM product");
        return query(builderSQL.toString(), new ProductMapper());
    }

    public int count() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM bookshopdb.product"
        );
        return count(builderSQL.toString());
    }

    // Sản phẩm còn hàng (quantity > 0)
    public int countAvailable() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM bookshopdb.product WHERE quantity > 0"
        );
        return count(builderSQL.toString());
    }

    // Sản phẩm sắp hết hàng (quantity > 0 nhưng nhỏ, ví dụ <= 10)
    public int countAlmostOutOfStock() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM bookshopdb.product WHERE quantity > 0 AND quantity <= 10"
        );
        return count(builderSQL.toString());
    }

    // Sản phẩm hết hàng (quantity = 0)
    public int countOutOfStock() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM bookshopdb.product WHERE quantity = 0"
        );
        return count(builderSQL.toString());
    }

    // Phương thức để đếm số lượng sản phẩm theo ID của một danh mục
    public int countByCategoryId(Long id) {
        clearSQL();
        builderSQL.append("SELECT COUNT(productId) FROM product_category WHERE categoryId = ?");
        return count(builderSQL.toString(), id);
    }

    // Phương thức để đếm số lượng sản phẩm theo ID của một danh mục và các điều kiện lọc
    @Override
    public int countByCategoryIdAndFilters(Long id, String filtersQuery) {
        clearSQL();
        builderSQL.append("SELECT COUNT(p.id) ");
        builderSQL.append("FROM product_category pc ");
        builderSQL.append("JOIN product p ON pc.productId = p.id ");
        builderSQL.append("WHERE pc.categoryId = ? ");
        builderSQL.append("AND " + filtersQuery);
        return count(builderSQL.toString(), id);
    }

    @Override
    public int countByFilter(String filtersQuery) {
        clearSQL();
        clearSQL();
        builderSQL.append("SELECT COUNT(*) FROM product p WHERE " + filtersQuery);
        return count(builderSQL.toString());
    }


    @Override
    public String getIDByCategoriesName(String categoryNames) {
        clearSQL();
        builderSQL.append(
                "SELECT pc.productId FROM product_category pc " +
                        "JOIN category c ON pc.categoryId = c.id " +
                        "WHERE c.name in " + categoryNames
        );

        return builderSQL.toString();
    }

    @Override
    public List<Product> getProductByFilter(String filters) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM product p WHERE " + filters
        );
        List<Product> products = query(builderSQL.toString(), new ProductMapper());
        return products.isEmpty() ? new LinkedList<>() : products;
    }

    // Phương thức để lấy một phần danh sách Product từ cơ sở dữ liệu với sắp xếp theo các thuộc tính được chỉ định và theo ID của một danh mục
    @Override
    public List<Product> getOrderedPartByCategoryId(int limit, int offset, String orderBy, String sort, Long id) {
        clearSQL();
        builderSQL.append("SELECT p.* FROM product_category pc JOIN product p ");
        builderSQL.append("ON pc.productId = p.id WHERE pc.categoryId = ? ");
        builderSQL.append("ORDER BY p." + orderBy + " " + sort + " ");
        builderSQL.append("LIMIT " + offset + ", " + limit);
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), id);
        return products.isEmpty() ? null : products;
    }

    // Phương thức để lấy một phần danh sách Product từ cơ sở dữ liệu với sắp xếp theo các thuộc tính được chỉ định, theo ID của một danh mục và theo điều kiện lọc
    @Override
    public List<Product> getOrderedPartByCategoryIdAndFilters(int limit, int offset, String orderBy, String sort, Long id, String filtersQuery) {
        clearSQL();
        builderSQL.append(
                "SELECT p.* " +
                        "FROM product_category pc " +
                        "JOIN product p ON pc.productId = p.id " +
                        "WHERE pc.categoryId = ? " +
                        "AND " + filtersQuery +
                        "ORDER BY p." + orderBy + " " + sort + " " +
                        "LIMIT " + offset + ", " + limit
        );
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), id);
        return products.isEmpty() ? new LinkedList<Product>() : products;
    }

    @Override
    public List<Product> getOrderedPartByFilters(int limit, int offset, String orderBy, String sort, String filters) {
        clearSQL();
        builderSQL.append(
                "SELECT p.* " +
                        "FROM product p " +
                        "WHERE " + filters + " " +
                        "ORDER BY p." + orderBy + " " + sort + " " +
                        "LIMIT " + offset + ", " + limit
        );
        List<Product> products = query(builderSQL.toString(), new ProductMapper());
        return products.isEmpty() ? new LinkedList<>() : products;
    }

    // Phương thức để lấy danh sách các nhà xuất bản từ cơ sở dữ liệu theo ID của một danh mục
    @Override
    public List<String> getPublishersByCategoryId(Long id) {
        clearSQL();
        builderSQL.append(
                "SELECT DISTINCT p.publisher " +
                        "FROM product_category pc " +
                        "JOIN product p ON pc.productId = p.id " +
                        "WHERE pc.categoryId = ? " +
                        "ORDER BY p.publisher"
        );
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            List<String> result = new LinkedList<>();
            statement = connection.prepareStatement(builderSQL.toString());
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString("publisher"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Đảm bảo đóng các tài nguyên liên quan đến cơ sở dữ liệu
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
    }

    // Phương thức để lấy một phần danh sách Product từ cơ sở dữ liệu với sắp xếp ngẫu nhiên, theo ID của một danh mục
    @Override
    public List<Product> getRandomPartByCategoryId(int limit, int offset, Long categoryId) {
        clearSQL();
        builderSQL.append(
                "SELECT p.* FROM product_category pc " +
                        "JOIN product p ON pc.productId = p.id " +
                        "WHERE pc.categoryId = ? AND shop = 1 " +
                        "ORDER BY RAND() " +
                        "LIMIT " + offset + ", " + limit
        );
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), categoryId);
        return products.isEmpty() ? new LinkedList<>() : products;
    }

    // Phương thức để lấy danh sách Product từ cơ sở dữ liệu theo ID của một danh mục
    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        clearSQL();
        builderSQL.append("SELECT p.* FROM product_category pc JOIN product p ");
        builderSQL.append("ON pc.productId = p.id WHERE pc.categoryId = ? ");
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), categoryId);
        return products.isEmpty() ? new LinkedList<Product>() : products;
    }

    @Override
    public List<String> getPublishers() {
        clearSQL();
        builderSQL.append("SELECT DISTINCT(publisher) FROM product");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            List<String> result = new LinkedList<>();
            statement = connection.prepareStatement(builderSQL.toString());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString("publisher"));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Đảm bảo đóng các tài nguyên liên quan đến cơ sở dữ liệu
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public int countByQuery(String query) {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(id) " +
                        "FROM product " +
                        "WHERE name LIKE CONCAT('%', ?, '%')"
        );
        return count(builderSQL.toString(), query);
    }

    @Override
    public List<Product> getByQuery(String query, int limit, int offset) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM product " +
                        "WHERE name LIKE CONCAT('%', ?, '%') " +
                        "LIMIT " + limit + " OFFSET " + offset
        );
        List<Product> products = query(builderSQL.toString(), new ProductMapper(), query);
        return products.isEmpty() ? new LinkedList<>() : products;
    }

    @Override
    public void insertProductCategory(long productId, long categoryId) {
        clearSQL();
        builderSQL.append(
                "INSERT product_category (productId, categoryId) VALUES (?, ?)"
        );
        insertNoGenerateKey(builderSQL.toString(), productId, categoryId);
    }

    @Override
    public void updateProductCategory(long productId, long categoryId) {
        clearSQL();
        builderSQL.append(
                "UPDATE product_category SET categoryId = ? WHERE productId = ?"
        );
        update(builderSQL.toString(), categoryId, productId);
    }

    @Override
    public void deleteProductCategory(long productId, long categoryId) {
        clearSQL();
        builderSQL.append(
                "DELETE FROM product_category WHERE productId = ? AND categoryId = ?"
        );
        update(builderSQL.toString(), productId, categoryId);
    }

    @Override
    public Product mapResultSetToEntity(ResultSet resultSet) {
        try {
            Product product = new Product();
            product.setId(resultSet.getLong("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getDouble("price"));
            product.setDiscount(resultSet.getDouble("discount"));
            product.setQuantity(resultSet.getInt("quantity"));
            product.setTotalBuy(resultSet.getInt("totalBuy"));
            product.setAuthor(resultSet.getString("author"));
            product.setPages(resultSet.getInt("pages"));
            product.setPublisher(resultSet.getString("publisher"));
            product.setYearPublishing(resultSet.getInt("yearPublishing"));
            product.setDescription(resultSet.getString("description"));
            product.setImageName(resultSet.getString("imageName"));
            product.setShop(resultSet.getInt("shop"));
            product.setCreatedAt(resultSet.getTimestamp("createdAt"));
            product.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
            return product;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getProductsWithFilters(Long categoryId, String stock,
                                                      String sortOption, String search,
                                                      int offset, int limit) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> products = new ArrayList<>();
        long totalElements = 0;

        Connection conn = null;
        PreparedStatement stmtCount = null;
        PreparedStatement stmtProducts = null;
        ResultSet rsCount = null;
        ResultSet rsProducts = null;

        try {
            conn = getConnection();

            // Xây dựng câu truy vấn SQL cơ bản
            StringBuilder baseQueryBuilder = new StringBuilder();
            baseQueryBuilder.append("FROM bookshopdb.product p ");

            // Thêm JOIN nếu có lọc theo danh mục
            if (categoryId != null) {
                baseQueryBuilder.append("INNER JOIN bookshopdb.product_category pc ON p.id = pc.productId ");
                baseQueryBuilder.append("WHERE pc.categoryId = ? ");
            } else {
                baseQueryBuilder.append("WHERE 1=1 ");
            }

            // Thêm điều kiện lọc theo trạng thái tồn kho
            stockFilters(stock, baseQueryBuilder);

            // Thêm điều kiện tìm kiếm
            if (search != null && !search.isEmpty()) {
                baseQueryBuilder.append("AND (p.name LIKE ? OR p.description LIKE ? OR p.author LIKE ?) ");
            }

            // Truy vấn đếm tổng số sản phẩm
            StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(DISTINCT p.id) AS total ");
            countQueryBuilder.append(baseQueryBuilder);

            // Truy vấn lấy thông tin sản phẩm
            StringBuilder productQueryBuilder = new StringBuilder("SELECT DISTINCT p.* ");
            productQueryBuilder.append(baseQueryBuilder);

            // Thêm sắp xếp
            sortOptions(sortOption, productQueryBuilder);

            // Thêm phân trang
            productQueryBuilder.append("LIMIT ? OFFSET ?");

            // Chuẩn bị câu lệnh và thực thi truy vấn đếm
            stmtCount = conn.prepareStatement(countQueryBuilder.toString());
            int paramIndex = 1;

            if (categoryId != null) {
                stmtCount.setLong(paramIndex++, categoryId);
            }

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                stmtCount.setString(paramIndex++, searchPattern);
                stmtCount.setString(paramIndex++, searchPattern);
                stmtCount.setString(paramIndex++, searchPattern);
            }

            rsCount = stmtCount.executeQuery();
            if (rsCount.next()) {
                totalElements = rsCount.getLong("total");
            }

            // Chuẩn bị câu lệnh và thực thi truy vấn sản phẩm
            stmtProducts = conn.prepareStatement(productQueryBuilder.toString());
            paramIndex = 1;

            if (categoryId != null) {
                stmtProducts.setLong(paramIndex++, categoryId);
            }

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                stmtProducts.setString(paramIndex++, searchPattern);
                stmtProducts.setString(paramIndex++, searchPattern);
                stmtProducts.setString(paramIndex++, searchPattern);
            }

            stmtProducts.setInt(paramIndex++, limit);
            stmtProducts.setInt(paramIndex++, offset);

            rsProducts = stmtProducts.executeQuery();

            // Xử lý kết quả
            while (rsProducts.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rsProducts.getLong("id"));
                product.put("name", rsProducts.getString("name"));
                product.put("price", rsProducts.getFloat("price"));
                product.put("discount", rsProducts.getFloat("discount"));
                product.put("quantity", rsProducts.getInt("quantity"));
                product.put("totalBuy", rsProducts.getInt("totalBuy"));
                product.put("author", rsProducts.getString("author"));
                product.put("pages", rsProducts.getInt("pages"));
                product.put("publisher", rsProducts.getString("publisher"));
                product.put("yearPublishing", rsProducts.getInt("yearPublishing"));
                product.put("description", rsProducts.getString("description"));
                product.put("imageName", rsProducts.getString("imageName"));
                product.put("shop", rsProducts.getBoolean("shop"));

                // Xử lý các trường datetime
                Timestamp createdAt = rsProducts.getTimestamp("createdAt");
                product.put("createdAt", createdAt != null ? createdAt.toString() : null);

                Timestamp updatedAt = rsProducts.getTimestamp("updatedAt");
                product.put("updatedAt", updatedAt != null ? updatedAt.toString() : null);

                Timestamp startsAt = rsProducts.getTimestamp("startsAt");
                product.put("startsAt", startsAt != null ? startsAt.toString() : null);

                Timestamp endsAt = rsProducts.getTimestamp("endsAt");
                product.put("endsAt", endsAt != null ? endsAt.toString() : null);

                // Thêm danh mục của sản phẩm
                product.put("categories", getProductCategories(conn, rsProducts.getLong("id")));

                products.add(product);
            }

            // Tính toán tổng số trang
            int totalPages = (int) Math.ceil((double) totalElements / limit);
            // Tạo kết quả trả về
            result.put("data", products);
            result.put("totalElements", totalElements);
            result.put("totalPages", totalPages);
            result.put("page", offset / limit + 1);
            result.put("limit", limit);

        } catch (SQLException e) {
            System.out.println("Error Function getProductsWithFilters: " + e);
            throw new RuntimeException(e);
        } finally {
            // Đóng tất cả các resource
            if (rsProducts != null) try {
                rsProducts.close();
            } catch (SQLException e) { /* ignored */ }
            if (rsCount != null) try {
                rsCount.close();
            } catch (SQLException e) { /* ignored */ }
            if (stmtProducts != null) try {
                stmtProducts.close();
            } catch (SQLException e) { /* ignored */ }
            if (stmtCount != null) try {
                stmtCount.close();
            } catch (SQLException e) { /* ignored */ }
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) { /* ignored */ }
        }

        return result;
    }

    // Helper method để lấy danh mục của sản phẩm
    private Map<Long, String> getProductCategories(Connection conn, long productId) throws SQLException {
        Map<Long, String> categories = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT c.id, c.name FROM bookshopdb.category c " +
                    "INNER JOIN bookshopdb.product_category pc ON c.id = pc.categoryId " +
                    "WHERE pc.productId = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, productId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                categories.put(rs.getLong("id"), rs.getString("name"));
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) { /* ignored */ }
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) { /* ignored */ }
        }

        return categories;
    }

    public void stockFilters(String stock, StringBuilder baseQueryBuilder) {
        if (stock != null) {

            switch (stock) {
                case "AVAILABLE":
                    baseQueryBuilder.append("AND p.quantity > 10 ");
                    break;
                case "ALMOST_OUT_OF_STOCK":
                    baseQueryBuilder.append("AND p.quantity > 0 AND p.quantity <= 10 ");
                    break;
                case "OUT_OF_STOCK":
                    baseQueryBuilder.append("AND p.quantity = 0 ");
                    break;
                default:
                    // Không thêm điều kiện
                    break;
            }
        }
    }

    public void sortOptions(String sortOption, StringBuilder productQueryBuilder) {

        switch (sortOption) {
            case "PRICE_ASC":
                productQueryBuilder.append("ORDER BY p.price ASC ");
                break;
            case "PRICE_DESC":
                productQueryBuilder.append("ORDER BY p.price DESC ");
                break;
            case "NAME_ASC":
                productQueryBuilder.append("ORDER BY p.name ASC ");
                break;
            case "NAME_DESC":
                productQueryBuilder.append("ORDER BY p.name DESC ");
                break;
            case "POPULARITY_ASC":
                productQueryBuilder.append("ORDER BY p.totalBuy DESC ");
                break;
            case "CREATED_AT_ASC":
                productQueryBuilder.append("ORDER BY p.createdAt ASC ");
                break;
            case "CREATED_AT_DESC":
                productQueryBuilder.append("ORDER BY p.createdAt DESC ");
                break;
            default:
                productQueryBuilder.append("ORDER BY p.id DESC ");
                break;
        }
    }

    /**
     * Lấy tất cả nhà xuất bản và số lượng sản phẩm của mỗi nhà xuất bản
     *
     * @return Map với key là ID (được tạo tự động), value là một Map chứa thông tin nhà xuất bản và số lượng sản phẩm
     */
    public List<Object> getPublishersAndCountProduct() {
        List<Object> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection(); // Phương thức để lấy kết nối đến database

            String query = "SELECT publisher, COUNT(*) AS productCount " +
                    "FROM product " +
                    "GROUP BY publisher " +
                    "ORDER BY publisher";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            long id = 1; // ID tự tạo cho các nhà xuất bản

            while (rs.next()) {
                Map<String, Object> publisherInfo = new HashMap<>();

                String publisherName = rs.getString("publisher");
                int productCount = rs.getInt("productCount");

                publisherInfo.put("id", id);
                publisherInfo.put("name", publisherName);
                publisherInfo.put("productCount", productCount);

                result.add(publisherInfo);
                id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý ngoại lệ tùy thuộc vào ứng dụng của bạn
        } finally {
            // Đóng các tài nguyên
            close(conn, stmt, rs);
        }

        return result;
    }

    public List<Product> getFilteredProducts(String searchTerm, String[] categories, String[] publishers, Float priceFrom, Float priceTo, Integer rating, String[] services, String sortBy, int limit, int offset) {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Xây dựng câu truy vấn SQL với các bộ lọc
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT p.* FROM product p ");

            // Join bảng category nếu cần
            if (categories != null && categories.length > 0) {
                sql.append("JOIN product_category pc ON p.id = pc.productId ");
                sql.append("JOIN category c ON pc.categoryId = c.id ");
            }

            // Điều kiện WHERE
            sql.append("WHERE 1=1 ");

            // Thêm các điều kiện lọc
            List<Object> params = new ArrayList<>();

            // Tìm kiếm
            if (searchTerm != null && !searchTerm.isEmpty()) {
                sql.append("AND (p.name LIKE ? OR p.author LIKE ? OR p.description LIKE ?) ");
                String searchPattern = "%" + searchTerm + "%";
                params.add(searchPattern);
                params.add(searchPattern);
                params.add(searchPattern);
            }

            // Lọc theo category
            if (categories != null && categories.length > 0) {
                sql.append("AND (");
                for (int i = 0; i < categories.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("c.name = ?");
                    params.add(categories[i]);
                }
                sql.append(") ");
            }

            // Lọc theo nhà xuất bản
            if (publishers != null && publishers.length > 0) {
                sql.append("AND (");
                for (int i = 0; i < publishers.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("p.publisher = ?");
                    params.add(publishers[i]);
                }
                sql.append(") ");
            }

            // Lọc theo khoảng giá
            if (priceFrom != null) {
                sql.append("AND p.price >= ? ");
                params.add(priceFrom);
            }

            if (priceTo != null) {
                sql.append("AND p.price <= ? ");
                params.add(priceTo);
            }

            // Lọc theo đánh giá
            if (rating != null) {
                // Trong trường hợp thực tế, cần có bảng ratings hoặc trường rating trong bảng product
                sql.append("AND p.rating >= ? ");
                params.add(rating);
            }

            // Lọc theo services (ví dụ: freeship, sale,...)
            if (services != null && services.length > 0) {
                for (String service : services) {
                    if ("freeship".equals(service)) {
                        // Thêm điều kiện freeship (giả sử có trường freeShipping)
                        sql.append("AND p.freeShipping = 1 ");
                    } else if ("sale".equals(service)) {
                        // Thêm điều kiện sale (có discount > 0)
                        sql.append("AND p.discount > 0 ");
                    }
                    // Thêm các service khác nếu cần
                }
            }

            // Sắp xếp
            sql.append("ORDER BY ");
            if ("popular".equals(sortBy)) {
                sql.append("p.totalBuy DESC ");
            } else if ("price-asc".equals(sortBy)) {
                sql.append("p.price ASC ");
            } else if ("price-desc".equals(sortBy)) {
                sql.append("p.price DESC ");
            } else if ("newest".equals(sortBy)) {
                sql.append("p.createdAt DESC ");
            } else {
                sql.append("p.totalBuy DESC "); // Mặc định
            }

            // Phân trang
            sql.append("LIMIT ? OFFSET ?");
            params.add(limit);
            params.add(offset);

            // Chuẩn bị statement
            stmt = conn.prepareStatement(sql.toString());

            // Đặt các tham số
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            // Thực thi truy vấn
            rs = stmt.executeQuery();

            // Xử lý kết quả
            while (rs.next()) {
                Product product = mapResultSetToEntity(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt, rs);
        }

        return products;
    }

    /**
     * Đếm tổng số sản phẩm thỏa mãn điều kiện lọc
     */
    public int countFilteredProducts(
            String searchTerm, String[] categories, String[] publishers,
            Float priceFrom, Float priceTo, Integer rating, String[] services) {

        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Xây dựng câu truy vấn SQL với các bộ lọc
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(DISTINCT p.id) AS total FROM product p ");

            // Tương tự như getFilteredProducts() nhưng không có ORDER BY và LIMIT
            if (categories != null && categories.length > 0) {
                sql.append("JOIN product_category pc ON p.id = pc.productId ");
                sql.append("JOIN category c ON pc.categoryId = c.id ");
            }

            // Điều kiện WHERE
            sql.append("WHERE 1=1 ");

            // Thêm các điều kiện lọc
            List<Object> params = new ArrayList<>();

            // Tìm kiếm
            if (searchTerm != null && !searchTerm.isEmpty()) {
                sql.append("AND (p.name LIKE ? OR p.author LIKE ? OR p.description LIKE ?) ");
                String searchPattern = "%" + searchTerm + "%";
                params.add(searchPattern);
                params.add(searchPattern);
                params.add(searchPattern);
            }

            // Lọc theo category
            if (categories != null && categories.length > 0) {
                sql.append("AND (");
                for (int i = 0; i < categories.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("c.name = ?");
                    params.add(categories[i]);
                }
                sql.append(") ");
            }

            // Lọc theo nhà xuất bản
            if (publishers != null && publishers.length > 0) {
                sql.append("AND (");
                for (int i = 0; i < publishers.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("p.publisher = ?");
                    params.add(publishers[i]);
                }
                sql.append(") ");
            }

            // Lọc theo khoảng giá
            if (priceFrom != null) {
                sql.append("AND p.price >= ? ");
                params.add(priceFrom);
            }

            if (priceTo != null) {
                sql.append("AND p.price <= ? ");
                params.add(priceTo);
            }

            // Lọc theo đánh giá
            if (rating != null) {
                // Trong trường hợp thực tế, cần có bảng ratings hoặc trường rating trong bảng product
                sql.append("AND p.rating >= ? ");
                params.add(rating);
            }

            // Lọc theo services (ví dụ: freeship, sale,...)
            if (services != null) {
                for (String service : services) {
                    if ("freeship".equals(service)) {
                        // Thêm điều kiện freeship (giả sử có trường freeShipping)
                        sql.append("AND p.freeShipping = 1 ");
                    } else if ("sale".equals(service)) {
                        // Thêm điều kiện sale (có discount > 0)
                        sql.append("AND p.discount > 0 ");
                    }
                    // Thêm các service khác nếu cần
                }
            }

            // Chuẩn bị statement
            stmt = conn.prepareStatement(sql.toString());

            // Đặt các tham số
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            // Thực thi truy vấn
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, stmt, rs);
        }

        return count;
    }

    public ProductDetailDto getProductDetail(Long productId) {
        ProductDetailDto productDetail = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Thiết lập kết nối
            conn = getConnection();

            // SQL query
            String sql = "SELECT p.*, c.id AS categoryId, c.name AS categoryBreadcrumb, \n" +
                    "(SELECT COUNT(id) FROM bookshopdb.product_review WHERE productId = p.id AND isShow = 1) AS totalProductReviews,\n" +
                    "(SELECT CASE  \n" +
                    "    WHEN COUNT(id) = 0 THEN 0 \n" +
                    "    ELSE SUM(ratingScore) / COUNT(id)\n" +
                    "END FROM bookshopdb.product_review WHERE productId = p.id AND isShow = 1) AS averageRatingScore\n" +
                    "FROM bookshopdb.product p \n" +
                    "LEFT JOIN bookshopdb.category c ON c.id = (SELECT categoryId FROM bookshopdb.product_category WHERE productId = p.id LIMIT 1)\n" +
                    "WHERE p.id = ?";

            // Chuẩn bị statement
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, productId);

            // Thực thi query
            rs = stmt.executeQuery();

            // Xử lý kết quả
            if (rs.next()) {
                productDetail = new ProductDetailDto();

                // Map dữ liệu từ ResultSet vào DTO
                productDetail.setId(rs.getLong("id"));
                productDetail.setName(rs.getString("name"));
                productDetail.setPrice(rs.getFloat("price"));
                productDetail.setDiscount(rs.getFloat("discount"));
                productDetail.setQuantity(rs.getShort("quantity"));
                productDetail.setTotalBuy(rs.getShort("totalBuy"));
                productDetail.setAuthor(rs.getString("author"));
                productDetail.setPages(rs.getShort("pages"));
                productDetail.setPublisher(rs.getString("publisher"));
                productDetail.setYearPublishing(rs.getInt("yearPublishing"));
                productDetail.setDescription(rs.getString("description"));
                productDetail.setImageName(rs.getString("imageName"));
                productDetail.setShop(rs.getBoolean("shop"));
                productDetail.setCreatedAt(rs.getTimestamp("createdAt"));

                // Xử lý các trường có thể null
                Timestamp updatedAt = rs.getTimestamp("updatedAt");
                if (!rs.wasNull()) {
                    productDetail.setUpdatedAt(updatedAt);
                }

                Timestamp startsAt = rs.getTimestamp("startsAt");
                if (!rs.wasNull()) {
                    productDetail.setStartsAt(startsAt);
                }

                Timestamp endsAt = rs.getTimestamp("endsAt");
                if (!rs.wasNull()) {
                    productDetail.setEndsAt(endsAt);
                }

                // Lấy thông tin đánh giá
                productDetail.setTotalProductReviews(rs.getInt("totalProductReviews"));
                productDetail.setAverageRatingScore(rs.getDouble("averageRatingScore"));
                productDetail.setCategoryId(rs.getLong("categoryId"));
                productDetail.setCategoryBreadcrumb(rs.getString("categoryBreadcrumb"));
            }

        } catch (SQLException e) {
            // Xử lý exception
            e.printStackTrace();
            throw new RuntimeException("Error getting product detail: " + e.getMessage(), e);
        } finally {
            // Đóng tài nguyên
            close(conn, stmt, rs);
        }

        return productDetail;
    }

    public void updateProductQuantity(Long productId, int newQuantity) {
        clearSQL();
        builderSQL.append("UPDATE product SET quantity = ? WHERE id = ?");
        update(builderSQL.toString(), newQuantity, productId);
    }
}
