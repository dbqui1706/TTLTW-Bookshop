package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.InventoryReceiptItemsMapper;
import com.example.bookshopwebapplication.dao.mapper.InventoryReceiptsMapper;
import com.example.bookshopwebapplication.entities.InventoryReceiptItems;
import com.example.bookshopwebapplication.entities.InventoryReceipts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryReceiptsDao extends AbstractDao<InventoryReceipts> {

    public InventoryReceiptsDao() {
        super("inventory_receipts");
    }

    @Override
    public InventoryReceipts mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return null;
    }


    //    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    receipt_code VARCHAR(20) NOT NULL,  -- Mã phiếu, ví dụ: NK-20250422-001
//    receipt_type ENUM('import', 'export') NOT NULL,  -- Loại phiếu: nhập/xuất
//    supplier_id BIGINT NULL,  -- ID nhà cung cấp (cho phiếu nhập)
//    customer_id BIGINT NULL,  -- ID khách hàng (cho phiếu xuất)
//    order_id BIGINT NULL,  -- ID đơn hàng liên quan (nếu có)
//    total_items INT NOT NULL,  -- Tổng số mặt hàng
//    total_quantity INT NOT NULL,  -- Tổng số lượng
//    notes TEXT NULL,  -- Ghi chú
//    status ENUM('draft', 'pending', 'completed', 'cancelled') NOT NULL DEFAULT 'draft',
//    created_by BIGINT NOT NULL,  -- Người tạo
//    approved_by BIGINT NULL,  -- Người duyệt
//    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
//    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//    completed_at DATETIME NULL  -- Thời gian hoàn thành
    public Long saveWithConnection(Connection conn, InventoryReceipts inventoryReceipts) {
        try {
            clearSQL();
            builderSQL.append("INSERT INTO inventory_receipts (receipt_code, receipt_type, supplier, customer_id, " +
                    "order_id, total_items, total_quantity, notes, status, created_by, approved_by) ");
            builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            return insertWithConnection(conn, builderSQL.toString(),
                    inventoryReceipts.getReceiptCode(),
                    inventoryReceipts.getReceiptType(),
                    inventoryReceipts.getSupplier(),
                    inventoryReceipts.getCustomerId(),
                    inventoryReceipts.getOrderId(),
                    inventoryReceipts.getTotalItems(),
                    inventoryReceipts.getTotalQuantity(),
                    inventoryReceipts.getNotes(),
                    inventoryReceipts.getStatus(),
                    inventoryReceipts.getCreatedBy(),
                    inventoryReceipts.getApprovedBy()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveItemsWithConnection(Connection conn, List<InventoryReceiptItems> items) {
        PreparedStatement stmt = null;
        try {
            clearSQL();
            builderSQL.append("INSERT INTO inventory_receipt_items (receipt_id, product_id, quantity, unit_price, notes) ");
            builderSQL.append("VALUES ");
            String values = items.stream()
                    .map(item -> "(?, ?, ?, ?, ?)")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            builderSQL.append(values);

            stmt = conn.prepareStatement(builderSQL.toString());
            List<Object> parameters = new java.util.ArrayList<>(List.of());
            for (InventoryReceiptItems item : items) {
                parameters.add(item.getReceiptId());
                parameters.add(item.getProductId());
                parameters.add(item.getQuantity());
                parameters.add(item.getUnitPrice());
                parameters.add(item.getNotes());
            }
            setParameters(stmt, parameters);
            int rowsInserted = stmt.executeUpdate();

            return rowsInserted > 0;

        } catch (Exception e) {
            System.out.println("Error while saving items: " + e.getMessage());
            return false;
        } finally {
            close(null, stmt, null);
        }
    }

    private void setParameters(PreparedStatement pstmt, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            pstmt.setObject(i + 1, parameters.get(i));
        }
    }

    public boolean updateWithConnection(Connection conn, InventoryReceipts inventoryReceipts) {
        try {
            clearSQL();
            builderSQL.append("UPDATE inventory_receipts SET ");
            builderSQL.append("receipt_code = ?, receipt_type = ?, supplier = ?, customer_id = ?, order_id = ?, ");
            builderSQL.append("total_items = ?, total_quantity = ?, notes = ?, status = ?, ");
            builderSQL.append("approved_by = ? ");
            builderSQL.append("WHERE id = ?");

            updateWithConnection(conn, builderSQL.toString(),
                    inventoryReceipts.getReceiptCode(),
                    inventoryReceipts.getReceiptType(),
                    inventoryReceipts.getSupplier(),
                    inventoryReceipts.getCustomerId(),
                    inventoryReceipts.getOrderId(),
                    inventoryReceipts.getTotalItems(),
                    inventoryReceipts.getTotalQuantity(),
                    inventoryReceipts.getNotes(),
                    inventoryReceipts.getStatus(),
                    inventoryReceipts.getApprovedBy(),
                    inventoryReceipts.getId()
            );
            return true;
        } catch (Exception e) {
            System.out.println("Error while updating inventory receipt: " + e.getMessage());
            return false;
        }
    }

    public InventoryReceipts findByCode(String code) {
        clearSQL();
        builderSQL.append("SELECT * FROM inventory_receipts WHERE receipt_code = ?");
        List<InventoryReceipts> result = query(builderSQL.toString(), new InventoryReceiptsMapper(), code);
        InventoryReceipts inventoryReceipts = result.isEmpty() ? null : result.get(0);

        if (inventoryReceipts == null) {
            throw new RuntimeException("Không tìm thấy phiếu nhập với mã: " + code);
        }
        // Lay danh sách các mặt hàng liên quan đến phiếu nhập

        clearSQL();
        builderSQL.append("SELECT * FROM inventory_receipt_items WHERE receipt_id = ?");
        Connection connection = null;
        List<InventoryReceiptItems> items = new ArrayList<>();
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(builderSQL.toString());
            preparedStatement.setLong(1, inventoryReceipts.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            InventoryReceiptItemsMapper itemsMapper = new InventoryReceiptItemsMapper();
            while (resultSet.next()) {
                InventoryReceiptItems item = itemsMapper.mapRow(resultSet);
                items.add(item);
            }
            inventoryReceipts.setItems(items);
        } catch (SQLException e) {
            System.out.println("Error while finding items: " + e.getMessage());
        } finally {
            close(connection, null, null);
        }
        return result.isEmpty() ? null : result.get(0);
    }
}
