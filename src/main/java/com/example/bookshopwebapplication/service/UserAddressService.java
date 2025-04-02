package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.UserAddressDao;
import com.example.bookshopwebapplication.entities.UserAddress;

import java.util.List;

public class UserAddressService {
    private final UserAddressDao userAddressDao;

    public UserAddressService() {
        this.userAddressDao = new UserAddressDao();
    }

    public Long insert(UserAddress userAddress) {
        Long id = userAddressDao.save(userAddress);
        return id;
    }

    public boolean update(UserAddress userAddress) {
        try {
            userAddressDao.update(userAddress);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long id) {
        try {
            userAddressDao.delete(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserAddress> findByUser(Long userId) {
        return userAddressDao.findByUser(userId);
    }
}
