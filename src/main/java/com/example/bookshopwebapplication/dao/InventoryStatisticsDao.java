package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryDistributionData;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryItem;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryRecently;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryTrendData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }catch (SQLException e) {
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
    public List<InventoryRecently> getInventoryExportRecently(){
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
        }catch (SQLException e) {
            System.out.println("Error creating connection or statement: " + e.getMessage());
            throw new RuntimeException("Lỗi trong hàm InventoryStatistícDao.getInventoryExportRecently() ", e);
        } finally {
            close(conn, stm, rs);
        }
        return exports;
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
}
