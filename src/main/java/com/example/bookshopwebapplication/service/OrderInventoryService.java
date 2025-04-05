package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.OrderDao2;
import com.example.bookshopwebapplication.dao.OrderItemDao2;
import com.example.bookshopwebapplication.entities.Order2;
import com.example.bookshopwebapplication.entities.OrderItem;
import com.example.bookshopwebapplication.entities.OrderItem2;
import com.example.bookshopwebapplication.exceptions.BadRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service xử lý tích hợp giữa đơn hàng và quản lý tồn kho
 */
public class OrderInventoryService {

    private final OrderDao2 orderDao;
    private final OrderItemDao2 orderItemDao;
    private final InventoryService inventoryService;

    public OrderInventoryService() {
        this.orderDao = new OrderDao2();
        this.orderItemDao = new OrderItemDao2();
        this.inventoryService = new InventoryService();
    }

    /**
     * Xử lý khi đơn hàng được tạo mới - đặt trước số lượng sản phẩm
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processNewOrder(Long orderId) {
        Optional<Order2> orderOpt = orderDao.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        List<OrderItem2> orderItems = orderItemDao.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return false;
        }

        boolean success = true;
        // Đặt trước số lượng sản phẩm trong đơn hàng
        for (OrderItem2 item : orderItems) {
            if (!inventoryService.reserveProduct(item.getProductId(), item.getQuantity())) {
                success = false;
                // Nếu có lỗi, hủy đặt trước toàn bộ các sản phẩm đã xử lý trước đó
                for (OrderItem2 processedItem : orderItems) {
                    if (processedItem.equals(item)) {
                        break;
                    }
                    inventoryService.cancelReserveProduct(processedItem.getProductId(), processedItem.getQuantity());
                }
                break;
            }
        }

        return success;
    }

    /**
     * Xử lý khi đơn hàng bị hủy - trả lại số lượng đặt trước
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processCancelledOrder(Long orderId) {
        Optional<Order2> orderOpt = orderDao.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        Order2 order = orderOpt.get();
        // Chỉ xử lý các đơn hàng chưa giao
        if ("delivered".equals(order.getStatus()) ||
                "shipping".equals(order.getStatus())) {
            return false;
        }

        List<OrderItem2> orderItems = orderItemDao.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return true;
        }

        boolean success = true;
        // Hủy đặt trước số lượng sản phẩm trong đơn hàng
        for (OrderItem2 item : orderItems) {
            if (!inventoryService.cancelReserveProduct(item.getProductId(), item.getQuantity())) {
                success = false;
                break;
            }
        }

        return success;
    }

    /**
     * Xử lý khi đơn hàng đã giao hàng thành công - giảm số lượng tồn kho thực tế
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processCompletedOrder(Long orderId) {
        Optional<Order2> orderOpt = orderDao.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        List<OrderItem2> orderItems = orderItemDao.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return true;
        }

        boolean success = true;
        // Xuất kho số lượng sản phẩm trong đơn hàng
        for (OrderItem2 item : orderItems) {
            if (!inventoryService.exportProduct(item.getProductId(), item.getQuantity(), orderId)) {
                success = false;
                break;
            }
        }

        return success;
    }

    /**
     * Xử lý khi đơn hàng đang giao - xác nhận xuất kho và giảm số lượng tồn kho thực tế
     *
     * @param orderId ID đơn hàng
     * @return true nếu xử lý thành công
     */
    public boolean processShippingOrder(Long orderId) {
        Optional<Order2> orderOpt = orderDao.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        // Xác nhận đơn hàng đang ở trạng thái hợp lệ để giao hàng
        Order2 order = orderOpt.get();
        if (!"processing".equals(order.getStatus())) {
            throw new BadRequestException("Đơn hàng không ở trạng thái có thể giao hàng");
        }

        // Xử lý tương tự như đơn hàng đã hoàn thành
        // (giảm số lượng tồn kho thực tế ngay khi bắt đầu giao hàng)
        return processCompletedOrder(orderId);
    }

    /**
     * Kiểm tra xem đơn hàng có thể được xử lý không (đủ số lượng tồn kho)
     *
     * @param orderId ID đơn hàng
     * @return true nếu đủ số lượng tồn kho
     */
    public boolean canProcessOrder(Long orderId) {
        List<OrderItem2> orderItems = orderItemDao.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return false;
        }

        for (OrderItem2 item : orderItems) {
            var inventoryOpt = inventoryService.getInventoryStatus(item.getProductId());
            if (!inventoryOpt.isPresent() || inventoryOpt.get().getAvailableQuantity() < item.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Lấy danh sách đơn hàng có thể xử lý (đủ số lượng tồn kho)
     *
     * @param orderIds Danh sách ID đơn hàng cần kiểm tra
     * @return Danh sách ID đơn hàng có thể xử lý
     */
    public List<Long> getProcessableOrders(List<Long> orderIds) {
        return orderIds.stream()
                .filter(this::canProcessOrder)
                .toList();
    }

    /**
     * Xử lý khi đơn hàng được hoàn trả - cập nhật lại tồn kho
     *
     * @param orderId ID đơn hàng
     * @param reason  Lý do hoàn trả
     * @return true nếu xử lý thành công
     */
    public boolean processRefundedOrder(Long orderId, String reason) {
        Optional<Order2> orderOpt = orderDao.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        // Xác nhận đơn hàng đã ở trạng thái đã giao hoặc đang giao
        Order2 order = orderOpt.get();
        if (!"delivered".equals(order.getStatus()) && !"shipping".equals(order.getStatus())) {
            throw new BadRequestException("Chỉ đơn hàng đã giao hoặc đang giao mới có thể hoàn trả");
        }

        List<OrderItem2> orderItems = orderItemDao.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return true;
        }

        boolean success = true;
        // Cập nhật lại số lượng tồn kho cho các sản phẩm trong đơn hàng
        for (OrderItem2 item : orderItems) {
            // Tăng số lượng tồn kho thực tế
            if (!inventoryService.increaseActualQuantity(item.getProductId(), item.getQuantity())) {
                success = false;
                break;
            }
        }

        return success;
    }
}