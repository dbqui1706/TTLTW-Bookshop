package com.example.bookshopwebapplication.servlet.client2.api;


import com.example.bookshopwebapplication.entities.PaymentMethod;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.payment.vnpay.VNPayConfig;
import com.example.bookshopwebapplication.service.PaymentMethodService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
    private final PaymentMethodService paymentMethodService = new PaymentMethodService();

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
//        // Lấy tất cả các tham số từ VNPay
//        Map<String, String> fields = new HashMap<>();
//        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
//            String fieldName = params.nextElement();
//            String fieldValue = request.getParameter(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                fields.put(fieldName, fieldValue);
//            }
//        }
//
//        // Xác thực chữ ký từ VNPay
//        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
//        if (vnp_SecureHash != null) {
//            fields.remove("vnp_SecureHash");
//            fields.remove("vnp_SecureHashType");
//
//            // Kiểm tra checksum
//            String signValue = VNPayConfig.hashAllFields(fields, VNPayConfig.vnp_HashSecret);
//            if (signValue.equals(vnp_SecureHash)) {
//                // Xác thực thành công
//                String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
//                String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
//                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
//                String vnp_Amount = request.getParameter("vnp_Amount");
//                String vnp_PayDate = request.getParameter("vnp_PayDate");
//                String vnp_BankCode = request.getParameter("vnp_BankCode");
//                String vnp_CardType = request.getParameter("vnp_CardType");
//
//                // Lấy mã đơn hàng từ vnp_TxnRef
//                String[] orderInfo = vnp_TxnRef.split("_");
//                String orderId = orderInfo[0];
//
//                // Xử lý thanh toán thành công (00)
//                if ("00".equals(vnp_ResponseCode)) {
//                    try {
//                        // 1. Lưu thông tin giao dịch vào database
//                        PaymentTransactionDAO paymentTransactionDAO = new PaymentTransactionDAO();
//                        OrderDAO orderDAO = new OrderDAO();
//
//                        // Tìm đơn hàng theo ID
//                        Order order = orderDAO.findById(Long.parseLong(orderId));
//
//                        if (order != null) {
//                            // Tạo giao dịch thanh toán
//                            PaymentTransaction transaction = new PaymentTransaction();
//                            transaction.setOrderId(order.getId());
//                            transaction.setPaymentMethodId(Long.parseLong(request.getParameter("paymentMethodId"))); // Lấy từ request hoặc database
//                            transaction.setAmount(new BigDecimal(Long.parseLong(vnp_Amount) / 100)); // Chia cho 100 vì VNPay gửi amount*100
//                            transaction.setTransactionCode(vnp_TxnRef);
//                            transaction.setPaymentProviderRef(vnp_BankCode + "-" + vnp_CardType);
//                            transaction.setStatus("completed");
//                            transaction.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
//                            transaction.setNote("Thanh toán VNPay thành công");
//
//                            // Lưu giao dịch vào database
//                            paymentTransactionDAO.save(transaction);
//
//                            // 2. Cập nhật trạng thái đơn hàng
//                            order.setStatus("processing"); // hoặc trạng thái phù hợp khác
//                            order.setIsVerified(true); // Đánh dấu đã thanh toán
//                            orderDAO.update(order);
//
//                            // 3. Chuyển hướng đến trang thông báo thành công
//                            response.sendRedirect(request.getContextPath() + "/payment-success.jsp?orderId=" + orderId);
//                            return;
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        // Xử lý lỗi nếu có
//                        response.sendRedirect(request.getContextPath() + "/payment-error.jsp?message=" + URLEncoder.encode("Lỗi xử lý thanh toán", StandardCharsets.UTF_8.toString()));
//                        return;
//                    }
//                } else {
//                    // Thanh toán thất bại, chuyển hướng đến trang thông báo lỗi
//                    response.sendRedirect(request.getContextPath() + "/payment-error.jsp?code=" + vnp_ResponseCode);
//                    return;
//                }
//            } else {
//                // Chữ ký không hợp lệ
//                response.sendRedirect(request.getContextPath() + "/payment-error.jsp?message=" + URLEncoder.encode("Chữ ký không hợp lệ", StandardCharsets.UTF_8.toString()));
//                return;
//            }
//        } else {
//            // Thiếu tham số bắt buộc
//            response.sendRedirect(request.getContextPath() + "/payment-error.jsp?message=" + URLEncoder.encode("Thiếu tham số bắt buộc", StandardCharsets.UTF_8.toString()));
//            return;
//        }
//
//        // Mặc định chuyển hướng về trang chủ nếu có lỗi không xác định
//        response.sendRedirect(request.getContextPath() + "/index.jsp");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Lấy thông tin đơn hàng từ request
            String orderId = request.getParameter("orderId");
            long amount = Long.parseLong(request.getParameter("amount")); // Số tiền * 100 (VNPay yêu cầu số tiền * 100)
            String orderInfo = request.getParameter("orderInfo");
            String returnUrl = request.getParameter("returnUrl");

            if (returnUrl == null || returnUrl.isEmpty()) {
                returnUrl = VNPayConfig.vnp_ReturnUrl;
            }

            // Tạo các tham số thanh toán
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân với 100 (VND)

            // Tạo mã giao dịch cho VNPay
            String vnp_TxnRef = orderId + "_" + VNPayConfig.getRandomNumber(8);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "250000"); // Mặc định là thanh toán hóa đơn

            String locate = request.getParameter("language");
            if (locate != null && !locate.isEmpty()) {
                vnp_Params.put("vnp_Locale", locate);
            } else {
                vnp_Params.put("vnp_Locale", "vn");
            }

            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", getClientIpAddr(request));

            // Thêm thời gian tạo giao dịch
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Thêm thời gian hết hạn giao dịch (mặc định là 15 phút sau khi tạo)
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Tạo URL thanh toán VNPay
            String paymentUrl = VNPayConfig.getPaymentUrl(vnp_Params, VNPayConfig.vnp_HashSecret);

            // Trả về URL thanh toán
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("paymentUrl", paymentUrl);
            jsonResponse.addProperty("orderId", orderId);
            jsonResponse.addProperty("transactionId", vnp_TxnRef);

            out.print(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Có lỗi xảy ra: " + e.getMessage());
            JsonUtils.out(
                    response,
                    jsonResponse,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            // Trả về lỗi
        }

        out.flush();
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
