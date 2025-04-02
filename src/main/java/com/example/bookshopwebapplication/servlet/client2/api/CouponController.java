package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.entities.Coupon;
import com.example.bookshopwebapplication.service.CouponService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(
        name = "CouponController",
        urlPatterns = {
                "/api/coupons",
        }
)
public class CouponController extends HttpServlet {
    private final CouponService couponService = new CouponService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/coupons":
                getCoupon(req, resp);
                break;
        }
    }

    private void getCoupon(HttpServletRequest req, HttpServletResponse resp) {
        try {
            BigDecimal orderValue = new BigDecimal(req.getParameter("orderValue"));
            List<Coupon> coupons = couponService.getAvailableCoupons(null, orderValue);

            JsonUtils.out(
                    resp,
                    coupons,
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
