<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 9:38 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản Lý Đơn Hàng - Quản Trị Hệ Thống</title>
    <jsp:include page="/common/admin2/head-meta.jsp"></jsp:include>
    <link rel="stylesheet" href="/app/css/orders/order-detail.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="sidebar" value="ORDER-ORDERS"/>
    </jsp:include>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <jsp:include page="/common/admin2/navbar.jsp"/>

        <!-- Dashboard Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="bg-primary text-white rounded p-3">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Chi tiết Đơn Hàng</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container-fluid py-3">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h4 id="orderDetailTitle">Chi tiết đơn hàng <strong>ORD-###</strong></h4>
                <button type="button" class="btn btn-secondary" id="btnBackToOrders">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </button>
            </div>

            <!-- Order Info -->
            <div class="row g-4 mb-4">
                <div class="col-md-6">
                    <div class="card border-0 rounded shadow-sm h-100">
                        <div class="card-body" id="customerInfoCard">
                            <h6 class="card-title d-flex align-items-center mb-3">
                                <i class="bi bi-person me-2"></i> Thông tin khách hàng
                            </h6>
                            <p class="mb-1"><strong>Khách hàng:</strong> <span id="customerName"></span></p>
                            <p class="mb-1"><strong>Số điện thoại:</strong> <span id="customerPhone"></span></p>
                            <p class="mb-1"><strong>Email:</strong> <span id="customerEmail"></span></p>
                            <p class="mb-1"><strong>Địa chỉ giao hàng:</strong> <span id="customerAddress"></span></p>
                            <p class="mb-1"><strong>Phương thức giao hàng:</strong> <span id="shippingMethod"></span>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card border-0 rounded shadow-sm h-100">
                        <div class="card-body" id="orderInfoCard">
                            <h6 class="card-title d-flex align-items-center mb-3">
                                <i class="bi bi-receipt me-2"></i> Thông tin đơn hàng
                            </h6>
                            <p class="mb-1"><strong>Mã đơn hàng:</strong> <span id="orderCode"></span></p>
                            <p class="mb-1"><strong>Ngày đặt:</strong> <span id="orderDate"></span></p>
                            <p class="mb-1"><strong>Trạng thái:</strong> <span id="orderStatus" class=""></span></p>
                            <p class="mb-1">
                                <strong>Thanh toán:</strong>
                                <span id="paymentStatus"></span>
                                <small id="paymentMethod"></small>
                            </p>
                            <p class="mb-0"><strong>Ghi chú:</strong> <span id="orderNote"></span></p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Order Items -->
            <h6 class="d-flex align-items-center mb-3">
                <i class="bi bi-box me-2"></i> Sản phẩm
            </h6>
            <div id="orderItemsContainer">
                <!-- Order items will be inserted here by JavaScript -->
            </div>

            <!-- Order Summary -->
            <div class="row mt-3 ">
                <div class="col-md-6 offset-md-6">
                    <div class="card border rounded shadow-sm">
                        <div class="card-body">
                            <h6 class="card-title mb-3">Thông tin thanh toán</h6>
                            <div class="order-summary ">
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Tạm tính:</span>
                                    <span id="orderSubtotal">0đ</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Giảm giá:</span>
                                    <span id="orderDiscount">0đ</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Phí vận chuyển:</span>
                                    <span id="orderShipping">0đ</span>
                                </div>
                                <hr>
                                <div class="d-flex justify-content-between fw-bold">
                                    <span>Tổng cộng:</span>
                                    <span class="text-primary" id="orderTotal">0đ</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Order Timeline -->
            <h6 class="d-flex align-items-center mt-4 mb-3">
                <i class="bi bi-clock-history me-2"></i> Lịch sử đơn hàng
            </h6>
            <div class="timeline" id="orderTimeline">
                <!-- Timeline items will be inserted here by JavaScript -->
            </div>

            <!-- Action Buttons -->
            <div class="mt-4 text-end">
                <button type="button" class="btn btn-secondary me-2" id="btnBackToOrdersBottom">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </button>
                <button type="button" class="btn btn-success me-2" id="btnUpdateStatus">
                    <i class="bi bi-arrow-right"></i> Cập nhật trạng thái
                </button>
                <button type="button" class="btn btn-primary" id="btnPrintOrder">
                    <i class="bi bi-printer"></i> In đơn hàng
                </button>
            </div>
        </div>
        <!-- Footer -->
        <jsp:include page="/common/admin2/footer.jsp"></jsp:include>
    </div>
</div>
<jsp:include page="/common/admin2/scripts-footer.jsp"></jsp:include>
<!-- Custom JS -->
<script src="/app/main/order-detail.js" type="module"></script>
</body>

</html>