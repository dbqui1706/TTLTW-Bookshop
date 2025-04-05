package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.InventoryHistoryDao;
import com.example.bookshopwebapplication.dao.InventoryImportDao;
import com.example.bookshopwebapplication.dao.InventoryStatusDao;
import com.example.bookshopwebapplication.entities.InventoryHistory;
import com.example.bookshopwebapplication.entities.InventoryImport;
import com.example.bookshopwebapplication.entities.InventoryStatus;
import com.example.bookshopwebapplication.utils.RequestContext;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý các nghiệp vụ liên quan đến quản lý tồn kho
 */
public class InventoryService {

    private final InventoryStatusDao inventoryStatusDao;
    private final InventoryImportDao inventoryImportDao;
    private final InventoryHistoryDao inventoryHistoryDao;

    public InventoryService() {
        this.inventoryStatusDao = new InventoryStatusDao();
        this.inventoryImportDao = new InventoryImportDao();
        this.inventoryHistoryDao = new InventoryHistoryDao();
    }

    /**
     * Nhập kho hàng hóa
     * @param importRecord Thông tin phiếu nhập kho
     * @return ID của phiếu nhập kho
     */
    public Long importProduct(InventoryImport importRecord) {
        if (importRecord.getCreatedBy() == null) {
            importRecord.setCreatedBy(RequestContext.getUserId());
        }

        if (importRecord.getImportDate() == null) {
            importRecord.setImportDate(new Timestamp(System.currentTimeMillis()));
        }

        return inventoryImportDao.save(importRecord);
    }

    /**
     * Xuất kho sản phẩm (khi giao hàng)
     * @param productId ID sản phẩm
     * @param quantity Số lượng xuất
     * @param orderId ID đơn hàng
     * @return true nếu xuất kho thành công
     */
    public boolean exportProduct(Long productId, int quantity, Long orderId) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        if (status.getActualQuantity() < quantity) {
            return false;
        }

        // Giảm số lượng thực tế trong kho và số lượng đặt trước
        if (inventoryStatusDao.decreaseActualQuantity(productId, quantity)) {
            // Ghi log lịch sử xuất kho
            inventoryHistoryDao.recordExport(productId, quantity, "Xuất kho cho đơn hàng #" + orderId, orderId);
            return true;
        }

        return false;
    }

    /**
     * Đặt trước số lượng sản phẩm (khi đặt hàng)
     * @param productId ID sản phẩm
     * @param quantity Số lượng đặt
     * @return true nếu đặt trước thành công
     */
    public boolean reserveProduct(Long productId, int quantity) {
        return inventoryStatusDao.updateReservedQuantity(productId, quantity);
    }

    /**
     * Hủy đặt trước sản phẩm (khi hủy đơn hàng)
     * @param productId ID sản phẩm
     * @param quantity Số lượng cần hủy đặt
     * @return true nếu hủy đặt trước thành công
     */
    public boolean cancelReserveProduct(Long productId, int quantity) {
        return inventoryStatusDao.updateReservedQuantity(productId, -quantity);
    }

    /**
     * Điều chỉnh số lượng tồn kho (kiểm kê, hủy hàng hỏng,...)
     * @param productId ID sản phẩm
     * @param newQuantity Số lượng mới
     * @param reason Lý do điều chỉnh
     * @param notes Ghi chú bổ sung
     * @return true nếu điều chỉnh thành công
     */
    public boolean adjustInventory(Long productId, int newQuantity, String reason, String notes) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        int quantityChange = newQuantity - status.getActualQuantity();

        // Cập nhật số lượng trong bảng inventory_status
        status.setActualQuantity(newQuantity);
        status.setAvailableQuantity(newQuantity - status.getReservedQuantity());
        inventoryStatusDao.update(status);

        // Cập nhật số lượng trong bảng product
        updateProductQuantity(productId, newQuantity);

        // Ghi log lịch sử điều chỉnh
        inventoryHistoryDao.recordAdjustment(productId, quantityChange, reason, notes);

        return true;
    }

    /**
     * Cập nhật ngưỡng cảnh báo tồn kho
     * @param productId ID sản phẩm
     * @param threshold Ngưỡng mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateReorderThreshold(Long productId, int threshold) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        status.setReorderThreshold(threshold);
        inventoryStatusDao.update(status);

        return true;
    }

    /**
     * Lấy danh sách sản phẩm có số lượng dưới ngưỡng cảnh báo
     * @return Danh sách thông tin tồn kho
     */
    public List<InventoryStatus> getProductsBelowThreshold() {
        return inventoryStatusDao.findBelowThreshold();
    }

    /**
     * Lấy lịch sử nhập kho của sản phẩm
     * @param productId ID sản phẩm
     * @return Danh sách phiếu nhập kho
     */
    public List<InventoryImport> getImportHistory(Long productId) {
        return inventoryImportDao.findByProductId(productId);
    }

    /**
     * Lấy lịch sử thay đổi tồn kho của sản phẩm
     * @param productId ID sản phẩm
     * @return Danh sách lịch sử thay đổi
     */
    public List<InventoryHistory> getInventoryHistory(Long productId) {
        return inventoryHistoryDao.findByProductId(productId);
    }

    /**
     * Lấy thông tin tồn kho của sản phẩm
     * @param productId ID sản phẩm
     * @return Thông tin tồn kho
     */
    public Optional<InventoryStatus> getInventoryStatus(Long productId) {
        return inventoryStatusDao.findByProductId(productId);
    }

    /**
     * Cập nhật số lượng trong bảng product
     * @param productId ID sản phẩm
     * @param newQuantity Số lượng mới
     */
    private void updateProductQuantity(Long productId, int newQuantity) {
        // Chỉ cập nhật mà không lưu log lịch sử
        try {
            String sql = "UPDATE product SET quantity = ?, updatedAt = CURRENT_TIMESTAMP WHERE id = ?";
            try (var conn = inventoryStatusDao.getConnection();
                 var stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newQuantity);
                stmt.setLong(2, productId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật số lượng sản phẩm", e);
        }
    }


    public boolean increaseActualQuantity(Long productId, Integer quantity) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (statusOpt.isEmpty()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        int newActualQuantity = status.getActualQuantity() + quantity;

        // Cập nhật số lượng thực tế trong kho
        status.setActualQuantity(newActualQuantity);
        inventoryStatusDao.update(status);

        // Ghi log lịch sử nhập kho
        inventoryHistoryDao.recordImport(productId, quantity, "Nhập kho", null);

        return true;
    }
}