package com.example.bookshopwebapplication.servlet.client2.api;


import com.example.bookshopwebapplication.entities.PaymentMethod;
import com.example.bookshopwebapplication.service.PaymentMethodService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "PaymentMethodController",
        urlPatterns = {"/api/payment-methods"}
)
public class PaymentMethodController extends HttpServlet {
    private final PaymentMethodService paymentMethodService = new PaymentMethodService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<PaymentMethod> result = paymentMethodService.getAll();
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
