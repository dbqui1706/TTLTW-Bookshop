package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IProductReviewDao;
import com.example.bookshopwebapplication.dao.mapper.ProductReviewMapper;
import com.example.bookshopwebapplication.entities.ProductReview;
import com.example.bookshopwebapplication.http.response.reviews.RatingsSummary;
import com.example.bookshopwebapplication.http.response.reviews.ReviewDTO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProductReviewDao extends AbstractDao<ProductReview> implements IProductReviewDao {

    public ProductReviewDao() {
        super("product_review");
    }

    // Phương thức để lưu đánh giá sản phẩm mới vào cơ sở dữ liệu
    public Long save(ProductReview pr) {
        // Thiết lập câu truy vấn mới
        clearSQL();
        builderSQL.append(
                "INSERT INTO product_review (userId, productId, ratingScore, content, isShow" +
                        " createdAt, updatedAt) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)"
        );
        return insert(builderSQL.toString(), pr.getUserId(), pr.getProductId(), pr.getRatingScore(),
                pr.getContent(), pr.getIsShow(), pr.getCreatedAt(), new Timestamp(System.currentTimeMillis()));
    }

    // Phương thức để cập nhật thông tin đánh giá sản phẩm trong cơ sở dữ liệu
    public void update(ProductReview pr) {
        clearSQL();
        builderSQL.append(
                "UPDATE product_review SET ratingScore = ?, content = ?, " +
                        "updatedAt = " + new Timestamp(System.currentTimeMillis()) + " "
                        + "WHERE id = ?"
        );
        update(builderSQL.toString(), pr.getRatingScore(), pr.getContent(), pr.getUpdatedAt(), pr.getId());
    }

    // Phương thức để xóa đánh giá sản phẩm từ cơ sở dữ liệu
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM product_review WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    // Phương thức để lấy đánh giá sản phẩm dựa trên ID
    public Optional<ProductReview> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM product_review WHERE id = ?");
        List<ProductReview> list = query(builderSQL.toString(), new ProductReviewMapper(), id);
        return list.isEmpty() ? null : Optional.ofNullable(list.get(0));
    }

    // Phương thức để lấy một phần danh sách đánh giá sản phẩm từ cơ sở dữ liệu
    public List<ProductReview> getPart(Integer limit, Integer offset) {
        clearSQL();
        builderSQL.append("SELECT * FROM product_review LIMIT " + offset + ", " + limit);
        return super.getPart(builderSQL.toString(), new ProductReviewMapper());
    }

    // Phương thức để lấy một phần danh sách đánh giá sản phẩm từ cơ sở dữ liệu với sắp xếp
    public List<ProductReview> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        clearSQL();
        builderSQL.append("SELECT * FROM product_review ORDER BY " + orderBy + " " + sort);
        builderSQL.append(" LIMIT " + offset + ", " + limit + "");
        return super.getOrderedPart(builderSQL.toString(), new ProductReviewMapper());
    }

    // Phương thức để đếm số lượng đánh giá sản phẩm trong cơ sở dữ liệu
    public int count() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM product_review"
        );
        return count(builderSQL.toString());
    }

    // Phương thức để lấy một phần danh sách đánh giá sản phẩm từ cơ sở dữ liệu với sắp xếp theo sản phẩm và giới hạn số lượng
    @Override
    public List<ProductReview> getOrderedPartByProductId(int limit, int offset, String orderBy, String sort, long productId) {
        clearSQL();
        builderSQL.append(
                "SELECT pr.*, u.fullname " +
                        "FROM product_review pr " +
                        "JOIN user u ON pr.userId = u.id " +
                        "WHERE productId = ? AND pr.isShow = 1 " +
                        "ORDER BY " + orderBy + " " + sort +
                        " LIMIT " + offset + ", " + limit
        );
        List<ProductReview> productReviews = query(builderSQL.toString(), new ProductReviewMapper(), productId);
        return productReviews.isEmpty() ? new LinkedList<>() : productReviews;
    }

    // Phương thức để đếm số lượng đánh giá sản phẩm dựa trên ID sản phẩm
    @Override
    public int countByProductId(long productId) {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(id) FROM product_review WHERE productId = ?"
        );
        return count(builderSQL.toString(), productId);
    }

    // Phương thức để tính tổng điểm đánh giá sản phẩm dựa trên ID sản phẩm
    @Override
    public int sumRatingScoresByProductId(long productId) {
        clearSQL();
        builderSQL.append(
                "SELECT SUM(ratingScore) FROM product_review WHERE productId = ?"
        );
        return count(builderSQL.toString(), productId);
    }

    // Phương thức để ẩn một đánh giá sản phẩm dựa trên ID
    @Override
    public void hide(long id) {
        clearSQL();
        builderSQL.append(
                "UPDATE product_review SET isShow = 0," +
                        " updatedAt = NOW()" +
                        " WHERE id = ?"
        );
        update(builderSQL.toString(), id);
    }

    // Phương thức để hiển thị một đánh giá sản phẩm dựa trên ID
    @Override
    public void show(long id) {
        clearSQL();
        builderSQL.append(
                "UPDATE product_review SET isShow = 1," +
                        " updatedAt = NOW() " +
                        " WHERE id = ?"
        );
        update(builderSQL.toString(), id);
    }

    public RatingsSummary getProductRatings(Long productId) {
        RatingsSummary summary = new RatingsSummary();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT " +
                    "AVG(pr.ratingScore) AS average_rating, " +
                    "COUNT(pr.id) AS total_reviews, " +
                    "SUM(CASE WHEN pr.ratingScore = 5 THEN 1 ELSE 0 END) AS five_star, " +
                    "SUM(CASE WHEN pr.ratingScore = 4 THEN 1 ELSE 0 END) AS four_star, " +
                    "SUM(CASE WHEN pr.ratingScore = 3 THEN 1 ELSE 0 END) AS three_star, " +
                    "SUM(CASE WHEN pr.ratingScore = 2 THEN 1 ELSE 0 END) AS two_star, " +
                    "SUM(CASE WHEN pr.ratingScore = 1 THEN 1 ELSE 0 END) AS one_star " +
                    "FROM bookshopdb.product_review pr " +
                    "WHERE pr.productId = ? AND pr.isShow = 1";

            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, productId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                summary.setAverageRating(rs.getDouble("average_rating"));
                summary.setTotalReviews(rs.getInt("total_reviews"));

                Map<Integer, Integer> distribution = new HashMap<>();
                distribution.put(5, rs.getInt("five_star"));
                distribution.put(4, rs.getInt("four_star"));
                distribution.put(3, rs.getInt("three_star"));
                distribution.put(2, rs.getInt("two_star"));
                distribution.put(1, rs.getInt("one_star"));
                summary.setDistribution(distribution);

                // Tính phần trăm cho mỗi mức sao
                Map<Integer, Integer> percentages = new HashMap<>();
                int totalReviews = summary.getTotalReviews();

                if (totalReviews > 0) {
                    for (Map.Entry<Integer, Integer> entry : distribution.entrySet()) {
                        int percent = (entry.getValue() * 100) / totalReviews;
                        percentages.put(entry.getKey(), percent);
                    }
                }
                summary.setPercentages(percentages);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            close(conn, stmt, rs);
        }

        return summary;
    }

    @Override
    public ProductReview mapResultSetToEntity(ResultSet resultSet) {
        try {
            ProductReview productReview = new ProductReview();
            productReview.setId(resultSet.getLong("id"));
            productReview.setUserId(resultSet.getLong("userId"));
            productReview.setProductId(resultSet.getLong("productId"));
            productReview.setRatingScore(resultSet.getInt("ratingScore"));
            productReview.setContent(resultSet.getString("content"));
            productReview.setIsShow(resultSet.getInt("isShow"));
            productReview.setCreatedAt(resultSet.getTimestamp("createdAt"));
            productReview.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
            return productReview;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ReviewDTO> getProductReviews(Long productId, String filter, int page, int limit) {
        List<ReviewDTO> reviews = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sqlBuilder = new StringBuilder(
                    "SELECT " +
                            "pr.id AS review_id, " +
                            "pr.ratingScore, " +
                            "pr.content, " +
                            "pr.createdAt, " +
                            "u.id AS user_id, " +
                            "u.fullname AS user_name " +
                            "FROM bookshopdb.product_review pr " +
                            "JOIN bookshopdb.user u ON pr.userId = u.id " +
                            "WHERE pr.productId = ? AND pr.isShow = 1 "
            );

            // Phần lọc theo số sao
            int ratingFilter = 0;
            if (filter != null && !filter.equals("newest")) {
                try {
                    ratingFilter = Integer.parseInt(filter);
                    if (ratingFilter >= 1 && ratingFilter <= 5) {
                        sqlBuilder.append("AND pr.ratingScore = ? ");
                    } else {
                        ratingFilter = 0;
                    }
                } catch (NumberFormatException e) {
                    // Không phải số, nên không lọc theo rating
                    ratingFilter = 0;
                }
            }

            // Sắp xếp
            sqlBuilder.append("ORDER BY pr.createdAt DESC ");

            // Phân trang
            sqlBuilder.append("LIMIT ?, ?");

            stmt = conn.prepareStatement(sqlBuilder.toString());

            int paramIndex = 1;
            stmt.setLong(paramIndex++, productId);

            if (ratingFilter > 0) {
                stmt.setInt(paramIndex++, ratingFilter);
            }

            stmt.setInt(paramIndex++, (page - 1) * limit);
            stmt.setInt(paramIndex++, limit);

            rs = stmt.executeQuery();

            while (rs.next()) {
                ReviewDTO review = new ReviewDTO();
                review.setId(rs.getLong("review_id"));
                review.setRating(rs.getInt("ratingScore"));
                review.setContent(rs.getString("content"));

                // Định dạng ngày tháng
                Timestamp createdAt = rs.getTimestamp("createdAt");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                review.setReviewDate(sdf.format(createdAt));

                // Thông tin người dùng
                review.setUserId(rs.getLong("user_id"));
                review.setUserName(rs.getString("user_name"));

                // Thêm nhãn dựa trên số sao
                review.setRatingLabel(getRatingLabel(review.getRating()));

                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng tài nguyên
            close(conn, stmt, rs);
        }

        return reviews;
    }

    // Phương thức lấy chữ cái đầu của tên
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        StringBuilder initials = new StringBuilder();
        String[] parts = name.split("\\s+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    // Phương thức lấy nhãn đánh giá dựa vào số sao
    private String getRatingLabel(int rating) {
        switch (rating) {
            case 5:
                return "Cực kì hài lòng";
            case 4:
                return "Hài lòng";
            case 3:
                return "Bình thường";
            case 2:
                return "Không hài lòng";
            case 1:
                return "Rất không hài lòng";
            default:
                return "";
        }
    }
}
