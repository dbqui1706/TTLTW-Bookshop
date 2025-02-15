package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.OrderDao;
import com.example.bookshopwebapplication.dao.OrderHashDao;
import com.example.bookshopwebapplication.dao.OrderInfoDao;
import com.example.bookshopwebapplication.dao.OrderItemDao;
import com.example.bookshopwebapplication.dao.UserKeysDao;
import com.example.bookshopwebapplication.dto.OrderDto;
import com.example.bookshopwebapplication.dto.OrderHashDto;
import com.example.bookshopwebapplication.entities.Order;
import com.example.bookshopwebapplication.entities.OrderHash;
import com.example.bookshopwebapplication.entities.OrderInfo;
import com.example.bookshopwebapplication.entities.OrderItem;
import com.example.bookshopwebapplication.entities.UserKeys;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OrderHashService {
    private final OrderHashDao orderHashDao = new OrderHashDao();
    private final OrderDao orderDao = new OrderDao();

    public Optional<OrderHashDto> insert(OrderHashDto dto){
        OrderHash orderHash = new OrderHash();
        orderHash.setOrderId(dto.getOrder().getId());
        orderHash.setUserId(dto.getUser().getId());
        orderHash.setDataHash(dto.getDataHash());
        UserKeys userKeys = UserService.getInstance().isExistKey(dto.getUser().getId());
        String publicKey = "";
        if(userKeys != null) publicKey = userKeys.getPublicKey();
        orderHash.setPublicKey(publicKey);
        Long id = orderHashDao.save(orderHash);
        return this.getById(id);
    }

    public Optional<OrderHashDto> getById(Long id) {
        Optional<OrderHash> optional = orderHashDao.getById(id);
        if (optional.isPresent()) {
            OrderHash orderHash = optional.get();
            OrderHashDto orderHashDto = new OrderHashDto();
            orderHashDto.setId(orderHash.getId());
            orderHashDto.setOrder(OrderService.getInstance().getById(orderHash.getOrderId()).get());
            orderHashDto.setUser(UserService.getInstance().getById(orderHash.getUserId()).get());
            orderHashDto.setDataHash(orderHash.getDataHash());
            orderHashDto.setCreatedAt(orderHash.getCreatedAt());
            orderHashDto.setUpdatedAt(orderHash.getUpdatedAt());
            return Optional.of(orderHashDto);
        }
        return Optional.empty();
    }

    public boolean verifyOrderById(Long id){
        Optional<OrderHash> optional = orderHashDao.getByOrderId(id);
        if (!optional.isPresent()) return false;
        String signature = optional.get().getDataHash();

        JsonObject finalJSON = new JsonObject();
        OrderInfoDao orderInfoDao = new OrderInfoDao();
        OrderItemDao orderItemDao =  new OrderItemDao();
        UserKeysDao userKeysDao = new UserKeysDao();
        KeyService keyService = new KeyService();
        keyService.initVariable();

        Order order = orderDao.getById(id).get();
        OrderInfo orderInfo = orderInfoDao.getByOrderId(id).get();
        List<OrderItem> orderItems = orderItemDao.getByOrderId(id);
        orderItems.sort(Comparator.comparingLong(OrderItem::getProductId));

        String publicKey = optional.get().getPublicKey();
        finalJSON.addProperty("userId", String.valueOf(order.getUserId()));
        finalJSON.addProperty("receiver", orderInfo.getReceiver());
        finalJSON.addProperty("emailReceiver", orderInfo.getEmailReceiver());
        finalJSON.addProperty("addressReceiver", orderInfo.getAddressReceiver());
        finalJSON.addProperty("phone", orderInfo.getPhone());
        finalJSON.addProperty("city", orderInfo.getCity());
        finalJSON.addProperty("district", orderInfo.getDistrict());
        finalJSON.addProperty("ward", orderInfo.getWard());
        finalJSON.addProperty("totalPrice", orderInfo.getTotalPrice().intValue());

        JsonObject orderJSON = new JsonObject();
        orderJSON.addProperty("deliveryMethod", String.valueOf(order.getDeliveryMethod()));
        orderJSON.addProperty("deliveryPrice", order.getDeliveryPrice().intValue());
        JsonArray orderItemsJSON = new JsonArray();
        for(OrderItem item : orderItems){
            JsonObject itemJSON = new JsonObject();
            itemJSON.addProperty("productId", item.getProductId());
            itemJSON.addProperty("price", item.getPrice().intValue());
            itemJSON.addProperty("discount", item.getDiscount().intValue());
            itemJSON.addProperty("quantity", item.getQuantity());
            orderItemsJSON.add(itemJSON);
        }
        orderJSON.add("orderItems", orderItemsJSON);
        finalJSON.add("order", orderJSON);

        // Thực hiện verify dựa trên dữ liệu JSON
        Key pub = keyService.parseKey(publicKey);
        keyService.setPublicKey((PublicKey) pub);
        return keyService.verify(finalJSON.toString(), Base64.getDecoder().decode(signature));
    }

    public boolean verify(Long userID, String data, String signature){
        Optional<UserKeys> optional = new UserKeysDao().getByUserId(userID);
        if(!optional.isPresent()) return false;
        KeyService keyService = new KeyService();
        keyService.initVariable();
        Key pub = keyService.parseKey(optional.get().getPublicKey());
        keyService.setPublicKey((PublicKey) pub);
        return keyService.verify(data, Base64.getDecoder().decode(signature));
    }

    public List<Long> getUncanceledOrderHaveDangerByUserId(Long userID){
        List<Long> result = new ArrayList<>();
        OrderService orderService = new OrderService();
        int totalOrders = orderService.countByUserId(userID);
        List<OrderDto> orders = orderService.getPart(totalOrders, 0);
        for(OrderDto order : orders){
            if(order.getIsVerified() == 1 && order.getStatus() != 3){
                if(!verifyOrderById(order.getId())){
                    result.add(order.getId());
                }
            }
        }
        return result;
    }
}
