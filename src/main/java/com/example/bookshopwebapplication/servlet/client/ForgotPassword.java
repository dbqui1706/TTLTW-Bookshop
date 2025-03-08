package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.service.UserService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/forgot-password", "/check-email"})
public class ForgotPassword extends HttpServlet {

    private final UserService userService = new UserService();

    private final Gson gson = new Gson();
    // Pattern cho email RFC 5322
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            Pattern.CASE_INSENSITIVE);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");

        // Kiểm tra email có tồn tại trong hệ thống không
        Optional<UserDto> isEmailExists = userService.getByEmail(email);

        if (isEmailExists.isPresent()) {
            // Nếu email tồn tại, gửi email khôi phục mật khẩu
            userService.sendPasswordResetEmail(isEmailExists.get());
        }

        // Dù email có tồn tại hay không, vẫn hiển thị thông báo thành công (vì lý do bảo mật)
        req.setAttribute("message", "Nếu email này đã đăng ký, bạn sẽ nhận được hướng dẫn lấy lại mật khẩu.");
        doGet(req, resp);
    }

    /**
     * Kiểm tra xem một chuỗi có phải là định dạng email hợp lệ không
     *
     * @param email Chuỗi cần kiểm tra
     * @return true nếu là email hợp lệ, false nếu ngược lại
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
