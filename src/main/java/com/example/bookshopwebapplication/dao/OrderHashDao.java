package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.OrderHashMapper;
import com.example.bookshopwebapplication.dao.mapper.OrderInfoMapper;
import com.example.bookshopwebapplication.entities.Order;
import com.example.bookshopwebapplication.entities.OrderHash;
import com.example.bookshopwebapplication.entities.OrderInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderHashDao extends AbstractDao<OrderHash> {

    public OrderHashDao() {
        super("order_data_hash");
    }

    public Long save(OrderHash orderHash) {
        clearSQL();
        builderSQL.append(
                "INSERT INTO " +
                        "order_data_hash (orderId, user_id, data_hash, publicKey)" +
                        " VALUES (?, ?, ?, ?)"
        );
        return insert(builderSQL.toString(), orderHash.getOrderId(), orderHash.getUserId(),
                orderHash.getDataHash(), orderHash.getPublicKey());
    }

    public Optional<OrderHash> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM order_data_hash WHERE id = ?");
        return Optional.ofNullable(query(builderSQL.toString(), new OrderHashMapper(), id).get(0));
    }

    public Optional<OrderHash> getByOrderId(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM order_data_hash WHERE orderId = ?");
        List<OrderHash> orderHashes = query(builderSQL.toString(), new OrderHashMapper(), orderId);
        return orderHashes.isEmpty() ? Optional.empty() : Optional.of(orderHashes.get(0));
    }

    @Override
    public OrderHash mapResultSetToEntity(ResultSet resultSet) {
        try {
            return new OrderHash(
                    resultSet.getLong("id"),
                    resultSet.getLong("orderId"),
                    resultSet.getLong("user_id"),
                    resultSet.getString("data_hash"),
                    resultSet.getString("publicKey"),
                    resultSet.getTimestamp("createdAt"),
                    resultSet.getTimestamp("updatedAt")
            );
        } catch (Exception e) {
            System.out.println("OrderHashMapper.mapRow: " + e.getMessage());
        }
        return null;
    }
}
