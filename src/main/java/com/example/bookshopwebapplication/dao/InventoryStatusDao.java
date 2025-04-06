package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.InventoryStatusMapper;
import com.example.bookshopwebapplication.entities.InventoryStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class InventoryStatusDao extends AbstractDao<InventoryStatus> {

    public InventoryStatusDao() {
        super("inventory_status");
    }

    /**
     * Lấy thông tin tồn kho theo ID sản phẩm
     *
     * @param productId ID của sản phẩm
     * @return Optional chứa thông tin tồn kho hoặc empty nếu không tìm thấy
     */
    public Optional<InventoryStatus> findByProductId(Long productId) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_status WHERE product_id = ?");
        List<InventoryStatus> result = query(builderSQL.toString(), new InventoryStatusMapper(), productId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    /**
     * Cập nhật thông tin tồn kho
     *
     * @param status đối tượng InventoryStatus cần cập nhật
     */
    public void update(InventoryStatus status) {
        clearSQL();
        builderSQL.append("UPDATE inventory_status SET ");
        builderSQL.append("actual_quantity = ?, available_quantity = ?, reserved_quantity = ?, ");
        builderSQL.append("reorder_threshold = ?, last_updated = CURRENT_TIMESTAMP ");
        builderSQL.append("WHERE id = ?");

        update(builderSQL.toString(),
                status.getActualQuantity(),
                status.getAvailableQuantity(),
                status.getReservedQuantity(),
                status.getReorderThreshold(),
                status.getId());
    }

    public boolean updateReservedQuantityWithConnection(Long productId, int newReservedQuantity, Connection conn) {
        clearSQL();
        builderSQL.append("UPDATE bookshopdb.inventory_status SET available_quantity = available_quantity - 1,  \n" +
                "reserved_quantity = 1 WHERE product_id = 51;");
        updateWithConnection(conn, builderSQL.toString(), newReservedQuantity,
                newReservedQuantity, Timestamp.valueOf(LocalDateTime.now()), productId);
        return true;
    }

    /**
     * Lấy danh sách các sản phẩm có số lượng dưới ngưỡng cảnh báo
     *
     * @return Danh sách các thông tin tồn kho dưới ngưỡng
     */
    public List<InventoryStatus> findBelowThreshold() {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_status ");
        builderSQL.append("WHERE actual_quantity <= reorder_threshold ");
        builderSQL.append("ORDER BY (reorder_threshold - actual_quantity) DESC");

        return query(builderSQL.toString(), new InventoryStatusMapper());
    }

    /**
     * Cập nhật số lượng đặt trước cho sản phẩm
     *
     * @param productId      ID sản phẩm
     * @param quantityChange Số lượng thay đổi (dương: tăng, âm: giảm)
     * @return true nếu cập nhật thành công, false nếu không đủ số lượng
     */
    public boolean updateReservedQuantity(Long productId, int quantityChange) {
        Optional<InventoryStatus> statusOpt = findByProductId(productId);
        if (!statusOpt.isPresent()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        int newReserved = status.getReservedQuantity() + quantityChange;
        int newAvailable = status.getAvailableQuantity() - quantityChange;

        // Kiểm tra nếu số lượng khả dụng không đủ
        if (newAvailable < 0) {
            return false;
        }

        clearSQL();
        builderSQL.append("UPDATE inventory_status SET ");
        builderSQL.append("available_quantity = ?, reserved_quantity = ?, ");
        builderSQL.append("last_updated = CURRENT_TIMESTAMP ");
        builderSQL.append("WHERE product_id = ?");

        update(builderSQL.toString(), newAvailable, newReserved, productId);
        return true;
    }

    /**
     * Giảm số lượng thực tế trong kho (khi giao hàng) và giảm số lượng đặt trước
     *
     * @param productId ID sản phẩm
     * @param quantity  Số lượng cần giảm
     * @return true nếu cập nhật thành công
     */
    public boolean decreaseActualQuantity(Long productId, int quantity) {
        Optional<InventoryStatus> statusOpt = findByProductId(productId);
        if (!statusOpt.isPresent()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        if (status.getActualQuantity() < quantity) {
            return false;
        }

        int newActual = status.getActualQuantity() - quantity;
        int newReserved = Math.max(0, status.getReservedQuantity() - quantity);

        clearSQL();
        builderSQL.append("UPDATE inventory_status SET ");
        builderSQL.append("actual_quantity = ?, reserved_quantity = ?, ");
        builderSQL.append("last_updated = CURRENT_TIMESTAMP ");
        builderSQL.append("WHERE product_id = ?");

        update(builderSQL.toString(), newActual, newReserved, productId);
        return true;
    }

    /**
     * Tăng số lượng thực tế trong kho (khi nhập hàng)
     *
     * @param productId ID sản phẩm
     * @param quantity  Số lượng cần tăng
     * @return true nếu cập nhật thành công
     */
    public boolean increaseActualQuantity(Long productId, int quantity) {
        Optional<InventoryStatus> statusOpt = findByProductId(productId);
        if (statusOpt.isEmpty()) {
            return false;
        }

        InventoryStatus status = statusOpt.get();
        int newActual = status.getActualQuantity() + quantity;
        int newAvailable = status.getAvailableQuantity() + quantity;

        clearSQL();
        builderSQL.append("UPDATE inventory_status SET ");
        builderSQL.append("actual_quantity = ?, available_quantity = ?, ");
        builderSQL.append("last_updated = CURRENT_TIMESTAMP ");
        builderSQL.append("WHERE product_id = ?");

        update(builderSQL.toString(), newActual, newAvailable, productId);
        return true;
    }

    @Override
    public InventoryStatus mapResultSetToEntity(ResultSet rs) throws SQLException {
        return InventoryStatus.builder()
                .id(rs.getLong("id"))
                .productId(rs.getLong("product_id"))
                .actualQuantity(rs.getInt("actual_quantity"))
                .availableQuantity(rs.getInt("available_quantity"))
                .reservedQuantity(rs.getInt("reserved_quantity"))
                .reorderThreshold(rs.getInt("reorder_threshold"))
                .lastUpdated(rs.getTimestamp("last_updated"))
                .build();
    }

    public boolean updateWithConnection(InventoryStatus inventoryStatusObj, Connection conn) {
        try {
            clearSQL();
            builderSQL.append("UPDATE inventory_status SET ");
            builderSQL.append("actual_quantity = ?, available_quantity = ?, reserved_quantity = ?, ");
            builderSQL.append("reorder_threshold = ?, last_updated = CURRENT_TIMESTAMP ");
            builderSQL.append("WHERE product_id = ?");
            updateWithConnection(conn, builderSQL.toString(),
                    inventoryStatusObj.getActualQuantity(),
                    inventoryStatusObj.getAvailableQuantity(),
                    inventoryStatusObj.getReservedQuantity(),
                    inventoryStatusObj.getReorderThreshold(),
                    inventoryStatusObj.getProductId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}