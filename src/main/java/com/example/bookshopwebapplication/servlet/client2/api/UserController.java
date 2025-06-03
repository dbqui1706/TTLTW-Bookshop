package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.OauthUser;
import com.example.bookshopwebapplication.entities.UserAddress;
import com.example.bookshopwebapplication.http.request.user.LoginDTO;
import com.example.bookshopwebapplication.http.request.user.RegisterDTO;
import com.example.bookshopwebapplication.service.OauthUserService;
import com.example.bookshopwebapplication.service.UserAddressService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.CookieUtil;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.MultiPart;
import com.example.bookshopwebapplication.utils.login_api.EOAuthProvider;
import com.example.bookshopwebapplication.utils.login_api.OauthLoginService;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;
import com.google.gson.Gson;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet(
        name = "UserController",
        urlPatterns = {
                "/api/auth/login",
                "/api/auth/register",
                "/api/auth/logout",
                "/api/auth/active-account",
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/api/auth/change-password",
                "/api/auth/verify-reset-token",
                "/login-gg",
                "/login-fb",
        }
)
public class UserController extends HttpServlet {
    private final UserService userService = new UserService();
    private final UserAddressService userAddressService = new UserAddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        switch (uri) {
            case "/api/auth/active-account":
                // Xác thực tài khoản
                activeAccount(request, response);
                break;
            case "/api/auth/logout":
                logout(request, response);
                break;
            case "/api/auth/forgot-password":
                forgotPassword(request, response);
                break;
            case "/api/auth/change-password":
                changePassword(request, response);
                break;
            case "/login-gg", "/login-fb":
                loginApiProvider(request, response);
                break;
            case "/api/auth/verify-reset-token":
                verifyResetToken(request, response);
                break;

            default:
                JsonUtils.out(
                        response,
                        "Method not allowed",
                        HttpServletResponse.SC_NOT_IMPLEMENTED
                );
        }
    }

    private void verifyResetToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String email = request.getParameter("email");
            String code = request.getParameter("code");

            if (email == null || code == null || email.isEmpty() || code.isEmpty()) {
                JsonUtils.out(
                        response,
                        Map.of("success", false, "message", "Thiếu thông tin xác thực"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Verify token với EmailUtils
            boolean isValid = EmailUtils.isValidVerificationCode(email, code);

            if (isValid) {
                JsonUtils.out(
                        response,
                        Map.of("success", true, "message", "Token hợp lệ"),
                        HttpServletResponse.SC_OK
                );
            } else {
                JsonUtils.out(
                        response,
                        Map.of("success", false, "message", "Token không hợp lệ hoặc đã hết hạn"),
                        HttpServletResponse.SC_UNAUTHORIZED
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtils.out(
                    response,
                    Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void loginApiProvider(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");
            if (code == null || code.isEmpty()) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
            } else {
                OauthUser oauthUser = request.getRequestURI().equals("/login-gg")
                        ? OauthLoginService.login(EOAuthProvider.GOOGLE.getName(), code).get()
                        : OauthLoginService.login(EOAuthProvider.FACEBOOK.getName(), code).get();
                OauthUserService oauthUserService = new OauthUserService();
                Optional<OauthUser> insertOauth = oauthUserService.insert(oauthUser);

                if (insertOauth.isPresent()) {
                    Optional<UserDto> userFromServer = userService.getById(insertOauth.get().getUserID());
                    request.getSession().setAttribute("currentUser", userFromServer.get());

                    UserDto user = userFromServer.get();
                    user.setPassword(null);
                    user.setCreatedAt(null);
                    user.setUpdatedAt(null);
                    user.setUsername(null);

                    String token = UUID.randomUUID().toString();

                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().write(
                            "<!DOCTYPE html><html><head><title>Đăng nhập thành công</title></head><body>" +
                                    "<script>" +
                                    "  localStorage.setItem('token', '" + token + "');" +
                                    "  localStorage.setItem('user', '" + new Gson().toJson(user) + "');" +
                                    "  window.location.href = '/';" +
                                    "</script>" +
                                    "</body></html>"
                    );
                }
            }
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    private void activeAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Optional<String> email = Optional.ofNullable(request.getParameter("email"));
            Optional<String> code = Optional.ofNullable(request.getParameter("code"));

            if (EmailUtils.isValidVerificationCode(email.get(), code.get())) {
                UserDto user = (UserDto) request.getSession().getAttribute("userSignUp");

                // Update email was active
                userService.setActiveEmail(email.get());

                request.getSession().removeAttribute("userSignUp");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        switch (uri) {
            case "/api/auth/login":
                login(request, response);
                break;
            case "/api/auth/register":
                register(request, response);
                break;
            case "/api/auth/reset-password":
                resetPassword(request, response);
                break;
            default:
                JsonUtils.out(
                        response,
                        "Method not allowed",
                        HttpServletResponse.SC_NOT_IMPLEMENTED
                );
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) return;
            LoginDTO loginDTO = MultiPart.get(request, LoginDTO.class);

            // Validate reCAPTCHA
            if (loginDTO.getRecaptcha() == null
                    || loginDTO.getRecaptcha().isEmpty()
                    || !verifyRecaptcha(loginDTO.getRecaptcha())) {
                JsonUtils.out(
                        response,
                        "reCAPTCHA is required",
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            Optional<UserDto> user = userService.login(loginDTO.getEmail(), loginDTO.getPassword());

            // Check if user is present
            if (user.isEmpty()) {
                JsonUtils.out(
                        response,
                        "Email or password is incorrect",
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                return;
            }
            // Lấy địa chỉ người dùng thông qua bảng "user_address"
            List<UserAddress> userAddresses = userAddressService.findByUser(user.get().getId());

            // Save user to session
            request.getSession().setAttribute("currentUser", user.get());
            // Lưu quyền vào session để hạn chế việc truy vấn cơ sở dữ liệu

            // Lưu trạng thái người dùng
            // Kiểm tra xem session của người dùng đã tồn tại chưa nếu chưa thì save vào database và cache
            // Lưu thông tin vào bảng user_session
            String token = UUID.randomUUID().toString();
            UserService.getInstance().saveUserSession(
                    request,
                    user.get().getId(),
                    token
            );
            user.get().setPassword(null);
            user.get().setCreatedAt(null);
            user.get().setUpdatedAt(null);
            user.get().setUsername(null);
            Map<String, Object> result = Map.of(
                    "user", user.get(),
                    "address", userAddresses,
                    "token", token
            );

            // Lưu vào cookie
            // Lưu vào cookie
            System.out.println("Saving session to cookie...");
            CookieUtil.saveSession(token, request, response);

            // Check cookie after setting
            Cookie[] cookies = request.getCookies();
            System.out.println("Cookies after setting:");
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println(" - " + cookie.getName() + "=" + cookie.getValue());
                }
            } else {
                System.out.println("No cookies found in request after setting");
            }

            // Response headers
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Debug response headers
            System.out.println("Response headers:");
            for (String headerName : response.getHeaderNames()) {
                System.out.println(headerName + ": " + response.getHeader(headerName));
            }

            JsonUtils.out(
                    response,
                    result,
                    HttpServletResponse.SC_OK
            );

        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) return;
            RegisterDTO registerDTO = MultiPart.get(request, RegisterDTO.class);

            Optional<UserDto> isRegistered = userService.register(registerDTO);
            if (isRegistered.isEmpty()) {
                JsonUtils.out(
                        response,
                        "Register failed",
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Tiến hành gửi email xác nhận
            EmailUtils.sendEmail(isRegistered.get(), UUID.randomUUID().toString());
            System.out.println("Email đã được gửi: " + isRegistered.get());
            // Lưu vào session
            request.getSession().setAttribute("userSignUp", isRegistered.get());

            JsonUtils.out(
                    response,
                    "Register successfully",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    private boolean verifyRecaptcha(String recaptchaResponse) {
        String secretKey = "6LdiJlMrAAAAAIyAy58tJVagNSkEVlWKlqxIPSme";
        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        try {
            URL url = new URL(verifyUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postParams = "secret=" + secretKey + "&response=" + recaptchaResponse;

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(postParams);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) {
    }

    private void forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        try {
            String email = request.getParameter("email");
            if (email == null || email.isEmpty()) {
                JsonUtils.out(
                        response,
                        "Email is required",
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Kiểm tra xem email có tồn tại trong hệ thống không
            Optional<UserDto> user = userService.getByEmail(email);
            if (user.isEmpty()) {
                JsonUtils.out(
                        response,
                        "Email not found",
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            // Gửi email reset password
            EmailUtils.sendPasswordResetEmail(user.get());
            JsonUtils.out(
                    response,
                    "Reset password email sent successfully",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    response,
                    e.getMessage(),
                    HttpServletResponse.SC_BAD_REQUEST
            );
        }
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            String email, code, password;

            if (isMultipart) {
                // Handle multipart form data
                Map<String, String> params = MultiPart.get(request, Map.class);
                email = params.get("email");
                code = params.get("code");
                password = params.get("password");
            } else {
                // Handle regular form data
                email = request.getParameter("email");
                code = request.getParameter("code");
                password = request.getParameter("password");
            }

            if (email == null || code == null || password == null ||
                    email.isEmpty() || code.isEmpty() || password.isEmpty()) {
                JsonUtils.out(
                        response,
                        Map.of("success", false, "message", "Thiếu thông tin bắt buộc"),
                        HttpServletResponse.SC_BAD_REQUEST
                );
                return;
            }

            // Update password in database
            UserDto user = userService.getByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Email not found"));
            user.setPassword(password);
            UserDto updated = userService.update(user).orElseThrow(() -> new IllegalArgumentException("Không thể cập nhật mật khẩu"));

            if (updated != null) {
                JsonUtils.out(
                        response,
                        Map.of("success", true, "message", "Mật khẩu đã được đặt lại thành công"),
                        HttpServletResponse.SC_OK
                );
            } else {
                JsonUtils.out(
                        response,
                        Map.of("success", false, "message", "Không thể cập nhật mật khẩu"),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonUtils.out(
                    response,
                    Map.of("success", false, "message", "Có lỗi xảy ra: " + e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) {
    }
}
