package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IOrderAdminDao;
import com.example.bookshopwebapplication.dao.mapper.OrderDTOMapper;
import com.example.bookshopwebapplication.dao.mapper.OrderItemDTOMapper;
import com.example.bookshopwebapplication.dao.utils.QueryBuilderResult;
import com.example.bookshopwebapplication.entities.Order2;
import com.example.bookshopwebapplication.http.response.order_detail.OrderItemDetailDTO;
import com.example.bookshopwebapplication.http.response.order_detail.OrderSummaryDTO;
import com.example.bookshopwebapplication.http.response_admin.orders.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OrderAdminDao extends AbstractDao<Order2> implements IOrderAdminDao {

    public OrderAdminDao() {
        super("orders");
    }

    @Override
    public Order2 mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    public OrderListResponse getOrdersWithPagination(Map<String, String> params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderDTO> orderDTOs = new ArrayList<>();
        try {
            conn = getConnection();

            // 1. Xây dựng và thực thi truy vấn chính
            QueryBuilderResult queryResult = buildMainQuery(params);
            pstmt = prepareStatement(conn, queryResult.sql(), queryResult.params());
            rs = pstmt.executeQuery();

            // 2. Xử lý kết quả và chuyển thành DTO
            orderDTOs = processQueryResults(conn, rs);

            // 3. Đếm tổng số bản ghi (không có filter)
            int totalRecords = countTotalRecords(conn);

            // 4. Đếm tổng số bản ghi sau khi áp dụng filter
            int totalRecordsFiltered = countFilteredRecords(conn, params);

            // 5. Tạo response cho DataTable
            int draw = Integer.parseInt(params.getOrDefault("draw", "1"));
            return OrderListResponse.builder()
                    .data(orderDTOs)
                    .draw(draw)
                    .recordsTotal(totalRecords)
                    .recordsFiltered(totalRecordsFiltered)
                    .build();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage(), e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * Tạo PreparedStatement và đặt các tham số
     */
    private PreparedStatement prepareStatement(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }

        return pstmt;
    }

    /**
     * Đếm tổng số bản ghi không áp dụng filter
     */
    private int countTotalRecords(Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder().append("SELECT COUNT(*) FROM orders");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString());
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Đếm tổng số bản ghi sau khi áp dụng filter
     */
    private int countFilteredRecords(Connection conn, Map<String, String> params) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) FROM orders o ");
        queryBuilder.append("LEFT JOIN user u ON o.user_id = u.id ");
        queryBuilder.append("WHERE 1=1 ");

        List<Object> queryParams = new ArrayList<>();

        // Thêm các điều kiện lọc vào truy vấn đếm
        addFilterConditions(queryBuilder, queryParams, params);

        try (PreparedStatement pstmt = prepareStatement(conn, queryBuilder.toString(), queryParams);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Xử lý kết quả truy vấn và chuyển đổi thành danh sách OrderDTO
     *
     * @param conn Kết nối cơ sở dữ liệu
     * @param rs   ResultSet chứa kết quả truy vấn
     * @return Danh sách OrderDTO
     */
    private List<OrderDTO> processQueryResults(Connection conn, ResultSet rs) {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        try {
            while (rs.next()) {
                OrderDTO orderDTO = new OrderDTOMapper().mapRow(rs);
                orderDTO.setItems(getOrderItems(conn, orderDTO.getId()));
                orderDTOs.add(orderDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xử lý kết quả truy vấn: " + e.getMessage(), e);
        }
        return orderDTOs;
    }

    /*
     * Xây dựng truy vấn chính cho danh sách đơn hàng
     * @param params Tham số truy vấn từ client
     * @return QueryBuilderResult Chứa truy vấn SQL và các tham số
     */
    private QueryBuilderResult buildMainQuery(Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder();
        List<Object> queryParams = new ArrayList<>();

        // 1. Xây dựng phần SELECT của truy vấn
        buildSelectClause(queryBuilder);

        // 2. Thêm các điều kiện lọc
        addFilterConditions(queryBuilder, queryParams, params);

        // 3. Thêm sắp xếp
        addOrderByClause(queryBuilder, params);

        // 4. Thêm phân trang
        addPaginationClause(queryBuilder, params);

        return new QueryBuilderResult(queryBuilder.toString(), queryParams);
    }

    /**
     * Thêm mệnh đề LIMIT cho phân trang
     */
    private void addPaginationClause(StringBuilder queryBuilder, Map<String, String> params) {
        int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1;
        int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;

        int offset = (page - 1) * limit;

        queryBuilder.append("LIMIT ").append(offset).append(", ").append(limit);

    }

    /**
     * Thêm các điều kiện lọc vào truy vấn
     */
    private void addFilterConditions(StringBuilder queryBuilder, List<Object> queryParams, Map<String, String> params) {
        // Lọc theo từ khóa tìm kiếm
        if (params.containsKey("search") && !params.get("search").isEmpty()) {
            String searchTerm = "%" + params.get("search") + "%";
            queryBuilder.append("AND (o.order_code LIKE ? OR u.fullname LIKE ? OR u.phoneNumber LIKE ?) ");
            queryParams.add(searchTerm);
            queryParams.add(searchTerm);
            queryParams.add(searchTerm);
        }

        // Lọc theo trạng thái
        if (params.containsKey("status") && !params.get("status").isEmpty()) {
            queryBuilder.append("AND o.status = ? ");
            queryParams.add(params.get("status"));
        }

        // Lọc theo phương thức thanh toán
        if (params.containsKey("paymentMethod") && !params.get("paymentMethod").isEmpty()) {
            queryBuilder.append("AND o.payment_method_id = ? ");
            queryParams.add(Long.parseLong(params.get("paymentMethod")));
        }

        // Lọc theo khoảng thời gian
        if (params.containsKey("fromDate") && !params.get("fromDate").isEmpty()) {
            queryBuilder.append("AND o.created_at >= ? ");
            queryParams.add(java.sql.Date.valueOf(params.get("fromDate")));
        }

        if (params.containsKey("toDate") && !params.get("toDate").isEmpty()) {
            queryBuilder.append("AND o.created_at <= ? ");
            // Thêm 1 ngày để bao gồm cả ngày kết thúc
            java.sql.Date toDate = java.sql.Date.valueOf(params.get("toDate"));
            Calendar c = Calendar.getInstance();
            c.setTime(toDate);
            c.add(Calendar.DATE, 1);
            queryParams.add(new java.sql.Date(c.getTimeInMillis()));
        }
    }

    /**
     * Thêm mệnh đề ORDER BY vào truy vấn
     */
    private void addOrderByClause(StringBuilder queryBuilder, Map<String, String> params) {
        // Map tên cột frontend sang tên cột database
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("id", "o.id");
        columnMap.put("orderCode", "o.order_code");
        columnMap.put("userName", "u.fullname");
        columnMap.put("status", "o.status");
        columnMap.put("totalAmount", "o.total_amount");
        columnMap.put("createdAt", "o.created_at");

        String sortBy = params.getOrDefault("sortBy", "created_at");
        String sortDir = params.getOrDefault("sortDir", "desc");

        String dbColumn = columnMap.getOrDefault(sortBy, "o.created_at");
        queryBuilder.append("ORDER BY ").append(dbColumn).append(" ").append(sortDir).append(" ");
    }

    private void buildSelectClause(StringBuilder queryBuilder) {
        queryBuilder.append("SELECT o.*, u.fullname AS user_name, u.email AS user_email, ");
        queryBuilder.append("u.phoneNumber AS user_phone, pm.name AS payment_method_name, ");
        queryBuilder.append("pt.status AS payment_status, os.receiver_name, os.receiver_phone, ");
        queryBuilder.append("CONCAT(os.address_line1, ', ', os.district, ', ', os.city) AS address ");
        queryBuilder.append("FROM orders o ");
        queryBuilder.append("LEFT JOIN user u ON o.user_id = u.id ");
        queryBuilder.append("LEFT JOIN payment_method pm ON o.payment_method_id = pm.id ");
        queryBuilder.append("LEFT JOIN payment_transaction pt ON o.id = pt.order_id AND pt.id = (");
        queryBuilder.append("    SELECT MAX(id) FROM payment_transaction WHERE order_id = o.id");
        queryBuilder.append(") ");
        queryBuilder.append("LEFT JOIN order_shipping os ON o.id = os.order_id ");
        queryBuilder.append("WHERE 1=1 ");
    }

    /**
     * Lấy danh sách sản phẩm trong đơn hàng
     *
     * @param conn    Connection hiện tại
     * @param orderId ID đơn hàng
     * @return List<OrderItemDTO> Danh sách sản phẩm
     */
    private List<OrderItemDTO> getOrderItems(Connection conn, Long orderId) throws SQLException {
        List<OrderItemDTO> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT oi.id, oi.product_id, oi.product_name, oi.product_image, ");
        sql.append("oi.base_price, oi.discount_percent, oi.price, oi.quantity, oi.subtotal ");
        sql.append("FROM order_item oi WHERE oi.order_id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setLong(1, orderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItemDTO item = new OrderItemDTOMapper().mapRow(rs);
                    items.add(item);
                }
            }
        }

        return items;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId) {
        return null;
    }

    @Override
    public OrderDetailResponse getOrderDetailByCode(String code) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // 1. Lấy thông tin cơ bản của đơn hàng
            OrderInfoDTO orderInfo = getOrderInfo(conn, code);
            if (orderInfo == null) {
                return null; // Không tìm thấy đơn hàng
            }

            // 2. Lấy thông tin phương thức giao hàng
            DeliveryMethodDTO deliveryMethod = getDeliveryMethod(conn, orderInfo.getId());

            // 3. Lấy thông tin phương thức thanh toán
            PaymentMethodDTO paymentMethod = getPaymentMethod(conn, orderInfo.getId());

            // 4. Lấy thông tin giao hàng
            ShippingInfoDTO shippingInfo = getShippingInfo(conn, orderInfo.getId());

            // 5. Lấy thông tin giao dịch thanh toán
            PaymentTransactionDTO paymentTransaction = getPaymentTransaction(conn, orderInfo.getId());

            // 6. Lấy danh sách sản phẩm trong đơn hàng
            List<OrderItemDetailDTO> orderItems = getOrderItemDetails(conn, orderInfo.getId());

            // 7. Lấy lịch sử trạng thái đơn hàng
            List<OrderStatusHistoryDTO> timeline = getOrderStatusHistory(conn, orderInfo.getId());

            // 8. Kiểm tra xem đơn hàng có thể hủy không
            boolean canCancel = canCancelOrder(orderInfo.getStatus());

            // 9. Lấy thông tin tổng hợp đơn hàng
            OrderSummaryDTO summary = getOrderSummary(conn, orderInfo.getId());

            // 10. Tạo và trả về đối tượng OrderDetailResponse
            return OrderDetailResponse.builder()
                    .order(orderInfo)
                    .delivery(deliveryMethod)
                    .payment(paymentMethod)
                    .shipping(shippingInfo)
                    .paymentTransaction(paymentTransaction)
                    .items(orderItems)
                    .timeLine(timeline)
                    .canCancel(canCancel)
                    .summary(summary)
                    .build();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage(), e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin cơ bản của đơn hàng theo mã
     */
    private OrderInfoDTO getOrderInfo(Connection conn, String orderCode) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.id, o.order_code AS code, o.created_at, o.status, o.note, ");
        sql.append("o.user_id, os.receiver_name AS customer_name, ");
        sql.append("os.receiver_phone AS customer_phone, os.receiver_email AS customer_email, ");
        sql.append("os.address_line1 AS customer_address_line1, os.address_line2 AS customer_address_line2, ");
        sql.append("CONCAT(os.address_line1, ");
        sql.append("       CASE WHEN os.address_line2 IS NOT NULL THEN CONCAT(', ', os.address_line2) ELSE '' END, ");
        sql.append("       ', ', os.ward, ");
        sql.append("       ', ', os.district, ");
        sql.append("       ', ', os.city) AS customer_full_address, ");
        sql.append("o.coupon_code, o.subtotal, o.discount_amount AS discount, ");
        sql.append("o.delivery_price AS shipping, o.total_amount AS total ");
        sql.append("FROM bookshopdb.orders o ");
        sql.append("JOIN bookshopdb.order_shipping os ON o.id = os.order_id ");
        sql.append("WHERE o.order_code = ?");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, orderCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new OrderInfoDTO(rs);
            }

            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin phương thức giao hàng
     */
    private DeliveryMethodDTO getDeliveryMethod(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dm.id, dm.name, dm.description, dm.price, dm.estimated_days, dm.icon ");
        sql.append("FROM delivery_method dm ");
        sql.append("JOIN orders o ON o.delivery_method_id = dm.id ");
        sql.append("WHERE o.id = ?");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new DeliveryMethodDTO(rs);
            }

            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin phương thức thanh toán
     */
    private PaymentMethodDTO getPaymentMethod(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pm.id, pm.name, pm.code, pm.description, pm.icon ");
        sql.append("FROM payment_method pm ");
        sql.append("JOIN orders o ON o.payment_method_id = pm.id ");
        sql.append("WHERE o.id = ?");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PaymentMethodDTO(rs);
            }

            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin giao hàng
     */
    private ShippingInfoDTO getShippingInfo(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT os.receiver_name, os.receiver_email, os.receiver_phone, ");
        sql.append("os.address_line1, os.address_line2, os.city, os.district, os.ward, ");
        sql.append("os.postal_code, os.shipping_notes, os.tracking_number, os.shipping_carrier ");
        sql.append("FROM order_shipping os ");
        sql.append("WHERE os.order_id = ?");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ShippingInfoDTO(rs);
            }
            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin giao dịch thanh toán gần nhất
     */
    private PaymentTransactionDTO getPaymentTransaction(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pt.id, pt.transaction_code, pt.payment_provider_ref, pt.amount, ");
        sql.append("pt.status, pt.payment_date, pt.note, pt.created_at, pt.updated_at ");
        sql.append("FROM payment_transaction pt ");
        sql.append("WHERE pt.order_id = ? ");
        sql.append("ORDER BY pt.id DESC LIMIT 1");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PaymentTransactionDTO().mapRow(rs);
            }
            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy danh sách sản phẩm trong đơn hàng
     */
    private List<OrderItemDetailDTO> getOrderItemDetails(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT oi.id, oi.product_id, oi.product_name, oi.product_image, ");
        sql.append("p.author, oi.base_price, oi.discount_percent, oi.price, oi.quantity, oi.subtotal ");
        sql.append("FROM order_item oi ");
        sql.append("LEFT JOIN product p ON oi.product_id = p.id ");
        sql.append("WHERE oi.order_id = ?");

        List<OrderItemDetailDTO> items = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(new OrderItemDetailDTO().mapRow(rs));
            }
            return items;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy lịch sử trạng thái đơn hàng
     */
    private List<OrderStatusHistoryDTO> getOrderStatusHistory(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT osh.id, osh.status, osh.note, osh.changed_by, osh.created_at, ");
        sql.append("CASE ");
        sql.append("    WHEN osh.changed_by IS NOT NULL THEN ");
        sql.append("        (SELECT fullname FROM user WHERE id = osh.changed_by) ");
        sql.append("    ELSE NULL ");
        sql.append("END AS changed_by_name ");
        sql.append("FROM order_status_history osh ");
        sql.append("WHERE osh.order_id = ? ");
        sql.append("ORDER BY osh.created_at ASC");

        List<OrderStatusHistoryDTO> statusHistory = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderStatusHistoryDTO history = new OrderStatusHistoryDTO().mapRow(rs);
                statusHistory.add(history);
            }

            return statusHistory;
        } finally {
            close(null, pstmt, rs);
        }
    }

    /**
     * Lấy thông tin tổng hợp đơn hàng
     */
    private OrderSummaryDTO getOrderSummary(Connection conn, Long orderId) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.subtotal, o.discount_amount, o.delivery_price, o.tax_amount, o.total_amount, o.coupon_code ");
        sql.append("FROM orders o WHERE o.id = ?");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return new OrderSummaryDTO().mapRow(rs);
            }
            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }


    @Override
    public boolean updateOrderStatus(Long orderId, String status, String note, Long userId) {
        return false;
    }

    @Override
    public boolean isValidStatusTransition(String oldStatus, String newStatus) {
        return false;
    }

    @Override
    public boolean canCancelOrder(String status) {
        return false;
    }

    @Override
    public List<OrderTimelineDTO> buildOrderTimeline(List<OrderStatusHistoryDTO> statusHistory, OrderInfoDTO orderInfo) {
        return List.of();
    }

    @Override
    public OrderStatisticsDTO getOrderStatistics() {
        return null;
    }

    @Override
    public List<ProductSearchDTO> searchProducts(String keyword) {
        return List.of();
    }

    @Override
    public List<CustomerSearchDTO> searchCustomers(String keyword) {
        return List.of();
    }
}
