package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.ICategoryDao;
import com.example.bookshopwebapplication.dao.mapper.CategoryMapper;
import com.example.bookshopwebapplication.entities.Category;
import com.example.bookshopwebapplication.entities.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CategoryDao extends AbstractDao<Category> implements ICategoryDao {

    public CategoryDao() {
        super("category");
    }

    // Lưu một Category mới vào cơ sở dữ liệu.
    public Long save(Category category) {
        clearSQL();
        builderSQL.append("INSERT INTO category (name, description, imageName) ");
        builderSQL.append("VALUES (?, ?, ?)");
        return insert(builderSQL.toString(), category.getName(), category.getDescription(),
                category.getImageName());
    }

    //Cập nhật thông tin của một Category trong cơ sở dữ liệu.
    public void update(Category category) {
        clearSQL();
        builderSQL.append("UPDATE category SET name = ?, description = ?, imageName = ? ");
        builderSQL.append("WHERE id = ?");
        update(builderSQL.toString(), category.getName(), category.getDescription(), category.getImageName(),
                category.getId());
    }

    // Xóa một Category khỏi cơ sở dữ liệu dựa trên id.
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM category WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    // Lấy danh sách tất cả các Category từ cơ sở dữ liệu.
    @Override
    public List<Category> getAll() {
        clearSQL();
        builderSQL.append("SELECT * FROM category");
        return super.query(builderSQL.toString(), new CategoryMapper());
    }

    // Lấy Category dựa trên id của Product.
    @Override
    public Optional<Category> getByProductId(long id) {
        clearSQL();
        builderSQL.append(
                "SELECT c.* " +
                        "FROM product_category pc " +
                        "JOIN category c ON pc.categoryId = c.id " +
                        "WHERE productId = ?"
        );
        List<Category> categories = query(builderSQL.toString(), new CategoryMapper(), id);
        return categories.isEmpty() ? null : Optional.ofNullable(categories.get(0));
    }

    // danh sách các Category với giới hạn số lượng và vị trí bắt đầu.
    public List<Category> getPart(Integer limit, Integer offset) {
        clearSQL();
        builderSQL.append("SELECT * FROM category LIMIT " + offset + ", " + limit);
        return query(builderSQL.toString(), new CategoryMapper());
    }

    //Lấy danh sách các Category được sắp xếp theo một trường và thứ tự cụ thể.
    public List<Category> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        clearSQL();
        builderSQL.append("SELECT * FROM category ORDER BY " + orderBy + " " + sort);
        builderSQL.append(" LIMIT " + offset + ", " + limit + "");
        return query(builderSQL.toString(), new CategoryMapper());
    }

    public int count() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM category"
        );
        return count(builderSQL.toString());
    }

    //Lấy Category dựa trên id.
    public Optional<Category> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM category WHERE id = ?");
        List<Category> list = query(builderSQL.toString(), new CategoryMapper(), id);
        return list.isEmpty() ? null : Optional.ofNullable(list.get(0));
    }

    @Override
    public Category mapResultSetToEntity(ResultSet resultSet) {
        try {
            Category category = new Category();
            category.setId(resultSet.getLong("id"));
            category.setName(resultSet.getString("name"));
            category.setDescription(resultSet.getString("description"));
            category.setImageName(resultSet.getString("imageName"));
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Long, Integer> getProductsCountByCategory() {
        clearSQL();
        builderSQL.append(
                "SELECT pc.categoryId, COUNT(pc.productId) as productCount " +
                        "FROM bookshopdb.product_category pc " +
                        "GROUP BY pc.categoryId"
        );
        Map<Long, Integer> productCountMap = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(builderSQL.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long categoryId = rs.getLong("categoryId");
                Integer productCount = rs.getInt("productCount");
                productCountMap.put(categoryId, productCount);
            }
            return productCountMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // Đảm bảo đóng các tài nguyên liên quan đến cơ sở dữ liệu
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
    }

    /**
     * Lấy tất cả danh mục và số lượng sản phẩm trong mỗi danh mục
     * @return Map với key là ID của danh mục, value là một Map chứa thông tin danh mục và số lượng sản phẩm
     */
    public List<Object> getCategoriesAndCountProduct() {
        List<Object> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection(); // Phương thức để lấy kết nối đến database

            String query = "SELECT c.id, c.name, c.description, c.imageName, " +
                    "COUNT(DISTINCT pc.productId) AS productCount " +
                    "FROM category c " +
                    "LEFT JOIN product_category pc ON c.id = pc.categoryId " +
                    "LEFT JOIN product p ON pc.productId = p.id " +
                    "GROUP BY c.id, c.name, c.description, c.imageName " +
                    "ORDER BY c.name";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            while (rs.next()) {
                long categoryId = rs.getLong("id");
                Map<String, Object> categoryInfo = new HashMap<>();

                categoryInfo.put("id", categoryId);
                categoryInfo.put("name", rs.getString("name"));
                categoryInfo.put("description", rs.getString("description"));
                categoryInfo.put("imageName", rs.getString("imageName"));
                categoryInfo.put("productCount", rs.getInt("productCount"));

                result.add(categoryInfo);
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


}