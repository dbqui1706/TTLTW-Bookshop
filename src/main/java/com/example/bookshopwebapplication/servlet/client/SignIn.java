package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.service.PermissionService;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.EncodePassword;
import com.example.bookshopwebapplication.utils.Protector;
import com.example.bookshopwebapplication.utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/signin")
public class SignIn extends HttpServlet {
    private final PermissionService permissionService = new PermissionService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/client/signin.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> values = new HashMap<>();
        Map<String, List<String>> violations = new HashMap<>();
        int sumOfViolations;

        values.put("username", request.getParameter("username"));
        values.put("password", request.getParameter("password"));


        // Kiểm tra xem người dùng có tồn tại không
        Optional<UserDto> userFromServer = UserService.getInstance().getByUsername(values.get("username"));

        // Thực hiện xác minh tên tài khoản
        violations.put("usernameViolations", Validator.of(values.get("username"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .isAtMostOfLength(25)
                .isExistent(userFromServer.isPresent(), "Tên đăng nhập")
                .toList());

        //xác minh mật khẩu
        violations.put("passwordViolations", Validator.of(values.get("password"))
                .isNotNullAndEmpty()
                .isNotBlankAtBothEnds()
                .isAtMostOfLength(32)
                .changeTo(EncodePassword.hash(values.get("password")))
                .isEqualTo(userFromServer.map(UserDto::getPassword).orElse(""), "Mật khẩu")
                .toList());

        sumOfViolations = violations.values().stream().mapToInt(List::size).sum();

        if (sumOfViolations == 0 && userFromServer.isPresent()) {
            request.getSession().setAttribute("currentUser", userFromServer.get());

            // Lưu quyền vào session để hạn chế việc truy vấn cơ sở dữ liệu
            List<String> permissions = permissionService.getUserPermissions(userFromServer.get().getId());
            request.getSession().setAttribute("permissions", permissions);

            // Lưu trạng thái người dùng


            // Kiểm tra xem session của người dùng đã tồn tại chưa nếu chưa thì save vào database và cache
            // Lưu thông tin vào bảng user_session
            String deviceInfo = request.getHeader("User-Agent");
            String ip = request.getRemoteAddr();
            String sessionId = request.getSession().getId();
            System.out.println(
                    "deviceInfo = " + deviceInfo + "\n" +
                            "ip = " + ip + "\n" +
                            "sessionId = " + sessionId
            );

            // Lưu thông tin vào bảng user_session
            UserService.getInstance().saveUserSession(
                    sessionId,
                    ip,
                    deviceInfo,
                    userFromServer.get().getId()
            );
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            request.setAttribute("values", values);
            request.setAttribute("violations", violations);
            request.getRequestDispatcher("/WEB-INF/views/client/signin.jsp").forward(request, response);
        }
    }
}