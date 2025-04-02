package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.CouponDao;
import com.example.bookshopwebapplication.entities.Coupon;
import com.example.bookshopwebapplication.exceptions.CouponException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class CouponService {
    private final CouponDao couponDao;

    public CouponService() {
        this.couponDao = new CouponDao();
    }

    public Long insert(Coupon coupon) {
        Long id = couponDao.save(coupon);
        return id;
    }

    public boolean update(Coupon coupon) {
        try {
            couponDao.update(coupon);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra và lấy thông tin mã giảm giá
    public Coupon validateCoupon(String code, Double orderValue) throws CouponException {
        Coupon coupon = couponDao.findByCode(code);

        if (coupon == null) {
            throw new CouponException("Mã giảm giá không tồn tại");
        }

        // Kiểm tra coupon có đang active không
        if (!coupon.getIsActive()) {
            throw new CouponException("Mã giảm giá đã hết hiệu lực");
        }

        // Kiểm tra thời gian hiệu lực
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.before(coupon.getStartDate()) || now.after(coupon.getEndDate())) {
            throw new CouponException("Mã giảm giá không trong thời gian hiệu lực");
        }

        // Kiểm tra số lần sử dụng
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new CouponException("Mã giảm giá đã hết lượt sử dụng");
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new CouponException("Giá trị đơn hàng chưa đạt tối thiểu để sử dụng mã");
        }

        // Kiểm tra người dùng đã sử dụng coupon này chưa (cần thêm bảng user_coupon)
        // if (couponDAO.hasUserUsedCoupon(userId, coupon.getId())) {
        //    throw new CouponException("Bạn đã sử dụng mã giảm giá này");
        // }

        return coupon;
    }
    // Tính toán số tiền được giảm
    public Double calculateDiscount(Coupon coupon, BigDecimal orderValue) {
        BigDecimal discount;

        if ("percentage".equals(coupon.getDiscountType())) {
            // Giảm theo phần trăm
            BigDecimal discountValue = BigDecimal.valueOf(coupon.getDiscountValue());
            discount = orderValue.multiply(discountValue.divide(new BigDecimal(100)));

            // Kiểm tra giảm tối đa
            BigDecimal maxDiscount = BigDecimal.valueOf(coupon.getMaxDiscount());
            if (coupon.getMaxDiscount() != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else {
            // Giảm số tiền cố định
            discount = BigDecimal.valueOf(coupon.getDiscountValue());

            // Nếu giảm nhiều hơn giá trị đơn hàng
            if (discount.compareTo(orderValue) > 0) {
                discount = orderValue;
            }
        }

        return discount.doubleValue();
    }

    // Cập nhật số lần sử dụng khi áp dụng coupon thành công
    public void incrementUsageCount(String code) {
        Coupon coupon = couponDao.findByCode(code);
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponDao.update(coupon);
    }

    public List<Coupon> findAll() {
        return couponDao.findAll();
    }

    // Lấy danh sách coupon khả dụng cho người dùng
    public List<Coupon> getAvailableCoupons(Long userId, BigDecimal orderValue) {
        List<Coupon> coupons = couponDao.getActiveCoupons();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        return coupons.stream()
                .filter(Coupon::getIsActive)
                .filter(coupon -> now.after(coupon.getStartDate()) && now.before(coupon.getEndDate()))
                .filter(coupon -> coupon.getUsageLimit() == null || coupon.getUsageCount() < coupon.getUsageLimit())
                .filter(coupon -> orderValue.compareTo(BigDecimal.valueOf(coupon.getMinOrderValue())) >= 0)
                // Thêm logic lọc theo user nếu cần
                .collect(Collectors.toList());
    }
}
