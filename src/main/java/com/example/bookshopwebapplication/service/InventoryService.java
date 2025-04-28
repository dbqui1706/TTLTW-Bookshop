package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.*;
import com.example.bookshopwebapplication.entities.*;
import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryReceiptDTO;
import com.example.bookshopwebapplication.http.response_admin.invetory.ProductInventoryDTO;
import com.example.bookshopwebapplication.http.response_admin.invetory.ReceiptItemDetailDTO;
import com.example.bookshopwebapplication.utils.RequestContext;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ liên quan đến quản lý tồn kho
 */
public class InventoryService {
    private final InventoryStatusDao inventoryStatusDao;
    private final InventoryImportDao inventoryImportDao;
    private final InventoryHistoryDao inventoryHistoryDao;
    private final InventoryReceiptsDao inventoryReceiptsDao;

    public InventoryService() {
        this.inventoryStatusDao = new InventoryStatusDao();
        this.inventoryImportDao = new InventoryImportDao();
        this.inventoryHistoryDao = new InventoryHistoryDao();
        this.inventoryReceiptsDao = new InventoryReceiptsDao();
    }

    /**
     * Nhập kho hàng hóa
     *
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
     *
     * @param productId ID sản phẩm
     * @param quantity  Số lượng xuất
     * @param orderId   ID đơn hàng
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
     *
     * @param productId ID sản phẩm
     * @param quantity  Số lượng đặt
     * @return true nếu đặt trước thành công
     */
    public boolean reserveProduct(Long productId, int quantity) {
        return inventoryStatusDao.updateReservedQuantity(productId, quantity);
    }

    /**
     * Hủy đặt trước sản phẩm (khi hủy đơn hàng)
     *
     * @param productId ID sản phẩm
     * @param quantity  Số lượng cần hủy đặt
     * @return true nếu hủy đặt trước thành công
     */
    public boolean cancelReserveProduct(Long productId, int quantity) {
        return inventoryStatusDao.updateReservedQuantity(productId, -quantity);
    }

    /**
     * Điều chỉnh số lượng tồn kho (kiểm kê, hủy hàng hỏng,...)
     *
     * @param productId   ID sản phẩm
     * @param newQuantity Số lượng mới
     * @param reason      Lý do điều chỉnh
     * @param notes       Ghi chú bổ sung
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
     *
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
     *
     * @return Danh sách thông tin tồn kho
     */
    public List<InventoryStatus> getProductsBelowThreshold() {
        return inventoryStatusDao.findBelowThreshold();
    }

    /**
     * Lấy lịch sử nhập kho của sản phẩm
     *
     * @param productId ID sản phẩm
     * @return Danh sách phiếu nhập kho
     */
    public List<InventoryImport> getImportHistory(Long productId) {
        return inventoryImportDao.findByProductId(productId);
    }

    /**
     * Lấy lịch sử thay đổi tồn kho của sản phẩm
     *
     * @param productId ID sản phẩm
     * @return Danh sách lịch sử thay đổi
     */
    public List<InventoryHistory> getInventoryHistory(Long productId) {
        return inventoryHistoryDao.findByProductId(productId);
    }

    /**
     * Lấy thông tin tồn kho của sản phẩm
     *
     * @param productId ID sản phẩm
     * @return Thông tin tồn kho
     */
    public Optional<InventoryStatus> getInventoryStatus(Long productId) {
        return inventoryStatusDao.findByProductId(productId);
    }

    /**
     * Cập nhật số lượng trong bảng product
     *
     * @param productId   ID sản phẩm
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


    /*
     * Xử lý xuất/nhập kho
     */
    public Long processInventoryTransaction(InventoryReceipts inventoryReceipts) {
        // Sử dụng transaction để đảm bảo tính nhất quán
        inventoryReceiptsDao.executeTransaction(conn -> {
            // 1. Khởi tạo phiếu nhập kho
            // Lưu xuống database với trạng thái là "draft"
            // Tạo mã phiếu nhập kho tự động "NK-20250422-XXXXXXXX"
            String receiptCode = generateReceiptCode(inventoryReceipts.getReceiptType());
            inventoryReceipts.setReceiptCode(receiptCode);
            inventoryReceipts.setStatus("draft");
            Long receiptId = inventoryReceiptsDao.saveWithConnection(conn, inventoryReceipts);
            if (receiptId == null) {
                throw new RuntimeException("Lỗi khi tạo phiếu");
            }
            inventoryReceipts.setId(receiptId);

            // 2. Thêm sản phẩm vào phiếu nhập
            List<InventoryReceiptItems> items = inventoryReceipts.getItems();
            for (InventoryReceiptItems item : items) {
                // Lưu từng sản phẩm vào bảng inventory_receipt_items
                item.setReceiptId(receiptId);
            }
            boolean isSaved = inventoryReceiptsDao.saveItemsWithConnection(conn, items);
            if (!isSaved) {
                throw new RuntimeException("Lỗi khi lưu sản phẩm vào phiếu");
            }
            // 3. Cập nhật trạng thái phiếu từ "draft" sang "pending"
            inventoryReceipts.setStatus("pending");

            // cập nhập số lượng totalItems, totalQuantity
            inventoryReceipts.setTotalItems(items.size());
            inventoryReceipts.setTotalQuantity(items.stream().mapToInt(InventoryReceiptItems::getQuantity).sum());

            boolean isUpdated = inventoryReceiptsDao.updateWithConnection(conn, inventoryReceipts);
            if (!isUpdated) {
                throw new RuntimeException("Lỗi khi cập nhật trạng thái phiếu sang 'pending'");
            }
        });
        return inventoryReceipts.getId();
    }

    /**
     * Cập nhập trạng thái phiếu xuất/nhập kho
     *
     * @param code   Mã phiếu
     * @param status Trạng thái chỉ có thể là "cancelled" hoặc "completed"
     */
    public boolean updateInventoryReceiptsStatus(String code, String status) {
        try {
            inventoryReceiptsDao.executeTransaction(conn -> {
                // 1. Kiểm tra và cập nhật trạng thái phiếu xuất/nhập kho "pending" sang "completed"
                InventoryReceipts inventoryReceipts = inventoryReceiptsDao.findByCode(code);
                if (inventoryReceipts == null) {
                    throw new RuntimeException("Không tìm thấy phiếu xuất/nhập kho với mã: " + code);
                }

                // Kiểm tra trạng thái
                if (!status.equals("completed") && !status.equals("cancelled")) {
                    throw new RuntimeException("Trạng thái không hợp lệ");
                }

                // Kiểm tra chuyển trạng thái hợp lệ
                if (!inventoryReceipts.getStatus().equals("pending")) {
                    throw new RuntimeException("Chỉ có thể cập nhật trạng thái của phiếu ở trạng thái 'pending'");
                }

                // Cập nhật trạng thái phiếu
                inventoryReceipts.setStatus(status);
                inventoryReceipts.setCompletedAt(new Timestamp(System.currentTimeMillis()));
                inventoryReceiptsDao.updateWithConnection(conn, inventoryReceipts);

                // 2. Nếu trạng thái là "cancelled", dừng xử lý ở đây
                if (status.equals("cancelled")) {
                    return;
                }

                // Tiếp tục xử lý cho trạng thái "completed"
                // Lấy danh sách sản phẩm trong phiếu
                List<InventoryReceiptItems> items = inventoryReceipts.getItems();

                // Xác định dấu cho số lượng dựa vào loại phiếu
                // Với phiếu xuất, quantity sẽ âm trong inventory_history
                boolean isExport = inventoryReceipts.getReceiptType().equals("export");

                // Lấy danh sách ID sản phẩm
                List<Long> productIds = items.stream()
                        .map(InventoryReceiptItems::getProductId)
                        .collect(Collectors.toList());

                // Lấy thông tin tồn kho hiện tại của các sản phẩm
                Map<Long, InventoryStatus> inventoryStatusMap = new HashMap<>();
                List<InventoryStatus> inventoryStatuses = inventoryStatusDao.findByProductIds(productIds);

                for (InventoryStatus inventoryStatus : inventoryStatuses) {
                    inventoryStatusMap.put(inventoryStatus.getProductId(), inventoryStatus);
                }

                // Xử lý từng sản phẩm trong phiếu
                for (InventoryReceiptItems item : items) {
                    Long productId = item.getProductId();
                    int quantityChange = isExport ? -item.getQuantity() : item.getQuantity();

                    // Lấy số lượng hiện tại
                    int currentQuantity = inventoryStatuses.stream()
                            .filter(ist -> ist.getProductId().equals(productId))
                            .findFirst()
                            .map(InventoryStatus::getActualQuantity)
                            .orElse(0);

                    // Kiểm tra số lượng nếu là xuất kho
                    if (isExport && currentQuantity < item.getQuantity()) {
                        throw new RuntimeException("Không đủ số lượng để xuất kho cho sản phẩm: " + productId);
                    }

                    // Tính toán số lượng mới
                    int newQuantity = currentQuantity + quantityChange;

                    // 3.2 Tạo bản ghi trong inventory_history
                    InventoryHistory history = new InventoryHistory();
                    history.setProductId(productId);
                    history.setQuantityChange(quantityChange);
                    history.setPreviousQuantity(currentQuantity);
                    history.setCurrentQuantity(newQuantity);
                    history.setActionType(InventoryHistory.ActionType.fromValue(inventoryReceipts.getReceiptType())); // "import" hoặc "export"
                    history.setReason("Phiếu " + (isExport ? "xuất" : "nhập") + " kho: " + code);
                    history.setReferenceId(inventoryReceipts.getId());
                    history.setReferenceType("inventory_receipts");
                    history.setNotes(inventoryReceipts.getNotes());
                    history.setCreatedBy(inventoryReceipts.getCreatedBy());

                    // Lưu lịch sử
                    inventoryHistoryDao.saveWithConnection(history, conn);

                    // Cập nhật inventory_status
                    InventoryStatus inventoryStatus = inventoryStatusMap.get(productId);
                    // Cập nhật nếu đã tồn tại
                    inventoryStatus.setActualQuantity(inventoryStatus.getActualQuantity() + quantityChange);
                    inventoryStatus.setAvailableQuantity(inventoryStatus.getAvailableQuantity() + quantityChange);
                    inventoryStatus.setLastUpdated(new Timestamp(System.currentTimeMillis()));
                    inventoryStatusDao.updateWithConnection(inventoryStatus, conn);


                    // 3.1 Cập nhật số lượng trong bảng inventory_import
                    if (isExport) {
                        // Nếu là xuất kho, không cần lưu vào inventory_import
                        continue;
                    }
                    InventoryImport inventoryImport = new InventoryImport();
                    inventoryImport.setProductId(productId);
                    inventoryImport.setQuantity(item.getQuantity());
                    inventoryImport.setSupplier(inventoryReceipts.getSupplier());
                    inventoryImport.setCostPrice(item.getUnitPrice());
                    inventoryImport.setNotes(item.getNotes());
                    inventoryImport.setCreatedBy(inventoryReceipts.getCreatedBy());
                    inventoryImport.setImportDate(new Timestamp(System.currentTimeMillis()));

                    // Lưu vào bảng inventory_import
                    inventoryImportDao.saveWithConnection(conn, inventoryImport);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật trạng thái phiếu xuất/nhập kho", e);
        }

        return true;
    }

    public DataTable<InventoryReceiptDTO> getInventoryReceipts(
            int draw, int start,
            int length, String searchValue,
            int orderColumnIndex, String orderDirection,
            String receiptType, String supplier,
            Date startDate, Date endDate,
            String userFilter, String statusFilter
    ) {
        return inventoryReceiptsDao.getInventoryReceipts(
                draw, start, length, searchValue,
                orderColumnIndex, orderDirection,
                receiptType, supplier,
                startDate, endDate,
                userFilter, statusFilter
        );
    }

    public DataTable<ProductInventoryDTO> getProductsForInventory(
            int draw, int start, int length,
            String searchValue, int orderColumnIndex,
            String orderDirection, Long categoryId,
            String stockFilter
    ){
        return inventoryReceiptsDao.getProductsForInventory(
                draw, start, length,
                searchValue, orderColumnIndex,
                orderDirection, categoryId,
                stockFilter
        );
    }

    public List<ReceiptItemDetailDTO> getItemDetailsByReceiptCode(String receiptCode){
        return inventoryReceiptsDao.getItemDetailsByReceiptCode(receiptCode);
    }

    private String generateReceiptCode(String status) {
        // Lấy ngày hiện tại theo định dạng YYYYMMDD
        java.time.LocalDate today = java.time.LocalDate.now();
        String datePart = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Tạo phần mã ngẫu nhiên (8 ký tự)
        String randomPart = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String prefix = status.equals("import") ? "NK" : "XK";
        return prefix + datePart + "-" + randomPart;
    }
}