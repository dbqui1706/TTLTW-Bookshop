package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.Order2Mapper;
import com.example.bookshopwebapplication.entities.Order2;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
}
