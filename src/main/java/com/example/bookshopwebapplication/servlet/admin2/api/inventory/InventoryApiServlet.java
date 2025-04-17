package com.example.bookshopwebapplication.servlet.admin2.api.inventory;

import com.example.bookshopwebapplication.http.response_admin.DataTable;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryDistributionData;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryItem;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryRecently;
import com.example.bookshopwebapplication.http.response_admin.invetory.InventoryTrendData;
import com.example.bookshopwebapplication.service.InventoryStatisticsService;
import com.example.bookshopwebapplication.utils.JsonUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "InventoryApiServlet", urlPatterns = {
        "/api/admin/inventory/trends",
        "/api/admin/inventory/distribution",
        "/api/admin/inventory/inventory-status",
        "/api/admin/inventory/import-recently",
        "/api/admin/inventory/export-recently",
})
public class InventoryApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private InventoryStatisticsService inventoryStatisticsService;

    @Override
    public void init() throws ServletException {
        this.inventoryStatisticsService = new InventoryStatisticsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/api/admin/inventory/trends":
                getInventoryTrends(req, resp);
                break;
            case "/api/admin/inventory/distribution":
                getInventoryDistribution(req, resp);
                break;
            case "/api/admin/inventory/inventory-status":
                getInventoryStatus(req, resp);
                break;
            case  "/api/admin/inventory/import-recently":
                getInventoryImportRecently(req, resp);
                break;
            case "/api/admin/inventory/export-recently":
                getInventoryExportRecently(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "API not found");
                break;
        }
    }

    private void getInventoryExportRecently(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<InventoryRecently> result = inventoryStatisticsService.getInventoryExportRecently();

            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        }catch (Exception e) {
            JsonUtils.out(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getInventoryImportRecently(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<InventoryRecently> result = inventoryStatisticsService.getInventoryImportRecently();

            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        }catch (Exception e) {
            JsonUtils.out(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getInventoryStatus(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // Thu thập và xử lý tham số
            Map<String, String> params = getRequestParameters(req);

            // Đảm bảo các tham số có giá trị mặc định phù hợp
            if (!params.containsKey("start")) params.put("start", "0");
            if (!params.containsKey("length")) params.put("length", "10");
            if (!params.containsKey("draw")) params.put("draw", "1");

            // Chuyển đổi định dạng startDate và endDate
            if (params.containsKey("startDate")) {
                params.put("startDate", convertToTimestampFormat(params.get("startDate") + " 00:00:00"));
            }

            if (params.containsKey("endDate")) {
                params.put("endDate", convertToTimestampFormat(params.get("endDate") + " 23:59:59"));
            }

            // Gọi service để lấy dữ liệu
            DataTable<InventoryItem> result = inventoryStatisticsService.getInventoryItems(params);
            result.setDraw(Integer.parseInt(params.get("draw")));

            // Trả về dữ liệu dạng JSON
            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getInventoryDistribution(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<InventoryDistributionData> result = inventoryStatisticsService.getInventoryDistribution();

            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getInventoryTrends(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String interval = req.getParameter("interval");
            List<InventoryTrendData> result = inventoryStatisticsService.getInventoryTrendData(interval);

            JsonUtils.out(resp, result, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            JsonUtils.out(resp, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private String convertToTimestampFormat(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    /**
     * Thu thập tất cả tham số từ request và xử lý cho phù hợp với logic service
     */
    private Map<String, String> getRequestParameters(HttpServletRequest req) {
        Map<String, String> params = new HashMap<>();

        // Lấy các tham số từ request
        String[] requiredParams = {
                "draw", "start", "length",
                "order[0][column]", "order[0][dir]",
                "search[value]", "stockStatus",
                "category", "durationDays",
                "timeRange", "startDate",
                "endDate", "customDuration"
        };

        for (String param : requiredParams) {
            String value = req.getParameter(param);
            if (value != null && !value.trim().isEmpty()) {
                params.put(param, value);
            }
        }

        // Xử lý DataTables sorting
        String orderColumnIndex = params.get("order[0][column]");
        if (orderColumnIndex != null) {
            String columnNameParam = "columns[" + orderColumnIndex + "][data]";
            String columnName = req.getParameter(columnNameParam);
            if (columnName != null && !columnName.isEmpty()) {
                params.put("sortColumn", columnName);
            }
        }

        if (params.containsKey("order[0][dir]")) {
            params.put("sortDirection", params.get("order[0][dir]"));
        }

        // Xử lý tìm kiếm
        if (params.containsKey("search[value]")) {
            params.put("searchKeyword", params.get("search[value]"));
        }

        // Xử lý customDuration
        if ("custom".equals(params.get("durationDays"))) {
            String customDuration = params.get("customDuration");
            if (customDuration != null && !customDuration.isEmpty()) {
                params.put("durationDays", customDuration);
            }
        }

        // Xử lý timeRange và date range
        if (params.containsKey("timeRange")) {
            String timeRange = params.get("timeRange");
            if ("custom".equals(timeRange)) {
                // Sử dụng startDate và endDate đã có
            } else if (!"all".equals(timeRange)) {
                // Tính toán startDate và endDate dựa vào timeRange
                calculateDateRange(params, timeRange);
            }
        }

        // Đổi tên category thành categoryId để phù hợp với service
        if (params.containsKey("category")) {
            params.put("categoryId", params.get("category"));
        }

        return params;
    }

    /**
     * Tính toán khoảng thời gian dựa vào timeRange
     */
    private void calculateDateRange(Map<String, String> params, String timeRange) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        // Đặt endDate là ngày hiện tại
        String endDate = sdf.format(cal.getTime());
        params.put("endDate", endDate);

        switch (timeRange) {
            case "day":
                // Đặt startDate cũng là ngày hiện tại
                params.put("startDate", endDate);
                break;
            case "week":
                cal.add(Calendar.DAY_OF_WEEK, -7);
                params.put("startDate", sdf.format(cal.getTime()));
                break;
            case "month":
                cal.add(Calendar.MONTH, -1);
                params.put("startDate", sdf.format(cal.getTime()));
                break;
            case "quarter":
                cal.add(Calendar.MONTH, -3);
                params.put("startDate", sdf.format(cal.getTime()));
                break;
        }
    }
}
