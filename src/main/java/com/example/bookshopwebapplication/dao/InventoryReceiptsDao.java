package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.InventoryReceiptItemsMapper;
import com.example.bookshopwebapplication.dao.mapper.InventoryReceiptsMapper;
import com.example.bookshopwebapplication.entities.InventoryReceiptItems;
import com.example.bookshopwebapplication.entities.InventoryReceipts;
import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryReceiptDTO;
import com.example.bookshopwebapplication.http.response_admin.invetory.ProductInventoryDTO;
import com.example.bookshopwebapplication.http.response_admin.invetory.ReceiptItemDetailDTO;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class InventoryReceiptsDao extends AbstractDao<InventoryReceipts> {

    public InventoryReceiptsDao() {
        super("inventory_receipts");
    }

    @Override
    public InventoryReceipts mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return null;
    }

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

    /**
     * Lấy danh sách phiếu nhập/xuất kho với các tham số lọc
     *
     * @param draw             Request counter từ client (DataTables)
     * @param start            Vị trí bắt đầu của bản ghi
     * @param length           Số lượng bản ghi trên mỗi trang
     * @param searchValue      Chuỗi tìm kiếm toàn cục
     * @param orderColumnIndex Cột để sắp xếp (index)
     * @param orderDirection   Hướng sắp xếp (asc hoặc desc)
     * @param receiptType      Loại phiếu (import/export)
     * @param supplier         Nhà cung cấp
     * @param startDate        Ngày bắt đầu (định dạng ISO: YYYY-MM-DD)
     * @param endDate          Ngày kết thúc (định dạng ISO: YYYY-MM-DD)
     * @param userFilter       Người thực hiện
     * @param statusFilter     Trạng thái phiếu (draft/pending/completed/cancelled)
     * @return DataTable<InventoryReceiptDTO>
     */
    public DataTable<InventoryReceiptDTO> getInventoryReceipts(int draw, int start,
                                                               int length, String searchValue,
                                                               int orderColumnIndex, String orderDirection,
                                                               String receiptType, String supplier,
                                                               Date startDate, Date endDate,
                                                               String userFilter, String statusFilter) {

        DataTable<InventoryReceiptDTO> result = new DataTable<>();
        List<InventoryReceiptDTO> items = new ArrayList<>();
        int recordsTotal = 0;
        int recordsFiltered = 0;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            // 1. Lấy tổng số bản ghi (không lọc)
            recordsTotal = countTotalInventoryReceiptsRecords(conn, receiptType);

            // 2. Tạo câu lệnh SQL cơ bản với phần WHERE để lọc
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            sqlBuilder.append("r.id, ");
            sqlBuilder.append("r.receipt_code AS receiptCode, ");
            sqlBuilder.append("r.receipt_type AS receiptType, ");
            sqlBuilder.append("r.supplier, ");
            sqlBuilder.append("r.customer_id AS customerId, ");
            sqlBuilder.append("r.order_id AS orderId, ");
            sqlBuilder.append("r.total_items AS totalItems, ");
            sqlBuilder.append("r.total_quantity AS totalQuantity, ");
            sqlBuilder.append("r.notes, ");
            sqlBuilder.append("r.status, ");
            sqlBuilder.append("r.created_at AS createdAt, ");
            sqlBuilder.append("r.completed_at AS completedAt, ");
            sqlBuilder.append("u.id AS createdById, ");
            sqlBuilder.append("u.fullname AS createdByName, ");
            sqlBuilder.append("(SELECT SUM(ri.quantity * ri.unit_price) FROM inventory_receipt_items ri WHERE ri.receipt_id = r.id) AS totalValue ");
            sqlBuilder.append("FROM bookshopdb.inventory_receipts r ");
            sqlBuilder.append("LEFT JOIN bookshopdb.user u ON r.created_by = u.id ");
            sqlBuilder.append("WHERE 1=1 ");

            // Tạo danh sách tham số
            List<Object> parameters = new ArrayList<>();

            // Thêm các điều kiện lọc
            addFilterConditions(sqlBuilder, parameters, receiptType, supplier,
                    startDate, endDate, userFilter, statusFilter, searchValue);

            // Tạo câu lệnh đếm bản ghi đã lọc
            String countFilteredSql = "SELECT COUNT(*) FROM (" +
                    sqlBuilder.toString() + ") AS filtered_data";

            pstmt = conn.prepareStatement(countFilteredSql);
            this.setParameters(pstmt, parameters);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                recordsFiltered = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

            // Thêm ORDER BY
            sqlBuilder.append("ORDER BY ");
            String[] columns = {"r.id", "r.receipt_code", "r.created_at", "r.supplier",
                    "r.total_items", "r.total_quantity", "totalValue",
                    "u.fullname", "r.notes", "r.status"};
            if (orderColumnIndex >= 0 && orderColumnIndex < columns.length) {
                sqlBuilder.append(columns[orderColumnIndex]);
            } else {
                sqlBuilder.append("r.created_at");  // Default sort column
            }
            sqlBuilder.append(" ").append(orderDirection.equalsIgnoreCase("asc") ? "ASC" : "DESC");

            // Thêm LIMIT
            sqlBuilder.append(" LIMIT ").append(start).append(", ").append(length);

            // Thực thi truy vấn chính
            pstmt = conn.prepareStatement(sqlBuilder.toString());
            this.setParameters(pstmt, parameters);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InventoryReceiptDTO item = new InventoryReceiptDTO(rs);
                items.add(item);
            }
            rs.close();
            pstmt.close();

            // Đặt dữ liệu vào kết quả
            result.setData(items);
            result.setRecordsTotal(recordsTotal);
            result.setRecordsFiltered(recordsFiltered);
            result.setDraw(draw);
        } catch (SQLException e) {
            throw new RuntimeException("Error querying inventory receipts: " + e.getMessage());
        } finally {
            close(conn, pstmt, rs);
        }

        return result;
    }

    /**
     * Đếm tổng số bản ghi phiếu nhập/xuất kho
     */
    private int countTotalInventoryReceiptsRecords(Connection conn, String receiptType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookshopdb.inventory_receipts WHERE receipt_type = ?";
        if (receiptType == null || receiptType.isEmpty()) {
            sql = "SELECT COUNT(*) FROM bookshopdb.inventory_receipts";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (receiptType != null && !receiptType.isEmpty()) {
                pstmt.setString(1, receiptType);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Thêm các điều kiện lọc vào câu truy vấn
     */
    private void addFilterConditions(StringBuilder sqlBuilder, List<Object> parameters,
                                     String receiptType, String supplier,
                                     Date startDate, Date endDate,
                                     String userFilter, String statusFilter,
                                     String searchValue) {
        // Lọc theo loại phiếu
        if (receiptType != null && !receiptType.isEmpty()) {
            sqlBuilder.append("AND r.receipt_type = ? ");
            parameters.add(receiptType);
        }

        // Lọc theo nhà cung cấp
        if (supplier != null && !supplier.isEmpty()) {
            sqlBuilder.append("AND r.supplier LIKE ? ");
            parameters.add("%" + supplier + "%");
        }

        // Lọc theo ngày bắt đầu
        if (startDate != null) {
            sqlBuilder.append("AND DATE(r.created_at) >= ? ");
            parameters.add(startDate);
        }

        // Lọc theo ngày kết thúc
        if (endDate != null ) {
            sqlBuilder.append("AND DATE(r.created_at) <= ? ");
            parameters.add(endDate);
        }

        // Lọc theo người tạo
        if (userFilter != null && !userFilter.isEmpty()) {
            sqlBuilder.append("AND (u.fullname LIKE ? OR u.id = ?) ");
            parameters.add("%" + userFilter + "%");
            try {
                long userId = Long.parseLong(userFilter);
                parameters.add(userId);
            } catch (NumberFormatException e) {
                parameters.add(-1); // Invalid ID that won't match any user
            }
        }

        // Lọc theo trạng thái
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sqlBuilder.append("AND r.status = ? ");
            parameters.add(statusFilter);
        }

        // Tìm kiếm toàn cục
        if (searchValue != null && !searchValue.isEmpty()) {
            sqlBuilder.append("AND (r.receipt_code LIKE ? OR r.supplier LIKE ? OR r.notes LIKE ? OR u.fullname LIKE ?) ");
            parameters.add("%" + searchValue + "%");
            parameters.add("%" + searchValue + "%");
            parameters.add("%" + searchValue + "%");
            parameters.add("%" + searchValue + "%");
        }
    }

    /**
     * Lấy danh sách sản phẩm cho phiếu xuất/nhập kho với các tham số lọc
     *
     * @param draw             Request counter từ client (DataTables)
     * @param start            Vị trí bắt đầu của bản ghi
     * @param length           Số lượng bản ghi trên mỗi trang
     * @param searchValue      Chuỗi tìm kiếm toàn cục
     * @param orderColumnIndex Cột để sắp xếp (index)
     * @param orderDirection   Hướng sắp xếp (asc hoặc desc)
     * @param categoryId       Lọc theo danh mục
     * @param stockFilter      Lọc theo tồn kho ("high", "medium", "low", "out")
     * @return DataTable<ProductInventoryDTO>
     */
    public DataTable<ProductInventoryDTO> getProductsForInventory(int draw, int start, int length,
                                                                  String searchValue, int orderColumnIndex,
                                                                  String orderDirection, Long categoryId,
                                                                  String stockFilter) {
        DataTable<ProductInventoryDTO> result = new DataTable<>();
        List<ProductInventoryDTO> items = new ArrayList<>();
        int recordsTotal = 0;
        int recordsFiltered = 0;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // 1. Lấy tổng số bản ghi sản phẩm
            recordsTotal = countTotalProducts(conn);

            // 2. Tạo câu lệnh SQL để lấy danh sách sản phẩm với thông tin tồn kho
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            sqlBuilder.append("p.id, ");
            sqlBuilder.append("p.name, ");
            sqlBuilder.append("p.price, ");
            sqlBuilder.append("p.discount, ");
            sqlBuilder.append("p.author, ");
            sqlBuilder.append("p.publisher, ");
            sqlBuilder.append("p.imageName, ");
            sqlBuilder.append("p.yearPublishing, ");
            // Thông tin tồn kho
            sqlBuilder.append("ins.actual_quantity, ");
            sqlBuilder.append("ins.available_quantity, ");
            sqlBuilder.append("ins.reserved_quantity, ");
            sqlBuilder.append("ins.reorder_threshold, ");
            // Danh mục sản phẩm
            sqlBuilder.append("(SELECT GROUP_CONCAT(c.name SEPARATOR ', ') FROM bookshopdb.product_category pc ");
            sqlBuilder.append("JOIN bookshopdb.category c ON pc.categoryId = c.id ");
            sqlBuilder.append("WHERE pc.productId = p.id) AS categories, ");
            // Giá nhập gần nhất
            sqlBuilder.append("(SELECT MAX(ii.unit_price) FROM bookshopdb.inventory_receipt_items ii ");
            sqlBuilder.append("JOIN bookshopdb.inventory_receipts ir ON ii.receipt_id = ir.id ");
            sqlBuilder.append("WHERE ii.product_id = p.id AND ir.receipt_type = 'import' AND ir.status = 'completed' ");
            sqlBuilder.append("ORDER BY ir.completed_at DESC LIMIT 1) AS last_import_price ");
            sqlBuilder.append("FROM bookshopdb.product p ");
            sqlBuilder.append("LEFT JOIN bookshopdb.inventory_status ins ON p.id = ins.product_id ");
            sqlBuilder.append("WHERE 1=1 ");

            // Tạo danh sách tham số
            List<Object> parameters = new ArrayList<>();

            // Thêm các điều kiện lọc
            addProductFilterConditions(sqlBuilder, parameters, searchValue, categoryId, stockFilter);

            // Đếm số bản ghi sau khi lọc
            String countFilteredSql = "SELECT COUNT(*) FROM (" + sqlBuilder.toString() + ") AS filtered_data";

            pstmt = conn.prepareStatement(countFilteredSql);
            setParameters(pstmt, parameters);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                recordsFiltered = rs.getInt(1);
            }
            rs.close();
            pstmt.close();

            // Thêm ORDER BY vào câu truy vấn chính
            sqlBuilder.append("ORDER BY ");
            String[] columns = {"p.id", "p.name", "categories", "p.price",
                    "ins.actual_quantity", "ins.reserved_quantity"};
            if (orderColumnIndex >= 0 && orderColumnIndex < columns.length) {
                sqlBuilder.append(columns[orderColumnIndex]);
            } else {
                sqlBuilder.append("p.name");  // Mặc định sắp xếp theo tên
            }
            sqlBuilder.append(" ").append(orderDirection.equalsIgnoreCase("asc") ? "ASC" : "DESC");

            // Thêm LIMIT cho phân trang
            sqlBuilder.append(" LIMIT ").append(start).append(", ").append(length);

            // Thực thi truy vấn chính
            pstmt = conn.prepareStatement(sqlBuilder.toString());
            setParameters(pstmt, parameters);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductInventoryDTO item = new ProductInventoryDTO(rs);
                items.add(item);
            }

            // Đặt dữ liệu vào kết quả
            result.setData(items);
            result.setRecordsTotal(recordsTotal);
            result.setRecordsFiltered(recordsFiltered);
            result.setDraw(draw);

        } catch (SQLException e) {
            throw new RuntimeException("Error querying products for inventory: " + e.getMessage());
        } finally {
            close(conn, pstmt, rs);
        }

        return result;
    }

    /**
     * Đếm tổng số bản ghi sản phẩm
     */
    private int countTotalProducts(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookshopdb.product";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Thêm các điều kiện lọc vào câu truy vấn sản phẩm
     */
    private void addProductFilterConditions(StringBuilder sqlBuilder, List<Object> parameters,
                                            String searchValue, Long categoryId, String stockFilter) {
        // Lọc theo từ khóa tìm kiếm
        if (searchValue != null && !searchValue.isEmpty()) {
            sqlBuilder.append("AND (p.name LIKE ? OR p.author LIKE ? OR p.publisher LIKE ? OR CAST(p.id AS CHAR) = ?) ");
            parameters.add("%" + searchValue + "%");
            parameters.add("%" + searchValue + "%");
            parameters.add("%" + searchValue + "%");
            parameters.add(searchValue);
        }

        // Lọc theo danh mục
        if (categoryId != null && categoryId > 0) {
            sqlBuilder.append("AND p.id IN (SELECT productId FROM bookshopdb.product_category WHERE categoryId = ?) ");
            parameters.add(categoryId);
        }

        // Lọc theo trạng thái tồn kho
        if (stockFilter != null && !stockFilter.isEmpty()) {
            switch (stockFilter) {
                case "high":
                    sqlBuilder.append("AND (ins.actual_quantity > 100 OR (ins.actual_quantity IS NULL AND p.quantity > 100)) ");
                    break;
                case "medium":
                    sqlBuilder.append("AND ((ins.actual_quantity BETWEEN 51 AND 100) OR (ins.actual_quantity IS NULL AND p.quantity BETWEEN 51 AND 100)) ");
                    break;
                case "low":
                    sqlBuilder.append("AND ((ins.actual_quantity BETWEEN 1 AND 50) OR (ins.actual_quantity IS NULL AND p.quantity BETWEEN 1 AND 50)) ");
                    break;
                case "out":
                    sqlBuilder.append("AND ((ins.actual_quantity = 0 OR ins.actual_quantity IS NULL) AND (p.quantity = 0 OR p.quantity IS NULL)) ");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Lấy danh sách chi tiết sản phẩm của phiếu theo mã phiếu sử dụng DTO
     * @param receiptCode Mã phiếu
     * @return Danh sách chi tiết items dưới dạng DTO
     */
    public List<ReceiptItemDetailDTO> getItemDetailsByReceiptCode(String receiptCode) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<ReceiptItemDetailDTO> items = new ArrayList<>();

        try {
            // Câu query JOIN với tất cả bảng liên quan để lấy thông tin trong một lần query
            String sql =
                    "SELECT ri.id, ri.product_id, ri.quantity, ri.unit_price, ri.notes, " +
                            "p.name AS product_name, p.author AS product_author, p.publisher AS product_publisher, " +
                            "p.imageName AS product_image, p.price AS product_price, " +
                            "ins.actual_quantity " +
                            "FROM inventory_receipt_items ri " +
                            "JOIN inventory_receipts r ON ri.receipt_id = r.id " +
                            "JOIN product p ON ri.product_id = p.id " +
                            "LEFT JOIN inventory_status ins ON p.id = ins.product_id " +
                            "WHERE r.receipt_code = ?";

            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, receiptCode);
            resultSet = preparedStatement.executeQuery();

            int rowNum = 0;
            while (resultSet.next()) {
                ReceiptItemDetailDTO item = new ReceiptItemDetailDTO(resultSet);
                items.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Error while getting item details: " + e.getMessage());
        } finally {
            close(connection, preparedStatement, resultSet);
        }

        return items;
    }
}