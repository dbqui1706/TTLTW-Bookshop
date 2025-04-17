package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.InventoryStatisticsDao;
import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryDistributionData;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryItem;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryRecently;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryTrendData;

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
    public List<InventoryRecently> getInventoryImportRecently(){
        return inventoryStatisticsDao.getInventoryImportRecently();
    }

    /*
     * Lấy danh sách phiếu xuất gần đây
     *
     * @return List<InventoryRecently>
     */
    public List<InventoryRecently> getInventoryExportRecently(){
        return inventoryStatisticsDao.getInventoryExportRecently();
    }
}
