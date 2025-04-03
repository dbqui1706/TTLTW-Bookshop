package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.OrderStatusHistory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStatusHistoryMapper implements IRowMapper<OrderStatusHistory> {
    @Override
    public OrderStatusHistory mapRow(ResultSet resultSet) throws SQLException {
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
            System.out.println("Error in OrderStatusHistoryMapper: " + e.getMessage());
            throw new SQLException(e);
        }
    }
}
