package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.DeliveryMethodDao;
import com.example.bookshopwebapplication.entities.DeliveryMethod;

import java.util.List;

public class DeliveryMethodService {
    private final DeliveryMethodDao deliveryMethodDao;
    public DeliveryMethodService() {
        this.deliveryMethodDao = new DeliveryMethodDao();
    }

    public List<DeliveryMethod> getAll() {
        return deliveryMethodDao.getAll();  
    }
}
