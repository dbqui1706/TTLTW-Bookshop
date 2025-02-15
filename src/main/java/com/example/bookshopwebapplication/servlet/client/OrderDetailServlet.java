package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.dto.OrderInfoDto;
import com.example.bookshopwebapplication.dto.OrderItemDto;
import com.example.bookshopwebapplication.dto.ProductDto;
import com.example.bookshopwebapplication.service.OrderHashService;
import com.example.bookshopwebapplication.service.OrderInfoService;
import com.example.bookshopwebapplication.service.OrderItemService;
import com.example.bookshopwebapplication.service.OrderService;
import com.example.bookshopwebapplication.service.ProductService;
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

@WebServlet("/orderDetail")
public class OrderDetailServlet extends HttpServlet {
    private final OrderItemService orderItemService = new OrderItemService();
    private final OrderService orderService = new OrderService();
    private final ProductService productService = new ProductService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy id của order và đối tượng order từ database theo id này
        long id = Protector.of(() -> Long.parseLong(request.getParameter("id"))).get(0L);
        Optional<OrderDto> orderFromServer = Protector.of(() -> orderService.getById(id)).get(Optional::empty);

        if (orderFromServer.isPresent()) {
            OrderDto order = orderFromServer.get();
            List<OrderItemDto> orderItems = Protector.of(() -> orderItemService.getByOrderId(id)).get(ArrayList::new);
            OrderInfoService orderInfoService = new OrderInfoService();
            OrderHashService orderHashService = new OrderHashService();
            OrderInfoDto orderInfoDto = Protector.of(() -> orderInfoService.getByOrderId(id)).get(Optional::empty).orElseGet(OrderInfoDto::new);
            String verifyStatus = "NONE";
            if(order.getIsVerified() == 1){
                verifyStatus = orderHashService.verifyOrderById(order.getId()) ? "GOOD" : "BAD";
            }

            double tempPrice = 0;

            for (OrderItemDto orderItem : orderItems) {
                tempPrice += orderItem.getPrice() * orderItem.getQuantity();
                orderItem.setProduct(productService.getById(orderItem.getProduct().getId()).orElseGet(ProductDto::new));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            System.out.println(orderInfoDto);
            request.setAttribute("order", order);
            request.setAttribute("orderInfo", orderInfoDto);
            request.setAttribute("totalPrice", orderInfoDto.getTotalPrice());
            request.setAttribute("createdAt", dateFormat.format(order.getCreatedAt()));
            request.setAttribute("tempPrice", tempPrice);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute("verifyStatus", verifyStatus);
            request.getRequestDispatcher("/WEB-INF/views/client/orderDetail.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = Protector.of(() -> Long.parseLong(request.getParameter("id"))).get(0L);
        Protector.of(() -> orderService.cancelOrder(id));
        response.sendRedirect(request.getContextPath() + "/orderDetail?id=" + id);
    }
}
