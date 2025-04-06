package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.*;
import com.example.bookshopwebapplication.entities.*;
import com.example.bookshopwebapplication.exceptions.BadRequestException;
import com.example.bookshopwebapplication.http.request.order.CartItemRequest;
import com.example.bookshopwebapplication.http.request.order.DeliveryAddressRequest;
import com.example.bookshopwebapplication.http.request.order.OrderCreateRequest;
import com.example.bookshopwebapplication.http.response.order.*;
import com.example.bookshopwebapplication.payment.vnpay.VNPayConfig;
import com.example.bookshopwebapplication.utils.RequestContext;
import com.example.bookshopwebapplication.utils.StringUtils;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService2 {
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_WAITING_PAYMENT = "waiting_payment";

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
    private final InventoryHistoryDao inventoryHistoryDAO;
    // Tích hợp InventoryService và OrderInventoryService
    private final InventoryService inventoryService;
    private final OrderInventoryService orderInventoryService;

    public OrderService2() {
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
        this.inventoryHistoryDAO = new InventoryHistoryDao();
        this.inventoryStatusDAO = new InventoryStatusDao();

        // Khởi tạo service quản lý tồn kho
        this.inventoryService = new InventoryService();
        this.orderInventoryService = new OrderInventoryService();
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
                    InventoryStatus inventoryStatusObj = inventoryStatus.orElseThrow(
                            () -> new BadRequestException("Không tìm thấy thông tin tồn kho cho sản phẩm: " + item.getProductName())
                    );
                    int availableQuantity = inventoryStatusObj.getAvailableQuantity();
                    int reservedQuantity = inventoryStatusObj.getReservedQuantity();

                    if (availableQuantity < item.getQuantity()) {
                        throw new BadRequestException("Không đủ sản phẩm trong kho: " + item.getProductName());
                    }
                    // Cập nhật số lượng có thể bán (số lượng có sẵn - số lượng đã đặt)
                    inventoryStatusObj.setAvailableQuantity(availableQuantity - item.getQuantity());
                    // Cập nhập số lượng đã đặt (số lượng đã đặt + số lượng sản phẩm được order)
                    inventoryStatusObj.setReservedQuantity(reservedQuantity + item.getQuantity());

                    // Update InventoryStatus
                    boolean reserved = inventoryStatusDAO.updateWithConnection(inventoryStatusObj, conn);

                    if (!reserved) {
                        throw new BadRequestException("Không thể đặt trước sản phẩm: " + item.getProductName());
                    }

                    // Lưu lịch sử tồn kho
                    InventoryHistory history = InventoryHistory.builder()
                            .productId(item.getProductId())
                            .quantityChange(0) // Số lượng thực tế không thay đổi khi đặt hàng, chỉ đặt trước
                            .previousQuantity(inventoryStatus.get().getActualQuantity())
                            .currentQuantity(inventoryStatus.get().getActualQuantity())
                            .actionType(InventoryHistory.ActionType.ADJUSTMENT)
                            .reason("Đặt trước sản phẩm cho đơn hàng mới")
                            .referenceId(orderId)
                            .referenceType("order_reserve")
                            .notes("Đặt hàng: Reserved +" + item.getQuantity() + ", Available -" + item.getQuantity() + " cho đơn hàng #" + orderCode)
                            .createdBy(orderCreateRequest.getUserId())
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .build();

                    // Lưu lịch sử thay đổi tồn kho
                    Long idHistory = inventoryHistoryDAO.saveWithConnection(history, conn);
                    if (idHistory == null) {
                        throw new BadRequestException("Không thể lưu lịch sử tồn kho cho sản phẩm: " + item.getProductName());
                    }
                }

                // Tạo giao dịch thanh toán
                PaymentTransaction paymentTransaction = createPaymentTransaction(
                        orderId, paymentMethodObj.getId(),
                        orderObj.getTotalAmount(), orderCreateRequest.getPaymentMethod());

                Long transactionId = paymentTransactionDAO.saveWithConnection(paymentTransaction, conn);
                paymentTransaction.setId(transactionId);

                // Tạo lịch sử trạng thái đơn hàng
                // Tạo giao dịch thanh toán với trạng thái phù hợp
                String paymentStatus = STATUS_PENDING;
                String note = "Đơn hàng tạo thành công";
                if ("vnpay".equals(orderCreateRequest.getPaymentMethod())) {
                    paymentStatus = STATUS_WAITING_PAYMENT;
                    note = "Đơn hàng tạo thành công, đang chờ thanh toán";

                }
                OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                        .orderId(orderId)
                        .status(paymentStatus)
                        .note(note)
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
        if ("vnpay".equals(orderCreateRequest.getPaymentMethod())) {
            // Tạo URL thanh toán VNPay
            String vnpayPaymentUrl = createVNPayPaymentUrl(order[0], transaction[0]);

            // Trả về OrderResponse có thêm thông tin URL thanh toán
            return buildOrderResponseWithPaymentUrl(
                    order[0],
                    orderItems[0],
                    shipping[0],
                    transaction[0],
                    deliveryMethod[0],
                    paymentMethod[0],
                    vnpayPaymentUrl
            );
        } else {
            // Với COD, trả về response thông thường
            return buildOrderResponse(
                    order[0],
                    orderItems[0],
                    shipping[0],
                    transaction[0],
                    deliveryMethod[0],
                    paymentMethod[0]
            );
        }
    }

    /**
     * Tạo URL thanh toán VNPay
     */
    private String createVNPayPaymentUrl(Order2 order, PaymentTransaction transaction) {
        try {
            // Tạo các tham số thanh toán
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", VNPayConfig.vnp_Version);
            vnpParams.put("vnp_Command", VNPayConfig.vnp_Command);
            vnpParams.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
            long amount = Math.round(order.getTotalAmount() * 100);
            vnpParams.put("vnp_Amount", String.valueOf(amount));
            vnpParams.put("vnp_CurrCode", VNPayConfig.vnp_CurrCode);

            // Tạo mã giao dịch duy nhất
            String txnRef = order.getOrderCode();
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getOrderCode());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");

            // URL return sau khi thanh toán
            String returnUrl = VNPayConfig.vnp_ReturnUrl + "?orderId=" + order.getId();
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            // Địa chỉ IP
            String ipAddr = RequestContext.getIpAddress();
            if (ipAddr == null || ipAddr.isEmpty()) {
                ipAddr = "127.0.0.1"; // Fallback nếu không lấy được IP
            }
            vnpParams.put("vnp_IpAddr", ipAddr);

            // Thêm thời gian tạo
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")); // VNPay yêu cầu múi giờ GMT+7
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(calendar.getTime());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            // Thêm thời gian hết hạn (15 phút sau)
            calendar.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(calendar.getTime());
            vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);

            // Sắp xếp các tham số theo alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            // Tạo chuỗi hash
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Xây dựng chuỗi hash
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                    // Xây dựng query string
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            // Thêm hash signature
            String queryUrl = query.toString();
            // Sử dụng phương thức HMAC chính xác từ VNPayConfig
            String vnpSecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

            return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo URL thanh toán VNPay: " + e.getMessage(), e);
        }
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

    /**
     * Build OrderResponse với URL thanh toán VNPay
     */
    private OrderResponse buildOrderResponseWithPaymentUrl(
            Order2 order, List<OrderItem2> orderItems, OrderShipping shipping,
            PaymentTransaction transaction, DeliveryMethod deliveryMethod,
            PaymentMethod paymentMethod, String paymentUrl) {

        // Tạo response cơ bản
        OrderResponse response = buildOrderResponse(
                order, orderItems, shipping, transaction, deliveryMethod, paymentMethod);

        // Thêm trường paymentUrl vào response
        response.setPaymentUrl(paymentUrl);
        response.setRequirePayment(true);

        return response;
    }

    private PaymentTransaction createPaymentTransaction(Long orderId, Long paymentMethodId,
                                                        Double amount, String paymentMethodCode) {
        // Tạo giao dịch thanh toán với trạng thái phù hợp
        String paymentStatus = STATUS_PENDING;
        if ("vnpay".equals(paymentMethodCode)) {
            paymentStatus = STATUS_WAITING_PAYMENT;
        }

        return PaymentTransaction.builder()
                .orderId(orderId)
                .paymentMethodId(paymentMethodId)
                .amount(amount)
                .transactionCode(null)
                .paymentProviderRef(null)
                .status(paymentStatus)
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
        // Format: ORD-XXXXXXXXX (8 số đầu của UUID)
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        return "ORD-" + randomStr;
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

    /**
     * Cập nhật trạng thái giao dịch VNPay
     *
     * @param orderCode Mã đơn hàng
     * @param vnpParams Tham số từ VNPay
     * @return Kết quả xử lý
     */
    public boolean updateVNPAYTransactionStatus(String orderCode, Map<String, String> vnpParams) {
        final boolean[] result = new boolean[1];

        executeTransaction(conn -> {
            try {
                // Lấy thông tin đơn hàng từ order_code
                Optional<Order2> orderOpt = orderDAO.findByOrderCode(orderCode);
                if (orderOpt.isEmpty()) {
                    throw new BadRequestException("Không tìm thấy đơn hàng với mã: " + orderCode);
                }

                Order2 order = orderOpt.get();
                Long orderId = order.getId();

                // Lấy thông tin payment transaction từ orderId
                PaymentTransaction transaction = paymentTransactionDAO.findByOrderId(orderId);

                // Kiểm tra trạng thái hiện tại của đơn hàng
                if (!"waiting_payment".equals(transaction.getStatus())) {
                    // Đơn hàng có thể đã được xử lý rồi, không cần xử lý nữa
                    result[0] = false;
                    return;
                }

                // Lấy mã response và kiểm tra kết quả từ VNPay
                String vnpResponseCode = vnpParams.get("vnp_ResponseCode");

                // Lấy chi tiết giao dịch thanh toán
                Optional<PaymentTransaction> transactionOpt = Optional.ofNullable(paymentTransactionDAO.findByOrderId(orderId));
                if (transactionOpt.isEmpty()) {
                    throw new BadRequestException("Không tìm thấy giao dịch thanh toán cho đơn hàng: " + orderCode);
                }

                // Lấy thông tin từ VNPay
                String vnpAmount = vnpParams.get("vnp_Amount");
                String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");
                String vnpBankCode = vnpParams.get("vnp_BankCode");
                String vnpOrderInfo = vnpParams.get("vnp_OrderInfo");
                String vnpPayDate = vnpParams.get("vnp_PayDate");

                // Kiểm tra response code từ VNPay
                if ("00".equals(vnpResponseCode)) {
                    // Thanh toán thành công

                    // 1. Cập nhật trạng thái thanh toán
                    transaction.setStatus("completed");
                    transaction.setTransactionCode(vnpTransactionNo);
                    transaction.setPaymentProviderRef("VNPAY:" + vnpBankCode);
                    transaction.setNote("Thanh toán VNPay thành công. Số tiền: " +
                            (Double.parseDouble(vnpAmount) / 100) + " VND");
                    transaction.setPaymentDate(new Timestamp(System.currentTimeMillis()));

                    paymentTransactionDAO.updateWithConnection(transaction, conn);

                    // 2. Cập nhật trạng thái đơn hàng
                    order.setStatus("processing");
                    order.setIsVerified(true);
                    orderDAO.updateWithConnection(order, conn);

                    // 3. Thêm lịch sử trạng thái đơn hàng
                    OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                            .orderId(orderId)
                            .status("processing")
                            .note("Đơn hàng đã được thanh toán qua VNPay")
                            .changedBy(order.getUserId())
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .build();

                    orderStatusHistoryDAO.saveWithConnection(statusHistory, conn);

                    // 4. Xử lý tồn kho: Chuyển từ reserved_quantity sang thực tế bán
                    List<OrderItem2> orderItems = orderItemDAO.findByOrderId(orderId);

                    for (OrderItem2 item : orderItems) {
                        // Lấy thông tin tồn kho hiện tại
                        Optional<InventoryStatus> inventoryOpt = inventoryStatusDAO.findByProductId(item.getProductId());

                        if (inventoryOpt.isPresent()) {
                            InventoryStatus inventory = inventoryOpt.get();

                            // Số lượng sản phẩm trong đơn hàng
                            int quantity = item.getQuantity();

                            // Khi thanh toán thành công, chuyển từ reserved sang actual
                            // actualQuantity không cần thay đổi vì đã được reserved trước đó khi tạo đơn hàng
                            // reservedQuantity cần giảm đi vì sản phẩm đã được "xác nhận" bán
                            int currentReserved = inventory.getReservedQuantity();
                            int newReservedQuantity = currentReserved - quantity;
                            inventory.setReservedQuantity(newReservedQuantity);

                            // Cập nhật lại reserved_quantity trong inventory_status
                            inventoryStatusDAO.updateWithConnection(inventory, conn);

                            // Thêm lịch sử inventory
                            InventoryHistory history = InventoryHistory.builder()
                                    .productId(item.getProductId())
                                    .quantityChange(0) // Tổng số lượng không thay đổi
                                    .previousQuantity(inventory.getActualQuantity())
                                    .currentQuantity(inventory.getActualQuantity())
                                    .actionType(InventoryHistory.ActionType.ADJUSTMENT)
                                    .reason("Đơn hàng được xác nhận thanh toán")
                                    .referenceId(orderId)
                                    .referenceType("order")
                                    .notes("Đơn hàng #" + orderCode + " đã thanh toán thành công qua VNPay")
                                    .createdBy(order.getUserId())
                                    .createdAt(new Timestamp(System.currentTimeMillis()))
                                    .build();

                            // Lưu lịch sử thay đổi tồn kho
                            inventoryHistoryDAO.saveWithConnection(history, conn);
                        }
                    }

                } else {
                    // Thanh toán thất bại
                    // 1. Cập nhật trạng thái thanh toán
                    transaction.setStatus("failed");
                    transaction.setTransactionCode(vnpTransactionNo != null ? vnpTransactionNo : "unknown");
                    transaction.setPaymentProviderRef("VNPAY:" + (vnpBankCode != null ? vnpBankCode : "unknown"));
                    transaction.setNote("Thanh toán VNPay thất bại. Mã lỗi: " + vnpResponseCode);

                    paymentTransactionDAO.updateWithConnection(transaction, conn);

                    // 2. Cập nhật trạng thái đơn hàng
                    order.setStatus("payment_failed");
                    orderDAO.updateStatusWithConnection(orderId, "payment_failed", conn);

                    // 3. Thêm lịch sử trạng thái đơn hàng
                    OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                            .orderId(orderId)
                            .status("payment_failed")
                            .note("Thanh toán thất bại qua VNPay. Mã lỗi: " + vnpResponseCode)
                            .changedBy(order.getUserId())
                            .createdAt(new Timestamp(System.currentTimeMillis()))
                            .build();

                    orderStatusHistoryDAO.saveWithConnection(statusHistory, conn);

                    // 4. Hoàn trả số lượng đặt trước (reserved) về available
                    List<OrderItem2> orderItems = orderItemDAO.findByOrderId(orderId);
                    for (OrderItem2 item : orderItems) {
                        // Lấy thông tin tồn kho hiện tại
                        Optional<InventoryStatus> inventoryOpt = inventoryStatusDAO.findByProductId(item.getProductId());
                        InventoryStatus inventory = inventoryOpt.orElse(null);
                        if (inventory != null) {

                            // Số lượng sản phẩm trong đơn hàng
                            int quantity = item.getQuantity();

                            // Khi thanh toán thất bại, cần hoàn trả lại số lượng đã reserved
                            int availableQuantity = inventory.getAvailableQuantity() + quantity;
                            int currentReserved = inventory.getReservedQuantity();
                            int newReservedQuantity = Math.max(0, currentReserved - quantity);
                            inventory.setReservedQuantity(newReservedQuantity);
                            inventory.setAvailableQuantity(availableQuantity);

                            // Cập nhật lại reserved_quantity trong inventory_status
                            inventoryStatusDAO.updateWithConnection(inventory, conn);

                            // Thêm lịch sử inventory
                            InventoryHistory history = InventoryHistory.builder()
                                    .productId(item.getProductId())
                                    .quantityChange(0) // Tổng số lượng không thay đổi
                                    .previousQuantity(inventory.getActualQuantity())
                                    .currentQuantity(inventory.getActualQuantity())
                                    .actionType(InventoryHistory.ActionType.ADJUSTMENT)
                                    .reason("Đơn hàng thanh toán thất bại")
                                    .referenceId(orderId)
                                    .referenceType("order")
                                    .notes("Đơn hàng #" + orderCode + " thanh toán thất bại qua VNPay")
                                    .createdBy(order.getUserId())
                                    .createdAt(new Timestamp(System.currentTimeMillis()))
                                    .build();

                            // Lưu lịch sử thay đổi tồn kho
                            inventoryHistoryDAO.saveWithConnection(history, conn);
                        }
                    }
                }

                result[0] = true;

            } catch (Exception e) {
                // Ghi log lỗi
                System.err.println("Lỗi khi cập nhật trạng thái thanh toán VNPay: " + e.getMessage());
                e.printStackTrace();

                // Ném lại exception để rollback transaction
                throw new RuntimeException("Lỗi xử lý thanh toán VNPay: " + e.getMessage(), e);
            }
        });
        return result[0];
    }
}