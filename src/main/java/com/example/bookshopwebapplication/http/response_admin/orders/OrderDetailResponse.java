package com.example.bookshopwebapplication.http.response_admin.orders;

import com.example.bookshopwebapplication.http.response.order_detail.OrderItemDetailDTO;
import com.example.bookshopwebapplication.http.response.order_detail.OrderSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private OrderInfoDTO order;
    private DeliveryMethodDTO delivery;
    private PaymentMethodDTO payment;
    private ShippingInfoDTO shipping;
    private PaymentTransactionDTO paymentTransaction;
    private List<OrderItemDetailDTO> items;
    private List<OrderStatusHistoryDTO> timeLine;
    private boolean canCancel;
    private OrderSummaryDTO summary;
}
