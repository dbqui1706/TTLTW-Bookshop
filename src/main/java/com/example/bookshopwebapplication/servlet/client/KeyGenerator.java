package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.UserKeys;
import com.example.bookshopwebapplication.service.KeyService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.Protector;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "KeyGenerator", urlPatterns = {"/keys", "/key-generate", "/key-cancel", "/key-import"})
public class KeyGenerator extends HttpServlet {
    private final HashMap<Long, String[]> keys = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        // Handel for cancel key
        if (url.startsWith("/key-cancel")) {
            // if user can verify email
            Optional<String> email = Optional.ofNullable(req.getParameter("email"));
            Optional<String> code = Optional.ofNullable(req.getParameter("code"));
            boolean isNotSendEmail = (email.isPresent() &&
                    code.isPresent() &&
                    EmailUtils.isValidVerificationCode(email.get(), code.get()));
            if (isNotSendEmail) {
                UserDto user = (UserDto) req.getSession().getAttribute("currentUser");
                // 3.1
                req.getSession().removeAttribute("publicKey");
                req.removeAttribute("INFO");

                UserService userService = new UserService();
                UserKeys userKeysDB = userService.isExistKey(user.getId());
                if (userKeysDB != null) {
                    userKeysDB.setIsActive(0);
                }
                Protector.of(() -> userService.updateKey(userKeysDB))
                        .done(e -> {
                            req.setAttribute("INFO", "Hủy khóa thành công!");
                        })
                        .fail(e -> {
                            req.setAttribute("error", "Có lỗi xảy ra khi lưu khóa vui lòng thử lại sau!");
                        });
            }
            // 3.2 send redirect to /key
        } else {
            // 1. Get the user id from the session
            UserDto user = (UserDto) req.getSession().getAttribute("currentUser");
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/signin");
                return;
            }
            UserService userService = new UserService();
            UserKeys userKeysDB = userService.isExistKey(user.getId());
            boolean response = userKeysDB != null;

            // Sử lý sự kiện khi order
            boolean checkKey = req.getParameter("checkKey") != null;
            if (checkKey) {
                if (response) {
                    JsonUtils.out(resp, null, HttpServletResponse.SC_OK);
                } else {
                    JsonUtils.out(resp, null, HttpServletResponse.SC_NOT_FOUND);
                }
                return;
            }
            if (response) {
                req.setAttribute("publicKey", userKeysDB.getPublicKey());
            }
            // 2. Get the user keys by user id
        }
        req.getRequestDispatcher("/WEB-INF/views/client/keys.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Get the user id from the session
        UserDto user = (UserDto) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/signin");
            return;
        }
        // 2. Generate a new key pair
        if (req.getRequestURI().equals("/key-generate")) {
            // 2.1
            keyGeneration(req, resp, user);
            // 2.2 send redirect to /key
            return;
        }
        // 3. Save the new key pair to the database
        if (req.getRequestURI().startsWith("/key-cancel")) {
            // if user can verify email
            EmailUtils.sendEmailCancelKey(user, UUID.randomUUID().toString());
        }
        if (req.getRequestURI().equals("/key-import")) {
            String publicKeyString = req.getParameter("publicKey");
            System.out.println(publicKeyString);
            try {
                KeyService keyService = new KeyService();
                PublicKey publicKey = keyService.pemToPublicKey(publicKeyString);
            } catch (Exception e) {
                req.setAttribute("error", "Đã có sự cố xảy ra khi cập nhật khóa, có thể là do khóa của bạn không chuẩn PEM");
                req.setAttribute("publicKey", publicKeyString);
                req.getRequestDispatcher("/WEB-INF/views/client/keys.jsp").forward(req, resp);
                return;
            }
            UserService userService = new UserService();
            UserKeys userKeysDB = userService.isExistKey(user.getId());
            if (userKeysDB != null) {
                userKeysDB.setIsActive(0);
                userService.updateKey(userKeysDB);
            }
            UserKeys userKeys = new UserKeys();
            userKeys.setUserId(user.getId());
            userKeys.setPublicKey(publicKeyString);
            userKeys.setIsActive(1);
            Protector.of(() -> userService.saveKey(userKeys))
                    .done(e -> {
                        req.setAttribute("INFO", "Đã cập nhật khóa");
                    })
                    .fail(e -> {
                        req.setAttribute("error", "Có lỗi xảy ra khi lưu khóa vui lòng thử lại sau!");
                    });
            req.setAttribute("publicKey", publicKeyString);
            req.getRequestDispatcher("/WEB-INF/views/client/keys.jsp").forward(req, resp);
        }
    }

    private void exportKey(HttpServletRequest req, HttpServletResponse resp, UserDto user) throws IOException, RuntimeException {

    }

    private void keyGeneration(HttpServletRequest req, HttpServletResponse resp, UserDto user) throws ServletException, IOException {
        // Kiểm tra xem người dùng đã có khóa chưa nếu có thì không tạo mới
        UserService userService = new UserService();
        UserKeys userKeysDB = userService.isExistKey(user.getId());
        if (userKeysDB != null) {
            req.setAttribute("publicKey", userKeysDB.getPublicKey());
            req.setAttribute("INFO", "Bạn đã có khóa rồi!");
            req.getRequestDispatcher("/WEB-INF/views/client/keys.jsp").forward(req, resp);
            return;
        }

        KeyService keyService = new KeyService();
        keyService.initVariable();
        keyService.generateKeyPair();
        PublicKey publicKey = keyService.getPublicKey();
        PrivateKey privateKey = keyService.getPrivateKey();

        String pub_base64 = keyService.publicKeyToPEM(publicKey);
        String pri_base64 = keyService.privateKeyToPEM(privateKey);

        // Lưu public key vào database
        UserKeys userKeys = new UserKeys();
        userKeys.setUserId(user.getId());
        userKeys.setPublicKey(pub_base64);
        userKeys.setIsActive(1);

        Protector.of(() -> userService.saveKey(userKeys))
                .fail(e -> {
                    req.setAttribute("error", "Có lỗi xảy ra khi lưu khóa vui lòng thử lại sau!");
                });

        String[] keys = {pub_base64, pri_base64};
        this.keys.put(user.getId(), keys);
        req.getSession().setAttribute("publicKey", this.keys.get(user.getId())[0]);

        // Save private key vào máy người dùng
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment;filename=%s_private_key.pem".formatted(user.getUsername()));
        resp.getOutputStream().write(this.keys.get(user.getId())[1].getBytes());
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
        resp.sendRedirect(req.getContextPath() + "/keys");
    }
}
