package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.EncodePassword;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/reset-password", "/reset-password-confirm"})
public class ResetPassword extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Nếu request có sẵn thuộc tính email và code (từ doPost), sử dụng chúng
        String email = (String) req.getAttribute("email");
        String code = (String) req.getAttribute("code");

        // Nếu không có sẵn trong attributes, lấy từ parameters
        if (email == null && code == null) {
            email = req.getParameter("email");
            code = req.getParameter("code");

            // Kiểm tra email và code có hợp lệ không
            if (email == null || code == null || email.isEmpty() || code.isEmpty() || !ForgotPassword.isValidEmail(email)) {
                req.setAttribute("alertType", "danger");
                req.setAttribute("alertMessage", "Liên kết không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
                req.getRequestDispatcher("/WEB-INF/views/client/reset-password.jsp").forward(req, resp);
                return;
            }

            // Kiểm tra mã xác minh có hợp lệ không
            boolean isValidCode = EmailUtils.isValidVerificationCode(email, code);

            if (!isValidCode) {
                req.setAttribute("alertType", "danger");
                req.setAttribute("alertMessage", "Liên kết không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
                req.getRequestDispatcher("/WEB-INF/views/client/reset-password.jsp").forward(req, resp);
                return;
            }

            // Lưu email và code vào request attribute để sử dụng trong JSP
            req.setAttribute("email", email);
            req.setAttribute("code", code);
        }

        // Chuyển hướng đến trang đặt lại mật khẩu
        req.getRequestDispatcher("/WEB-INF/views/client/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Xác định xem request có phải là AJAX không
        String acceptHeader = req.getHeader("Accept");
        boolean isAjaxRequest = acceptHeader != null && acceptHeader.contains("application/json");

        // Thiết lập response type là JSON nếu là request AJAX
        if (isAjaxRequest) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
        }

        // Lấy thông tin từ form
        String email = req.getParameter("email");
        String code = req.getParameter("code");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        // Kiểm tra dữ liệu đầu vào
        if (email == null || code == null || password == null || confirmPassword == null ||
                email.isEmpty() || code.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

            String errorMessage = "Vui lòng điền đầy đủ thông tin.";
            if (isAjaxRequest) {
                sendJsonResponse(resp, false, errorMessage);
            } else {
                req.setAttribute("alertType", "danger");
                req.setAttribute("alertMessage", errorMessage);
                req.setAttribute("email", email);
                req.setAttribute("code", code);
                doGet(req, resp);
            }
            return;
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp nhau không
        if (!password.equals(confirmPassword)) {
            String errorMessage = "Mật khẩu xác nhận không khớp.";
            if (isAjaxRequest) {
                sendJsonResponse(resp, false, errorMessage);
            } else {
                req.setAttribute("alertType", "danger");
                req.setAttribute("alertMessage", errorMessage);
                req.setAttribute("email", email);
                req.setAttribute("code", code);
                doGet(req, resp);
            }
            return;
        }

        final UserService userService = new UserService();
        // Đặt lại mật khẩu
        boolean success = userService.resetPassword(email,
                EncodePassword.hash(password)
        );

        if (success) {
            // Đặt lại mật khẩu thành công
            String successMessage = "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập với mật khẩu mới.";
            sendJsonResponse(resp, true, successMessage);

        } else {
            // Đặt lại mật khẩu thất bại
            String errorMessage = "Đã xảy ra lỗi khi đặt lại mật khẩu. Vui lòng thử lại.";
            if (isAjaxRequest) {
                sendJsonResponse(resp, false, errorMessage);
            } else {
                req.setAttribute("alertType", "danger");
                req.setAttribute("alertMessage", errorMessage);
                req.setAttribute("email", email);
                req.setAttribute("code", code);
                doGet(req, resp);
            }
        }
    }

    // Phương thức gửi phản hồi JSON
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}
