package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.CouponMapper;
import com.example.bookshopwebapplication.entities.Coupon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CouponDao extends AbstractDao<Coupon> {
    /**
     * Constructor nhận vào tên bảng của entity
     */
    public CouponDao() {
        super("coupon");
    }

    public Long save(Coupon coupon) {
        clearSQL();
        builderSQL.append("INSERT INTO bookshopdb.coupon (code, description, discount_type," +
                " discount_value, min_order_value, max_discount, start_date, end_date," +
                " usage_limit, usage_count, is_active) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), coupon.getCode(), coupon.getDescription(),
                coupon.getDiscountType(), coupon.getDiscountValue(),
                coupon.getMinOrderValue(), coupon.getMaxDiscount(),
                coupon.getStartDate(), coupon.getEndDate(), coupon.getUsageLimit(),
                coupon.getUsageCount(), coupon.getIsActive() ? 1 : 0);
    }

    public void update(Coupon coupon) {
        clearSQL();
        builderSQL.append("UPDATE bookshopdb.coupon SET code = ?, description = ?, discount_type = ?, " +
                "discount_value = ?, min_order_value = ?, max_discount = ?, start_date = ?, end_date = ?, " +
                "usage_limit = ?, usage_count = ?, is_active = ? WHERE id = ?");
        update(builderSQL.toString(), coupon.getCode(), coupon.getDescription(),
                coupon.getDiscountType(), coupon.getDiscountValue(),
                coupon.getMinOrderValue(), coupon.getMaxDiscount(),
                coupon.getStartDate(), coupon.getEndDate(), coupon.getUsageLimit(),
                coupon.getUsageCount(), coupon.getIsActive() ? 1 : 0, coupon.getId());
    }

    public Coupon findByCode(String code) {
        clearSQL();
        builderSQL.append("SELECT * FROM bookshopdb.coupon WHERE code = ?");
        List<Coupon> coupons = query(builderSQL.toString(), new CouponMapper(), code);
        return coupons.isEmpty() ? null : coupons.get(0);
    }

    public List<Coupon> findAll() {
        clearSQL();
        builderSQL.append("SELECT * FROM bookshopdb.coupon");
        return query(builderSQL.toString(), new CouponMapper());
    }

    @Override
    public Coupon mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        try {
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

    public void incrementUsageCount(String code) {
        clearSQL();
        builderSQL.append("UPDATE bookshopdb.coupon SET usage_count = usage_count + 1 WHERE code = ?");
        update(builderSQL.toString(), code);
    }

    public List<Coupon> getActiveCoupons() {
        clearSQL();
        builderSQL.append("SELECT * FROM bookshopdb.coupon WHERE is_active = 1");
        return query(builderSQL.toString(), new CouponMapper());
    }
}
