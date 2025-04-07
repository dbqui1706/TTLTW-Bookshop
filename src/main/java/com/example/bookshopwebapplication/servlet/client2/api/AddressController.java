package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.entities.UserAddress;
import com.example.bookshopwebapplication.service.UserAddressService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
    name = "AddressController",
    urlPatterns = {
            "/api/address",
            "/api/address/add",
            "/api/address/update",
            "/api/address/delete",
    }
)
public class AddressController extends HttpServlet {
    private final UserAddressService userAddressService = new UserAddressService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long userId = req.getAttribute("userId") != null ? (Long) req.getAttribute("userId") : -1L;
            JsonUtils.out(
                    resp,
                    userAddressService.findByUser(userId),
                    HttpServletResponse.SC_OK
            );
        }catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String uri = req.getRequestURI();
            switch (uri) {
                case "/api/address/add":
                    addAddress(req, resp);
                    break;
                case "/api/address/update":
                    updateAddress(req, resp);
                    break;
                case "/api/address/delete":
                    break;
            }
        }catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void updateAddress(HttpServletRequest req, HttpServletResponse resp) {
        try{
            UserAddress userAddress = JsonUtils.get(req, UserAddress.class);
            userAddressService.update(userAddress);

            JsonUtils.out(
                    resp,
                    "Cập nhật địa chỉ thành công",
                    HttpServletResponse.SC_OK
            );
        } catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void addAddress(HttpServletRequest req, HttpServletResponse resp) {
        try {
            UserAddress userAddress = JsonUtils.get(req, UserAddress.class);
            Long id = userAddressService.insert(userAddress);

            JsonUtils.out(
                    resp,
                    "Thêm địa chỉ thành công",
                    HttpServletResponse.SC_OK
            );
        }catch (Exception e) {
            JsonUtils.out(
                    resp,
                    e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
