package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.InventoryImportMapper;
import com.example.bookshopwebapplication.entities.InventoryImport;
import com.example.bookshopwebapplication.utils.RequestContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InventoryImportDao extends AbstractDao<InventoryImport> {

    private final InventoryStatusDao inventoryStatusDao;
    private final InventoryHistoryDao inventoryHistoryDao;

    public InventoryImportDao() {
        super("inventory_import");
        this.inventoryStatusDao = new InventoryStatusDao();
        this.inventoryHistoryDao = new InventoryHistoryDao();
    }

    /**
     * Lưu thông tin nhập kho và cập nhật số lượng tồn kho
     * @param importRecord Thông tin nhập kho
     * @return ID của bản ghi nhập kho
     */
    public Long save(InventoryImport importRecord) {
        clearSQL();
        builderSQL.append("INSERT INTO inventory_import ");
        builderSQL.append("(product_id, quantity, cost_price, supplier, import_date, notes, created_by) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?)");

        Long userId = RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L;

        // Thêm vào bảng inventory_import
        Long importId = insert(builderSQL.toString(),
                importRecord.getProductId(),
                importRecord.getQuantity(),
                importRecord.getCostPrice(),
                importRecord.getSupplier(),
                importRecord.getImportDate() != null ? importRecord.getImportDate() : new Timestamp(System.currentTimeMillis()),
                importRecord.getNotes(),
                importRecord.getCreatedBy() != null ? importRecord.getCreatedBy() : userId);

        // Cập nhật số lượng tồn kho
        if (importId != null) {
            inventoryStatusDao.increaseActualQuantity(importRecord.getProductId(), importRecord.getQuantity());

            // Cập nhật bảng product (quantity)
            updateProductQuantity(importRecord.getProductId(), importRecord.getQuantity());

            // Ghi log lịch sử
            inventoryHistoryDao.recordImport(importRecord.getProductId(), importRecord.getQuantity(),
                    "Nhập kho từ " + importRecord.getSupplier(), importId);
        }

        return importId;
    }

    /**
     * Lấy danh sách lịch sử nhập kho theo sản phẩm
     * @param productId ID sản phẩm
     * @return Danh sách các bản ghi nhập kho
     */
    public List<InventoryImport> findByProductId(Long productId) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_import WHERE product_id = ? ");
        builderSQL.append("ORDER BY import_date DESC");

        return query(builderSQL.toString(), new InventoryImportMapper(), productId);
    }

    /**
     * Lấy danh sách lịch sử nhập kho theo khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách các bản ghi nhập kho
     */
    public List<InventoryImport> findByDateRange(Timestamp startDate, Timestamp endDate) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_import WHERE import_date BETWEEN ? AND ? ");
        builderSQL.append("ORDER BY import_date DESC");

        return query(builderSQL.toString(), new InventoryImportMapper(), startDate, endDate);
    }

    /**
     * Lấy thông tin chi tiết của một phiếu nhập kho
     * @param importId ID của phiếu nhập kho
     * @return Optional chứa thông tin phiếu nhập hoặc empty nếu không tìm thấy
     */
    public Optional<InventoryImport> findById(Long importId) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_import WHERE id = ?");

        List<InventoryImport> results = query(builderSQL.toString(), new InventoryImportMapper(), importId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Cập nhật số lượng trong bảng product
     * @param productId ID sản phẩm
     * @param quantityToAdd Số lượng cần thêm
     */
    private void updateProductQuantity(Long productId, int quantityToAdd) {
        clearSQL();
        builderSQL.append("UPDATE product SET quantity = quantity + ?, ");
        builderSQL.append("updatedAt = CURRENT_TIMESTAMP ");
        builderSQL.append("WHERE id = ?");

        try (var conn = getConnection();
             var stmt = conn.prepareStatement(builderSQL.toString())) {
            stmt.setInt(1, quantityToAdd);
            stmt.setLong(2, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật số lượng sản phẩm", e);
        }
    }

    @Override
    public InventoryImport mapResultSetToEntity(ResultSet rs) throws SQLException {
        return InventoryImport.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .quantity(rs.getInt("quantity"))
                .costPrice(rs.getDouble("cost_price"))
                .supplier(rs.getString("supplier"))
                .importDate(rs.getTimestamp("import_date"))
                .notes(rs.getString("notes"))
                .createdBy(rs.getLong("created_by"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }

    public void saveWithConnection(Connection conn, InventoryImport inventoryImport) {
        clearSQL();
        builderSQL.append("INSERT INTO inventory_import ");
        builderSQL.append("(product_id, quantity, cost_price, supplier, import_date, notes, created_by) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?)");
        insertWithConnection(conn, builderSQL.toString(), inventoryImport.getProductId(),
                inventoryImport.getQuantity(), inventoryImport.getCostPrice(), inventoryImport.getSupplier(),
                inventoryImport.getImportDate() != null ? inventoryImport.getImportDate() : new Timestamp(System.currentTimeMillis())
                , inventoryImport.getNotes(), inventoryImport.getCreatedBy()
                );
    }
}