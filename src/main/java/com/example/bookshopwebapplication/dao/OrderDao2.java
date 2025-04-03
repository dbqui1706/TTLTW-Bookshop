package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.Order2Mapper;
import com.example.bookshopwebapplication.entities.Order2;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderDao2 extends AbstractDao<Order2> {
    public OrderDao2() {
        super("orders");
    }

    public Long save(Order2 order) {
        clearSQL();
        builderSQL.append("INSERT INTO orders (order_code, user_id, status, delivery_method_id, payment_method_id, ");
        builderSQL.append("subtotal, delivery_price, discount_amount, tax_amount, total_amount, coupon_code, is_verified, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), order.getOrderCode(), order.getUserId(), order.getStatus(),
                order.getDeliveryMethodId(), order.getPaymentMethodId(), order.getSubtotal(),
                order.getDeliveryPrice(), order.getDiscountAmount(), order.getTaxAmount(),
                order.getTotalAmount(), order.getCouponCode(), order.getIsVerified(), order.getNote());
    }

    public Optional<Order2> findById(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE id = ?");
        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper(), orderId);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
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
                    .subtotal(rs.getBigDecimal("subtotal"))
                    .deliveryPrice(rs.getBigDecimal("delivery_price"))
                    .discountAmount(rs.getBigDecimal("discount_amount"))
                    .taxAmount(rs.getBigDecimal("tax_amount"))
                    .totalAmount(rs.getBigDecimal("total_amount"))
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
}
