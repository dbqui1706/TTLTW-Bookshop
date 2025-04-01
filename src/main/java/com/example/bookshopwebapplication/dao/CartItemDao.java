package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.ICartItemDao;
import com.example.bookshopwebapplication.dao.mapper.CartItemMapper;
import com.example.bookshopwebapplication.entities.Cart;
import com.example.bookshopwebapplication.entities.CartItem;
import com.example.bookshopwebapplication.http.request.cart.SaveCartRequest;
import com.example.bookshopwebapplication.http.response.cart.CartResponse;
import com.example.bookshopwebapplication.http.response.product.CartProductResponse;
import com.sun.source.tree.TryTree;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CartItemDao extends AbstractDao<CartItem> implements ICartItemDao {

    public CartItemDao() {
        super("cart_item");
    }

    //Lưu một CartItem mới vào cơ sở dữ liệu.
    public Long save(CartItem cartItem) {
        clearSQL();
        builderSQL.append(
                "INSERT INTO cart_item (cartId, productId, quantity, createdAt) " +
                        "VALUES(?, ?, ?, ?)"
        );
        return insert(builderSQL.toString(), cartItem.getCartId(), cartItem.getProductId(),
                cartItem.getQuantity(), new Timestamp(System.currentTimeMillis())
        );
    }

    // Cập nhật thông tin của một CartItem trong cơ sở dữ liệu.
    public void update(CartItem cartItem) {
        clearSQL();
        // Cập nhật thông tin của một CartItem trong cơ sở dữ liệu.
        // Đảm bảo số lượng người dùng mua không vượt quá số lượng sản phẩm còn lại.
        builderSQL.append(
                "UPDATE cart_item SET cartId = ?, productId = ?, quantity = ?, " +
                        "createdAt = ?, updatedAt = ? WHERE id = ?"
        );
        update(builderSQL.toString(), cartItem.getCartId(), cartItem.getProductId(),
                cartItem.getQuantity(), cartItem.getCreatedAt(), new Timestamp(System.currentTimeMillis()),
                cartItem.getId());
    }

    // Xóa một CartItem khỏi cơ sở dữ liệu dựa trên id.
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM cart_item WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    //Lấy một CartItem từ cơ sở dữ liệu dựa trên id.
    public Optional<CartItem> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM cart_item WHERE id = ?");
        return Optional.ofNullable(query(builderSQL.toString(), new CartItemMapper(), id).get(0));
    }

    //Lấy danh sách CartItem từ cơ sở dữ liệu với giới hạn số lượng và vị trí bắt đầu.
    public List<CartItem> getPart(Integer limit, Integer offset) {
        clearSQL();
        builderSQL.append("SELECT * FROM cart LIMIT " + offset + ", " + limit);
        List<CartItem> cartItems = query(builderSQL.toString(), new CartItemMapper());
        return cartItems.isEmpty() ? new LinkedList<>() : cartItems;
    }

    //Lấy danh sách CartItem từ cơ sở dữ liệu được sắp xếp theo một trường và thứ tự cụ thể.
    public List<CartItem> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        clearSQL();
        builderSQL.append("SELECT * FROM cart ORDER BY " + orderBy + " " + sort);
        builderSQL.append(" LIMIT " + offset + ", " + limit + "");
        List<CartItem> cartItems = query(builderSQL.toString(), new CartItemMapper());
        return cartItems.isEmpty() ? new LinkedList<>() : cartItems;
    }

    //Đếm số lượng CartItem trong cơ sở dữ liệu.
    public int count() {
        clearSQL();
        builderSQL.append(
                "SELECT COUNT(*) FROM cart_item"
        );
        return count(builderSQL.toString());
    }

    //Lấy danh sách CartItem dựa trên cartId.
    @Override
    public List<CartItem> getByCartId(long cartId) {
        clearSQL();
        builderSQL.append(
                "SELECT ci.*, p.name product_name, p.price product_price, p.discount product_discount, " +
                        "p.quantity product_quantity, p.imageName product_imageName " +
                        "FROM cart_item ci " +
                        "JOIN product p on p.id = ci.productId " +
                        "WHERE cartId = ? " +
                        "ORDER BY createdAt DESC"
        );
        List<CartItem> cartItems = query(builderSQL.toString(), new CartItemMapper(), cartId);
        return cartItems.isEmpty() ? new LinkedList<>() : cartItems;
    }

    //Lấy CartItem dựa trên cartId và productId.
    @Override
    public Optional<CartItem> getByCartIdAndProductId(long cartId, long productId) {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM cart_item WHERE cartId = ? AND productId = ?"
        );
        List<CartItem> cartItems = query(builderSQL.toString(), new CartItemMapper(), cartId, productId);
        return cartItems.isEmpty() ? Optional.empty() : Optional.ofNullable(cartItems.get(0));
    }

    // Tính tổng số lượng CartItem bằng cách lấy tổng của quantity dựa trên userId
    @Override
    public int sumQuantityByUserId(long userId) {
        clearSQL();
        builderSQL.append(
                "SELECT SUM(ci.quantity) " +
                        "FROM cart_item ci " +
                        "JOIN cart c on c.id = ci.cartId " +
                        "WHERE c.userId = ?;"
        );
        return count(builderSQL.toString(), userId);
    }

    public void deleteCartItemByCartIdAndProductId(long cartId, long productId) {
        clearSQL();
        builderSQL.append("DELETE FROM cart_item WHERE cartId = ? and productId = ?");
        update(builderSQL.toString(), cartId, productId);
    }

    @Override
    public CartItem mapResultSetToEntity(ResultSet resultSet) {
        try {
            CartItem cartItem = new CartItem();
            cartItem.setId(resultSet.getLong("id"));
            cartItem.setCartId(resultSet.getLong("cartId"));
            cartItem.setProductId(resultSet.getLong("productId"));
            cartItem.setQuantity(resultSet.getInt("quantity"));
            cartItem.setCreatedAt(resultSet.getTimestamp("createdAt"));
            if (resultSet.getTimestamp("createdAt") != null) {
                cartItem.setUpdatedAt(resultSet.getTimestamp("updatedAt"));
            }
            return cartItem;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bulkInsert(Long cartId, List<SaveCartRequest.CartItem> cartItems) {
        Connection conn = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Chuẩn bị câu lệnh kiểm tra sản phẩm đã tồn tại trong giỏ hàng chưa
            String checkSql = "SELECT cartId, productId, quantity FROM cart_item WHERE cartId = ? AND productId = ?";
            checkStmt = conn.prepareStatement(checkSql);

            // Chuẩn bị câu lệnh cập nhật số lượng nếu sản phẩm đã tồn tại
            String updateSql = "UPDATE cart_item SET quantity = quantity + ? WHERE cartId = ? AND productId = ?";
            updateStmt = conn.prepareStatement(updateSql);

            // Danh sách các sản phẩm cần thêm mới (không tồn tại trong giỏ hàng)
            List<SaveCartRequest.CartItem> itemsToInsert = new ArrayList<>();

            // Kiểm tra và cập nhật các sản phẩm đã tồn tại
            for (SaveCartRequest.CartItem item : cartItems) {
                checkStmt.setLong(1, cartId);
                checkStmt.setLong(2, item.getProductId());
                rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Sản phẩm đã tồn tại, cập nhật số lượng
                    updateStmt.setInt(1, item.getQuantity());
                    updateStmt.setLong(2, cartId);
                    updateStmt.setLong(3, item.getProductId());
                    updateStmt.executeUpdate();
                } else {
                    // Sản phẩm chưa tồn tại, thêm vào danh sách cần insert
                    itemsToInsert.add(item);
                }

                rs.close();
            }

            // Nếu có sản phẩm cần thêm mới
            if (!itemsToInsert.isEmpty()) {
                StringBuilder insertSql = new StringBuilder();
                insertSql.append("INSERT INTO cart_item (cartId, productId, quantity) VALUES ");
                for (int i = 0; i < itemsToInsert.size(); i++) {
                    insertSql.append("(?, ?, ?)");
                    if (i < itemsToInsert.size() - 1) {
                        insertSql.append(", ");
                    }
                }

                insertStmt = conn.prepareStatement(insertSql.toString());
                int index = 1;
                for (SaveCartRequest.CartItem product : itemsToInsert) {
                    insertStmt.setLong(index++, cartId);
                    insertStmt.setLong(index++, product.getProductId());
                    insertStmt.setInt(index++, product.getQuantity());
                }
                insertStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            close(conn, insertStmt, null);
            close(null, updateStmt, null);
            close(null, checkStmt, rs);
        }

    }

    public List<CartProductResponse> getByCartIdAndUserId(long cartId, long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT \n" +
                    "    ci.id AS cartItemId,\n" +
                    "    p.id AS productId,\n" +
                    "    p.name AS productName,\n" +
                    "    p.imageName AS productImage,\n" +
                    "    p.price AS productPrice,\n" +
                    "    p.discount AS productDiscount,\n" +
                    "    ci.quantity AS quantity \n" +
                    "FROM bookshopdb.cart c\n" +
                    "JOIN \n" +
                    "    bookshopdb.cart_item ci ON ci.cartId = ?\n" +
                    "JOIN \n" +
                    "    bookshopdb.product p ON ci.productId = p.id\n" +
                    "WHERE \n" +
                    "    c.userId = ?;";
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, cartId);
            stmt.setLong(2, userId);
            rs = stmt.executeQuery();
            List<CartProductResponse> cartResponses = new LinkedList<>();
            while (rs.next()) {
                CartProductResponse cartResponse = new CartProductResponse();
                cartResponse.setCartItemId(rs.getLong("cartItemId"));
                cartResponse.setProductId(rs.getLong("productId"));
                cartResponse.setProductName(rs.getString("productName"));
                cartResponse.setProductImage(rs.getString("productImage"));
                cartResponse.setProductPrice(rs.getDouble("productPrice"));
                cartResponse.setProductDiscount(rs.getDouble("productDiscount"));
                cartResponse.setQuantity(rs.getInt("quantity"));
                cartResponses.add(cartResponse);
            }
            return cartResponses;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        } finally {
            close(conn, stmt, rs);
        }
    }

    public boolean updateQuantity(long cartItemId, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE cart_item SET quantity = ? WHERE id = ?";
            update(sql, quantity, cartItemId);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }
}