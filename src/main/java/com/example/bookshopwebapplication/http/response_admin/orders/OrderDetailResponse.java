package com.example.bookshopwebapplication.http.response_admin.orders;

import com.example.bookshopwebapplication.http.response.order_detail.DeliveryMethodDTO;
import com.example.bookshopwebapplication.http.response.order_detail.OrderItemDetailDTO;
import com.example.bookshopwebapplication.http.response.order_detail.OrderSummaryDTO;

import java.util.List;

public class OrderDetailResponse {
    private OrderInfoDTO order;
    private DeliveryMethodDTO delivery;
    private PaymentMethodDTO payment;
    private ShippingInfoDTO shipping;
    private PaymentTransactionDTO paymentTransaction;
    private List<OrderItemDetailDTO> items;
    private List<OrderStatusHistoryDTO> statusHistory;
    private List<OrderTimelineDTO> timeline;
    private boolean canCancel;
    private OrderSummaryDTO summary;
}
