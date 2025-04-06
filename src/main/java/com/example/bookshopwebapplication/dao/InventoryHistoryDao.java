package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.InventoryHistoryMapper;
import com.example.bookshopwebapplication.entities.InventoryHistory;
import com.example.bookshopwebapplication.entities.InventoryStatus;
import com.example.bookshopwebapplication.utils.RequestContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class InventoryHistoryDao extends AbstractDao<InventoryHistory> {

    private final InventoryStatusDao inventoryStatusDao;

    public InventoryHistoryDao() {
        super("inventory_history");
        this.inventoryStatusDao = new InventoryStatusDao();
    }

    public Long saveWithConnection(InventoryHistory history, Connection conn) {
        try {
            clearSQL();
            builderSQL.append("INSERT INTO inventory_history (product_id, quantity_change, previous_quantity, current_quantity, ");
            builderSQL.append("action_type, reason, reference_id, reference_type, notes, created_by) ");
            builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            return insertWithConnection(conn, builderSQL.toString(), history.getProductId(), history.getQuantityChange(),
                    history.getPreviousQuantity(), history.getCurrentQuantity(), history.getActionType().getValue(),
                    history.getReason(), history.getReferenceId(), history.getReferenceType(), history.getNotes(),
                    history.getCreatedBy()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ghi lại lịch sử thay đổi tồn kho khi nhập hàng
     *
     * @param productId   ID sản phẩm
     * @param quantity    Số lượng nhập
     * @param reason      Lý do
     * @param referenceId ID tham chiếu (ID của phiếu nhập)
     * @return ID của bản ghi lịch sử
     */
    public Long recordImport(Long productId, int quantity, String reason, Long referenceId) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy thông tin tồn kho cho sản phẩm ID: " + productId);
        }

        InventoryStatus status = statusOpt.get();
        int previousQuantity = status.getActualQuantity() - quantity; // Số lượng trước khi nhập

        return saveHistoryRecord(
                productId,
                quantity, // Luôn dương khi nhập hàng
                previousQuantity,
                status.getActualQuantity(),
                InventoryHistory.ActionType.IMPORT,
                reason,
                referenceId,
                "import",
                null
        );
    }

    /**
     * Ghi lại lịch sử thay đổi tồn kho khi xuất hàng
     *
     * @param productId   ID sản phẩm
     * @param quantity    Số lượng xuất (dương)
     * @param reason      Lý do
     * @param referenceId ID tham chiếu (ID của đơn hàng)
     * @return ID của bản ghi lịch sử
     */
    public Long recordExport(Long productId, int quantity, String reason, Long referenceId) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy thông tin tồn kho cho sản phẩm ID: " + productId);
        }

        InventoryStatus status = statusOpt.get();
        int previousQuantity = status.getActualQuantity() + quantity; // Số lượng trước khi xuất

        return saveHistoryRecord(
                productId,
                -quantity, // Âm khi xuất hàng
                previousQuantity,
                status.getActualQuantity(),
                InventoryHistory.ActionType.EXPORT,
                reason,
                referenceId,
                "order",
                null
        );
    }

    /**
     * Ghi lại lịch sử thay đổi tồn kho khi điều chỉnh
     *
     * @param productId      ID sản phẩm
     * @param quantityChange Số lượng thay đổi (dương hoặc âm)
     * @param reason         Lý do
     * @param notes          Ghi chú bổ sung
     * @return ID của bản ghi lịch sử
     */
    public Long recordAdjustment(Long productId, int quantityChange, String reason, String notes) {
        Optional<InventoryStatus> statusOpt = inventoryStatusDao.findByProductId(productId);
        if (!statusOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy thông tin tồn kho cho sản phẩm ID: " + productId);
        }

        InventoryStatus status = statusOpt.get();
        int previousQuantity = status.getActualQuantity() - quantityChange;

        return saveHistoryRecord(
                productId,
                quantityChange,
                previousQuantity,
                status.getActualQuantity(),
                InventoryHistory.ActionType.ADJUSTMENT,
                reason,
                null,
                null,
                notes
        );
    }

    /**
     * Lưu bản ghi lịch sử thay đổi tồn kho
     */
    private Long saveHistoryRecord(Long productId, int quantityChange, int previousQuantity,
                                   int currentQuantity, InventoryHistory.ActionType actionType,
                                   String reason, Long referenceId, String referenceType, String notes) {
        clearSQL();
        builderSQL.append("INSERT INTO inventory_history ");
        builderSQL.append("(product_id, quantity_change, previous_quantity, current_quantity, ");
        builderSQL.append("action_type, reason, reference_id, reference_type, notes, created_by) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        Long userId = RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L;

        return insert(builderSQL.toString(),
                productId,
                quantityChange,
                previousQuantity,
                currentQuantity,
                actionType.getValue(),
                reason,
                referenceId,
                referenceType,
                notes,
                userId);
    }

    /**
     * Lấy lịch sử thay đổi tồn kho của một sản phẩm
     *
     * @param productId ID sản phẩm
     * @return Danh sách các bản ghi lịch sử
     */
    public List<InventoryHistory> findByProductId(Long productId) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_history WHERE product_id = ? ");
        builderSQL.append("ORDER BY created_at DESC");

        return query(builderSQL.toString(), new InventoryHistoryMapper(), productId);
    }

    /**
     * Lấy lịch sử thay đổi tồn kho theo loại hành động
     *
     * @param actionType Loại hành động
     * @return Danh sách các bản ghi lịch sử
     */
    public List<InventoryHistory> findByActionType(InventoryHistory.ActionType actionType) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_history WHERE action_type = ? ");
        builderSQL.append("ORDER BY created_at DESC");

        return query(builderSQL.toString(), new InventoryHistoryMapper(), actionType.getValue());
    }

    /**
     * Lấy lịch sử thay đổi tồn kho theo khoảng thời gian
     *
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách các bản ghi lịch sử
     */
    public List<InventoryHistory> findByDateRange(Timestamp startDate, Timestamp endDate) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_history WHERE created_at BETWEEN ? AND ? ");
        builderSQL.append("ORDER BY created_at DESC");

        return query(builderSQL.toString(), new InventoryHistoryMapper(), startDate, endDate);
    }

    /**
     * Lấy lịch sử thay đổi tồn kho liên quan đến một tham chiếu cụ thể
     *
     * @param referenceId   ID tham chiếu
     * @param referenceType Loại tham chiếu
     * @return Danh sách các bản ghi lịch sử
     */
    public List<InventoryHistory> findByReference(Long referenceId, String referenceType) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_history WHERE reference_id = ? AND reference_type = ? ");
        builderSQL.append("ORDER BY created_at DESC");

        return query(builderSQL.toString(), new InventoryHistoryMapper(), referenceId, referenceType);
    }

    @Override
    public InventoryHistory mapResultSetToEntity(ResultSet rs) throws SQLException {
        return InventoryHistory.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .quantityChange(rs.getInt("quantity_change"))
                .previousQuantity(rs.getInt("previous_quantity"))
                .currentQuantity(rs.getInt("current_quantity"))
                .actionType(InventoryHistory.ActionType.fromValue(rs.getString("action_type")))
                .reason(rs.getString("reason"))
                .referenceId(rs.getLong("reference_id"))
                .referenceType(rs.getString("reference_type"))
                .notes(rs.getString("notes"))
                .createdBy(rs.getLong("created_by"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }
}