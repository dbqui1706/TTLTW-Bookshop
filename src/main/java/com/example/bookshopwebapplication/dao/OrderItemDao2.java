package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.OrderItem2Mapper;
import com.example.bookshopwebapplication.entities.OrderItem;
import com.example.bookshopwebapplication.entities.OrderItem2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
                    .basePrice(resultSet.getDouble("base_price"))
                    .discountPercent(resultSet.getDouble("discount_percent"))
                    .price(resultSet.getDouble("price"))
                    .quantity(resultSet.getInt("quantity"))
                    .subtotal(resultSet.getDouble("subtotal"))
                    .createdAt(resultSet.getTimestamp("created_at"))
                    .updatedAt(resultSet.getTimestamp("updated_at"))
                    .build();
        } catch (Exception e) {
            System.out.println("Error mapping ResultSet to OrderItem2 entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Thêm vào OrderItemDao2
    public Long saveWithConnection(OrderItem2 item, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO order_item (order_id, product_id, product_name, ");
        builderSQL.append("product_image, base_price, discount_percent, price, quantity, ");
        builderSQL.append("subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insertWithConnection(conn, builderSQL.toString(), item.getOrderId(), item.getProductId(), item.getProductName(),
                item.getProductImage(), item.getBasePrice(), item.getDiscountPercent(),
                item.getPrice(), item.getQuantity(), item.getSubtotal());
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

    public List<OrderItem2> findByOrderId(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM order_item WHERE order_id = ?");
        List<OrderItem2> orderItems = query(builderSQL.toString(), new OrderItem2Mapper(), orderId);
        return orderItems;
    }
}
