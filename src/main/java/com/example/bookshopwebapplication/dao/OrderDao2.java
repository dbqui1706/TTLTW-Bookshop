package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.Order2Mapper;
import com.example.bookshopwebapplication.entities.Order2;
import com.example.bookshopwebapplication.http.response.order.OrderDTO;
import com.example.bookshopwebapplication.http.response.order.OrderItemDTO;
import com.example.bookshopwebapplication.http.response.order.OrderPageResponse;
import com.google.gson.Gson;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.sql.*;
import java.util.*;

public class OrderDao2 extends AbstractDao<Order2> {
    public OrderDao2() {
        super("orders");
    }

    public Long save(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO orders (order_code, user_id, status, delivery_method_id, payment_method_id, ");
        builderSQL.append("subtotal, delivery_price, discount_amount, tax_amount, total_amount, coupon_code, is_verified, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), order.getOrderCode(), order.getUserId(), order.getStatus(),
                order.getDeliveryMethodId(), order.getPaymentMethodId(), order.getSubtotal(),
                order.getDeliveryPrice(), order.getDiscountAmount(), order.getTaxAmount(),
                order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0, order.getNote());
    }
    /**
     * Cập nhật toàn bộ thông tin đơn hàng
     * @param order Đơn hàng cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean update(Order2 order) {
        clearSQL();
        builderSQL.append("UPDATE orders SET ");
        builderSQL.append("status = ?, ");
        builderSQL.append("delivery_method_id = ?, ");
        builderSQL.append("payment_method_id = ?, ");
        builderSQL.append("subtotal = ?, ");
        builderSQL.append("delivery_price = ?, ");
        builderSQL.append("discount_amount = ?, ");
        builderSQL.append("tax_amount = ?, ");
        builderSQL.append("total_amount = ?, ");
        builderSQL.append("coupon_code = ?, ");
        builderSQL.append("is_verified = ?, ");
        builderSQL.append("note = ?, ");
        builderSQL.append("updated_at = ? ");
        builderSQL.append("WHERE id = ?");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(),
                order.getStatus(),
                order.getDeliveryMethodId(),
                order.getPaymentMethodId(),
                order.getSubtotal(),
                order.getDeliveryPrice(),
                order.getDiscountAmount(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getIsVerified() ? 1 : 0,
                order.getNote(),
                now,
                order.getId());
        return true;
    }

    public Optional<Order2> findById(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE id = ?");
        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper(), orderId);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }
    /**
     * Cập nhật trạng thái đơn hàng
     * @param orderId ID đơn hàng
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(Long orderId, String status) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ?, updated_at = ? WHERE id = ?");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(), status, now, orderId);
        return true;
    }

    /**
     * Tìm tất cả đơn hàng theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách đơn hàng
     */
    public List<Order2> findByStatus(String status) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC");
        return query(builderSQL.toString(), new Order2Mapper(), status);
    }

    /**
     * Tìm đơn hàng theo mã đơn hàng
     * @param orderCode Mã đơn hàng
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    public Optional<Order2> findByOrderCode(String orderCode) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE order_code = ?");
        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper(), orderCode);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    /**
     * Tìm đơn hàng theo người dùng
     * @param userId ID người dùng
     * @return Danh sách đơn hàng
     */
    public List<Order2> findByUserId(Long userId) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC");
        return query(builderSQL.toString(), new Order2Mapper(), userId);
    }

    /**
     * Đếm số đơn hàng theo trạng thái
     * @param status Trạng thái cần đếm
     * @return Số lượng đơn hàng
     */
    public int countByStatus(String status) {
        clearSQL();
        builderSQL.append("SELECT COUNT(*) FROM orders WHERE status = ?");
        return count(builderSQL.toString(), status);
    }

    /**
     * Tìm các đơn hàng đang chờ xử lý và có thể được xử lý (còn đủ hàng trong kho)
     * @return Danh sách ID đơn hàng
     */
    public List<Long> findPendingOrderIds() {
        clearSQL();
        builderSQL.append("SELECT id FROM orders WHERE status = 'pending' ORDER BY created_at ASC");

        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper());
        return orders.stream().map(Order2::getId).toList();
    }

    /**
     * Tìm các đơn hàng đã xác nhận và sẵn sàng giao hàng
     * @return Danh sách ID đơn hàng
     */
    public List<Long> findConfirmedOrderIds() {
        clearSQL();
        builderSQL.append("SELECT id FROM orders WHERE status = 'confirmed' ORDER BY created_at ASC");

        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper());
        return orders.stream().map(Order2::getId).toList();
    }

    /**
     * Cập nhật xác thực đơn hàng
     * @param orderId ID đơn hàng
     * @param isVerified Trạng thái xác thực
     * @return true nếu cập nhật thành công
     */
    public boolean updateVerificationStatus(Long orderId, boolean isVerified) {
        clearSQL();
        builderSQL.append("UPDATE orders SET is_verified = ?, updated_at = ? WHERE id = ?");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(), isVerified ? 1 : 0, now, orderId);
        return true;
    }

    // Thêm vào OrderDao2
    public Long saveWithConnection(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO orders (order_code, user_id, status, delivery_method_id, payment_method_id, ");
        builderSQL.append("subtotal, delivery_price, discount_amount, tax_amount, total_amount, coupon_code, is_verified, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insertWithConnection(conn, builderSQL.toString(), order.getOrderCode(), order.getUserId(), order.getStatus(),
                order.getDeliveryMethodId(), order.getPaymentMethodId(), order.getSubtotal(),
                order.getDeliveryPrice(), order.getDiscountAmount(), order.getTaxAmount(),
                order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0, order.getNote());
    }


    @Override
    public Order2 mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            Order2 order = Order2.builder()
                    .id(rs.getLong("id"))
                    .orderCode(rs.getString("order_code"))
                    .userId(rs.getLong("user_id"))
                    .status(rs.getString("status"))
                    .deliveryMethodId(rs.getLong("delivery_method_id"))
                    .paymentMethodId(rs.getLong("payment_method_id"))
                    .subtotal(rs.getDouble("subtotal"))
                    .deliveryPrice(rs.getDouble("delivery_price"))
                    .discountAmount(rs.getDouble("discount_amount"))
                    .taxAmount(rs.getDouble("tax_amount"))
                    .totalAmount(rs.getDouble("total_amount"))
                    .couponCode(rs.getString("coupon_code"))
                    .isVerified(rs.getBoolean("is_verified"))
                    .note(rs.getString("note"))
                    .createdAt(rs.getTimestamp("created_at"))
                    .updatedAt(rs.getTimestamp("updated_at") != null
                            ? rs.getTimestamp("updated_at") : null)
                    .build();
            return order;
        } catch (Exception e) {
            System.out.println("Error mapping ResultSet to Order2 entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateWithConnection(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ?, delivery_method_id = ?, payment_method_id = ?, ");
        builderSQL.append("subtotal = ?, delivery_price = ?, discount_amount = ?, tax_amount = ?, total_amount = ?, ");
        builderSQL.append("coupon_code = ?, is_verified = ?, note = ? WHERE id = ?");
        updateWithConnection(conn, builderSQL.toString(), order.getStatus(), order.getDeliveryMethodId(),
                order.getPaymentMethodId(),order.getSubtotal(), order.getDeliveryPrice(), order.getDiscountAmount(),
                order.getTaxAmount(), order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0,
                order.getNote(), order.getId());
    }
    public void updateStatusWithConnection(Long orderId, String processing, Connection conn) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ? WHERE id = ?");
        updateWithConnection(conn, builderSQL.toString(), processing, orderId);

    }
    /**
     * Lấy danh sách đơn hàng của người dùng với các điều kiện lọc và phân trang
     * @param userId ID người dùng
     * @param status Trạng thái đơn hàng (có thể null)
     * @param searchTerm Từ khóa tìm kiếm (có thể null)
     * @param sortBy Trường sắp xếp (có thể null)
     * @param page Số trang
     * @param pageSize Kích thước trang
     * @return OrderPageResponse chứa danh sách đơn hàng và thông tin phân trang
     */
    public OrderPageResponse getUserOrders(Long userId, String status,
                                           String searchTerm, String sortBy,
                                           int page, int pageSize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderDTO> orders = new ArrayList<>();

        try {
            conn = getConnection();

            // 1. Query lấy danh sách đơn hàng với phân trang
            StringBuilder mainQuery = new StringBuilder();
            mainQuery.append("SELECT ");
            // Thông tin đơn hàng
            mainQuery.append("o.id AS order_id, o.order_code, o.status, ");
            mainQuery.append("o.created_at AS order_date, ");
            mainQuery.append("o.subtotal, o.delivery_price, o.discount_amount, o.total_amount, ");

            // Thông tin sản phẩm trong đơn hàng
            mainQuery.append("GROUP_CONCAT( ");
            mainQuery.append("    JSON_OBJECT( ");
            mainQuery.append("        'id', oi.id, ");
            mainQuery.append("        'productId', oi.product_id, ");
            mainQuery.append("        'productName', oi.product_name, ");
            mainQuery.append("        'productImage', IFNULL(oi.product_image, '/asset/images/image.png'), ");
            mainQuery.append("        'productVariant', CONCAT('Phiên bản: ', IFNULL(p.publisher, 'Bìa mềm')), ");
            mainQuery.append("        'quantity', oi.quantity, ");
            mainQuery.append("        'price', oi.price, ");
            mainQuery.append("        'subtotal', oi.subtotal ");
            mainQuery.append("    ) SEPARATOR '||' ");
            mainQuery.append(") AS order_items, ");

            // Tổng số sản phẩm trong đơn hàng
            mainQuery.append("COUNT(oi.id) AS total_items, ");

            // Thông tin thanh toán
            mainQuery.append("MAX(pm.name) AS payment_method, ");

            // Thông tin giao hàng
            mainQuery.append("MAX(os.receiver_name) AS receiver_name, ");
            mainQuery.append("MAX(os.receiver_phone) AS receiver_phone, ");
            mainQuery.append("MAX(os.address_line1) AS address_line1, ");
            mainQuery.append("MAX(os.district) AS district, ");
            mainQuery.append("MAX(os.city) AS city, ");

            // Theo dõi đơn hàng
            mainQuery.append("MAX(os.tracking_number) AS tracking_number, ");
            mainQuery.append("MAX(os.shipping_carrier) AS shipping_carrier, ");

            // Thông tin thanh toán
            mainQuery.append("MAX(pt.transaction_code) AS transaction_code, ");
            mainQuery.append("MAX(pt.payment_date) AS payment_date, ");
            mainQuery.append("MAX(pt.status) AS payment_status ");

            mainQuery.append("FROM bookshopdb.orders o ");
            mainQuery.append("LEFT JOIN bookshopdb.order_item oi ON o.id = oi.order_id ");
            mainQuery.append("LEFT JOIN bookshopdb.product p ON oi.product_id = p.id ");
            mainQuery.append("LEFT JOIN bookshopdb.payment_method pm ON o.payment_method_id = pm.id ");
            mainQuery.append("LEFT JOIN bookshopdb.order_shipping os ON o.id = os.order_id ");
            mainQuery.append("LEFT JOIN bookshopdb.payment_transaction pt ON o.id = pt.order_id ");

            mainQuery.append("WHERE o.user_id = ? ");

            // Điều kiện lọc theo trạng thái
            if (status != null && !status.isEmpty()) {
                mainQuery.append("AND o.status = ? ");
            }

            // Tìm kiếm theo mã đơn hàng
            if (searchTerm != null && !searchTerm.isEmpty()) {
                mainQuery.append("AND o.order_code LIKE ? ");
            }

            mainQuery.append("GROUP BY o.id, o.order_code, o.status, o.created_at, o.subtotal, o.delivery_price, o.discount_amount, o.total_amount ");

            // Sắp xếp
            if (sortBy == null || sortBy.isEmpty() || sortBy.equals("newest")) {
                mainQuery.append("ORDER BY o.created_at DESC ");
            } else if (sortBy.equals("oldest")) {
                mainQuery.append("ORDER BY o.created_at ASC ");
            } else if (sortBy.equals("highest")) {
                mainQuery.append("ORDER BY o.total_amount DESC ");
            } else if (sortBy.equals("lowest")) {
                mainQuery.append("ORDER BY o.total_amount ASC ");
            }

            // Phân trang
            mainQuery.append("LIMIT ?, ? ");

            pstmt = conn.prepareStatement(mainQuery.toString());

            int paramIndex = 1;
            pstmt.setLong(paramIndex++, userId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            }

            pstmt.setInt(paramIndex++, (page - 1) * pageSize);
            pstmt.setInt(paramIndex++, pageSize);

            rs = pstmt.executeQuery();

            Gson gson = new Gson();

            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setId(rs.getLong("order_id"));
                order.setOrderCode(rs.getString("order_code"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getString("order_date"));
                order.setSubtotal(rs.getDouble("subtotal"));
                order.setDeliveryPrice(rs.getDouble("delivery_price"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setTotalItems(rs.getInt("total_items"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setAddress(rs.getString("address_line1"));
                order.setDistrict(rs.getString("district"));
                order.setCity(rs.getString("city"));
                order.setTrackingNumber(rs.getString("tracking_number"));
                order.setShippingCarrier(rs.getString("shipping_carrier"));
                order.setTransactionCode(rs.getString("transaction_code"));
                order.setPaymentDate(rs.getTimestamp("payment_date"));
                order.setPaymentStatus(rs.getString("payment_status"));

                // Xử lý danh sách sản phẩm trong đơn hàng
                String orderItemsJson = rs.getString("order_items");
                if (orderItemsJson != null && !orderItemsJson.isEmpty()) {
                    List<OrderItemDTO> orderItems = new ArrayList<>();
                    String[] itemsArray = orderItemsJson.split("\\|\\|");
                    for (String itemJson : itemsArray) {
                        OrderItemDTO item = gson.fromJson(itemJson, OrderItemDTO.class);
                        orderItems.add(item);
                    }
                    order.setOrderItems(orderItems);
                }

                orders.add(order);
            }

            // 2. Query để đếm tổng số đơn hàng phù hợp với điều kiện lọc
            StringBuilder countQuery = new StringBuilder();
            countQuery.append("SELECT COUNT(DISTINCT o.id) AS total_orders ");
            countQuery.append("FROM bookshopdb.orders o ");
            countQuery.append("WHERE o.user_id = ? ");

            if (status != null && !status.isEmpty()) {
                countQuery.append("AND o.status = ? ");
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                countQuery.append("AND o.order_code LIKE ? ");
            }

            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = conn.prepareStatement(countQuery.toString());

            paramIndex = 1;
            pstmt.setLong(paramIndex++, userId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            }

            if (rs != null) {
                rs.close();
            }
            rs = pstmt.executeQuery();

            int totalOrders = 0;
            if (rs.next()) {
                totalOrders = rs.getInt("total_orders");
            }

            // 3. Query để đếm số lượng đơn hàng theo từng trạng thái
            StringBuilder statusCountQuery = new StringBuilder();
            statusCountQuery.append("SELECT status, COUNT(*) as count ");
            statusCountQuery.append("FROM bookshopdb.orders ");
            statusCountQuery.append("WHERE user_id = ? ");
            statusCountQuery.append("GROUP BY status");

            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = conn.prepareStatement(statusCountQuery.toString());
            pstmt.setLong(1, userId);

            if (rs != null) {
                rs.close();
            }
            rs = pstmt.executeQuery();

            Map<String, Integer> orderStatusCounts = new HashMap<>();
            while (rs.next()) {
                orderStatusCounts.put(rs.getString("status"), rs.getInt("count"));
            }

            // Tính toán thông tin phân trang
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

            // Tạo và trả về OrderPageResponse
            return OrderPageResponse.builder()
                    .orders(orders)
                    .totalOrders(totalOrders)
                    .totalPages(totalPages)
                    .currentPage(page)
                    .pageSize(pageSize)
                    .orderStatusCounts(orderStatusCounts)
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return OrderPageResponse.builder()
                    .orders(Collections.emptyList())
                    .totalOrders(0)
                    .totalPages(0)
                    .currentPage(page)
                    .pageSize(pageSize)
                    .orderStatusCounts(Collections.emptyMap())
                    .build();
        } finally {
            close(conn, pstmt, rs);
        }
    }
}
