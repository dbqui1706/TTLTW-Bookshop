package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.*;
import com.example.bookshopwebapplication.entities.*;
import com.example.bookshopwebapplication.exceptions.BadRequestException;
import com.example.bookshopwebapplication.http.request.order.CartItemRequest;
import com.example.bookshopwebapplication.http.request.order.DeliveryAddressRequest;
import com.example.bookshopwebapplication.http.request.order.OrderCreateRequest;
import com.example.bookshopwebapplication.http.response.order.*;
import com.example.bookshopwebapplication.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OderService2 {
    private final CouponDao couponDAO;
    private final UserDao userDAO;
    private final PaymentMethodDao paymentMethodDAO;
    private final DeliveryMethodDao deliveryMethodDAO;
    private final OrderDao2 orderDAO;
    private final OderShippingDao orderShippingDAO;
    private final OrderItemDao2 orderItemDAO;
    private final ProductDao productDAO;
    private final PaymentTransactionDao paymentTransactionDAO;
    private final OrderStatusHistoryDao orderStatusHistoryDAO;

    public OderService2() {
        this.couponDAO = new CouponDao();
        this.userDAO = new UserDao();
        this.paymentMethodDAO = new PaymentMethodDao();
        this.deliveryMethodDAO = new DeliveryMethodDao();
        this.orderDAO = new OrderDao2();
        this.orderShippingDAO = new OderShippingDao();
        this.orderItemDAO = new OrderItemDao2();
        this.productDAO = new ProductDao();
        this.paymentTransactionDAO = new PaymentTransactionDao();
        this.orderStatusHistoryDAO = new OrderStatusHistoryDao();
    }

    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
        // Xác thực thông tin đơn hàng
        validateOrderRequest(orderCreateRequest);

        // Lấy thông tin người dùng
        User user = userDAO.getById(orderCreateRequest.getUserId()).orElseThrow(
                () -> new BadRequestException("Lỗi không tìm thấy người dùng")
        );
        // Lấy thông tin phương thức thanh toán
        PaymentMethod paymentMethod = paymentMethodDAO.findByCode(orderCreateRequest.getPaymentMethod())
                .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy phương thức thanh toán"));

        // Lấy thông tin phương thức giao hàng
        DeliveryMethod deliveryMethod = deliveryMethodDAO.findById(orderCreateRequest.getDeliveryMethod())
                .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy phương thức giao hàng"));

        // Áp dụng mã giảm giá nếu có
        BigDecimal discountAmount = processDiscount(orderCreateRequest);

        // Tạo mã đơn hàng (e.g., ORD-20250403-XXXXX)
        String orderCode = generateOrderCode();

        // Tạo đơn hàng
        Order2 order = createOrderEntity(
                orderCreateRequest,
                orderCode,
                user.getId(),
                deliveryMethod.getId(),
                paymentMethod.getId(),
                discountAmount
        );
        Long orderId = orderDAO.save(order);
        order.setId(orderId);

        // Tạo thông tin vân chuyển cho đơn hàng
        OrderShipping shipping = createShippingEntity(orderId, orderCreateRequest.getDeliveryAddress(), user);
        Long shippingId = orderShippingDAO.save(shipping);
        shipping.setId(shippingId);

        // Tạo các order item cho đơn hàng
        List<OrderItem2> orderItems = createOrderItems(orderId, orderCreateRequest.getCartItems());
        for (OrderItem2 item : orderItems) {
            Long itemId = orderItemDAO.save(item);
            item.setId(itemId);

            // Update product quantity
            Product product = productDAO.getById(item.getProductId())
                    .orElseThrow(() -> new BadRequestException(
                            "Không tìm thấy sản phẩm: " + item.getProductName()
                                    + " | id: " + item.getProductId()));

            short newQuantity = (short) (product.getQuantity() - item.getQuantity());
            if (newQuantity < 0) {
                throw new BadRequestException("Không đủ sản phẩm trong kho: " + product.getName());
            }
            productDAO.updateProductQuantity(item.getProductId(), newQuantity);
        }

        // Tạo giao dịch thanh toán
        PaymentTransaction transaction = createPaymentTransaction(orderId, paymentMethod.getId(),
                order.getTotalAmount(), orderCreateRequest.getPaymentMethod());
        Long transactionId = paymentTransactionDAO.save(transaction);
        transaction.setId(transactionId);

        // Tạo lịch sử trạng thái đơn hàng
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("pending")
                .note("Order created")
                .changedBy(user.getId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        // Cập nhập lại sô lượng coupon nếu có
        if (orderCreateRequest.getCouponCode() != null && !orderCreateRequest.getCouponCode().isEmpty()) {
            couponDAO.incrementUsageCount(orderCreateRequest.getCouponCode());
        }

        // Build và trả về OrderResponse
        return buildOrderResponse(order, orderItems, shipping, transaction, deliveryMethod, paymentMethod);
    }

    private OrderResponse buildOrderResponse(Order2 order, List<OrderItem2> orderItems,
                                             OrderShipping shipping, PaymentTransaction transaction,
                                             DeliveryMethod deliveryMethod, PaymentMethod paymentMethod) {
        // Convert items to response objects
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .basePrice(item.getBasePrice())
                        .discountPercent(item.getDiscountPercent())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        // Create shipping response
        OrderShippingResponse shippingResponse = OrderShippingResponse.builder()
                .id(shipping.getId())
                .receiverName(shipping.getReceiverName())
                .receiverEmail(shipping.getReceiverEmail())
                .receiverPhone(shipping.getReceiverPhone())
                .addressLine1(shipping.getAddressLine1())
                .addressLine2(shipping.getAddressLine2())
                .city(shipping.getCity())
                .district(shipping.getDistrict())
                .ward(shipping.getWard())
                .postalCode(shipping.getPostalCode())
                .shippingNotes(shipping.getShippingNotes())
                .trackingNumber(shipping.getTrackingNumber())
                .shippingCarrier(shipping.getShippingCarrier())
                .build();

        // Create delivery method response
        DeliveryMethodResponse deliveryMethodResponse = DeliveryMethodResponse.builder()
                .id(deliveryMethod.getId())
                .name(deliveryMethod.getName())
                .description(deliveryMethod.getDescription())
                .price(BigDecimal.valueOf(deliveryMethod.getPrice()))
                .estimatedDays(deliveryMethod.getEstimatedDays())
                .icon(deliveryMethod.getIcon())
                .build();

        // Create payment method response
        PaymentMethodResponse paymentMethodResponse = PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .name(paymentMethod.getName())
                .code(paymentMethod.getCode())
                .description(paymentMethod.getDescription())
                .icon(paymentMethod.getIcon())
                .requiresConfirmation(paymentMethod.getRequireConfirmation())
                .processingFee(BigDecimal.valueOf(paymentMethod.getProcessingFee()))
                .build();

        // Create payment transaction response
        PaymentTransactionResponse transactionResponse = PaymentTransactionResponse.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .paymentProviderRef(transaction.getPaymentProviderRef())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .paymentDate(transaction.getPaymentDate())
                .build();

        // Build and return the final order response
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUserId())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .deliveryPrice(order.getDeliveryPrice())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .couponCode(order.getCouponCode())
                .isVerified(order.getIsVerified())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .deliveryMethod(deliveryMethodResponse)
                .paymentMethod(paymentMethodResponse)
                .items(itemResponses)
                .shipping(shippingResponse)
                .transaction(transactionResponse)
                .build();
    }

    private PaymentTransaction createPaymentTransaction(Long orderId, Long paymentMethodId,
                                                        BigDecimal amount, String paymentMethodCode) {
        String status = "pending";

        return PaymentTransaction.builder()
                .orderId(orderId)
                .paymentMethodId(paymentMethodId)
                .amount(amount)
                .transactionCode(null)
                .paymentProviderRef(null)
                .status(status)
                .paymentDate(null)
                .note(null)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }


    private List<OrderItem2> createOrderItems(Long orderId, List<CartItemRequest> cartItems) {
        List<OrderItem2> orderItems = new ArrayList<>();

        for (CartItemRequest cartItem : cartItems) {
            OrderItem2 item = OrderItem2.builder()
                    .orderId(orderId)
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getName())
                    .productImage(cartItem.getImage())
                    .basePrice(cartItem.getOriginalPrice())
                    .discountPercent(BigDecimal.valueOf(cartItem.getDiscount()))
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            orderItems.add(item);
        }

        return orderItems;
    }

    private OrderShipping createShippingEntity(Long orderId, DeliveryAddressRequest addressRequest, User user) {
        return OrderShipping.builder()
                .orderId(orderId)
                .receiverName(addressRequest.getFullname())
                .receiverEmail(user.getEmail())
                .receiverPhone(addressRequest.getPhone())
                .addressLine1(addressRequest.getAddress())
                .addressLine2(null)
                .city(addressRequest.getProvince())
                .district(addressRequest.getDistrict())
                .ward(addressRequest.getWard())
                .postalCode(null)
                .shippingNotes(null)
                .trackingNumber(null)
                .shippingCarrier(null)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }


    private void validateOrderRequest(OrderCreateRequest request) {
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart items cannot be empty");
        }

        if (request.getDeliveryMethod() == null || request.getDeliveryMethod() <= 0) {
            throw new BadRequestException("Invalid delivery method");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isEmpty()) {
            throw new BadRequestException("Payment method is required");
        }

        if (request.getDeliveryAddress() == null) {
            throw new BadRequestException("Delivery address is required");
        }

        DeliveryAddressRequest address = request.getDeliveryAddress();
        if (address.getFullname() == null || address.getFullname().isEmpty() ||
                address.getPhone() == null || address.getPhone().isEmpty() ||
                address.getAddress() == null || address.getAddress().isEmpty() ||
                address.getProvince() == null || address.getProvince().isEmpty() ||
                address.getDistrict() == null || address.getDistrict().isEmpty() ||
                address.getWard() == null || address.getWard().isEmpty()) {
            throw new BadRequestException("Incomplete delivery address information");
        }
    }

    private BigDecimal processDiscount(OrderCreateRequest request) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Add promotion discount if any
        if (request.getDiscountPromotionAmount() != null && request.getDiscountPromotionAmount().compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = discountAmount.add(request.getDiscountPromotionAmount());
        }

        // Process coupon discount if coupon code provided
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = Optional.ofNullable(couponDAO.findByCode(request.getCouponCode()));
            if (couponOpt.isPresent()) {
                BigDecimal couponDiscount = getBigDecimal(request, couponOpt);

                discountAmount = discountAmount.add(couponDiscount);
            } else {
                throw new BadRequestException("Invalid or expired coupon code");
            }
        }

        return discountAmount;
    }

    private static @NotNull BigDecimal getBigDecimal(OrderCreateRequest request, Optional<Coupon> couponOpt) {
        Coupon coupon = couponOpt.get();

        // Check if order value meets minimum required
        if (request.getTotalAmount().compareTo(BigDecimal.valueOf(coupon.getMinOrderValue())) < 0) {
            throw new BadRequestException("Order total does not meet minimum value for coupon");
        }

        // Calculate discount based on coupon type
        BigDecimal couponDiscount;
        if ("percentage".equals(coupon.getDiscountType())) {
            couponDiscount = request.getTotalAmount()
                    .multiply(BigDecimal.valueOf(coupon.getDiscountValue()))
                    .divide(new BigDecimal(100));

            // Apply max discount limit if set
            if (coupon.getMaxDiscount() != null && couponDiscount.compareTo(BigDecimal.valueOf(coupon.getMaxDiscount())) > 0) {
                couponDiscount = BigDecimal.valueOf(coupon.getMaxDiscount());
            }
        } else {
            // Fixed discount
            couponDiscount = BigDecimal.valueOf(coupon.getDiscountValue());
        }
        return couponDiscount;
    }

    private String generateOrderCode() {
        // Format: ORD-YYYYMMdd-XXXXX (where XXXXX is a random string)
        String dateStr = java.time.LocalDate.now().toString().replace("-", "");
        String randomStr = StringUtils.generateRandomString(5).toUpperCase();
        return "ORD-" + dateStr + "-" + randomStr;
    }

    private Order2 createOrderEntity(OrderCreateRequest request, String orderCode,
                                     Long userId, Long deliveryMethodId,
                                     Long paymentMethodId, BigDecimal discountAmount) {
        BigDecimal subtotal = calculateSubtotal(request.getCartItems());

        return Order2.builder()
                .orderCode(orderCode)
                .userId(userId)
                .status("pending")
                .deliveryMethodId(deliveryMethodId)
                .paymentMethodId(paymentMethodId)
                .subtotal(subtotal)
                .deliveryPrice(request.getDeliveryPrice())
                .discountAmount(discountAmount)
                .taxAmount(BigDecimal.ZERO) // Giả sử không có thuế cho việc này
                .totalAmount(subtotal.add(request.getDeliveryPrice()).subtract(discountAmount))
                .couponCode(request.getCouponCode())
                .isVerified(false)
                .note(null)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    private BigDecimal calculateSubtotal(List<CartItemRequest> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
