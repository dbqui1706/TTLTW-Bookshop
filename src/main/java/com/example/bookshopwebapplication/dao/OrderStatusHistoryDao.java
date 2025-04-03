package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.OrderStatusHistory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStatusHistoryDao extends AbstractDao<OrderStatusHistory> {
    public OrderStatusHistoryDao() {
        super("order_status_history");
    }

    @Override
    public OrderStatusHistory mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        try{
            return OrderStatusHistory.builder()
                    .id(resultSet.getLong("id"))
                    .orderId(resultSet.getLong("order_id"))
                    .status(resultSet.getString("status"))
                    .note(resultSet.getString("note"))
                    .changedBy(resultSet.getLong("changed_by"))
                    .createdAt(resultSet.getTimestamp("created_at"))
                    .build();
        }catch (Exception e){
            System.out.println("Error in OrderStatusHistoryDao: " + e.getMessage());
            throw new SQLException(e);
        }
    }

    public Long save(OrderStatusHistory statusHistory) {
        clearSQL();
        builderSQL.append("INSERT INTO order_status_history (order_id, status, note, changed_by) ");
        builderSQL.append("VALUES (?, ?, ?, ?)");
        return insert(builderSQL.toString(), statusHistory.getOrderId(),
                statusHistory.getStatus(), statusHistory.getNote(), statusHistory.getChangedBy());
    }
}
