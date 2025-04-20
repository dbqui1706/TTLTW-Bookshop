package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.InventoryStatisticsDao;
import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InventoryStatisticsService {
    private final InventoryStatisticsDao inventoryStatisticsDao;

    public InventoryStatisticsService() {
        this.inventoryStatisticsDao = new InventoryStatisticsDao();
    }

    /**
     * Get inventory trend data
     *
     * @param freq
     * @return List<InventoryTrendData>
     */
    public List<InventoryTrendData> getInventoryTrendData(String freq) {
        return inventoryStatisticsDao.getInventoryTrendData(freq);
    }

    /**
     * Phân bố theo số lượng sản phẩm trong mỗi danh mục
     *
     * @return List<InventoryDistributionData>
     */
    public List<InventoryDistributionData> getInventoryDistribution() {
        return inventoryStatisticsDao.getInventoryDistribution();
    }

    /**
     * Lấy danh sách sản phẩm tồn kho với các tham số lọc
     *
     * @param params Các tham số lọc
     * @return DataTable<InventoryItem> Kết quả phân trang
     */
    public DataTable<InventoryItem> getInventoryItems(Map<String, String> params) {
        // Xử lý an toàn cho stockStatus
        String stockStatus = params.get("stockStatus");

        // Xử lý an toàn cho categoryId
        Long categoryId = null;
        if (params.containsKey("categoryId") && params.get("categoryId") != null && !params.get("categoryId").isEmpty()) {
            try {
                categoryId = Long.valueOf(params.get("categoryId"));
            } catch (NumberFormatException e) {
                // Log lỗi nếu cần
            }
        }

        // Xử lý an toàn cho durationDays
        Integer durationDays = null;
        if (params.containsKey("durationDays") && params.get("durationDays") != null && !params.get("durationDays").isEmpty()) {
            try {
                durationDays = Integer.parseInt(params.get("durationDays"));
            } catch (NumberFormatException e) {
                // Log lỗi nếu cần
            }
        }

        // Xử lý an toàn cho startDate và endDate
        Timestamp startDateTs = null;
        if (params.containsKey("startDate") && params.get("startDate") != null && !params.get("startDate").isEmpty()) {
            try {
                startDateTs = Timestamp.valueOf(params.get("startDate"));
            } catch (IllegalArgumentException e) {
                // Thử chuyển đổi từ định dạng date khác
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = dateFormat.parse(params.get("startDate"));
                    startDateTs = new Timestamp(parsedDate.getTime());
                } catch (Exception ex) {
                    // Log lỗi nếu cần
                }
            }
        }

        Timestamp endDateTs = null;
        if (params.containsKey("endDate") && params.get("endDate") != null && !params.get("endDate").isEmpty()) {
            try {
                endDateTs = Timestamp.valueOf(params.get("endDate"));
            } catch (IllegalArgumentException e) {
                // Thử chuyển đổi từ định dạng date khác
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = dateFormat.parse(params.get("endDate"));
                    // Thêm 23:59:59 cho endDate
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    endDateTs = new Timestamp(cal.getTimeInMillis());
                } catch (Exception ex) {
                    // Log lỗi nếu cần
                }
            }
        }

        // Xử lý an toàn cho searchKeyword
        String searchKeyword = params.get("searchKeyword");

        // Xử lý an toàn cho start và length
        int start = 0;
        if (params.containsKey("start") && params.get("start") != null && !params.get("start").isEmpty()) {
            try {
                start = Integer.parseInt(params.get("start"));
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định 0
            }
        }

        int length = 10; // Giá trị mặc định
        if (params.containsKey("length") && params.get("length") != null && !params.get("length").isEmpty()) {
            try {
                length = Integer.parseInt(params.get("length"));
            } catch (NumberFormatException e) {
                // Sử dụng giá trị mặc định 10
            }
        }

        // Xử lý cho sortColumn và sortDirection
        String sortColumn = params.get("sortColumn");
        String sortDirection = params.get("sortDirection");
        if (sortDirection == null || (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc"))) {
            sortDirection = "desc"; // Giá trị mặc định
        }

        // Gọi DAO để lấy dữ liệu
        return inventoryStatisticsDao.getInventoryItems(
                stockStatus,
                categoryId,
                durationDays,
                startDateTs,
                endDateTs,
                searchKeyword,
                start,
                length,
                sortColumn,
                sortDirection
        );
    }

    /**
     * Lấy danh sách phiếu nhập gần đây
     *
     * @return List<InventoryRecently>
     */
    public List<InventoryRecently> getInventoryImportRecently() {
        return inventoryStatisticsDao.getInventoryImportRecently();
    }

    /*
     * Lấy danh sách phiếu xuất gần đây
     *
     * @return List<InventoryRecently>
     */
    public List<InventoryRecently> getInventoryExportRecently() {
        return inventoryStatisticsDao.getInventoryExportRecently();
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
        return inventoryStatisticsDao.getInventoryHistory(draw, start, length, searchValue,
                orderColumnIndex, orderDirection, productId, actionType, quantityChangeFilter,
                referenceFilter, reasonFilter, startDate, endDate, userFilter, groupByDay);
    }

    /**
     * Lấy danh sách sản phẩm có giá trị tồn kho cao nhất
     *
     * @param limit Số lượng sản phẩm cần lấy
     * @return List<InventoryValueItem>
     */
    public List<InventoryValueItem> getTopInventoryValue(int limit) {
        return inventoryStatisticsDao.getTopInventoryValueProducts(limit);
    }

    /**
     * Lấy biến động xuất nhập tồn kho theo giai đoạn
     *
     * @param period    Giai đoạn (day, week, month, quarter)
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     */

    public List<InventoryMovementData> getInventoryMovementData(String period, Date startDate, Date endDate) {
        if (period == null || period.isEmpty()) {
            period = "day"; // Default period
        }

        switch (period.toLowerCase()) {
            case "week":
                return inventoryStatisticsDao.getInventoryMovementByWeek(startDate, endDate);
            case "month":
                return inventoryStatisticsDao.getInventoryMovementByMonth(startDate, endDate);
            case "quarter":
                return inventoryStatisticsDao.getInventoryMovementByQuarter(startDate, endDate);
            case "day":
            default:
                return inventoryStatisticsDao.getInventoryMovementByDay(startDate, endDate);
        }
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
            int orderColumnIndex, String orderDirection,
            Long categoryId, Double minPrice, Double maxPrice,
            Integer minDaysInStock, Double maxTurnover) {


        DataTable<SlowMovingItem> result = inventoryStatisticsDao.getSlowMovingProducts(
                draw, start, length, searchValue,
                orderColumnIndex, orderDirection,
                categoryId, minPrice, maxPrice,
                minDaysInStock, maxTurnover);
        result.setDraw(draw);
        return result;
    }
}
