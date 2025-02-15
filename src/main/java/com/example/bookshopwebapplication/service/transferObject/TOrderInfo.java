package com.example.bookshopwebapplication.service.transferObject;

import com.example.bookshopwebapplication.dto.OrderInfoDto;
import com.example.bookshopwebapplication.entities.OrderInfo;

public class TOrderInfo implements ITransfer<OrderInfoDto, OrderInfo> {

    @Override
    public OrderInfoDto toDto(OrderInfo entity) {
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        orderInfoDto.setId(entity.getId());
        orderInfoDto.setCity(entity.getCity());
        orderInfoDto.setCreatedAt(entity.getCreatedAt());
        orderInfoDto.setPhone(entity.getPhone());
        orderInfoDto.setDistrict(entity.getDistrict());
        orderInfoDto.setEmailReceiver(entity.getEmailReceiver());
        orderInfoDto.setTotalPrice(entity.getTotalPrice());
        orderInfoDto.setWard(entity.getWard());
        orderInfoDto.setAddressReceiver(entity.getAddressReceiver());
        orderInfoDto.setReceiver(entity.getReceiver());
        orderInfoDto.setUpdatedAt(entity.getUpdatedAt());

        return orderInfoDto;
    }

    @Override
    public OrderInfo toEntity(OrderInfoDto orderInfoDto) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderInfoDto.getOrder().getId());
        orderInfo.setReceiver(orderInfoDto.getReceiver());
        orderInfo.setAddressReceiver(orderInfoDto.getAddressReceiver());
        orderInfo.setEmailReceiver(orderInfoDto.getEmailReceiver());
        orderInfo.setPhone(orderInfoDto.getPhone());
        orderInfo.setCity(orderInfoDto.getCity());
        orderInfo.setDistrict(orderInfoDto.getDistrict());
        orderInfo.setWard(orderInfoDto.getWard());
        orderInfo.setTotalPrice(orderInfoDto.getTotalPrice());
        orderInfo.setCreatedAt(orderInfoDto.getCreatedAt());
        orderInfo.setUpdatedAt(orderInfoDto.getUpdatedAt());
        return orderInfo;
    }

}
