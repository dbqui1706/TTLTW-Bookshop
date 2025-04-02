package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.Coupon;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponMapper implements IRowMapper<Coupon> {
    @Override
    public Coupon mapRow(ResultSet resultSet) throws SQLException {
        try{
            Coupon coupon = new Coupon();
            coupon.setId(resultSet.getLong("id"));
            coupon.setCode(resultSet.getString("code"));
            coupon.setDescription(resultSet.getString("description"));
            coupon.setDiscountType(resultSet.getString("discount_type"));
            coupon.setDiscountValue(resultSet.getDouble("discount_value"));
            coupon.setMinOrderValue(resultSet.getDouble("min_order_value"));
            coupon.setMaxDiscount(resultSet.getDouble("max_discount"));
            coupon.setStartDate(resultSet.getTimestamp("start_date"));
            coupon.setEndDate(resultSet.getTimestamp("end_date"));
            coupon.setUsageLimit(resultSet.getInt("usage_limit"));
            coupon.setUsageCount(resultSet.getInt("usage_count"));
            coupon.setIsActive(resultSet.getBoolean("is_active"));
            coupon.setCreatedAt(resultSet.getTimestamp("created_at"));
            coupon.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return coupon;
        } catch (SQLException e) {
            throw new SQLException("Error occurred while mapping coupon entity", e);
        }
    }
}
