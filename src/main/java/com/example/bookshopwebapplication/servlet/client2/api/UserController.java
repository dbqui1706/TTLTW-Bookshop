package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.entities.UserAddress;
import com.example.bookshopwebapplication.http.request.user.LoginDTO;
import com.example.bookshopwebapplication.http.request.user.RegisterDTO;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.UserAddressService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.CookieUtil;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.MultiPart;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            case "/api/auth/reset-password":
                resetPassword(request, response);
                break;
            case "/api/auth/change-password":
                changePassword(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
                response.sendRedirect(request.getContextPath() + "/signin");
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

    private void logout(HttpServletRequest request, HttpServletResponse response) {
    }

    private void forgotPassword(HttpServletRequest request, HttpServletResponse response) {
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response) {
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) {
    }
}
