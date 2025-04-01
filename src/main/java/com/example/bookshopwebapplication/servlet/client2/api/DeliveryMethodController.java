package com.example.bookshopwebapplication.servlet.client2.api;

import com.example.bookshopwebapplication.entities.DeliveryMethod;
import com.example.bookshopwebapplication.service.DeliveryMethodService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DeliveryMethodController", urlPatterns = {
        "/api/delivery-methods"
})
public class DeliveryMethodController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DeliveryMethodService deliveryMethodService = new DeliveryMethodService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lấy danh sách phương thức giao hàng
        try {
            List<DeliveryMethod> result = deliveryMethodService.getAll();
            JsonUtils.out(
                    resp,
                    result,
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
