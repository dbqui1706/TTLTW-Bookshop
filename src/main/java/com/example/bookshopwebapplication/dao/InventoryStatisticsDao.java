package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class InventoryStatisticsDao extends AbstractDao<Object> {
    public InventoryStatisticsDao() {
        super("");
    }

    @Override
    public Object mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dữ liệu thống kê tồn kho theo xu hướng
     * day: XU HƯỚNG TỒN KHO THEO NGÀY (trong 7 ngày gần nhất)
     * week: XU HƯỚNG TỒN KHO THEO TUẦN (trong 6 tuần gần nhất)
     * month: XU HƯỚNG TỒN KHO THEO THÁNG (trong 6 tháng gần nhất)
     * quater: XU HƯỚNG TỒN KHO THEO QUÝ (trong 4 quý gần nhất)
     *
     * @param frequency day, week, month, quater
     * @return List<InventoryTrendData>
     */
    public List<InventoryTrendData> getInventoryTrendData(String frequency) {
        List<InventoryTrendData> result = new ArrayList<InventoryTrendData>();
        Connection connection = null;
        CallableStatement statement = null; // CallableStatement để gọi thủ tục
        ResultSet rs = null;
        try {
            connection = getConnection();
            statement = connection.prepareCall("{CALL bookshopdb.GetInventoryTrends(?)}");
            statement.setString(1, frequency);
            rs = statement.executeQuery();
            while (rs.next()) {
                InventoryTrendData data = new InventoryTrendData(rs);
                result.add(data);
            }
        } catch (SQLException e) {
            System.out.println("Error creating connection or statement: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(connection, statement, rs);
        }
        return result;
    }

    /**
     * Dữ liệu thống kê tồn kho theo phân phối
     *
     * @return List<InventoryDistributionData>
     */
    public List<InventoryDistributionData> getInventoryDistribution() {
        List<InventoryDistributionData> result = new ArrayList<InventoryDistributionData>();
        // Query dữ liệu từ cơ sở dữ liệu
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.name AS category_name, COUNT(DISTINCT p.id) AS product_count, ");
        sql.append("SUM(is2.actual_quantity) AS total_quantity, ");
        sql.append("ROUND(SUM(is2.actual_quantity * p.price), 0) AS total_value, ");
        sql.append("ROUND(SUM(is2.actual_quantity) / ( ");
        sql.append("SELECT SUM(actual_quantity) FROM bookshopdb.inventory_status) * 100, 1) AS percentage_by_quantity ");
        sql.append("FROM bookshopdb.category c ");
        sql.append("JOIN bookshopdb.product_category pc ON c.id = pc.categoryId ");
        sql.append("JOIN bookshopdb.product p ON pc.productId = p.id ");
        sql.append("JOIN bookshopdb.inventory_status is2 ON p.id = is2.product_id ");
        sql.append("GROUP BY c.id, c.name ");
        sql.append("ORDER BY total_quantity DESC;");

        Connection connection = null;
        PreparedStatement statement = null; // CallableStatement để gọi thủ tục
        ResultSet rs = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            rs = statement.executeQuery();
            while (rs.next()) {
                InventoryDistributionData data = new InventoryDistributionData(rs);
                result.add(data);
            }
        } catch (SQLException e) {
            System.out.println("Error creating connection or statement: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(connection, statement, rs);
        }
        return result;
    }

    /**
     * Lấy danh sách tồn kho với các bộ lọc
     *
     * @param stockStatus   Trạng thái tồn kho (out, low, normal)
     * @param categoryId    ID danh mục
     * @param durationDays  Số ngày tồn kho tối thiểu
     * @param startDate     Ngày bắt đầu khoảng thời gian cập nhật
     * @param endDate       Ngày kết thúc khoảng thời gian cập nhật
     * @param searchKeyword Từ khóa tìm kiếm
     * @param start         Vị trí bắt đầu
     * @param length        Số lượng bản ghi
     * @param sortColumn    Cột sắp xếp
     * @param sortDirection Hướng sắp xếp (asc, desc)
     * @return DataTable<InventoryItem> chứa tổng số bản ghi và danh sách tồn kho
     */
    public DataTable<InventoryItem> getInventoryItems
    (String stockStatus, Long categoryId,
     Integer durationDays, Timestamp startDate, Timestamp endDate, String searchKeyword,
     int start, int length, String sortColumn, String sortDirection) {

        // Khởi tạo đối tượng DataTable
        DataTable<InventoryItem> table = new DataTable<>();
        List<InventoryItem> items = new ArrayList<>();
        int totalRecords = 0;
        int totalRecordsFiltered = 0;

        // Query dữ liệu từ cơ sở dữ liệu
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // Đếm tổng số bản ghi
            totalRecords = countAll(conn);

            // Main query
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p.id AS product_id, p.name AS product_name, p.author AS product_author, ")
                    .append("p.imageName AS product_image, (")
                    .append("    SELECT GROUP_CONCAT(c.name SEPARATOR ', ') ")
                    .append("    FROM bookshopdb.product_category pc ")
                    .append("    JOIN bookshopdb.category c ON pc.categoryId = c.id ")
                    .append("    WHERE pc.productId = p.id ")
                    .append(") AS categories, ist.actual_quantity AS stock_quantity, ")
                    .append("ist.reserved_quantity AS reserved_quantity, ist.available_quantity AS available_quantity, ")
                    .append("ist.reorder_threshold AS threshold, ist.last_updated AS last_updated, ")
                    .append("    DATEDIFF(CURRENT_DATE(), ")
                    .append("        COALESCE( ")
                    .append("            (SELECT MIN(ii.import_date) FROM bookshopdb.inventory_import ii WHERE ii.product_id = p.id), ")
                    .append("            p.createdAt ")
                    .append("        ) ")
                    .append("    ) AS days_in_inventory, ")
                    .append("    CASE ")
                    .append("        WHEN EXISTS ( ")
                    .append("            SELECT 1 FROM bookshopdb.inventory_history ")
                    .append("            WHERE product_id = p.id AND action_type = 'export' ")
                    .append("        ) THEN DATEDIFF(CURRENT_DATE(), ")
                    .append("            (SELECT MAX(created_at) ")
                    .append("             FROM bookshopdb.inventory_history ")
                    .append("             WHERE product_id = p.id AND action_type = 'export') ")
                    .append("        ) ")
                    .append("        ELSE DATEDIFF(CURRENT_DATE(), ")
                    .append("            COALESCE( ")
                    .append("                (SELECT MIN(ii.import_date) FROM bookshopdb.inventory_import ii WHERE ii.product_id = p.id), ")
                    .append("                p.createdAt ")
                    .append("            ) ")
                    .append("        ) ")
                    .append("    END AS days_since_last_sale, ")
                    .append("    CASE ")
                    .append("        WHEN NOT EXISTS ( ")
                    .append("            SELECT 1 FROM bookshopdb.inventory_history ")
                    .append("            WHERE product_id = p.id AND action_type = 'export' ")
                    .append("        ) THEN 'Chưa bán lần nào' ")
                    .append("        WHEN DATEDIFF(CURRENT_DATE(), ")
                    .append("            (SELECT MAX(created_at) ")
                    .append("             FROM bookshopdb.inventory_history ")
                    .append("             WHERE product_id = p.id AND action_type = 'export') ")
                    .append("        ) > 90 THEN 'Không bán được > 90 ngày' ")
                    .append("        WHEN DATEDIFF(CURRENT_DATE(), ")
                    .append("            (SELECT MAX(created_at) ")
                    .append("             FROM bookshopdb.inventory_history ")
                    .append("             WHERE product_id = p.id AND action_type = 'export') ")
                    .append("        ) > 60 THEN 'Không bán được > 60 ngày' ")
                    .append("        WHEN DATEDIFF(CURRENT_DATE(), ")
                    .append("            (SELECT MAX(created_at) ")
                    .append("             FROM bookshopdb.inventory_history ")
                    .append("             WHERE product_id = p.id AND action_type = 'export') ")
                    .append("        ) > 30 THEN 'Không bán được > 30 ngày' ")
                    .append("        ELSE 'Bán hàng bình thường' ")
                    .append("    END AS sales_status, ")
                    .append("    CASE ")
                    .append("        WHEN ist.actual_quantity = 0 THEN 'out' ")
                    .append("        WHEN ist.actual_quantity <= ist.reorder_threshold THEN 'low' ")
                    .append("        ELSE 'normal' ")
                    .append("    END AS stock_status, ")
                    .append("    p.price AS price, ")
                    .append("    (p.price * ist.actual_quantity) AS inventory_value ")
                    .append("FROM ")
                    .append("    bookshopdb.product p ")
                    .append("JOIN ")
                    .append("    bookshopdb.inventory_status ist ON p.id = ist.product_id ")
                    .append("LEFT JOIN ")
                    .append("    bookshopdb.product_category pc ON p.id = pc.productId ")
                    .append("WHERE 1=1 ");

            // Tạo danh sách tham số
            List<Object> parameters = new ArrayList<>();

            // Điều kiện lọc
            // 1. Lọc theo trạng thái tồn kho
            filterByStockStatus(sql, parameters, stockStatus);
            // 2. Lọc theo danh mục
            filterByCategory(sql, parameters, categoryId);
            // 3. Lọc theo thời gian tồn kho
            filterByDuration(sql, parameters, durationDays);
            // 4. Lọc theo khoảng thời gian cập nhật
            filterByDateRange(sql, parameters, startDate, endDate);
            // 5. Tìm kiếm theo từ khóa
            filterByKeyword(sql, parameters, searchKeyword);

            // Thực hiện truy vấn
            sql.append("GROUP BY p.id ");

            // Đếm số bản ghi sau khi lọc
            totalRecordsFiltered = countFilteredRecords(conn, sql.toString(), parameters);

            // Thêm điều kiện sắp xếp
            // Mặc định sắp xếp theo số ngày không bán và trạng thái tồn kho
            sortCondition(sql, sortColumn, sortDirection);
            // Thêm LIMIT
            sql.append(" LIMIT ").append(start).append(", ").append(length);

            // Tạo PreparedStatement
            stm = conn.prepareStatement(sql.toString());
            // Thiết lập các tham số
            for (int i = 0; i < parameters.size(); i++) {
                stm.setObject(i + 1, parameters.get(i));
            }
            rs = stm.executeQuery();
            while (rs.next()) {
                InventoryItem item = new InventoryItem(rs);
                items.add(item);
            }

            // Đặt dữ liệu vào đối tượng DataTable
            table.setData(items);
            table.setRecordsTotal(totalRecords);
            table.setRecordsFiltered(totalRecordsFiltered);
        } catch (Exception e) {
            System.out.println("Lỗi trong quá trình InventoryStatisticsDao.getInventoryItems() " + e.getMessage());
            throw new RuntimeException("Lỗi trong quá trình InventoryStatisticsDao.getInventoryItems()", e);
        } finally {
            close(conn, stm, rs);
        }
        return table;
    }

    /**
     * Lấy danh sách phiếu nhập gần đây
     *
     * @return List<InventoryRecently>
     */
    public List<InventoryRecently> getInventoryImportRecently() {
        List<InventoryRecently> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ii.id AS id, p.id AS product_id, p.name AS product_name, ");
        sql.append("p.author as product_author, p.imageName AS product_image, ii.import_date AS date,");
        sql.append("ii.quantity AS quantity, u.fullname AS created_by_name ");
        sql.append("FROM bookshopdb.inventory_import ii ");
        sql.append("JOIN bookshopdb.product p ON ii.product_id = p.id ");
        sql.append("JOIN bookshopdb.user u ON ii.created_by = u.id ");
        sql.append("ORDER BY ii.import_date DESC LIMIT 5;");

        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stm = conn.prepareStatement(sql.toString());
            rs = stm.executeQuery();
            while (rs.next()) {
                InventoryRecently item = new InventoryRecently(rs);
                result.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error creating connection or statement: " + e.getMessage());
            throw new RuntimeException("Lỗi trong hàm InventoryStatistícDao.getInventoryImportRecently() ", e);
        } finally {
            close(conn, stm, rs);
        }

        return result;
    }

    /*
     * Lấy danh sách phiếu xuất gần đây
     *
     * @return List<InventoryRecently>
     */
    public List<InventoryRecently> getInventoryExportRecently() {
        List<InventoryRecently> exports = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ih.id AS id, p.id AS product_id, p.name AS product_name, ");
        sql.append("p.author as product_author, p.imageName AS product_image, ih.created_at AS date, ");
        sql.append("ABS(ih.quantity_change) AS quantity,  u.fullname AS created_by_name, ih.reason ");
        sql.append("FROM bookshopdb.inventory_history ih ");
        sql.append("JOIN bookshopdb.product p ON ih.product_id = p.id ");
        sql.append("JOIN bookshopdb.user u ON ih.created_by = u.id ");
        sql.append("WHERE ih.action_type = 'export' ");
        sql.append("ORDER BY ih.created_at DESC LIMIT 5 ");

        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stm = conn.prepareStatement(sql.toString());
            rs = stm.executeQuery();
            while (rs.next()) {
                InventoryRecently item = new InventoryRecently(rs);
                exports.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error creating connection or statement: " + e.getMessage());
            throw new RuntimeException("Lỗi trong hàm InventoryStatistícDao.getInventoryExportRecently() ", e);
        } finally {
            close(conn, stm, rs);
        }
        return exports;
    }

    /**
     * Lấy danh sách lịch sử tồn kho với các tham số lọc
     *
     * @param draw                 Request counter từ client (DataTables)
     * @param start                Vị trí bắt đầu của bản ghi
     * @param length               Số lượng bản ghi trên mỗi trang
     * @param searchValue          Chuỗi tìm kiếm toàn cục
     * @param orderColumnIndex     Cột để sắp xếp (index)
     * @param orderDirection       Hướng sắp xếp (asc hoặc desc)
     * @param productId            ID sản phẩm cần lọc
     * @param actionType           Loại hành động (import/export/adjustment)
     * @param quantityChangeFilter Hướng thay đổi số lượng (increase/decrease)
     * @param referenceFilter      Mã tham chiếu
     * @param reasonFilter         Lý do
     * @param startDate            Ngày bắt đầu (định dạng ISO: YYYY-MM-DD)
     * @param endDate              Ngày kết thúc (định dạng ISO: YYYY-MM-DD)
     * @param userFilter           Người thực hiện
     * @param groupByDay           Nhóm kết quả theo ngày (1/0)
     * @return DataTable<InventoryHistoryItem>
     */
    public DataTable<InventoryHistoryItem> getInventoryHistory(int draw, int start,
                                                               int length, String searchValue,
                                                               int orderColumnIndex, String orderDirection,
                                                               String productId, String actionType,
                                                               String quantityChangeFilter,
                                                               String referenceFilter, String reasonFilter,
                                                               String startDate, String endDate,
                                                               String userFilter, boolean groupByDay) {

        DataTable<InventoryHistoryItem> result = new DataTable<>();
        List<InventoryHistoryItem> items = new ArrayList<>();
        int recordsTotal = 0;
        int recordsFiltered = 0;
        InventorySummary summary = new InventorySummary();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            // 1. Lấy tổng số bản ghi (không lọc)
            recordsTotal = countTotalInventoryHistoryRecords(conn);
            // 2. Tạo câu lệnh SQL cơ bản với phần WHERE để lọc
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ");
            sqlBuilder.append("ih.id, ");
            sqlBuilder.append("ih.created_at AS createdAt, ");
            sqlBuilder.append("p.id AS productId, ");
            sqlBuilder.append("p.name AS productName, ");
            sqlBuilder.append("p.imageName AS productImage, ");
            sqlBuilder.append("ih.action_type AS actionType, ");
            sqlBuilder.append("ih.previous_quantity AS previousQuantity, ");
            sqlBuilder.append("ih.quantity_change AS quantityChange, ");
            sqlBuilder.append("ih.current_quantity AS currentQuantity, ");
            sqlBuilder.append("ih.reference_id AS referenceId, ");
            sqlBuilder.append("ih.reference_type AS referenceType, ");
            sqlBuilder.append("ih.reason, ");
            sqlBuilder.append("u.fullname AS createdByName ");
            sqlBuilder.append("FROM bookshopdb.inventory_history ih ");
            sqlBuilder.append("JOIN bookshopdb.product p ON ih.product_id = p.id ");
            sqlBuilder.append("JOIN bookshopdb.user u ON ih.created_by = u.id ");
            sqlBuilder.append("WHERE 1=1 ");

            // Tạo danh sách tham số
            List<Object> parameters = new ArrayList<>();

            // Thêm các điều kiện lọc
            addFilterConditions(sqlBuilder, parameters, productId, actionType,
                    quantityChangeFilter, referenceFilter, reasonFilter,
                    startDate, endDate, userFilter, searchValue);
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
            String[] columns = {"ih.id", "ih.created_at", "p.name", "ih.action_type",
                    "ih.previous_quantity", "ih.quantity_change", "ih.current_quantity",
                    "ih.reference_id", "ih.reason", "u.fullname"};
            if (orderColumnIndex >= 0 && orderColumnIndex < columns.length) {
                sqlBuilder.append(columns[orderColumnIndex]);
            } else {
                sqlBuilder.append("ih.created_at");  // Default sort column
            }
            sqlBuilder.append(" ").append(orderDirection.equalsIgnoreCase("asc") ? "ASC" : "DESC");

            // Thêm LIMIT
            sqlBuilder.append(" LIMIT ").append(start).append(", ").append(length);

            // Thực thi truy vấn chính
            pstmt = conn.prepareStatement(sqlBuilder.toString());
            this.setParameters(pstmt, parameters);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InventoryHistoryItem item = new InventoryHistoryItem(rs);
                items.add(item);
            }
            rs.close();
            pstmt.close();

            // Lấy thông tin tóm tắt
            StringBuilder summarySql = new StringBuilder("SELECT " +
                    "SUM(CASE WHEN ih.action_type = 'import' THEN 1 ELSE 0 END) AS totalImport, " +
                    "SUM(CASE WHEN ih.action_type = 'export' THEN 1 ELSE 0 END) AS totalExport, " +
                    "SUM(CASE WHEN ih.action_type = 'adjustment' THEN 1 ELSE 0 END) AS totalAdjustment " +
                    "FROM bookshopdb.inventory_history ih " +
                    "JOIN bookshopdb.product p ON ih.product_id = p.id " +
                    "JOIN bookshopdb.user u ON ih.created_by = u.id " +
                    "WHERE 1=1 ");
            List<Object> parametersSummary = new ArrayList<>();
            StringBuilder summaryConditions = new StringBuilder();
            addFilterConditions(summaryConditions, parametersSummary, productId, actionType,
                    quantityChangeFilter, referenceFilter, reasonFilter,
                    startDate, endDate, userFilter, searchValue);

            pstmt = conn.prepareStatement(summarySql.toString() + summaryConditions.toString());
            setParameters(pstmt, parametersSummary);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                summary.setTotalImport(rs.getInt("totalImport"));
                summary.setTotalExport(rs.getInt("totalExport"));
                summary.setTotalAdjustment(rs.getInt("totalAdjustment"));
            }

            // Đặt dữ liệu vào kết quả
            result.setData(items);
            result.setRecordsTotal(recordsTotal);
            result.setRecordsFiltered(recordsFiltered);
            result.setSummary(summary);
        } catch (SQLException e) {
            throw new RuntimeException("Error querying inventory history: " + e.getMessage());
        } finally {
            close(conn, pstmt, rs);
        }

        return result;
    }

    private void addFilterConditions(StringBuilder sql, List<Object> parameters,
                                     String productId, String actionType,
                                     String quantityChangeFilter, String referenceFilter,
                                     String reasonFilter, String startDate, String endDate,
                                     String userFilter, String searchValue) {
        // Lọc theo sản phẩm
        if (productId != null && !productId.isEmpty()) {
            sql.append("AND ih.product_id = ? ");
            parameters.add(Long.parseLong(productId));
        }

        // Lọc theo loại hành động
        if (actionType != null && !actionType.isEmpty()) {
            sql.append("AND ih.action_type = ? ");
            parameters.add(actionType);
        }

        // Lọc theo thay đổi số lượng
        if (quantityChangeFilter != null && !quantityChangeFilter.isEmpty()) {
            if ("increase".equals(quantityChangeFilter)) {
                sql.append("AND ih.quantity_change > 0 ");
            } else if ("decrease".equals(quantityChangeFilter)) {
                sql.append("AND ih.quantity_change < 0 ");
            }
        }

        // Lọc theo mã tham chiếu
        if (referenceFilter != null && !referenceFilter.isEmpty()) {
            sql.append("AND (ih.reference_id LIKE ? OR ih.reference_type LIKE ?) ");
            parameters.add("%" + referenceFilter + "%");
            parameters.add("%" + referenceFilter + "%");
        }

        // Lọc theo lý do
        if (reasonFilter != null && !reasonFilter.isEmpty()) {
            sql.append("AND ih.reason LIKE ? ");
            parameters.add("%" + reasonFilter + "%");
        }

        // Lọc theo khoảng thời gian
        if (startDate != null && !startDate.isEmpty()) {
            sql.append("AND ih.created_at >= ? ");
            parameters.add(startDate + " 00:00:00");
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append("AND ih.created_at <= ? ");
            parameters.add(endDate + " 23:59:59");
        }

        // Lọc theo người thực hiện
        if (userFilter != null && !userFilter.isEmpty()) {
            sql.append("AND u.fullname = ? ");
            parameters.add(userFilter);
        }

        // Tìm kiếm toàn cục
        if (searchValue != null && !searchValue.isEmpty()) {
            sql.append("AND (p.name LIKE ? OR ih.action_type LIKE ? OR ih.reason LIKE ? OR ");
            sql.append("u.fullname LIKE ? OR CAST(ih.id AS CHAR) LIKE ? OR CAST(ih.quantity_change AS CHAR) LIKE ?) ");
            for (int i = 0; i < 6; i++) {
                parameters.add("%" + searchValue + "%");
            }
        }
    }

    /**
     * Lấy danh sách sản phẩm có giá trị tồn kho cao nhất
     *
     * @param limit Số lượng sản phẩm cần lấy
     * @return List<InventoryValueItem>
     */
    public List<InventoryValueItem> getTopInventoryValueProducts(int limit) {
        List<InventoryValueItem> result = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("    p.id AS product_id, ");
            sql.append("    p.name AS product_name, ");
            sql.append("    p.author AS product_author, ");
            sql.append("    p.imageName AS product_image, ");
            sql.append("    p.price AS unit_price, ");
            sql.append("    ist.actual_quantity AS inventory_quantity, ");
            sql.append("    (p.price * ist.actual_quantity) AS inventory_value, ");
            sql.append("    (SELECT GROUP_CONCAT(c.name SEPARATOR ', ') ");
            sql.append("     FROM bookshopdb.product_category pc ");
            sql.append("     JOIN bookshopdb.category c ON pc.categoryId = c.id ");
            sql.append("     WHERE pc.productId = p.id) AS category_name ");
            sql.append("FROM ");
            sql.append("    bookshopdb.product p ");
            sql.append("JOIN ");
            sql.append("    bookshopdb.inventory_status ist ON p.id = ist.product_id ");
            sql.append("WHERE ");
            sql.append("    ist.actual_quantity > 0 ");
            sql.append("ORDER BY ");
            sql.append("    inventory_value DESC ");
            sql.append("LIMIT ?");

            stmt = conn.prepareStatement(sql.toString());
            stmt.setInt(1, limit);

            rs = stmt.executeQuery();

            while (rs.next()) {
                InventoryValueItem item = new InventoryValueItem(rs);
                result.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying top inventory value products: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return result;
    }


    /**
     * Lấy biến động xuất nhập tồn kho theo giai đoạn
     *
     * @param period    Giai đoạn (day, week, month, quarter)
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     */

    public List<InventoryMovementData> getInventoryMovementData(String period, Date startDate, Date endDate) {
        return null;
    }

    private void sortCondition(StringBuilder sql, String sortColumn, String sortDirection) {
        if (sortColumn != null && !sortColumn.isEmpty()) {
            sql.append("ORDER BY ");

            // Map tên cột từ DataTable sang tên cột trong DB
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put("productId", "p.id");
            columnMap.put("name", "p.name");
            columnMap.put("category", "categories");
            columnMap.put("stock", "stock_quantity");
            columnMap.put("reserved", "reserved_quantity");
            columnMap.put("available", "available_quantity");
            columnMap.put("threshold", "threshold");
            columnMap.put("lastUpdated", "last_updated");
            columnMap.put("storageDays", "days_in_inventory");
            columnMap.put("daysSinceLastSale", "days_since_last_sale");
            columnMap.put("status", "stock_status");

            String dbColumn = columnMap.getOrDefault(sortColumn, "days_since_last_sale");
            sql.append(dbColumn);

            if ("desc".equalsIgnoreCase(sortDirection)) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        } else {
            // Mặc định sắp xếp theo số ngày không bán và trạng thái tồn kho
            sql.append("ORDER BY days_since_last_sale DESC, stock_status ASC");
        }
    }

    private void filterByKeyword(StringBuilder sql, List<Object> parameters, String searchKeyword) {
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            sql.append("AND (p.name LIKE ? OR p.author LIKE ? OR CAST(p.id AS CHAR) = ?) ");
            parameters.add("%" + searchKeyword + "%");
            parameters.add("%" + searchKeyword + "%");
            parameters.add(searchKeyword);
        }
    }

    private void filterByDateRange(StringBuilder sql, List<Object> parameters, Timestamp startDate, Timestamp endDate) {
        if (startDate != null) {
            sql.append("AND ist.last_updated >= ? ");
            parameters.add(startDate);
        }

        if (endDate != null) {
            sql.append("AND ist.last_updated <= ? ");
            parameters.add(endDate);
        }
    }

    private void filterByDuration(StringBuilder sql, List<Object> parameters, Integer durationDays) {
        if (durationDays != null && durationDays > 0) {
            sql.append("AND DATEDIFF(CURRENT_DATE(), COALESCE((SELECT MIN(ii.import_date) FROM bookshopdb.inventory_import ii WHERE ii.product_id = p.id), p.createdAt)) >= ? ");
            parameters.add(durationDays);
        }
    }

    private void filterByCategory(StringBuilder sql, List<Object> parameters, Long categoryId) {
        if (categoryId != null) {
            sql.append("AND EXISTS (SELECT 1 FROM bookshopdb.product_category pc2 WHERE pc2.productId = p.id AND pc2.categoryId = ?) ");
            parameters.add(categoryId);
        }
    }

    private void filterByStockStatus(StringBuilder sql, List<Object> parameters, String stockStatus) {
        if (stockStatus != null && !stockStatus.isEmpty()) {
            sql.append("AND (");
            switch (stockStatus) {
                case "out":
                    sql.append("ist.actual_quantity = 0");
                    break;
                case "low":
                    sql.append("ist.actual_quantity <= ist.reorder_threshold AND ist.actual_quantity > 0");
                    break;
                case "normal":
                    sql.append("ist.actual_quantity > ist.reorder_threshold");
                    break;
            }
            sql.append(") ");
        }
    }

    private int countFilteredRecords(Connection conn, String sql, List<Object> parameters) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder countFilteredSQL = new StringBuilder();
            countFilteredSQL.append("SELECT COUNT(*) FROM (")
                    .append(sql)
                    .append(") AS filtered_table");
            ps = conn.prepareStatement(countFilteredSQL.toString());

            // Thiết lập các tham số
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Lỗi trong quá trình InventoryStatisticsDao.CountAll() " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(null, ps, rs);
        }
        return 0;
    }

    private void setParameters(PreparedStatement pstmt, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            pstmt.setObject(i + 1, parameters.get(i));
        }
    }

    private int countAll(Connection conn) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM bookshopdb.product p " +
                    "JOIN bookshopdb.inventory_status ist ON p.id = ist.product_id");
            stm = conn.prepareStatement(sql.toString());
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi trong quá trình InventoryStatisticsDao.CountAll() " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(null, stm, rs);
        }
        return 0;
    }

    /**
     * Đếm tổng số bản ghi trong bảng inventory_history
     *
     * @param conn Connection đến database
     * @return Tổng số bản ghi
     * @throws SQLException nếu có lỗi truy vấn
     */
    private int countTotalInventoryHistoryRecords(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM bookshopdb.inventory_history");
            pstmt = conn.prepareStatement(countSql.toString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }

    public List<InventoryMovementData> getInventoryMovementByDay(Date startDate, Date endDate) {
        List<InventoryMovementData> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("WITH date_ranges AS (");
            sql.append("    SELECT ");
            sql.append("        DATE_ADD(?, INTERVAL n DAY) AS date_point");
            sql.append("    FROM ");
            sql.append("        (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5");
            sql.append("         UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10");
            sql.append("         UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15");
            sql.append("         UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20");
            sql.append("         UNION SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25");
            sql.append("         UNION SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30) numbers");
            sql.append("    WHERE ");
            sql.append("        DATE_ADD(?, INTERVAL n DAY) <= ?");
            sql.append("),");

            sql.append("import_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(import_date) AS date_point,");
            sql.append("        SUM(quantity) AS import_quantity,");
            sql.append("        SUM(quantity * cost_price) AS import_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_import");
            sql.append("    WHERE ");
            sql.append("        import_date BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(import_date)");
            sql.append("),");

            sql.append("export_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(created_at) AS date_point,");
            sql.append("        SUM(ABS(quantity_change)) AS export_quantity,");
            sql.append("        SUM(ABS(quantity_change) * (");
            sql.append("            SELECT price FROM bookshopdb.product WHERE id = product_id");
            sql.append("        )) AS export_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_history");
            sql.append("    WHERE ");
            sql.append("        action_type = 'export'");
            sql.append("        AND created_at BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(created_at)");
            sql.append(")");

            sql.append("SELECT ");
            sql.append("    dr.date_point,");
            sql.append("    DATE_FORMAT(dr.date_point, '%d/%m') AS date_label,");
            sql.append("    COALESCE(id.import_quantity, 0) AS import_quantity,");
            sql.append("    COALESCE(id.import_value, 0) AS import_value,");
            sql.append("    COALESCE(ed.export_quantity, 0) AS export_quantity,");
            sql.append("    COALESCE(ed.export_value, 0) AS export_value,");
            sql.append("    (COALESCE(id.import_quantity, 0) - COALESCE(ed.export_quantity, 0)) AS net_quantity,");
            sql.append("    (COALESCE(id.import_value, 0) - COALESCE(ed.export_value, 0)) AS net_value");
            sql.append(" FROM ");
            sql.append("    date_ranges dr");
            sql.append(" LEFT JOIN ");
            sql.append("    import_data id ON dr.date_point = id.date_point");
            sql.append(" LEFT JOIN ");
            sql.append("    export_data ed ON dr.date_point = ed.date_point");
            sql.append(" ORDER BY ");
            sql.append("    dr.date_point");

            stmt = conn.prepareStatement(sql.toString());

            // Thêm 1 ngày vào endDate để bao gồm cả ngày cuối
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            java.sql.Date sqlEndDate = new java.sql.Date(calendar.getTimeInMillis());

            // Thiết lập tham số
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());

            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlStartDate);
            stmt.setDate(3, sqlEndDate);
            stmt.setTimestamp(4, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(5, new Timestamp(sqlEndDate.getTime()));
            stmt.setTimestamp(6, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(7, new Timestamp(sqlEndDate.getTime()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new InventoryMovementData(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying inventory movement by day: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }
        return result;
    }

    /**
     * Lấy dữ liệu biến động nhập/xuất kho theo tuần
     */
    public List<InventoryMovementData> getInventoryMovementByWeek(Date startDate, Date endDate) {
        List<InventoryMovementData> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("WITH week_ranges AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_SUB(DATE_ADD(?, INTERVAL (n * 7) DAY), INTERVAL WEEKDAY(DATE_ADD(?, INTERVAL (n * 7) DAY)) DAY)) AS week_start,");
            sql.append("        DATE(DATE_ADD(DATE_SUB(DATE_ADD(?, INTERVAL (n * 7) DAY), INTERVAL WEEKDAY(DATE_ADD(?, INTERVAL (n * 7) DAY)) DAY), INTERVAL 6 DAY)) AS week_end");
            sql.append("    FROM ");
            sql.append("        (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5");
            sql.append("         UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) numbers");
            sql.append("    WHERE ");
            sql.append("        DATE_ADD(?, INTERVAL (n * 7) DAY) <= ?");
            sql.append("),");

            sql.append("import_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_SUB(import_date, INTERVAL WEEKDAY(import_date) DAY)) AS week_start,");
            sql.append("        SUM(quantity) AS import_quantity,");
            sql.append("        SUM(quantity * cost_price) AS import_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_import");
            sql.append("    WHERE ");
            sql.append("        import_date BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(DATE_SUB(import_date, INTERVAL WEEKDAY(import_date) DAY))");
            sql.append("),");

            sql.append("export_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_SUB(created_at, INTERVAL WEEKDAY(created_at) DAY)) AS week_start,");
            sql.append("        SUM(ABS(quantity_change)) AS export_quantity,");
            sql.append("        SUM(ABS(quantity_change) * (");
            sql.append("            SELECT price FROM bookshopdb.product WHERE id = product_id");
            sql.append("        )) AS export_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_history");
            sql.append("    WHERE ");
            sql.append("        action_type = 'export'");
            sql.append("        AND created_at BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(DATE_SUB(created_at, INTERVAL WEEKDAY(created_at) DAY))");
            sql.append(")");

            sql.append("SELECT ");
            sql.append("    wr.week_start AS date_point,");
            sql.append("    CONCAT('Tuần ', WEEKOFYEAR(wr.week_start), ' (', DATE_FORMAT(wr.week_start, '%d/%m'), ' - ', DATE_FORMAT(wr.week_end, '%d/%m'), ')') AS date_label,");
            sql.append("    COALESCE(id.import_quantity, 0) AS import_quantity,");
            sql.append("    COALESCE(id.import_value, 0) AS import_value,");
            sql.append("    COALESCE(ed.export_quantity, 0) AS export_quantity,");
            sql.append("    COALESCE(ed.export_value, 0) AS export_value,");
            sql.append("    (COALESCE(id.import_quantity, 0) - COALESCE(ed.export_quantity, 0)) AS net_quantity,");
            sql.append("    (COALESCE(id.import_value, 0) - COALESCE(ed.export_value, 0)) AS net_value");
            sql.append(" FROM ");
            sql.append("    week_ranges wr");
            sql.append(" LEFT JOIN ");
            sql.append("    import_data id ON wr.week_start = id.week_start");
            sql.append(" LEFT JOIN ");
            sql.append("    export_data ed ON wr.week_start = ed.week_start");
            sql.append(" ORDER BY ");
            sql.append("    wr.week_start");

            stmt = conn.prepareStatement(sql.toString());

            // Thêm 1 ngày vào endDate để bao gồm cả ngày cuối
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            java.sql.Date sqlEndDate = new java.sql.Date(calendar.getTimeInMillis());

            // Thiết lập tham số
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());

            // 6 tham số đầu là cho bảng tạm week_ranges
            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlStartDate);
            stmt.setDate(3, sqlStartDate);
            stmt.setDate(4, sqlStartDate);
            stmt.setDate(5, sqlStartDate);
            stmt.setDate(6, sqlEndDate);

            // 2 tham số tiếp theo cho import_data
            stmt.setTimestamp(7, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(8, new Timestamp(sqlEndDate.getTime()));

            // 2 tham số còn lại cho export_data
            stmt.setTimestamp(9, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(10, new Timestamp(sqlEndDate.getTime()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new InventoryMovementData(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying inventory movement by week: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * Lấy dữ liệu biến động nhập/xuất kho theo tháng
     */
    public List<InventoryMovementData> getInventoryMovementByMonth(Date startDate, Date endDate) {
        List<InventoryMovementData> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("WITH month_ranges AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_FORMAT(DATE_ADD(?, INTERVAL n MONTH), '%Y-%m-01')) AS month_start");
            sql.append("    FROM ");
            sql.append("        (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5");
            sql.append("         UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10");
            sql.append("         UNION SELECT 11) numbers");
            sql.append("    WHERE ");
            sql.append("        DATE_ADD(?, INTERVAL n MONTH) <= ?");
            sql.append("),");

            sql.append("import_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_FORMAT(import_date, '%Y-%m-01')) AS month_start,");
            sql.append("        SUM(quantity) AS import_quantity,");
            sql.append("        SUM(quantity * cost_price) AS import_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_import");
            sql.append("    WHERE ");
            sql.append("        import_date BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(DATE_FORMAT(import_date, '%Y-%m-01'))");
            sql.append("),");

            sql.append("export_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(DATE_FORMAT(created_at, '%Y-%m-01')) AS month_start,");
            sql.append("        SUM(ABS(quantity_change)) AS export_quantity,");
            sql.append("        SUM(ABS(quantity_change) * (");
            sql.append("            SELECT price FROM bookshopdb.product WHERE id = product_id");
            sql.append("        )) AS export_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_history");
            sql.append("    WHERE ");
            sql.append("        action_type = 'export'");
            sql.append("        AND created_at BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        DATE(DATE_FORMAT(created_at, '%Y-%m-01'))");
            sql.append(")");

            sql.append("SELECT ");
            sql.append("    mr.month_start AS date_point,");
            sql.append("    DATE_FORMAT(mr.month_start, '%m/%Y') AS date_label,");
            sql.append("    COALESCE(id.import_quantity, 0) AS import_quantity,");
            sql.append("    COALESCE(id.import_value, 0) AS import_value,");
            sql.append("    COALESCE(ed.export_quantity, 0) AS export_quantity,");
            sql.append("    COALESCE(ed.export_value, 0) AS export_value,");
            sql.append("    (COALESCE(id.import_quantity, 0) - COALESCE(ed.export_quantity, 0)) AS net_quantity,");
            sql.append("    (COALESCE(id.import_value, 0) - COALESCE(ed.export_value, 0)) AS net_value");
            sql.append(" FROM ");
            sql.append("    month_ranges mr");
            sql.append(" LEFT JOIN ");
            sql.append("    import_data id ON mr.month_start = id.month_start");
            sql.append(" LEFT JOIN ");
            sql.append("    export_data ed ON mr.month_start = ed.month_start");
            sql.append(" ORDER BY ");
            sql.append("    mr.month_start");

            stmt = conn.prepareStatement(sql.toString());

            // Thiết lập tham số
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            // 3 tham số đầu là cho bảng tạm month_ranges
            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlStartDate);
            stmt.setDate(3, sqlEndDate);

            // 2 tham số tiếp theo cho import_data
            stmt.setTimestamp(4, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(5, new Timestamp(endDate.getTime()));

            // 2 tham số còn lại cho export_data
            stmt.setTimestamp(6, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(7, new Timestamp(endDate.getTime()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new InventoryMovementData(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying inventory movement by month: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * Lấy dữ liệu biến động nhập/xuất kho theo quý
     */
    public List<InventoryMovementData> getInventoryMovementByQuarter(Date startDate, Date endDate) {
        List<InventoryMovementData> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            StringBuilder sql = new StringBuilder();
            sql.append("WITH quarter_ranges AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(CONCAT(YEAR(DATE_ADD(?, INTERVAL (n*3) MONTH)), '-', ");
            sql.append("            CASE ");
            sql.append("                WHEN QUARTER(DATE_ADD(?, INTERVAL (n*3) MONTH)) = 1 THEN '01-01'");
            sql.append("                WHEN QUARTER(DATE_ADD(?, INTERVAL (n*3) MONTH)) = 2 THEN '04-01'");
            sql.append("                WHEN QUARTER(DATE_ADD(?, INTERVAL (n*3) MONTH)) = 3 THEN '07-01'");
            sql.append("                WHEN QUARTER(DATE_ADD(?, INTERVAL (n*3) MONTH)) = 4 THEN '10-01'");
            sql.append("            END)) AS quarter_start");
            sql.append("    FROM ");
            sql.append("        (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5");
            sql.append("         UNION SELECT 6 UNION SELECT 7) numbers");
            sql.append("    WHERE ");
            sql.append("        DATE_ADD(?, INTERVAL (n*3) MONTH) <= ?");
            sql.append("),");

            sql.append("import_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(CONCAT(YEAR(import_date), '-', ");
            sql.append("            CASE ");
            sql.append("                WHEN QUARTER(import_date) = 1 THEN '01-01'");
            sql.append("                WHEN QUARTER(import_date) = 2 THEN '04-01'");
            sql.append("                WHEN QUARTER(import_date) = 3 THEN '07-01'");
            sql.append("                WHEN QUARTER(import_date) = 4 THEN '10-01'");
            sql.append("            END)) AS quarter_start,");
            sql.append("        SUM(quantity) AS import_quantity,");
            sql.append("        SUM(quantity * cost_price) AS import_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_import");
            sql.append("    WHERE ");
            sql.append("        import_date BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        quarter_start");
            sql.append("),");

            sql.append("export_data AS (");
            sql.append("    SELECT ");
            sql.append("        DATE(CONCAT(YEAR(created_at), '-', ");
            sql.append("            CASE ");
            sql.append("                WHEN QUARTER(created_at) = 1 THEN '01-01'");
            sql.append("                WHEN QUARTER(created_at) = 2 THEN '04-01'");
            sql.append("                WHEN QUARTER(created_at) = 3 THEN '07-01'");
            sql.append("                WHEN QUARTER(created_at) = 4 THEN '10-01'");
            sql.append("            END)) AS quarter_start,");
            sql.append("        SUM(ABS(quantity_change)) AS export_quantity,");
            sql.append("        SUM(ABS(quantity_change) * (");
            sql.append("            SELECT price FROM bookshopdb.product WHERE id = product_id");
            sql.append("        )) AS export_value");
            sql.append("    FROM ");
            sql.append("        bookshopdb.inventory_history");
            sql.append("    WHERE ");
            sql.append("        action_type = 'export'");
            sql.append("        AND created_at BETWEEN ? AND ?");
            sql.append("    GROUP BY ");
            sql.append("        quarter_start");
            sql.append(")");

            sql.append("SELECT ");
            sql.append("    qr.quarter_start AS date_point,");
            sql.append("    CONCAT('Q', QUARTER(qr.quarter_start), '/', YEAR(qr.quarter_start)) AS date_label,");
            sql.append("    COALESCE(id.import_quantity, 0) AS import_quantity,");
            sql.append("    COALESCE(id.import_value, 0) AS import_value,");
            sql.append("    COALESCE(ed.export_quantity, 0) AS export_quantity,");
            sql.append("    COALESCE(ed.export_value, 0) AS export_value,");
            sql.append("    (COALESCE(id.import_quantity, 0) - COALESCE(ed.export_quantity, 0)) AS net_quantity,");
            sql.append("    (COALESCE(id.import_value, 0) - COALESCE(ed.export_value, 0)) AS net_value");
            sql.append(" FROM ");
            sql.append("    quarter_ranges qr");
            sql.append(" LEFT JOIN ");
            sql.append("    import_data id ON qr.quarter_start = id.quarter_start");
            sql.append(" LEFT JOIN ");
            sql.append("    export_data ed ON qr.quarter_start = ed.quarter_start");
            sql.append(" ORDER BY ");
            sql.append("    qr.quarter_start");

            stmt = conn.prepareStatement(sql.toString());

            // Thiết lập tham số
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            // 7 tham số đầu là cho bảng tạm quarter_ranges
            stmt.setDate(1, sqlStartDate);
            stmt.setDate(2, sqlStartDate);
            stmt.setDate(3, sqlStartDate);
            stmt.setDate(4, sqlStartDate);
            stmt.setDate(5, sqlStartDate);
            stmt.setDate(6, sqlStartDate);
            stmt.setDate(7, sqlEndDate);

            // 2 tham số tiếp theo cho import_data
            stmt.setTimestamp(8, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(9, new Timestamp(endDate.getTime()));

            // 2 tham số còn lại cho export_data
            stmt.setTimestamp(10, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(11, new Timestamp(endDate.getTime()));

            rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new InventoryMovementData(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying inventory movement by quarter: " + e.getMessage());
        } finally {
            close(conn, stmt, rs);
        }

        return result;
    }

    /**
     * Lấy danh sách hàng chậm luân chuyển dựa trên bộ lọc
     *
     * @param draw           Request counter từ client (DataTables)
     * @param start          Vị trí bắt đầu của bản ghi
     * @param length         Số lượng bản ghi trên mỗi trang
     * @param searchValue    Chuỗi tìm kiếm toàn cục
     * @param categoryId     ID danh mục sản phẩm
     * @param minPrice       Giá tối thiểu
     * @param maxPrice       Giá tối đa
     * @param minDaysInStock Số ngày tồn kho tối thiểu
     * @param maxTurnover    Vòng quay tồn kho tối đa
     * @return DataTable<SlowMovingItem> chứa tổng số bản ghi và danh sách hàng chậm luân chuyển
     */
    public DataTable<SlowMovingItem> getSlowMovingProducts(
            int draw, int start, int length, String searchValue,
            int columnIndex, String sortDirection,
            Long categoryId, Double minPrice, Double maxPrice,
            Integer minDaysInStock, Double maxTurnover) {

        // Khởi tạo đối tượng DataTable
        DataTable<SlowMovingItem> table = new DataTable<>();
        table.setDraw(draw); // Thiết lập draw counter từ client
        List<SlowMovingItem> items = new ArrayList<>();
        int recordsTotal = 0;
        int recordsFiltered = 0;

        // Query dữ liệu từ cơ sở dữ liệu
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Đếm tổng số bản ghi (không lọc)
            recordsTotal = countAllSlowMovingCandidates(conn);

            // Main query
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p.id AS product_id, p.name AS product_name, p.author, ")
                    .append("p.imageName AS thumbnail, p.price, ")
                    .append("(SELECT c.id FROM bookshopdb.product_category pc2 ")
                    .append("JOIN bookshopdb.category c ON pc2.categoryId = c.id ")
                    .append("WHERE pc2.productId = p.id LIMIT 1) AS category_id, ")
                    .append("(SELECT c.name FROM bookshopdb.product_category pc2 ")
                    .append("JOIN bookshopdb.category c ON pc2.categoryId = c.id ")
                    .append("WHERE pc2.productId = p.id LIMIT 1) AS category_name, ")
                    .append("is2.actual_quantity AS stock_quantity, ")

                    // Tính số ngày kể từ lần xuất/bán gần nhất (thực sự là "tồn đọng")
                    .append("DATEDIFF(CURRENT_DATE(), ")
                    .append("IFNULL((SELECT MAX(created_at) ")
                    .append("FROM bookshopdb.inventory_history ")
                    .append("WHERE product_id = p.id ")
                    .append("AND action_type = 'export' ")
                    .append("AND quantity_change < 0), ")
                    .append("(SELECT import_date ")
                    .append("FROM bookshopdb.inventory_import ")
                    .append("WHERE product_id = p.id ")
                    .append("ORDER BY import_date ASC LIMIT 1))) ")
                    .append("AS days_without_sale, ")

                    // Tính vòng quay tồn kho (dựa trên dữ liệu 90 ngày qua)
                    .append("IFNULL(")
                    .append("(SELECT SUM(ABS(quantity_change)) ")
                    .append("FROM bookshopdb.inventory_history ")
                    .append("WHERE product_id = p.id ")
                    .append("AND action_type = 'export' ")
                    .append("AND created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY)")
                    .append(") / NULLIF(is2.actual_quantity, 0),")
                    .append("0) AS turnover_rate, ")

                    // Tính giá trị tồn kho
                    .append("(is2.actual_quantity * p.price) AS stock_value ")

                    .append("FROM bookshopdb.product p ")
                    .append("JOIN bookshopdb.inventory_status is2 ON p.id = is2.product_id ")
                    .append("WHERE is2.actual_quantity > 0 ");  // Chỉ lấy sản phẩm có tồn kho

            // Tạo danh sách tham số
            List<Object> parameters = new ArrayList<>();

            // Thêm điều kiện lọc
            // 1. Lọc theo số ngày không bán được
            if (minDaysInStock != null && minDaysInStock > 0) {
                sql.append("AND DATEDIFF(CURRENT_DATE(), ")
                        .append("IFNULL((SELECT MAX(created_at) ")
                        .append("FROM bookshopdb.inventory_history ")
                        .append("WHERE product_id = p.id ")
                        .append("AND action_type = 'export' ")
                        .append("AND quantity_change < 0), ")
                        .append("(SELECT import_date ")
                        .append("FROM bookshopdb.inventory_import ")
                        .append("WHERE product_id = p.id ")
                        .append("ORDER BY import_date ASC LIMIT 1))) ")
                        .append(">= ? ");
                parameters.add(minDaysInStock);
            }

            // 2. Lọc theo vòng quay tồn kho
            if (maxTurnover != null) {
                sql.append("AND IFNULL(")
                        .append("(SELECT SUM(ABS(quantity_change)) ")
                        .append("FROM bookshopdb.inventory_history ")
                        .append("WHERE product_id = p.id ")
                        .append("AND action_type = 'export' ")
                        .append("AND created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 90 DAY)")
                        .append(") / NULLIF(is2.actual_quantity, 0),")
                        .append("0) <= ? ");
                parameters.add(maxTurnover);
            }

            // 3. Lọc theo danh mục
            if (categoryId != null) {
                sql.append("AND EXISTS (SELECT 1 FROM bookshopdb.product_category pc ")
                        .append("WHERE pc.productId = p.id AND pc.categoryId = ?) ");
                parameters.add(categoryId);
            }

            // 4. Lọc theo khoảng giá
            if (minPrice != null) {
                sql.append("AND p.price >= ? ");
                parameters.add(minPrice);
            }
            if (maxPrice != null) {
                sql.append("AND p.price <= ? ");
                parameters.add(maxPrice);
            }

            // 5. Tìm kiếm theo từ khóa
            if (searchValue != null && !searchValue.isEmpty()) {
                sql.append("AND (p.name LIKE ? OR p.author LIKE ? OR CAST(p.id AS CHAR) LIKE ?) ");
                parameters.add("%" + searchValue + "%");
                parameters.add("%" + searchValue + "%");
                parameters.add("%" + searchValue + "%");
            }

            // Đếm số bản ghi sau khi lọc
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM (").append(sql).append(") AS filtered_count");
            PreparedStatement countStmt = conn.prepareStatement(countSql.toString());

            // Thiết lập các tham số cho câu lệnh count
            this.setParameters(countStmt, parameters);

            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                recordsFiltered = countRs.getInt(1);
            }
            countRs.close();
            countStmt.close();

            // Thêm ORDER BY dựa trên tham số sắp xếp
            String[] columns = {
                    "p.id", "p.name", "category_name", "p.price", "stock_quantity",
                    "days_without_sale", "turnover_rate", "stock_value"
            };
            if (columnIndex >= 0 && columnIndex < columns.length) {
                sql.append("ORDER BY ").append(columns[columnIndex]).append(" ").append(sortDirection).append(" ");
            } else {
                sql.append("ORDER BY days_without_sale DESC ");  // Mặc định
            }

            // Thêm LIMIT
            sql.append("LIMIT ").append(start).append(", ").append(length);

            // Tạo PreparedStatement
            stm = conn.prepareStatement(sql.toString());

            // Thiết lập các tham số
            this.setParameters(stm, parameters);

            rs = stm.executeQuery();

            while (rs.next()) {
                SlowMovingItem item = new SlowMovingItem(rs);
                items.add(item);
            }

            // Đặt dữ liệu vào đối tượng DataTable
            table.setData(items);
            table.setRecordsTotal(recordsTotal);
            table.setRecordsFiltered(recordsFiltered);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying slow moving products: " + e.getMessage(), e);
        } finally {
            close(conn, stm, rs);
        }

        return table;
    }

    /**
     * Đếm tổng số sản phẩm có tồn kho
     *
     * @param conn Connection đến database
     * @return Tổng số bản ghi
     */
    private int countAllSlowMovingCandidates(Connection conn) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) FROM bookshopdb.product p " +
                    "JOIN bookshopdb.inventory_status ist ON p.id = ist.product_id " +
                    "WHERE ist.actual_quantity > 0";
            stm = conn.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, stm, rs);
        }
        return 0;
    }
}
