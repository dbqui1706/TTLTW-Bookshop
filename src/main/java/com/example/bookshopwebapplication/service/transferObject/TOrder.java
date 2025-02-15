package com.example.bookshopwebapplication.service.transferObject;

import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.entities.Order;

public class TOrder implements ITransfer<OrderDto, Order> {
    @Override
    public OrderDto toDto(Order entity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(entity.getId());
        orderDto.setStatus(entity.getStatus());
        orderDto.setDeliveryMethod(entity.getDeliveryMethod());
        orderDto.setDeliveryPrice(entity.getDeliveryPrice());
        orderDto.setIsVerified(entity.getIsVerified());
        orderDto.setIsTampered(entity.getIsTampered());
        orderDto.setCreatedAt(entity.getCreatedAt());
        orderDto.setUpdatedAt(entity.getUpdatedAt());
        return orderDto;
    }

    @Override
    public Order toEntity(OrderDto dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setUserId(dto.getUser().getId());
        order.setStatus(dto.getStatus());
        order.setDeliveryMethod(dto.getDeliveryMethod());
        order.setDeliveryPrice(dto.getDeliveryPrice());
        order.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : 0);
        order.setIsTampered(dto.getIsTampered() != null ? dto.getIsTampered() : 0);
        order.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : null);
        order.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : null);
        return order;
    }
}
