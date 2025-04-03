package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.OrderItem2;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemDao2 extends AbstractDao<OrderItem2> {
    public OrderItemDao2() {
        super("order_item");
    }

    @Override
    public OrderItem2 mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        try {
            return OrderItem2.builder()
                    .id(resultSet.getLong("id"))
                    .orderId(resultSet.getLong("order_id"))
                    .productId(resultSet.getLong("product_id"))
                    .productName(resultSet.getString("product_name"))
                    .productImage(resultSet.getString("product_image"))
                    .basePrice(resultSet.getBigDecimal("base_price"))
                    .discountPercent(resultSet.getBigDecimal("discount_percent"))
                    .price(resultSet.getBigDecimal("price"))
                    .quantity(resultSet.getInt("quantity"))
                    .subtotal(resultSet.getBigDecimal("subtotal"))
                    .createdAt(resultSet.getTimestamp("created_at"))
                    .updatedAt(resultSet.getTimestamp("updated_at"))
                    .build();
        } catch (Exception e) {
            System.out.println("Error mapping ResultSet to OrderItem2 entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Long save(OrderItem2 item) {
        clearSQL();
        builderSQL.append("INSERT INTO order_item (order_id, product_id, product_name, ");
        builderSQL.append("product_image, base_price, discount_percent, price, quantity, ");
        builderSQL.append("subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), item.getOrderId(), item.getProductId(), item.getProductName(),
                item.getProductImage(), item.getBasePrice(), item.getDiscountPercent(),
                item.getPrice(), item.getQuantity(), item.getSubtotal());
    }
}
