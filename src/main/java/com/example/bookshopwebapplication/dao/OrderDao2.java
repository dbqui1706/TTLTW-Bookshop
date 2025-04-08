package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.Order2Mapper;
import com.example.bookshopwebapplication.entities.Order2;
import com.example.bookshopwebapplication.http.response.order.OrderDTO;
import com.example.bookshopwebapplication.http.response.order.OrderItemDTO;
import com.example.bookshopwebapplication.http.response.order.OrderPageResponse;
import com.example.bookshopwebapplication.http.response.order_detail.*;
import com.google.gson.Gson;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class OrderDao2 extends AbstractDao<Order2> {
    public OrderDao2() {
        super("orders");
    }

    public Long save(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO orders (order_code, user_id, status, delivery_method_id, payment_method_id, ");
        builderSQL.append("subtotal, delivery_price, discount_amount, tax_amount, total_amount, coupon_code, is_verified, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), order.getOrderCode(), order.getUserId(), order.getStatus(),
                order.getDeliveryMethodId(), order.getPaymentMethodId(), order.getSubtotal(),
                order.getDeliveryPrice(), order.getDiscountAmount(), order.getTaxAmount(),
                order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0, order.getNote());
    }

    /**
     * Cập nhật toàn bộ thông tin đơn hàng
     *
     * @param order Đơn hàng cần cập nhật
     * @return true nếu cập nhật thành công
     */
    public boolean update(Order2 order) {
        clearSQL();
        builderSQL.append("UPDATE orders SET ");
        builderSQL.append("status = ?, ");
        builderSQL.append("delivery_method_id = ?, ");
        builderSQL.append("payment_method_id = ?, ");
        builderSQL.append("subtotal = ?, ");
        builderSQL.append("delivery_price = ?, ");
        builderSQL.append("discount_amount = ?, ");
        builderSQL.append("tax_amount = ?, ");
        builderSQL.append("total_amount = ?, ");
        builderSQL.append("coupon_code = ?, ");
        builderSQL.append("is_verified = ?, ");
        builderSQL.append("note = ?, ");
        builderSQL.append("updated_at = ? ");
        builderSQL.append("WHERE id = ?");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(),
                order.getStatus(),
                order.getDeliveryMethodId(),
                order.getPaymentMethodId(),
                order.getSubtotal(),
                order.getDeliveryPrice(),
                order.getDiscountAmount(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getIsVerified() ? 1 : 0,
                order.getNote(),
                now,
                order.getId());
        return true;
    }

    public Optional<Order2> findById(Long orderId) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE id = ?");
        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper(), orderId);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    /**
     * Cập nhật trạng thái đơn hàng
     *
     * @param orderId ID đơn hàng
     * @param status  Trạng thái mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(Long orderId, String status) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ?, updated_at = ? WHERE id = ?");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(), status, now, orderId);
        return true;
    }

    /**
     * Tìm tất cả đơn hàng theo trạng thái
     *
     * @param status Trạng thái cần tìm
     * @return Danh sách đơn hàng
     */
    public List<Order2> findByStatus(String status) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC");
        return query(builderSQL.toString(), new Order2Mapper(), status);
    }

    /**
     * Tìm đơn hàng theo mã đơn hàng
     *
     * @param orderCode Mã đơn hàng
     * @return Optional chứa đơn hàng nếu tìm thấy
     */
    public Optional<Order2> findByOrderCode(String orderCode) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE order_code = ?");
        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper(), orderCode);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    /**
     * Tìm đơn hàng theo người dùng
     *
     * @param userId ID người dùng
     * @return Danh sách đơn hàng
     */
    public List<Order2> findByUserId(Long userId) {
        clearSQL();
        builderSQL.append("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC");
        return query(builderSQL.toString(), new Order2Mapper(), userId);
    }

    /**
     * Đếm số đơn hàng theo trạng thái
     *
     * @param status Trạng thái cần đếm
     * @return Số lượng đơn hàng
     */
    public int countByStatus(String status) {
        clearSQL();
        builderSQL.append("SELECT COUNT(*) FROM orders WHERE status = ?");
        return count(builderSQL.toString(), status);
    }

    /**
     * Tìm các đơn hàng đang chờ xử lý và có thể được xử lý (còn đủ hàng trong kho)
     *
     * @return Danh sách ID đơn hàng
     */
    public List<Long> findPendingOrderIds() {
        clearSQL();
        builderSQL.append("SELECT id FROM orders WHERE status = 'pending' ORDER BY created_at ASC");

        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper());
        return orders.stream().map(Order2::getId).toList();
    }

    /**
     * Tìm các đơn hàng đã xác nhận và sẵn sàng giao hàng
     *
     * @return Danh sách ID đơn hàng
     */
    public List<Long> findConfirmedOrderIds() {
        clearSQL();
        builderSQL.append("SELECT id FROM orders WHERE status = 'confirmed' ORDER BY created_at ASC");

        List<Order2> orders = query(builderSQL.toString(), new Order2Mapper());
        return orders.stream().map(Order2::getId).toList();
    }

    /**
     * Cập nhật xác thực đơn hàng
     *
     * @param orderId    ID đơn hàng
     * @param isVerified Trạng thái xác thực
     * @return true nếu cập nhật thành công
     */
    public boolean updateVerificationStatus(Long orderId, boolean isVerified) {
        clearSQL();
        builderSQL.append("UPDATE orders SET is_verified = ?, updated_at = ? WHERE id = ?");
        Timestamp now = new Timestamp(System.currentTimeMillis());
        update(builderSQL.toString(), isVerified ? 1 : 0, now, orderId);
        return true;
    }

    // Thêm vào OrderDao2
    public Long saveWithConnection(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("INSERT INTO orders (order_code, user_id, status, delivery_method_id, payment_method_id, ");
        builderSQL.append("subtotal, delivery_price, discount_amount, tax_amount, total_amount, coupon_code, is_verified, note) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insertWithConnection(conn, builderSQL.toString(), order.getOrderCode(), order.getUserId(), order.getStatus(),
                order.getDeliveryMethodId(), order.getPaymentMethodId(), order.getSubtotal(),
                order.getDeliveryPrice(), order.getDiscountAmount(), order.getTaxAmount(),
                order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0, order.getNote());
    }


    @Override
    public Order2 mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            Order2 order = Order2.builder()
                    .id(rs.getLong("id"))
                    .orderCode(rs.getString("order_code"))
                    .userId(rs.getLong("user_id"))
                    .status(rs.getString("status"))
                    .deliveryMethodId(rs.getLong("delivery_method_id"))
                    .paymentMethodId(rs.getLong("payment_method_id"))
                    .subtotal(rs.getDouble("subtotal"))
                    .deliveryPrice(rs.getDouble("delivery_price"))
                    .discountAmount(rs.getDouble("discount_amount"))
                    .taxAmount(rs.getDouble("tax_amount"))
                    .totalAmount(rs.getDouble("total_amount"))
                    .couponCode(rs.getString("coupon_code"))
                    .isVerified(rs.getBoolean("is_verified"))
                    .note(rs.getString("note"))
                    .createdAt(rs.getTimestamp("created_at"))
                    .updatedAt(rs.getTimestamp("updated_at") != null
                            ? rs.getTimestamp("updated_at") : null)
                    .build();
            return order;
        } catch (Exception e) {
            System.out.println("Error mapping ResultSet to Order2 entity: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateWithConnection(Order2 order, Connection conn) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ?, delivery_method_id = ?, payment_method_id = ?, ");
        builderSQL.append("subtotal = ?, delivery_price = ?, discount_amount = ?, tax_amount = ?, total_amount = ?, ");
        builderSQL.append("coupon_code = ?, is_verified = ?, note = ? WHERE id = ?");
        updateWithConnection(conn, builderSQL.toString(), order.getStatus(), order.getDeliveryMethodId(),
                order.getPaymentMethodId(), order.getSubtotal(), order.getDeliveryPrice(), order.getDiscountAmount(),
                order.getTaxAmount(), order.getTotalAmount(), order.getCouponCode(), order.getIsVerified() ? 1 : 0,
                order.getNote(), order.getId());
    }

    public void updateStatusWithConnection(Long orderId, String processing, Connection conn) {
        clearSQL();
        builderSQL.append("UPDATE orders SET status = ? WHERE id = ?");
        updateWithConnection(conn, builderSQL.toString(), processing, orderId);

    }

    /**
     * Lấy danh sách đơn hàng của người dùng với các điều kiện lọc và phân trang
     *
     * @param userId     ID người dùng
     * @param status     Trạng thái đơn hàng (có thể null)
     * @param searchTerm Từ khóa tìm kiếm (có thể null)
     * @param sortBy     Trường sắp xếp (có thể null)
     * @param page       Số trang
     * @param pageSize   Kích thước trang
     * @return OrderPageResponse chứa danh sách đơn hàng và thông tin phân trang
     */
    public OrderPageResponse getUserOrders(Long userId, String status,
                                           String searchTerm, String sortBy,
                                           int page, int pageSize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderDTO> orders = new ArrayList<>();

        try {
            conn = getConnection();

            // 1. Query lấy danh sách đơn hàng với phân trang
            StringBuilder mainQuery = new StringBuilder();
            mainQuery.append("SELECT ");
            // Thông tin đơn hàng
            mainQuery.append("o.id AS order_id, o.order_code, o.status, ");
            mainQuery.append("o.created_at AS order_date, ");
            mainQuery.append("o.subtotal, o.delivery_price, o.discount_amount, o.total_amount, ");

            // Thông tin sản phẩm trong đơn hàng
            mainQuery.append("GROUP_CONCAT( ");
            mainQuery.append("    JSON_OBJECT( ");
            mainQuery.append("        'id', oi.id, ");
            mainQuery.append("        'productId', oi.product_id, ");
            mainQuery.append("        'productName', oi.product_name, ");
            mainQuery.append("        'productImage', IFNULL(oi.product_image, '/asset/images/image.png'), ");
            mainQuery.append("        'productVariant', CONCAT('Phiên bản: ', IFNULL(p.publisher, 'Bìa mềm')), ");
            mainQuery.append("        'quantity', oi.quantity, ");
            mainQuery.append("        'price', oi.price, ");
            mainQuery.append("        'subtotal', oi.subtotal ");
            mainQuery.append("    ) SEPARATOR '||' ");
            mainQuery.append(") AS order_items, ");

            // Tổng số sản phẩm trong đơn hàng
            mainQuery.append("COUNT(oi.id) AS total_items, ");

            // Thông tin thanh toán
            mainQuery.append("MAX(pm.name) AS payment_method, ");

            // Thông tin giao hàng
            mainQuery.append("MAX(os.receiver_name) AS receiver_name, ");
            mainQuery.append("MAX(os.receiver_phone) AS receiver_phone, ");
            mainQuery.append("MAX(os.address_line1) AS address_line1, ");
            mainQuery.append("MAX(os.district) AS district, ");
            mainQuery.append("MAX(os.city) AS city, ");

            // Theo dõi đơn hàng
            mainQuery.append("MAX(os.tracking_number) AS tracking_number, ");
            mainQuery.append("MAX(os.shipping_carrier) AS shipping_carrier, ");

            // Thông tin thanh toán
            mainQuery.append("MAX(pt.transaction_code) AS transaction_code, ");
            mainQuery.append("MAX(pt.payment_date) AS payment_date, ");
            mainQuery.append("MAX(pt.status) AS payment_status ");

            mainQuery.append("FROM bookshopdb.orders o ");
            mainQuery.append("LEFT JOIN bookshopdb.order_item oi ON o.id = oi.order_id ");
            mainQuery.append("LEFT JOIN bookshopdb.product p ON oi.product_id = p.id ");
            mainQuery.append("LEFT JOIN bookshopdb.payment_method pm ON o.payment_method_id = pm.id ");
            mainQuery.append("LEFT JOIN bookshopdb.order_shipping os ON o.id = os.order_id ");
            mainQuery.append("LEFT JOIN bookshopdb.payment_transaction pt ON o.id = pt.order_id ");

            mainQuery.append("WHERE o.user_id = ? ");

            // Điều kiện lọc theo trạng thái
            if (status != null && !status.isEmpty()) {
                mainQuery.append("AND o.status = ? ");
            }

            // Tìm kiếm theo mã đơn hàng
            if (searchTerm != null && !searchTerm.isEmpty()) {
                mainQuery.append("AND o.order_code LIKE ? ");
            }

            mainQuery.append("GROUP BY o.id, o.order_code, o.status, o.created_at, o.subtotal, o.delivery_price, o.discount_amount, o.total_amount ");

            // Sắp xếp
            if (sortBy == null || sortBy.isEmpty() || sortBy.equals("newest")) {
                mainQuery.append("ORDER BY o.created_at DESC ");
            } else if (sortBy.equals("oldest")) {
                mainQuery.append("ORDER BY o.created_at ASC ");
            } else if (sortBy.equals("highest")) {
                mainQuery.append("ORDER BY o.total_amount DESC ");
            } else if (sortBy.equals("lowest")) {
                mainQuery.append("ORDER BY o.total_amount ASC ");
            }

            // Phân trang
            mainQuery.append("LIMIT ?, ? ");

            pstmt = conn.prepareStatement(mainQuery.toString());

            int paramIndex = 1;
            pstmt.setLong(paramIndex++, userId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            }

            pstmt.setInt(paramIndex++, (page - 1) * pageSize);
            pstmt.setInt(paramIndex++, pageSize);

            rs = pstmt.executeQuery();

            Gson gson = new Gson();

            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setId(rs.getLong("order_id"));
                order.setOrderCode(rs.getString("order_code"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getString("order_date"));
                order.setSubtotal(rs.getDouble("subtotal"));
                order.setDeliveryPrice(rs.getDouble("delivery_price"));
                order.setDiscountAmount(rs.getDouble("discount_amount"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setTotalItems(rs.getInt("total_items"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setReceiverName(rs.getString("receiver_name"));
                order.setReceiverPhone(rs.getString("receiver_phone"));
                order.setAddress(rs.getString("address_line1"));
                order.setDistrict(rs.getString("district"));
                order.setCity(rs.getString("city"));
                order.setTrackingNumber(rs.getString("tracking_number"));
                order.setShippingCarrier(rs.getString("shipping_carrier"));
                order.setTransactionCode(rs.getString("transaction_code"));
                order.setPaymentDate(rs.getTimestamp("payment_date"));
                order.setPaymentStatus(rs.getString("payment_status"));

                // Xử lý danh sách sản phẩm trong đơn hàng
                String orderItemsJson = rs.getString("order_items");
                if (orderItemsJson != null && !orderItemsJson.isEmpty()) {
                    List<OrderItemDTO> orderItems = new ArrayList<>();
                    String[] itemsArray = orderItemsJson.split("\\|\\|");
                    for (String itemJson : itemsArray) {
                        OrderItemDTO item = gson.fromJson(itemJson, OrderItemDTO.class);
                        orderItems.add(item);
                    }
                    order.setOrderItems(orderItems);
                }

                orders.add(order);
            }

            // 2. Query để đếm tổng số đơn hàng phù hợp với điều kiện lọc
            StringBuilder countQuery = new StringBuilder();
            countQuery.append("SELECT COUNT(DISTINCT o.id) AS total_orders ");
            countQuery.append("FROM bookshopdb.orders o ");
            countQuery.append("WHERE o.user_id = ? ");

            if (status != null && !status.isEmpty()) {
                countQuery.append("AND o.status = ? ");
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                countQuery.append("AND o.order_code LIKE ? ");
            }

            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = conn.prepareStatement(countQuery.toString());

            paramIndex = 1;
            pstmt.setLong(paramIndex++, userId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            }

            if (rs != null) {
                rs.close();
            }
            rs = pstmt.executeQuery();

            int totalOrders = 0;
            if (rs.next()) {
                totalOrders = rs.getInt("total_orders");
            }

            // 3. Query để đếm số lượng đơn hàng theo từng trạng thái
            StringBuilder statusCountQuery = new StringBuilder();
            statusCountQuery.append("SELECT status, COUNT(*) as count ");
            statusCountQuery.append("FROM bookshopdb.orders ");
            statusCountQuery.append("WHERE user_id = ? ");
            statusCountQuery.append("GROUP BY status");

            if (pstmt != null) {
                pstmt.close();
            }
            pstmt = conn.prepareStatement(statusCountQuery.toString());
            pstmt.setLong(1, userId);

            if (rs != null) {
                rs.close();
            }
            rs = pstmt.executeQuery();

            Map<String, Integer> orderStatusCounts = new HashMap<>();
            while (rs.next()) {
                orderStatusCounts.put(rs.getString("status"), rs.getInt("count"));
            }

            // Tính toán thông tin phân trang
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

            // Tạo và trả về OrderPageResponse
            return OrderPageResponse.builder()
                    .orders(orders)
                    .totalOrders(totalOrders)
                    .totalPages(totalPages)
                    .currentPage(page)
                    .pageSize(pageSize)
                    .orderStatusCounts(orderStatusCounts)
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return OrderPageResponse.builder()
                    .orders(Collections.emptyList())
                    .totalOrders(0)
                    .totalPages(0)
                    .currentPage(page)
                    .pageSize(pageSize)
                    .orderStatusCounts(Collections.emptyMap())
                    .build();
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * Lấy chi tiết đơn hàng bao gồm thông tin đơn hàng, sản phẩm, giao hàng, thanh toán, và lịch sử
     *
     * @param orderCode Mã đơn hàng (ví dụ: BK2025031802)
     * @param userId    ID của người dùng (để xác thực quyền truy cập)
     * @return OrderDetailDTO chứa tất cả thông tin chi tiết đơn hàng hoặc null nếu không tìm thấy
     */
    public OrderDetailDTO getOrderDetail(String orderCode, Long userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // 1. Query thông tin đơn hàng chính
            StringBuilder mainQuery = new StringBuilder();
            mainQuery.append("SELECT ");
            mainQuery.append("o.id AS order_id, o.order_code, o.status, o.created_at AS order_date, ");
            mainQuery.append("o.subtotal, o.delivery_price, o.discount_amount, o.tax_amount, o.total_amount, ");
            mainQuery.append("o.coupon_code, o.note AS order_note, o.is_verified, ");

            // Thông tin phương thức giao hàng
            mainQuery.append("dm.id AS delivery_id, dm.name AS delivery_name, ");
            mainQuery.append("dm.description AS delivery_description, dm.estimated_days, dm.price AS delivery_method_price, ");

            // Thông tin phương thức thanh toán
            mainQuery.append("pm.id AS payment_id, pm.name AS payment_name, ");
            mainQuery.append("pm.code AS payment_code, pm.description AS payment_description, ");

            // Thông tin người dùng
            mainQuery.append("u.fullname AS user_name ");

            mainQuery.append("FROM bookshopdb.orders o ");
            mainQuery.append("JOIN bookshopdb.delivery_method dm ON o.delivery_method_id = dm.id ");
            mainQuery.append("JOIN bookshopdb.payment_method pm ON o.payment_method_id = pm.id ");
            mainQuery.append("JOIN bookshopdb.user u ON o.user_id = u.id ");
            mainQuery.append("WHERE o.order_code = ? ");

            // Bảo mật: Chỉ cho phép xem đơn hàng của chính mình
            mainQuery.append("AND o.user_id = ?");

            pstmt = conn.prepareStatement(mainQuery.toString());
            pstmt.setString(1, orderCode);
            pstmt.setLong(2, userId);

            rs = pstmt.executeQuery();

            // Nếu không tìm thấy đơn hàng hoặc người dùng không có quyền xem
            if (!rs.next()) {
                return null;
            }

            // Thông tin đơn hàng cơ bản
            Long orderId = rs.getLong("order_id");
            String status = rs.getString("status");

            OrderInfoDTO orderInfo = OrderInfoDTO.builder()
                    .id(orderId)
                    .orderCode(rs.getString("order_code"))
                    .status(status)
                    .statusText(getStatusText(status))
                    .orderDate(rs.getTimestamp("order_date"))
                    .subtotal(rs.getDouble("subtotal"))
                    .deliveryPrice(rs.getDouble("delivery_price"))
                    .discountAmount(rs.getDouble("discount_amount"))
                    .taxAmount(rs.getDouble("tax_amount"))
                    .totalAmount(rs.getDouble("total_amount"))
                    .couponCode(rs.getString("coupon_code"))
                    .note(rs.getString("order_note"))
                    .isVerified(rs.getBoolean("is_verified"))
                    .build();

            // Thông tin phương thức giao hàng
            DeliveryMethodDTO deliveryMethod = DeliveryMethodDTO.builder()
                    .id(rs.getLong("delivery_id"))
                    .name(rs.getString("delivery_name"))
                    .description(rs.getString("delivery_description"))
                    .estimatedDays(rs.getString("estimated_days"))
                    .price(rs.getDouble("delivery_method_price"))
                    .build();

            // Thông tin phương thức thanh toán
            PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
                    .id(rs.getLong("payment_id"))
                    .name(rs.getString("payment_name"))
                    .code(rs.getString("payment_code"))
                    .description(rs.getString("payment_description"))
                    .build();

            // Đóng tài nguyên để thực hiện truy vấn tiếp theo
            rs.close();
            pstmt.close();

            // 2. Query thông tin giao hàng
            StringBuilder shippingQuery = new StringBuilder();
            shippingQuery.append("SELECT * FROM bookshopdb.order_shipping WHERE order_id = ?");

            pstmt = conn.prepareStatement(shippingQuery.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            ShippingInfoDTO shippingInfo = null;
            if (rs.next()) {
                String addressLine1 = rs.getString("address_line1");
                String addressLine2 = rs.getString("address_line2");
                String city = rs.getString("city");
                String district = rs.getString("district");
                String ward = rs.getString("ward");

                // Tạo địa chỉ đầy đủ
                String fullAddress = addressLine1;
                if (addressLine2 != null && !addressLine2.isEmpty()) {
                    fullAddress += ", " + addressLine2;
                }
                if (ward != null && !ward.isEmpty()) {
                    fullAddress += ", " + ward;
                }
                if (district != null && !district.isEmpty()) {
                    fullAddress += ", " + district;
                }
                if (city != null && !city.isEmpty()) {
                    fullAddress += ", " + city;
                }

                shippingInfo = ShippingInfoDTO.builder()
                        .id(rs.getLong("id"))
                        .receiverName(rs.getString("receiver_name"))
                        .receiverEmail(rs.getString("receiver_email"))
                        .receiverPhone(rs.getString("receiver_phone"))
                        .addressLine1(addressLine1)
                        .addressLine2(addressLine2)
                        .city(city)
                        .district(district)
                        .ward(ward)
                        .postalCode(rs.getString("postal_code"))
                        .fullAddress(fullAddress)
                        .shippingNotes(rs.getString("shipping_notes"))
                        .trackingNumber(rs.getString("tracking_number"))
                        .shippingCarrier(rs.getString("shipping_carrier"))
                        .build();
            }

            rs.close();
            pstmt.close();

            // 3. Query thông tin thanh toán
            StringBuilder paymentQuery = new StringBuilder();
            paymentQuery.append("SELECT * FROM bookshopdb.payment_transaction WHERE order_id = ? ORDER BY created_at DESC LIMIT 1");

            pstmt = conn.prepareStatement(paymentQuery.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            PaymentTransactionDTO paymentTransaction = null;
            if (rs.next()) {
                String paymentStatus = rs.getString("status");
                paymentTransaction = PaymentTransactionDTO.builder()
                        .id(rs.getLong("id"))
                        .amount(rs.getDouble("amount"))
                        .transactionCode(rs.getString("transaction_code"))
                        .paymentProviderRef(rs.getString("payment_provider_ref"))
                        .status(paymentStatus)
                        .statusText(getPaymentStatusText(paymentStatus))
                        .paymentDate(rs.getTimestamp("payment_date"))
                        .note(rs.getString("note"))
                        .build();
            }

            rs.close();
            pstmt.close();

            // 4. Query danh sách sản phẩm trong đơn hàng
            StringBuilder itemsQuery = new StringBuilder();
            itemsQuery.append("SELECT oi.*, p.author FROM bookshopdb.order_item oi ");
            itemsQuery.append("LEFT JOIN bookshopdb.product p ON oi.product_id = p.id ");
            itemsQuery.append("WHERE oi.order_id = ?");

            pstmt = conn.prepareStatement(itemsQuery.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            List<OrderItemDetailDTO> orderItems = new ArrayList<>();
            while (rs.next()) {
                double price = rs.getDouble("price");
                double subtotal = rs.getDouble("subtotal");

                OrderItemDetailDTO item = OrderItemDetailDTO.builder()
                        .id(rs.getLong("id"))
                        .productId(rs.getLong("product_id"))
                        .productName(rs.getString("product_name"))
                        .productImage(rs.getString("product_image"))
                        .author(rs.getString("author"))
                        .variant("Bìa mềm") // Giả định, có thể lấy từ cột nếu có
                        .basePrice(rs.getDouble("base_price"))
                        .discountPercent(rs.getDouble("discount_percent"))
                        .price(price)
                        .quantity(rs.getInt("quantity"))
                        .subtotal(subtotal)
                        .build();

                orderItems.add(item);
            }

            rs.close();
            pstmt.close();

            // 5. Query lịch sử trạng thái đơn hàng
            StringBuilder historyQuery = new StringBuilder();
            historyQuery.append("SELECT * FROM bookshopdb.order_status_history WHERE order_id = ? ORDER BY created_at ASC");

            pstmt = conn.prepareStatement(historyQuery.toString());
            pstmt.setLong(1, orderId);
            rs = pstmt.executeQuery();

            List<OrderStatusHistoryDTO> statusHistory = new ArrayList<>();
            while (rs.next()) {
                String historyStatus = rs.getString("status");
                java.sql.Timestamp createdAt = rs.getTimestamp("created_at");

                OrderStatusHistoryDTO history = OrderStatusHistoryDTO.builder()
                        .id(rs.getLong("id"))
                        .status(historyStatus)
                        .statusText(getStatusText(historyStatus))
                        .note(rs.getString("note"))
                        .changedBy(rs.getLong("changed_by"))
                        .createdAt(createdAt)
                        .build();

                statusHistory.add(history);
            }

            // Xây dựng timeline từ lịch sử
            List<OrderTimelineDTO> timeline = buildOrderTimeline(statusHistory, orderInfo);

            // Tạo tổng kết đơn hàng
            double subtotal = orderInfo.getSubtotal();
            double discount = orderInfo.getDiscountAmount();
            double shipping = orderInfo.getDeliveryPrice();
            double tax = orderInfo.getTaxAmount();
            double total = orderInfo.getTotalAmount();

            OrderSummaryDTO summary = OrderSummaryDTO.builder()
                    .subtotal(subtotal)
                    .discount(discount)
                    .shipping(shipping)
                    .tax(tax)
                    .total(total)
                    .build();

            // Kiểm tra xem đơn hàng có thể hủy không
            boolean canCancel = canCancelOrder(status);

            // Tạo đối tượng OrderDetailDTO chứa tất cả thông tin
            return OrderDetailDTO.builder()
                    .order(orderInfo)
                    .delivery(deliveryMethod)
                    .payment(paymentMethod)
                    .shipping(shippingInfo)
                    .paymentTransaction(paymentTransaction)
                    .items(orderItems)
                    .statusHistory(statusHistory)
                    .timeline(timeline)
                    .canCancel(canCancel)
                    .summary(summary)
                    .build();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thông tin chi tiết đơn hàng: " + e.getMessage(), e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * Xây dựng timeline cho đơn hàng từ lịch sử trạng thái
     *
     * @param statusHistory Lịch sử trạng thái đơn hàng
     * @param orderInfo     Thông tin đơn hàng
     * @return Danh sách các mốc thời gian
     */
    private List<OrderTimelineDTO> buildOrderTimeline(List<OrderStatusHistoryDTO> statusHistory, OrderInfoDTO orderInfo) {
        List<OrderTimelineDTO> timeline = new ArrayList<>();

        // Các trạng thái cơ bản trong timeline
        String[] baseStatuses = {"pending", "processing", "shipping", "delivered"};
        Map<String, String> statusLabels = new HashMap<>();
        statusLabels.put("pending", "Đã đặt hàng");
        statusLabels.put("waiting_payment", "Chờ thanh toán");
        statusLabels.put("payment_failed", "Thanh toán thất bại");
        statusLabels.put("processing", "Đã xác nhận");
        statusLabels.put("shipping", "Đang giao hàng");
        statusLabels.put("delivered", "Đã giao hàng");
        statusLabels.put("cancelled", "Đã hủy");
        statusLabels.put("refunded", "Đã hoàn tiền");

        // Biểu tượng cho các trạng thái
        Map<String, String> statusIcons = new HashMap<>();
        statusIcons.put("pending", "fas fa-check");
        statusIcons.put("waiting_payment", "fas fa-dollar-sign");
        statusIcons.put("payment_failed", "fas fa-times");
        statusIcons.put("processing", "fas fa-check");
        statusIcons.put("shipping", "fas fa-truck");
        statusIcons.put("delivered", "fas fa-box");
        statusIcons.put("cancelled", "fas fa-ban");
        statusIcons.put("refunded", "fas fa-undo");

        // Lấy trạng thái hiện tại của đơn hàng
        String currentStatus = orderInfo.getStatus();
        java.sql.Timestamp orderDate = orderInfo.getOrderDate();

        // Tạo map lưu trữ thời gian mới nhất cho mỗi trạng thái từ lịch sử
        Map<String, java.sql.Timestamp> statusTimes = new HashMap<>();
        if (statusHistory != null && !statusHistory.isEmpty()) {
            for (OrderStatusHistoryDTO history : statusHistory) {
                statusTimes.put(history.getStatus(), history.getCreatedAt());
            }
        }

        // Xây dựng timeline từ các trạng thái cơ bản
        for (String status : baseStatuses) {
            // Tạo đối tượng timeline item
            OrderTimelineDTO timelineItem = new OrderTimelineDTO();
            timelineItem.setStatus(status);
            timelineItem.setLabel(statusLabels.getOrDefault(status, status));
            timelineItem.setIcon(statusIcons.getOrDefault(status, "fas fa-circle"));

            // Xác định thời gian cho trạng thái này
            if (statusTimes.containsKey(status)) {
                // Đã có thời gian cụ thể từ lịch sử
                java.sql.Timestamp timestamp = statusTimes.get(status);
                timelineItem.setTimestamp(timestamp);
                timelineItem.setCompleted(getStatusOrder(status) < getStatusOrder(currentStatus));
                timelineItem.setActive(status.equals(currentStatus));
            } else if (status.equals("pending")) {
                // Trạng thái pending luôn có thời gian là thời gian đặt hàng
                timelineItem.setTimestamp(orderDate);
                timelineItem.setCompleted(true);
            } else if (getStatusOrder(status) < getStatusOrder(currentStatus)) {
                // Các trạng thái đã qua nhưng không có ghi nhận
                timelineItem.setCompleted(true);
                // Thời gian ước tính
                java.sql.Timestamp estimatedTime = new Timestamp(orderDate.getTime() + getEstimatedTimeOffset(status));
                timelineItem.setTimestamp(estimatedTime);
            } else if (status.equals(currentStatus)) {
                // Trạng thái hiện tại
                java.sql.Timestamp now = new Timestamp(System.currentTimeMillis());
                timelineItem.setTimestamp(now);
                timelineItem.setActive(true);
                timelineItem.setCompleted(false);
            } else if (status.equals("delivered") && currentStatus.equals("shipping")) {
                // Nếu đang giao hàng, ước tính thời gian giao
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, 1); // Dự kiến giao vào ngày mai
                java.sql.Timestamp deliveryDate = new Timestamp(calendar.getTimeInMillis());
                timelineItem.setTimestamp(deliveryDate);
                timelineItem.setEstimated(true);
                timelineItem.setCompleted(false);
            }

            timeline.add(timelineItem);
        }

        return timeline;
    }

    /**
     * Lấy text hiển thị cho trạng thái đơn hàng
     *
     * @param status Trạng thái đơn hàng
     * @return Text hiển thị
     */
    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "Chờ xác nhận";
            case "waiting_payment":
                return "Chờ thanh toán";
            case "payment_failed":
                return "Thanh toán thất bại";
            case "processing":
                return "Đang xử lý";
            case "shipping":
                return "Đang giao hàng";
            case "delivered":
                return "Đã giao hàng";
            case "cancelled":
                return "Đã hủy";
            case "refunded":
                return "Đã hoàn tiền";
            default:
                return status;
        }
    }

    /**
     * Lấy text hiển thị cho trạng thái thanh toán
     *
     * @param status Trạng thái thanh toán
     * @return Text hiển thị
     */
    private String getPaymentStatusText(String status) {
        switch (status) {
            case "pending":
                return "Chờ thanh toán";
            case "waiting_payment":
                return "Chờ thanh toán";
            case "processing":
                return "Đang xử lý";
            case "completed":
                return "Đã thanh toán";
            case "failed":
                return "Thanh toán thất bại";
            case "expired":
                return "Hết hạn thanh toán";
            case "refunded":
                return "Đã hoàn tiền";
            case "partially_refunded":
                return "Hoàn tiền một phần";
            default:
                return status;
        }
    }

    /**
     * Lấy thứ tự của trạng thái để so sánh
     * @param status Trạng thái cần lấy thứ tự
     * @return Số thứ tự của trạng thái
     */
    private int getStatusOrder(String status) {
        switch (status) {
            case "pending": return 1;
            case "waiting_payment": return 2;
            case "processing": return 3;
            case "shipping": return 4;
            case "delivered": return 5;
            case "cancelled": return 6;
            case "refunded": return 7;
            case "payment_failed": return 8;
            default: return 0;
        }
    }

    /**
     * Lấy ước tính thời gian offset cho các trạng thái
     * @param status Trạng thái cần ước tính
     * @return Thời gian offset tính bằng milliseconds
     */
    private long getEstimatedTimeOffset(String status) {
        long hour = 3600 * 1000; // 1 giờ tính bằng milliseconds

        switch (status) {
            case "processing": return 2 * hour; // 2 giờ sau khi đặt hàng
            case "shipping": return 24 * hour; // 24 giờ sau khi đặt hàng
            case "delivered": return 48 * hour; // 48 giờ sau khi đặt hàng
            default: return 0;
        }
    }

    /**
     * Kiểm tra xem đơn hàng có thể hủy không
     * @param status Trạng thái đơn hàng
     * @return true nếu đơn hàng có thể hủy
     */
    private boolean canCancelOrder(String status) {
        // Chỉ có thể hủy đơn hàng ở trạng thái pending, waiting_payment hoặc processing
        return status.equals("pending") || status.equals("waiting_payment") || status.equals("processing");
    }
}
