package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.PaymentMethodDao;
import com.example.bookshopwebapplication.entities.PaymentMethod;

import java.util.List;

public class PaymentMethodService {
    private final PaymentMethodDao paymentMethodDao;
    public PaymentMethodService() {
        this.paymentMethodDao = new PaymentMethodDao();
    }

    public List<PaymentMethod> getAll() {
        return paymentMethodDao.getAll();
    }
}
