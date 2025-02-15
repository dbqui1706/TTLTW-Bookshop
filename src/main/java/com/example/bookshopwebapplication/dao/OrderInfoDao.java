package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IOrderInfoDao;
import com.example.bookshopwebapplication.dao.mapper.OrderInfoMapper;
import com.example.bookshopwebapplication.entities.OrderInfo;

import javax.swing.text.html.Option;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderInfoDao extends AbstractDao<OrderInfo> implements IOrderInfoDao {

    public OrderInfoDao() {
        super("order_info");
    }

    public Long save(OrderInfo orderInfo) {
        clearSQL();
        builderSQL.append(
                "INSERT INTO order_info (orderId, receiver, address_receiver, email_receiver, phone_receiver, city, district, ward, total_price)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );
        return insert(builderSQL.toString(), orderInfo.getOrderId(), orderInfo.getReceiver(),
                orderInfo.getAddressReceiver(), orderInfo.getEmailReceiver(), orderInfo.getPhone(),
                orderInfo.getCity(), orderInfo.getDistrict(), orderInfo.getWard(), orderInfo.getTotalPrice());
    }

    //Lấy đơn hàng dựa trên id.
    public Optional<OrderInfo> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM order_info WHERE id = ?");
        return Optional.ofNullable(query(builderSQL.toString(), new OrderInfoMapper(), id).get(0));
    }

    // Lay danh sach orderInfo theo orderId
    public Optional<OrderInfo> getByOrderId(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM order_info WHERE orderId = ?");
        List<OrderInfo> orderInfos = query(builderSQL.toString(), new OrderInfoMapper(), orderId);
        return orderInfos.isEmpty() ? Optional.empty() : Optional.of(orderInfos.get(0));
    }

    @Override
    public OrderInfo mapResultSetToEntity(ResultSet resultSet) {
        try {
            return new OrderInfo(
                    resultSet.getLong("id"),
                    resultSet.getLong("orderId"),
                    resultSet.getString("receiver"),
                    resultSet.getString("address_receiver"),
                    resultSet.getString("email_receiver"),
                    resultSet.getString("phone_receiver"),
                    resultSet.getString("city"),
                    resultSet.getString("district"),
                    resultSet.getString("ward"),
                    resultSet.getDouble("total_price"),
                    resultSet.getTimestamp("createdAt"),
                    resultSet.getTimestamp("updatedAt")
            );
        } catch (Exception e) {
            System.out.println("OrderInfoMapper.mapRow: " + e.getMessage());
        }
        return null;
    }
}
