package com.example.bookshopwebapplication.servlet.admin2.api.user;

import com.example.bookshopwebapplication.dao.UserDao;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.service.UserService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet( urlPatterns = {
        "/admin2/api/user/statistic"
})
public class Statistic extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin2/api/user/statistic":
                getUserStatistic(req, resp);
                break;
            default:
                throw new ServletException("Invalid URI: " + uri);
        }
    }

    private void getUserStatistic(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map<String, Object> userStatistic = userService.getStatistic();
            JsonUtils.out(resp, userStatistic, HttpServletResponse.SC_OK);
        }catch (Exception e) {
            JsonUtils .out(
                    resp,
                    new Message(500, "Lỗi truy vấn dữ liệu"),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
