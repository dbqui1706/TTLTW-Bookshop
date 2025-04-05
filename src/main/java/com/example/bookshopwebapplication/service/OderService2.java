package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.*;
import com.example.bookshopwebapplication.entities.*;
import com.example.bookshopwebapplication.exceptions.BadRequestException;
import com.example.bookshopwebapplication.http.request.order.CartItemRequest;
import com.example.bookshopwebapplication.http.request.order.DeliveryAddressRequest;
import com.example.bookshopwebapplication.http.request.order.OrderCreateRequest;
import com.example.bookshopwebapplication.http.response.order.*;
import com.example.bookshopwebapplication.utils.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
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
    private final CartItemDao cartItemDAO;
    private final InventoryStatusDao inventoryStatusDAO;
    // Tích hợp InventoryService và OrderInventoryService
    private final InventoryService inventoryService;
    private final OrderInventoryService orderInventoryService;

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
        this.cartItemDAO = new CartItemDao();

        // Khởi tạo service quản lý tồn kho
        this.inventoryService = new InventoryService();
        this.orderInventoryService = new OrderInventoryService();

        this.inventoryStatusDAO = new InventoryStatusDao();
    }
    /**
     * Thực thi một transaction với callback
     */
    private void executeTransaction(AbstractDao.TransactionCallback callback) {
        Connection conn = null;
        try {
            // Lấy connection từ dao bất kỳ
            conn = orderDAO.getConnection();
            conn.setAutoCommit(false);

            // Thực thi callback
            callback.execute(conn);

            // commit transaction
            conn.commit();
        } catch (Exception e) {
            // rollback transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Không thể rollback transaction", ex);
                }
            }
            // Ném lại exception
            throw new RuntimeException("Lỗi khi thực thi transaction: " + e.getMessage(), e);
        } finally {
            // Đóng connection trong mọi trường hợp
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Log lỗi khi đóng connection
                }
            }
        }
    }

    /**
     * Tạo đơn hàng mới
     *
     * @param orderCreateRequest Thông tin đơn hàng
     * @return Đơn hàng đã tạo
     */
    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
        // Tạo các biến để lưu kết quả từ transaction
        final Order2[] order = new Order2[1];
        final List<OrderItem2>[] orderItems = new List[1];
        final OrderShipping[] shipping = new OrderShipping[1];
        final PaymentTransaction[] transaction = new PaymentTransaction[1];
        final DeliveryMethod[] deliveryMethod = new DeliveryMethod[1];
        final PaymentMethod[] paymentMethod = new PaymentMethod[1];

        // Sử dụng transaction để đảm bảo tính nhất quán
        executeTransaction(conn -> {
            try {
                // Xác thực thông tin đơn hàng
                validateOrderRequest(orderCreateRequest);

                // Lấy thông tin người dùng
                User user = userDAO.getById(orderCreateRequest.getUserId())
                        .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy người dùng"));

                // Lấy thông tin phương thức thanh toán
                PaymentMethod paymentMethodObj = paymentMethodDAO.findByCode(
                                orderCreateRequest.getPaymentMethod())
                        .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy phương thức thanh toán"));

                // Lấy thông tin phương thức giao hàng
                DeliveryMethod deliveryMethodObj = deliveryMethodDAO.findById(
                                orderCreateRequest.getDeliveryMethod())
                        .orElseThrow(() -> new BadRequestException("Lỗi không tìm thấy phương thức giao hàng"));

                // Tạo mã đơn hàng
                String orderCode = generateOrderCode();

                // Tạo đơn hàng
                Order2 orderObj = createOrderEntity(
                        orderCreateRequest,
                        orderCode,
                        user.getId(),
                        deliveryMethodObj.getId(),
                        paymentMethodObj.getId()
                );

                // Lưu đơn hàng sử dụng connection từ transaction
                Long orderId = orderDAO.saveWithConnection(orderObj, conn);
                orderObj.setId(orderId);

                // Tạo thông tin vận chuyển
                OrderShipping shippingObj = createShippingEntity(orderId,
                        orderCreateRequest.getDeliveryAddress(), user);
                Long shippingId = orderShippingDAO.saveWithConnection(shippingObj, conn);
                shippingObj.setId(shippingId);

                // Tạo các order item
                List<OrderItem2> orderItemList = createOrderItems(orderId,
                        orderCreateRequest.getCartItems());

                // Lưu các order item và kiểm tra tồn kho
                for (OrderItem2 item : orderItemList) {
                    Long itemId = orderItemDAO.saveWithConnection(item, conn);
                    item.setId(itemId);

                    // Kiểm tra và đặt trước sản phẩm từ tồn kho
                    Optional<InventoryStatus> inventoryStatus =
                            inventoryService.getInventoryStatus(item.getProductId());

                    if (inventoryStatus.isEmpty() ||
                            inventoryStatus.get().getAvailableQuantity() < item.getQuantity()) {
                        throw new BadRequestException("Không đủ sản phẩm trong kho: " + item.getProductName());
                    }

                    // Đặt trước số lượng sản phẩm
                    boolean reserved = inventoryStatusDAO.updateReservedQuantityWithConnection(
                            item.getProductId(), item.getQuantity(), conn);

                    if (!reserved) {
                        throw new BadRequestException("Không thể đặt trước sản phẩm: " + item.getProductName());
                    }
                }

                // Tạo giao dịch thanh toán
                PaymentTransaction paymentTransaction = createPaymentTransaction(
                        orderId, paymentMethodObj.getId(),
                        orderObj.getTotalAmount(), orderCreateRequest.getPaymentMethod());

                Long transactionId = paymentTransactionDAO.saveWithConnection(paymentTransaction, conn);
                paymentTransaction.setId(transactionId);

                // Tạo lịch sử trạng thái đơn hàng
                OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                        .orderId(orderId)
                        .status("pending")
                        .note("Order created")
                        .changedBy(user.getId())
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .build();

                orderStatusHistoryDAO.saveWithConnection(statusHistory, conn);

                // Cập nhập lại số lượng coupon nếu có
                if (orderCreateRequest.getCouponCode() != null &&
                        !orderCreateRequest.getCouponCode().isEmpty()) {
                    couponDAO.incrementUsageCountWithConnection(
                            orderCreateRequest.getCouponCode(), conn);
                }

                // Xóa các cart item khỏi giỏ hàng
                List<Long> cartItemIds = orderCreateRequest.getCartItems().stream()
                        .map(CartItemRequest::getCartItemId)
                        .toList();

                if (!cartItemIds.isEmpty()) {
                    for (Long cartItemId : cartItemIds) {
                        cartItemDAO.deleteWithConnection(cartItemId, conn);
                    }
                }

                // Lưu kết quả vào biến để sử dụng sau khi transaction kết thúc
                order[0] = orderObj;
                orderItems[0] = orderItemList;
                shipping[0] = shippingObj;
                transaction[0] = paymentTransaction;
                deliveryMethod[0] = deliveryMethodObj;
                paymentMethod[0] = paymentMethodObj;

            } catch (Exception e) {
                // Bắt lại exception và ném ra để rollback transaction
                throw new RuntimeException("Lỗi khi xử lý đơn hàng: " + e.getMessage(), e);
            }
        });

        // Gửi email xác nhận đơn hàng sau khi transaction đã commit thành công

        // Build và trả về OrderResponse
        return buildOrderResponse(
                order[0],
                orderItems[0],
                shipping[0],
                transaction[0],
                deliveryMethod[0],
                paymentMethod[0]
        );
    }

    /**
     * Xử lý khi đơn hàng được xác nhận thanh toán
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processOrderConfirmation(Long orderId) {
        Optional<Order2> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }

        Order2 order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng
        if (!"pending".equals(order.getStatus())) {
            throw new BadRequestException("Đơn hàng không ở trạng thái có thể xác nhận");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus("processing");
        orderDAO.updateStatus(orderId, "processing");

        // Ghi lịch sử trạng thái
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("processing")
                .note("Payment confirmed, order is being processed")
                .changedBy(order.getUserId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        return true;
    }

    /**
     * Xử lý khi đơn hàng được giao đi
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processOrderShipping(Long orderId) {
        Optional<Order2> orderOpt = orderDAO.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }

        Order2 order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng
        if (!"processing".equals(order.getStatus())) {
            throw new BadRequestException("Đơn hàng không ở trạng thái có thể giao");
        }

        // Xử lý thông qua OrderInventoryService - giảm số lượng tồn kho thực tế
        if (!orderInventoryService.processShippingOrder(orderId)) {
            throw new BadRequestException("Lỗi khi cập nhật tồn kho cho đơn hàng đang giao");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus("shipping");
        orderDAO.updateStatus(orderId, "shipping");

        // Ghi lịch sử trạng thái
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("shipping")
                .note("Order is being shipped")
                .changedBy(order.getUserId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        return true;
    }

    /**
     * Xử lý khi đơn hàng được giao thành công
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processOrderDelivered(Long orderId) {
        Optional<Order2> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }

        Order2 order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng
        if (!"shipping".equals(order.getStatus())) {
            throw new BadRequestException("Đơn hàng không ở trạng thái đang giao");
        }

        // Cập nhật trạng thái đơn hàng - không cần giảm số lượng tồn kho vì đã giảm khi bắt đầu giao hàng
        order.setStatus("delivered");
        orderDAO.updateStatus(orderId, "delivered");

        // Ghi lịch sử trạng thái
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("delivered")
                .note("Order delivered successfully")
                .changedBy(order.getUserId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        return true;
    }

    /**
     * Xử lý hủy đơn hàng
     *
     * @param orderId ID đơn hàng
     * @param reason  Lý do hủy đơn
     * @param userId  ID người dùng thực hiện hủy
     * @return true nếu hủy thành công
     */
    public boolean cancelOrder(Long orderId, String reason, Long userId) {
        Optional<Order2> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }

        Order2 order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng - không thể hủy đơn hàng đã hoàn thành hoặc đang giao
        if ("delivered".equals(order.getStatus()) || "shipping".equals(order.getStatus())) {
            throw new BadRequestException("Không thể hủy đơn hàng ở trạng thái hiện tại");
        }

        // Hoàn trả số lượng đặt trước
        if (!orderInventoryService.processCancelledOrder(orderId)) {
            throw new BadRequestException("Lỗi khi hoàn trả số lượng sản phẩm");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus("cancelled");
        orderDAO.updateStatus(orderId, "cancelled");

        // Ghi lịch sử trạng thái
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("cancelled")
                .note("Order cancelled: " + reason)
                .changedBy(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        return true;
    }

    /**
     * Xử lý hoàn trả đơn hàng
     *
     * @param orderId ID đơn hàng
     * @param reason  Lý do hoàn trả
     * @param userId  ID người dùng thực hiện hoàn trả
     * @return true nếu hoàn trả thành công
     */
    public boolean refundOrder(Long orderId, String reason, Long userId) {
        Optional<Order2> orderOpt = orderDAO.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }

        Order2 order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng - chỉ có thể hoàn trả đơn hàng đã giao hoặc đang giao
        if (!"delivered".equals(order.getStatus()) && !"shipping".equals(order.getStatus())) {
            throw new BadRequestException("Không thể hoàn trả đơn hàng ở trạng thái hiện tại");
        }

        // Cập nhật lại tồn kho
        if (!orderInventoryService.processRefundedOrder(orderId, reason)) {
            throw new BadRequestException("Lỗi khi cập nhật lại tồn kho");
        }

        // Cập nhật trạng thái đơn hàng
        order.setStatus("refunded");
        orderDAO.updateStatus(orderId, "refunded");

        // Ghi lịch sử trạng thái
        OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                .orderId(orderId)
                .status("refunded")
                .note("Order refunded: " + reason)
                .changedBy(userId)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        orderStatusHistoryDAO.save(statusHistory);

        return true;
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
                                                        Double amount, String paymentMethodCode) {
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
                    .discountPercent(cartItem.getDiscount())
                    .price(cartItem.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(cartItem.getPrice() * cartItem.getQuantity())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            orderItems.add(item);
        }

        return orderItems;
    }

    private OrderShipping createShippingEntity(Long orderId, DeliveryAddressRequest addressRequest, User user) {
        return OrderShipping.builder()
                .orderId(orderId)
                .receiverName(addressRequest.getRecipientName())
                .receiverEmail(user.getEmail())
                .receiverPhone(addressRequest.getPhoneNumber())
                .addressLine1(addressRequest.getAddressLine1())
                .addressLine2(null)
                .city(addressRequest.getDistrictName())
                .district(addressRequest.getDistrictName())
                .ward(addressRequest.getWardName())
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
        if (address.getRecipientName() == null || address.getRecipientName().isEmpty() ||
                address.getPhoneNumber() == null || address.getPhoneNumber().isEmpty() ||
                address.getAddressLine1() == null || address.getAddressLine1().isEmpty() ||
                address.getProvinceName() == null || address.getProvinceName().isEmpty() ||
                address.getDistrictName() == null || address.getDistrictName().isEmpty() ||
                address.getWardName() == null || address.getWardName().isEmpty()) {
            throw new BadRequestException("Incomplete delivery address information");
        }

        // Kiểm tra tồn kho cho tất cả các sản phẩm trước khi xử lý đơn hàng
        for (CartItemRequest item : request.getCartItems()) {
            Optional<InventoryStatus> inventoryStatus = inventoryService.getInventoryStatus(item.getProductId());
            if (inventoryStatus.isEmpty()) {
                throw new BadRequestException("Không tìm thấy thông tin tồn kho cho sản phẩm: " + item.getName());
            }

            if (inventoryStatus.get().getAvailableQuantity() < item.getQuantity()) {
                throw new BadRequestException("Không đủ số lượng sản phẩm: " + item.getName() +
                        " (Còn lại: " + inventoryStatus.get().getAvailableQuantity() +
                        ", Cần: " + item.getQuantity() + ")");
            }
        }
    }

    private String generateOrderCode() {
        // Format: ORD-YYYYMMdd-XXXXX (where XXXXX is a random string)
        String dateStr = java.time.LocalDate.now().toString().replace("-", "");
        String randomStr = StringUtils.generateRandomString(5).toUpperCase();
        return "ORD-" + dateStr + "-" + randomStr;
    }

    private Order2 createOrderEntity(OrderCreateRequest request, String orderCode,
                                     Long userId, Long deliveryMethodId,
                                     Long paymentMethodId) {
        PriceSummary priceSummary = request.getPriceSummary();

        return Order2.builder()
                .orderCode(orderCode)
                .userId(userId)
                .status("pending")
                .deliveryMethodId(deliveryMethodId)
                .paymentMethodId(paymentMethodId)
                .subtotal(priceSummary.getSubtotal())
                .deliveryPrice(priceSummary.getDeliveryPrice())
                .discountAmount(priceSummary.getDiscountAmount())
                .taxAmount(0.0) // Giả sử không có thuế cho việc này
                .totalAmount(priceSummary.getTotalAmount())
                .couponCode(request.getCouponCode())
                .isVerified(false)
                .note(null)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}