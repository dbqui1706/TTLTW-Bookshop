package com.example.bookshopwebapplication.servlet.client;

import com.example.bookshopwebapplication.dto.*;
import com.example.bookshopwebapplication.message.Message;
import com.example.bookshopwebapplication.network.*;
import com.example.bookshopwebapplication.service.*;
import com.example.bookshopwebapplication.utils.JsonUtils;
import com.example.bookshopwebapplication.utils.Protector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//@WebServlet(value = {"/order-info", "/order-fetch-data", "/order-submit"})
public class OrderInformation extends HttpServlet {
    private final ProductService productService = new ProductService();
    private OrderInfoRequest orderInfoRequest;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/order-fetch-data".equals(request.getRequestURI())) {
            System.out.println(orderInfoRequest);
            JsonUtils.out(response, orderInfoRequest, HttpServletResponse.SC_OK);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/client/orderInfo.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getRequestURI().equals("/order-submit")) {
            handleOrderSubmit(request, response);
            return;
        }

        // 1. Lấy thông tin OrderRequest từ JSON trong request
        OrderRequest orderRequest = JsonUtils.get(request, OrderRequest.class);
        System.out.println(orderRequest);

        UserDto userSession = (UserDto) request.getSession().getAttribute("currentUser");

        // Lấy các sản phẩm từ OrderItemRequest
        List<OrderItemRequest> orderItemRequest = orderRequest.orderItems();

        // 2. Lấy thông tin sản phẩm từ database
        List<ProductDto> productDTOs = orderItemRequest.stream()
                .map(orderItem -> productService.getById(orderItem.productId()).get())
                .toList();
        // 3. Tạo danh sách ProductRequestForOrderInfo
        List<ProductRequestForOrderInfo> orderItemRequests = new ArrayList<>();
        for (int i = 0; i < productDTOs.size(); i++) {
            long id = productDTOs.get(i).getId();
            String name = productDTOs.get(i).getName();
            double price = productDTOs.get(i).getPrice();
            double discount = productDTOs.get(i).getDiscount();
            int quantity = orderItemRequest.get(i).quantity();
            String image = productDTOs.get(i).getImageName();
            orderItemRequests.add(new ProductRequestForOrderInfo(id, name, quantity, price * (1 - discount / 100), discount, image));
        }

        // 4. Tạo OrderInfoRequest để đẩy qua front-end
        orderInfoRequest = new OrderInfoRequest(
                userSession.getId(),
                orderRequest.cartId(),
                userSession.getFullName(),
                userSession.getAddress(),
                userSession.getPhoneNumber(),
                userSession.getEmail(),
                orderItemRequests,
                orderItemRequests.stream().map(
                        product -> product.price() * product.quantity() * (1 - product.discount() / 100)
                ).reduce(0.0, Double::sum),
                1,
                15000
        );
    }

    private void handleOrderSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Lấy thông tin OrderRequest từ JSON trong request
        OrderInfoPost orderInfoPost = JsonUtils.get(request, OrderInfoPost.class);
        String signature = orderInfoPost.signature();
        System.out.println(new String(orderInfoPost.toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        OrderHashService orderHashService = new OrderHashService();

        UserService userService = new UserService();
        UserDto userDto = userService.getById(orderInfoPost.userId()).get();
        if (signature != null) {
            boolean isVerified = orderHashService.verify(userDto.getId(), orderInfoPost.jsonData(), signature);
            if (!isVerified) {
                JsonUtils.out(
                        response,
                        new Message(403, "Chữ ký không hợp lệ!"),
                        HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // 2. Xử lý đơn hàng
        Long cartId = orderInfoPost.cartId();

        OrderNetwork orderNetwork = orderInfoPost.order();
        List<ProductRequestForOrderInfo> orderItemsResponse = orderNetwork.orderItems();

        OrderService orderService = new OrderService();

        OrderDto orderDto = new OrderDto();
        orderDto.setUser(userDto);
        orderDto.setStatus(1);
        orderDto.setDeliveryMethod(orderNetwork.deliveryMethod());
        orderDto.setDeliveryPrice(orderNetwork.deliveryPrice());
        if (signature != null) {
            orderDto.setIsVerified(1);
        } else {
            orderDto.setIsVerified(0);
        }
        OrderDto orderID = orderService.insert(orderDto).get();

        String successMessage = "Đã đặt hàng thành công!";
        String errorMessage = "Đã có lỗi truy vấn!";

        Runnable doneFunction = () -> JsonUtils.out(
                response,
                new Message(200, successMessage),
                HttpServletResponse.SC_OK);
        Runnable failFunction = () -> JsonUtils.out(
                response,
                new Message(404, errorMessage),
                HttpServletResponse.SC_NOT_FOUND);

        if (orderID != null) {
            List<OrderItemDto> orderItems = new ArrayList<>();
            for (ProductRequestForOrderInfo orderItemRequest : orderItemsResponse) {
                OrderItemDto orderItem = new OrderItemDto();
                orderItem.setPrice(orderItemRequest.price());
                orderItem.setDiscount(orderItemRequest.discount());
                orderItem.setQuantity(orderItemRequest.quantity());
                orderItem.setCreatedAt(orderID.getCreatedAt());
                orderItem.setOrder(orderID);
                orderItem.setProduct(productService.getById(orderItemRequest.productId()).get());
                orderItems.add(orderItem);
            }
            OrderItemService orderItemService = new OrderItemService();
            Protector.of(() -> {
                        orderItemService.bulkInsert(orderItems);
                        if (cartId != -1) {
                            // 1. Xóa cart item có có productID và cartID
                            CartItemService cartItemService = new CartItemService();
                            for (ProductRequestForOrderInfo orderItemRequest : orderItemsResponse) {
                                cartItemService.deleteByCartIdAndProductId(cartId, orderItemRequest.productId());
                            }
                        }
                        // TIến hành insert orderInfo
                        OrderInfoService orderInfoService = new OrderInfoService();
                        OrderInfoDto orderInfoDto = new OrderInfoDto();
                        orderInfoDto.setOrder(orderID);
                        orderInfoDto.setReceiver(orderInfoPost.receiver());
                        orderInfoDto.setAddressReceiver(orderInfoPost.addressReceiver());
                        orderInfoDto.setEmailReceiver(orderInfoPost.emailReceiver());
                        orderInfoDto.setPhone(orderInfoPost.phone());
                        orderInfoDto.setCity(orderInfoPost.city());
                        orderInfoDto.setDistrict(orderInfoPost.district());
                        orderInfoDto.setWard(orderInfoPost.ward());
                        orderInfoDto.setTotalPrice(orderInfoPost.totalPrice());

                        orderInfoService.insert(orderInfoDto);

                        // Thêm vào bảng order_hash_data
                        if (signature != null) {
                            OrderHashDto orderHashDto = new OrderHashDto();
                            orderHashDto.setOrder(orderID);
                            orderHashDto.setUser(userDto);
                            orderHashDto.setDataHash(signature);
                            orderHashService.insert(orderHashDto);
                        }
                    })

                    .done(r -> doneFunction.run())
                    .fail(e -> failFunction.run());
        } else {
            failFunction.run();
        }
    }
}
