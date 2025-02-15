package com.example.bookshopwebapplication.service._interface;

import com.example.bookshopwebapplication.dto.OrderInfoDto;

import java.util.Optional;

public interface IOrderInfoService extends IService<OrderInfoDto>{
    public Optional<OrderInfoDto> getByOrderId(Long orderId);
}
