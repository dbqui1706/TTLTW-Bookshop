package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.OrderInfoDao;
import com.example.bookshopwebapplication.dto.OrderInfoDto;
import com.example.bookshopwebapplication.entities.OrderInfo;
import com.example.bookshopwebapplication.service._interface.IOrderInfoService;
import com.example.bookshopwebapplication.service.transferObject.TOrderInfo;

import java.util.List;
import java.util.Optional;

public class OrderInfoService implements IOrderInfoService {
    private final OrderInfoDao orderInfoDao = new OrderInfoDao();
    private final TOrderInfo tOrderInfo = new TOrderInfo();

    @Override
    public Optional<OrderInfoDto> insert(OrderInfoDto orderInfoDto) {
        Long id = orderInfoDao.save(tOrderInfo.toEntity(orderInfoDto));
        return this.getById(id);
    }

    @Override
    public Optional<OrderInfoDto> update(OrderInfoDto orderInfoDto) {
        return Optional.empty();
    }

    @Override
    public void delete(Long[] ids) {

    }

    @Override
    public Optional<OrderInfoDto> getById(Long id) {
        Optional<OrderInfo> orderInfo = orderInfoDao.getById(id);
        if (orderInfo.isPresent()) {
            OrderInfoDto orderInfoDto = tOrderInfo.toDto(orderInfo.get());
            orderInfoDto.setOrder(OrderService.getInstance().getById(orderInfo.get().getOrderId()).get());
            return Optional.of(orderInfoDto);
        }
        return Optional.empty();
    }

    @Override
    public List<OrderInfoDto> getPart(Integer limit, Integer offset) {
        return List.of();
    }

    @Override
    public List<OrderInfoDto> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        return List.of();
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Optional<OrderInfoDto> getByOrderId(Long orderId) {
        Optional<OrderInfo> orderInfo = orderInfoDao.getByOrderId(orderId);
        if (orderInfo.isPresent()) {
            OrderInfoDto orderInfoDto = tOrderInfo.toDto(orderInfo.get());
            orderInfoDto.setOrder(OrderService.getInstance().getById(orderInfo.get().getOrderId()).get());
            return Optional.of(orderInfoDto);
        }
        return Optional.empty();
    }
}
