package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.network.OrderDangerCheckRespone;
import com.example.bookshopwebapplication.service.OrderHashService;
import com.example.bookshopwebapplication.service.OrderService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/orderDangerCheck")
public class OrderDangerCheck extends HttpServlet {
    private OrderHashService orderHashService = new OrderHashService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto user = (UserDto) req.getSession().getAttribute("currentUser");
        if(user != null){
            String inDangerStatus = orderHashService.getUncanceledOrderHaveDangerByUserId(user.getId()).isEmpty() ? "FINE" : "DANGER";
            OrderDangerCheckRespone result = new OrderDangerCheckRespone(inDangerStatus);
            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        }
        else{
            String errorMessage = "Đã có lỗi truy vấn!";
            JsonUtils.out(resp, new Message(404, errorMessage), HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}
