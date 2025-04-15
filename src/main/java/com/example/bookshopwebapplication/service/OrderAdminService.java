package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.*;
import com.example.bookshopwebapplication.entities.*;
import com.example.bookshopwebapplication.http.response_admin.orders.OrderDetailResponse;
import com.example.bookshopwebapplication.http.response_admin.orders.OrderListResponse;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;

public class OrderAdminService {
    // Định nghĩa các hằng số để code dễ đọc hơn
    private static final int INCREASE_ACTUAL = 1;
    private static final int DECREASE_ACTUAL = -1;
    private static final int INCREASE_RESERVED = 1;
    private static final int DECREASE_RESERVED = -1;
    private static final int NO_CHANGE = 0;

    private final OrderAdminDao orderAdminDao;
    private final PaymentMethodDao paymentMethodDao;
    private final PaymentTransactionDao paymentTransactionDao;
    private final OrderStatusHistoryDao orderStatusHistoryDao;
    private final OrderItemDao2 orderItemDao;
    private final InventoryStatusDao inventoryStatusDao;
    private final InventoryHistoryDao inventoryHistoryDao;

    public OrderAdminService() {
        this.orderAdminDao = new OrderAdminDao();
        this.paymentMethodDao = new PaymentMethodDao();
        this.paymentTransactionDao = new PaymentTransactionDao();
        this.orderStatusHistoryDao = new OrderStatusHistoryDao();
        this.orderItemDao = new OrderItemDao2();
        this.inventoryStatusDao = new InventoryStatusDao();
        this.inventoryHistoryDao = new InventoryHistoryDao();
    }

    /**
     * Lấy danh sách đơn hàng có phân trang
     *
     * @param params Tham số lọc và phân trang
     * @return OrderListResponse Đối tượng chứa danh sách đơn hàng và thông tin phân trang
     */
    public OrderListResponse getOrdersWithPagination(Map<String, String> params) {
        return orderAdminDao.getOrdersWithPagination(params);
    }

    /**
     * Lấy chi tiết đơn hàng theo mã đơn hàng
     *
     * @param code Mã đơn hàng
     * @return OrderDetailResponse Đối tượng chứa thông tin chi tiết đơn hàng
     */
    public OrderDetailResponse getOrderDetailByCode(String code) {
        return orderAdminDao.getOrderDetailByCode(code);
    }

    /**
     * Cập nhật trạng thái đơn hàng
     *
     * @param orderId      ID của đơn hàng cần cập nhật
     * @param status       Trạng thái mới
     * @param note         Ghi chú cho việc cập nhật trạng thái
     * @param userIdUpdate ID của người thực hiện cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateOrderStatus(Long orderId, String status, String note, Long userIdUpdate) {
        try {
            orderAdminDao.executeTransaction(conn -> {
                // Bước 1: Lấy thông tin hiện tại của đơn hàng
                Order2 order = orderAdminDao.getOrderById(orderId);

                String currentStatus = order.getStatus();
                Long paymentMethodId = order.getPaymentMethodId();
                // Bước 2: Kiểm tra xem việc chuyển trạng thái có hợp lệ không
                if (!orderAdminDao.isValidStatusTransition(currentStatus, status)) {
                    throw new IllegalArgumentException("Chuyển trạng thái không hợp lệ từ " + currentStatus + " sang " + status);
                }
                // Bước 3: Kiểm tra các điều kiện đặc biệt theo phương thức thanh toán
                // Ví dụ: Nếu phương thức thanh toán là "COD", không thể chuyển sang trạng thái "waiting_payment"
                PaymentMethod paymentMethod = paymentMethodDao.findById(paymentMethodId);
                if (!this.validateStatusChangeByPaymentMethod(orderId, paymentMethod, currentStatus, status)) {
                    throw new IllegalArgumentException("Không thể chuyển trạng thái từ " + currentStatus + " sang " + status + " với phương thức thanh toán " + paymentMethod.getName());
                }

                // Cập nhật trạng thái đơn hàng
                order.setStatus(status);
                order.setNote(note);

                // Bước 4: Cập nhật trạng thái đơn hàng xuống DB
                orderAdminDao.updateWithConnection(order, conn);

                // Bước 5: Lưu lịch sử trạng thái
                OrderStatusHistory history = OrderStatusHistory.builder()
                        .orderId(orderId)
                        .status(status)
                        .note(note)
                        .changedBy(userIdUpdate)
                        .build();
                orderStatusHistoryDao.saveWithConnection(history, conn);

                // Bước 6: Xử lý các tác vụ đặc biệt theo trạng thái
                handleSpecialStatusTasks(conn, order, userIdUpdate);
            });
            return true;
        } catch (Exception e) {
            // Xử lý lỗi nếu cần thiết
            return false;
        }
    }

    /**
     * Xử lý các tác vụ đặc biệt theo trạng thái
     *
     * @param conn  Kết nối cơ sở dữ liệu
     * @param order Đối tượng đơn hàng
     */
    private void handleSpecialStatusTasks(Connection conn, Order2 order, Long userIdUpdate) {
        String status = order.getStatus();
        // Lấy ra các items trong đơn hàng
        List<OrderItem2> orderItems = orderItemDao.findByOrderId(order.getId());
        // Cập nhập tồn kho trong bảng inventory_status và ghi nhận lại vào bảng inventory_history
        // theo từng trạng thái đơn hàng.
        switch (status) {
            case "shipping":
                // Giảm actual_quantity và giảm reserved_quantity trong bảng inventory_status
                updateInventory(conn, order, orderItems, userIdUpdate,
                        "Đơn hàng chuyển sang trạng thái giao hàng",
                        "Xuất kho sản phẩm cho đơn hàng",
                        InventoryHistory.ActionType.EXPORT,
                        DECREASE_ACTUAL, DECREASE_RESERVED);
                break;
            case "cancelled":
                // Tăng available_quantity và giảm reserved_quantity trong bảng inventory_status
                updateInventory(conn, order, orderItems, userIdUpdate,
                        "Đơn hàng bị hủy",
                        "Hoàn trả sản phẩm vào kho cho đơn hàng",
                        InventoryHistory.ActionType.ADJUSTMENT,
                        INCREASE_ACTUAL, DECREASE_RESERVED);
                break;
            case "refunded":
                // Xử lý các tác vụ đặc biệt khi đơn hàng được hoàn tiền
                // Ví dụ: Hoàn tiền cho khách hàng, cập nhật trạng thái giao dịch thanh toán, Cập nhập tồn kho, v.v.
                break;
            case "delivered":
                // Xử lý các tác vụ đặc biệt khi đơn hàng đã được giao
                // Ví dụ: Cập nhập trạng thái giao dịch thanh toán, cập nhật trạng thái đơn hàng, v.v.
                handleDeliveredStatus(conn, order, userIdUpdate);
                break;
            default:
                // Không có tác vụ đặc biệt nào cần xử lý
                break;
        }
    }

    /**
     * Cập nhật tồn kho và lưu lịch sử thay đổi
     *
     * @param conn                   Kết nối database
     * @param order                  Thông tin đơn hàng
     * @param orderItems             Danh sách sản phẩm trong đơn hàng
     * @param userIdUpdate           ID người thực hiện
     * @param reason                 Lý do thay đổi
     * @param notesPrefix            Ghi chú cho lịch sử
     * @param actionType             Loại hành động (EXPORT, IMPORT, ADJUSTMENT)
     * @param actualQuantityChange   Thay đổi actual_quantity (1: tăng, -1: giảm, 0: không đổi)
     * @param reservedQuantityChange Thay đổi reserved_quantity (1: tăng, -1: giảm, 0: không đổi)
     */
    private void updateInventory(Connection conn, Order2 order, List<OrderItem2> orderItems,
                                 Long userIdUpdate, String reason, String notesPrefix,
                                 InventoryHistory.ActionType actionType,
                                 int actualQuantityChange, int reservedQuantityChange) {

        for (OrderItem2 item : orderItems) {
            Optional<InventoryStatus> inventoryStatusOpt = inventoryStatusDao.findByProductId(item.getProductId());
            if (inventoryStatusOpt.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy thông tin tồn kho cho sản phẩm ID: " + item.getProductId());
            }

            InventoryStatus inventoryStatus = inventoryStatusOpt.get();
            int oldActualQuantity = inventoryStatus.getActualQuantity();
            int quantity = item.getQuantity();

            // Tính toán giá trị mới dựa trên tham số thay đổi
            int newActualQuantity = oldActualQuantity;
            if (actualQuantityChange != NO_CHANGE) {
                newActualQuantity = oldActualQuantity + (actualQuantityChange * quantity);
            }

            int oldReservedQuantity = inventoryStatus.getReservedQuantity();
            int newReservedQuantity = oldReservedQuantity;
            if (reservedQuantityChange != NO_CHANGE) {
                newReservedQuantity = oldReservedQuantity + (reservedQuantityChange * quantity);
            }

            // Cập nhật giá trị mới
            inventoryStatus.setActualQuantity(newActualQuantity);
            inventoryStatus.setReservedQuantity(newReservedQuantity);

            // Tính toán available_quantity mới (nếu cần)
            if (actualQuantityChange != NO_CHANGE || reservedQuantityChange != NO_CHANGE) {
                int availableQuantity = newActualQuantity - newReservedQuantity;
                inventoryStatus.setAvailableQuantity(availableQuantity);
            }

            // Cập nhật vào database
            inventoryStatusDao.updateWithConnection(inventoryStatus, conn);

            // Ghi nhận vào bảng inventory_history
            InventoryHistory history = InventoryHistory.builder()
                    .productId(item.getProductId())
                    .quantityChange(actualQuantityChange * quantity) // Giá trị thay đổi số lượng thực tế
                    .previousQuantity(oldActualQuantity)
                    .currentQuantity(newActualQuantity)
                    .actionType(actionType)
                    .reason(reason)
                    .referenceId(order.getId())
                    .referenceType("order")
                    .notes(notesPrefix + " #" + order.getOrderCode())
                    .createdBy(userIdUpdate)
                    .build();

            inventoryHistoryDao.saveWithConnection(history, conn);
        }
    }

    private void handleDeliveredStatus(Connection conn, Order2 order, Long userIdUpdate) {
        // Ví dụ: Cập nhật trạng thái thanh toán cho COD...
        // Kiểm tra phương thức thanh toán
        PaymentMethod paymentMethod = paymentMethodDao.findById(order.getPaymentMethodId());

        // Nếu là COD, cập nhật trạng thái thanh toán thành công nếu chưa có
        if ("cod".equalsIgnoreCase(paymentMethod.getCode())) {
            PaymentTransaction existingTx = paymentTransactionDao.findByOrderId(order.getId());

            if (existingTx == null) {
                // Tạo giao dịch thanh toán mới
                PaymentTransaction codTx = PaymentTransaction.builder()
                        .orderId(order.getId())
                        .paymentMethodId(order.getPaymentMethodId())
                        .amount(order.getTotalAmount())
                        .transactionCode("COD-" + order.getOrderCode())
                        .status("completed")
                        .paymentDate(new Timestamp(System.currentTimeMillis()))
                        .note("Thanh toán COD khi giao hàng thành công")
                        .createdBy(userIdUpdate)
                        .build();

                paymentTransactionDao.saveWithConnection(codTx, conn);
            } else if (!"completed".equals(existingTx.getStatus())) {
                // Cập nhật trạng thái giao dịch hiện có
                existingTx.setStatus("completed");
                existingTx.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                existingTx.setNote("Thanh toán COD khi giao hàng thành công");
                existingTx.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                paymentTransactionDao.updateWithConnection(existingTx, conn);
            }
        }
    }

    /**
     * Kiểm tra việc thay đổi trạng thái dựa trên phương thức thanh toán
     *
     * @param orderId       ID đơn hàng
     * @param paymentMethod Phương thức thanh toán
     * @param currentStatus Trạng thái hiện tại
     * @param newStatus     Trạng thái mới
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateStatusChangeByPaymentMethod(Long orderId, PaymentMethod paymentMethod, String currentStatus, String newStatus) {
        // Định nghĩa luồng trạng thái cho từng phương thức thanh toán
        Map<String, Map<String, List<String>>> statusFlows = new HashMap<>();

        // Luồng trạng thái cho COD
        Map<String, List<String>> codFlow = new HashMap<>();
        codFlow.put("pending", Arrays.asList("processing", "cancelled"));
        codFlow.put("processing", Arrays.asList("shipping", "cancelled"));
        codFlow.put("shipping", Arrays.asList("delivered", "cancelled"));
        codFlow.put("delivered", Arrays.asList("refunded"));
        codFlow.put("cancelled", Arrays.asList("refunded"));
        codFlow.put("refunded", Collections.emptyList());

        // Luồng trạng thái cho VNPay
        Map<String, List<String>> vnpayFlow = new HashMap<>();
        vnpayFlow.put("pending", Arrays.asList("waiting_payment", "cancelled"));
        vnpayFlow.put("waiting_payment", Arrays.asList("processing", "payment_failed", "cancelled"));
        vnpayFlow.put("payment_failed", Arrays.asList("waiting_payment", "cancelled"));
        vnpayFlow.put("processing", Arrays.asList("shipping", "cancelled"));
        vnpayFlow.put("shipping", Arrays.asList("delivered", "cancelled"));
        vnpayFlow.put("delivered", Arrays.asList("refunded"));
        vnpayFlow.put("cancelled", Arrays.asList("refunded"));
        vnpayFlow.put("refunded", Collections.emptyList());

        // Thêm vào map chính
        statusFlows.put("cod", codFlow);
        statusFlows.put("vnpay", vnpayFlow);

        // Lấy luồng trạng thái phù hợp với phương thức thanh toán
        String paymentCode = paymentMethod.getCode().toLowerCase();
        Map<String, List<String>> flow = statusFlows.getOrDefault(paymentCode, codFlow); // Mặc định dùng COD flow

        // Kiểm tra xem trạng thái hiện tại có trong flow không
        if (!flow.containsKey(currentStatus)) {
            return false;
        }

        // Lấy danh sách trạng thái cho phép chuyển đến
        List<String> allowedNextStatuses = flow.get(currentStatus);

        // Kiểm tra xem trạng thái mới có trong danh sách cho phép không
        if (!allowedNextStatuses.contains(newStatus)) {
            return false;
        }

        // Kiểm tra điều kiện đặc biệt cho VNPay
        if ("vnpay".equalsIgnoreCase(paymentCode)) {
            // Chỉ có thể chuyển sang processing nếu đã thanh toán thành công
            if ("waiting_payment".equals(currentStatus) && "processing".equals(newStatus)) {
                return hasSuccessfulPayment(orderId);
            }
        }

        // Mọi trường hợp khác đều hợp lệ
        return true;
    }

    /**
     * Kiểm tra xem đơn hàng đã có giao dịch thanh toán thành công hay chưa
     */
    private boolean hasSuccessfulPayment(Long orderId) {
        PaymentTransaction paymentTransaction = paymentTransactionDao.findByOrderId(orderId);
        return paymentTransaction != null && "completed".equals(paymentTransaction.getStatus());
    }
}
