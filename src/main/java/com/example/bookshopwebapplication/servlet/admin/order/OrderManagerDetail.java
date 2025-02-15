package com.example.bookshopwebapplication.servlet.admin.order;

import com.example.bookshopwebapplication.dto.*;
import com.example.bookshopwebapplication.service.*;
import com.example.bookshopwebapplication.utils.Protector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@WebServlet("/admin/orderManager/detail")
public class OrderManagerDetail extends HttpServlet {
    private final OrderService orderService = new OrderService();
    private final OrderInfoService orderInfoService  = new OrderInfoService();
    private final UserService userService = new UserService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = Protector.of(() -> Long.parseLong(request.getParameter("id"))).get(0L);
        Optional<OrderDto> orderFromServer = Protector.of(() -> orderService.getById(id)).get(Optional::empty);
        if (orderFromServer.isPresent()) {
            OrderDto orderDetail = orderFromServer.get();
            List<OrderItemDto> orderItems = Protector.of(() -> orderItemService.getByOrderId(id)).get(ArrayList::new);
            OrderInfoDto orderInfoDto = Protector.of(() -> orderInfoService.getByOrderId(id))
                    .get(Optional::empty).orElseGet(OrderInfoDto::new);
            Optional<UserDto> userOrder = Protector.of(() -> userService.getById(orderDetail.getUser()
                    .getId())).get(Optional::empty);
            OrderHashService orderHashService = new OrderHashService();
            String verifyStatus = "NONE";
            if(orderDetail.getIsVerified() == 1){
                verifyStatus = orderHashService.verifyOrderById(orderDetail.getId()) ? "GOOD" : "BAD";
            }

            double tempPrice = 0;

            for (OrderItemDto orderItem : orderItems) {
                tempPrice += orderItem.getPrice() * orderItem.getQuantity();
                orderItem.setProduct(productService.getById(orderItem.getProduct().getId()).orElseGet(ProductDto::new));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            System.out.println(orderInfoDto);

            request.setAttribute("userOrder", userOrder);
            request.setAttribute("order", orderDetail);
            request.setAttribute("orderInfo", orderInfoDto);
            request.setAttribute("totalPrice", orderInfoDto.getTotalPrice());
            request.setAttribute("createdAt", dateFormat.format(orderDetail.getCreatedAt()));
            request.setAttribute("tempPrice", tempPrice);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute("verifyStatus", verifyStatus);

            request.getRequestDispatcher("/WEB-INF/views/admin/order/detail.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/orderManager");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}
