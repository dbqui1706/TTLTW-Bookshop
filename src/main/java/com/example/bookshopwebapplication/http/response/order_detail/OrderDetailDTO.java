package com.example.bookshopwebapplication.http.response.order_detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
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