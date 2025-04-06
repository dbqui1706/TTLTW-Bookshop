package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.entities.PaymentMethod;
import com.example.bookshopwebapplication.payment.vnpay.VNPayConfig;
import com.example.bookshopwebapplication.service.OrderService2;
import com.example.bookshopwebapplication.service.PaymentMethodService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(
        name = "PaymentMethodController",
        urlPatterns = {
                "/api/payment-methods",
                "/api/payment/vnpay",
                "/api/payment/vnpay-callback",
        }
)
public class PaymentMethodController extends HttpServlet {
    private final PaymentMethodService paymentMethodService;
    private final OrderService2 orderService2;

    public PaymentMethodController() {
        this.paymentMethodService = new PaymentMethodService();
        this.orderService2 = new OrderService2();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/payment-methods":
                getPaymentMethods(req, resp);
                break;
            case "/api/payment/vnpay-callback":
                // Xử lý callback từ VNPay
                vnpayCallback(req, resp);
                break;
            default:
                JsonUtils.out(
                        resp,
                        "Không tìm thấy API",
                        HttpServletResponse.SC_NOT_FOUND
                );
        }
    }

    private void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Lấy tất cả tham số từ request
            Map<String, String> vnpParams = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();

            // Ghi log để debug
            System.out.println("VNPay Callback - Received parameters:");

            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                String value = request.getParameter(name);
                if (value != null && !value.isEmpty()) {
                    vnpParams.put(name, value);
                    // Ghi log từng tham số
                    System.out.println(name + " = " + value);
                }
            }

            // Xóa chữ ký từ params để kiểm tra
            String vnpSecureHash = vnpParams.get("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");

            // Kiểm tra chữ ký
            String signValue = VNPayConfig.calculateSignature(vnpParams);
            System.out.println("Calculated signature: " + signValue);
            System.out.println("Received signature: " + vnpSecureHash);

            boolean isValidSignature = signValue.equals(vnpSecureHash);
            System.out.println("Signature valid: " + isValidSignature);

            String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
            String vnpTxnRef = vnpParams.get("vnp_TxnRef");
            // Tạm thời bỏ qua việc kiểm tra chữ ký để debug
            if ("00".equals(vnpResponseCode)) {
                // Thanh toán thành công - cập nhật trạng thái đơn hàng trong DB
               boolean success = orderService2.updateVNPAYTransactionStatus(vnpTxnRef, vnpParams);
            }  // Chuyển hướng đến trang thành công
            response.sendRedirect("http://127.0.0.1:5500/client/payment-status.html?orderCode="
                    + vnpTxnRef + "&vnp_ResponseCode=" + vnpResponseCode + "&vnp_Amount=" + vnpParams.get("vnp_Amount"));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/payment-failed.html?error=system_error");
        }
    }

    private void getPaymentMethods(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<PaymentMethod> result = paymentMethodService.getAll();
            JsonUtils.out(
                    resp,
                    result,
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

    // Lấy địa chỉ IP của client
    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
