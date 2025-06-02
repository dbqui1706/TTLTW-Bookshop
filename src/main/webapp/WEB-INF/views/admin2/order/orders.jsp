<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 9:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Quản Lý Đơn Hàng - Quản Trị Hệ Thống</title>

   <jsp:include page="/common/admin2/head-meta.jsp"/>
    <!-- <link rel="stylesheet" href="../css/order/style.css" /> -->
    <style>
        .timeline {
            position: relative;
            padding-left: 30px;
        }

        .timeline::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            height: 100%;
            width: 2px;
            background-color: #e9ecef;
        }

        .timeline-item {
            position: relative;
            padding-bottom: 1.5rem;
        }

        .timeline-item:last-child {
            padding-bottom: 0;
        }

        .timeline-marker {
            position: absolute;
            left: -30px;
            top: 0;
            width: 16px;
            height: 16px;
            border-radius: 50%;
            background-color: #007bff;
            border: 2px solid #fff;
            z-index: 1;
        }

        .timeline-content {
            padding-left: 1rem;
        }

        /* Badge trạng thái đơn hàng */
        .badge.bg-pending {
            background-color: #ffc107;
        }

        .badge.bg-processing {
            background-color: #17a2b8;
        }

        .badge.bg-shipped {
            background-color: #6f42c1;
        }

        .badge.bg-delivered {
            background-color: #28a745;
        }

        .badge.bg-cancelled {
            background-color: #dc3545;
        }

        .badge.bg-returned {
            background-color: #6c757d;
        }

        /* Nút hành động */
        .btn-action {
            width: 32px;
            height: 32px;
            padding: 0;
            line-height: 32px;
            text-align: center;
        }

        /* Card đơn hàng trong modal */
        .order-item-card {
            border-left: 4px solid #007bff;
        }
    </style>
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

        <div class="row mb-4">
            <div class="col-12">
                <div class="bg-primary text-white rounded p-3">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Quản Lý Đơn Hàng</h2>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <button class="btn btn-light me-2" id="btnCreateOrder">
                                <i class="bi bi-plus-lg"></i> Tạo đơn hàng mới
                            </button>
                            <button class="btn btn-light" data-action="export-excel">
                                <i class="bi bi-file-earmark-excel"></i> Xuất Excel
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stats Row -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3 mb-md-0">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-primary bg-opacity-10 p-3 rounded text-primary">
                                    <i class="bi bi-cart-check fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Tổng đơn hàng</h6>
                                <h3 class="mb-0" id="totalOrders">0</h3>
                                <span class="text-success"><i class="bi bi-arrow-up"></i> 12.5%</span>
                                <small class="text-muted">so với tháng trước</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3 mb-md-0">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-danger bg-opacity-10 p-3 rounded text-danger">
                                    <i class="bi bi-hourglass-split fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Đơn chờ xử lý</h6>
                                <h3 class="mb-0" id="pendingOrders">0</h3>
                                <span class="text-danger"><i class="bi bi-arrow-up"></i> 5.2%</span>
                                <small class="text-muted">so với tháng trước</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3 mb-md-0">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-success bg-opacity-10 p-3 rounded text-success">
                                    <i class="bi bi-truck fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Đơn đang giao</h6>
                                <h3 class="mb-0" id="shippingOrders">0</h3>
                                <span class="text-success"><i class="bi bi-arrow-up"></i> 8.7%</span>
                                <small class="text-muted">so với tháng trước</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3 mb-3 mb-md-0">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-info bg-opacity-10 p-3 rounded text-info">
                                    <i class="bi bi-cash-stack fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Doanh thu tháng</h6>
                                <h3 class="mb-0" id="monthlyRevenue">0</h3>
                                <span class="text-success"><i class="bi bi-arrow-up"></i> 15.3%</span>
                                <small class="text-muted">so với tháng trước</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <form class="row g-3" id="orderFilterForm">
                            <div class="col-md-2">
                                <label class="form-label">Tìm kiếm</label>
                                <input type="text" class="form-control" id="searchOrder"
                                       placeholder="Mã đơn, khách hàng...">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" id="orderStatus">
                                    <option value="" selected>Tất cả</option>
                                    <option value="pending">Chờ xử lý</option>
                                    <option value="waiting_payment">Chờ thanh toán</option>
                                    <option value="processing">Đang xử lý</option>
                                    <option value="shipping">Đang giao</option>
                                    <option value="delivered">Đã giao</option>
                                    <option value="cancelled">Đã hủy</option>
                                    <option value="refunded">Hoàn tiền</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Từ ngày</label>
                                <input type="date" class="form-control" id="fromDate">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Đến ngày</label>
                                <input type="date" class="form-control" id="toDate">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label">Phương thức TT</label>
                                <select class="form-select" id="paymentMethod">
                                    <option value="" selected>Tất cả</option>
                                    <option value="1">COD</option>
                                    <option value="2">Chuyển khoản</option>
                                    <option value="3">Thẻ tín dụng</option>
                                    <option value="4">Ví MoMo</option>
                                    <option value="5">Khác</option>
                                </select>
                            </div>
                            <div class="col-md-2 d-flex align-items-end">
                                <button type="button" class="btn btn-primary w-100" id="btnFilter">
                                    <i class="bi bi-search me-1"></i> Tìm kiếm
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Orders Table -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Danh sách đơn hàng</h5>
                        <div class="btn-group">
                            <button type="button" class="btn btn-sm btn-outline-secondary" id="btnRefreshOrders">
                                <i class="bi bi-arrow-clockwise"></i> Làm mới
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-secondary" id="btnBulkActions"
                                    data-bs-toggle="dropdown">
                                <i class="bi bi-three-dots"></i> Hành động
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="#" id="btnBulkPrint">In đơn hàng đã chọn</a></li>
                                <li><a class="dropdown-item" href="#" id="btnBulkExport">Xuất Excel đơn đã chọn</a>
                                </li>
                                <li>
                                    <hr class="dropdown-divider">
                                </li>
                                <li><a class="dropdown-item text-danger" href="#" id="btnBulkCancel">Hủy đơn hàng đã
                                    chọn</a></li>
                            </ul>
                        </div>
                    </div>
                    <div class="card-body p-3">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-1" id="ordersTable">
                                <thead class="table-light">
                                <tr>
                                    <th width="20">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" id="checkAll">
                                        </div>
                                    </th>
                                    <th width="130">Mã đơn hàng</th>
                                    <th>Khách hàng</th>
                                    <th width="150">Sản phẩm</th>
                                    <th class="text-end">Tổng tiền</th>
                                    <th width="130">Thanh toán</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày đặt</th>
                                    <th class="text-end">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td colspan="9" class="text-center">Đang tải dữ liệu...</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xem Chi tiết Đơn hàng -->
        <div class="modal fade" id="viewOrderModal" tabindex="-1" aria-labelledby="viewOrderModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="viewOrderModalLabel">Chi tiết đơn hàng #ORD123456</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <!-- Order Info -->
                        <div class="row g-4 mb-4">
                            <div class="col-md-6">
                                <div class="card border-0 bg-light h-100">
                                    <div class="card-body">
                                        <h6 class="card-title d-flex align-items-center mb-3">
                                            <i class="bi bi-person me-2"></i> Thông tin khách hàng
                                        </h6>
                                        <p class="mb-1"><strong>Khách hàng:</strong> Nguyễn Văn A</p>
                                        <p class="mb-1"><strong>Số điện thoại:</strong> 0912345678</p>
                                        <p class="mb-1"><strong>Email:</strong> nguyenvana@example.com</p>
                                        <p class="mb-0"><strong>Địa chỉ giao hàng:</strong> 123 Đường ABC, Phường
                                            XYZ, Quận 1, TP. Hồ Chí Minh</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card border-0 bg-light h-100">
                                    <div class="card-body">
                                        <h6 class="card-title d-flex align-items-center mb-3">
                                            <i class="bi bi-receipt me-2"></i> Thông tin đơn hàng
                                        </h6>
                                        <p class="mb-1"><strong>Mã đơn hàng:</strong> #ORD123456</p>
                                        <p class="mb-1"><strong>Ngày đặt:</strong> 15/08/2023 15:30</p>
                                        <p class="mb-1"><strong>Trạng thái:</strong> <span
                                                class="badge bg-processing">Đang xử lý</span></p>
                                        <p class="mb-1">
                                            <strong>Thanh toán:</strong>
                                            <span class="badge bg-success">Đã thanh toán</span>
                                            <small>(Thẻ tín dụng)</small>
                                        </p>
                                        <p class="mb-0"><strong>Ghi chú:</strong> Giao hàng trong giờ hành chính</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Order Items -->
                        <h6 class="d-flex align-items-center mb-3">
                            <i class="bi bi-box me-2"></i> Sản phẩm
                        </h6>
                        <div class="card order-item-card mb-3">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-2 col-4 mb-2 mb-md-0">
                                        <img src="https://via.placeholder.com/80" class="img-fluid rounded"
                                             alt="iPhone 14 Pro Max">
                                    </div>
                                    <div class="col-md-6 col-8 mb-2 mb-md-0">
                                        <h6 class="mb-0">iPhone 14 Pro Max 256GB</h6>
                                        <small class="text-muted">Màu: Đen | Bảo hành: 12 tháng</small>
                                    </div>
                                    <div class="col-md-2 col-6 text-md-center">
                                        <div class="text-muted">Số lượng</div>
                                        <div class="fw-medium">1</div>
                                    </div>
                                    <div class="col-md-2 col-6 text-end">
                                        <div class="text-muted">Thành tiền</div>
                                        <div class="fw-medium">29,990,000đ</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="card order-item-card mb-3">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-2 col-4 mb-2 mb-md-0">
                                        <img src="https://via.placeholder.com/80" class="img-fluid rounded"
                                             alt="Ốp lưng iPhone">
                                    </div>
                                    <div class="col-md-6 col-8 mb-2 mb-md-0">
                                        <h6 class="mb-0">Ốp lưng iPhone 14 Pro Max Silicon Case</h6>
                                        <small class="text-muted">Màu: Đen | Bảo hành: 1 tháng</small>
                                    </div>
                                    <div class="col-md-2 col-6 text-md-center">
                                        <div class="text-muted">Số lượng</div>
                                        <div class="fw-medium">1</div>
                                    </div>
                                    <div class="col-md-2 col-6 text-end">
                                        <div class="text-muted">Thành tiền</div>
                                        <div class="fw-medium">550,000đ</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="card order-item-card mb-3">
                            <div class="card-body">
                                <div class="row align-items-center">
                                    <div class="col-md-2 col-4 mb-2 mb-md-0">
                                        <img src="https://via.placeholder.com/80" class="img-fluid rounded"
                                             alt="Cường lực">
                                    </div>
                                    <div class="col-md-6 col-8 mb-2 mb-md-0">
                                        <h6 class="mb-0">Kính cường lực iPhone 14 Pro Max</h6>
                                        <small class="text-muted">Loại: Full màn hình | Bảo hành: 1 tháng</small>
                                    </div>
                                    <div class="col-md-2 col-6 text-md-center">
                                        <div class="text-muted">Số lượng</div>
                                        <div class="fw-medium">2</div>
                                    </div>
                                    <div class="col-md-2 col-6 text-end">
                                        <div class="text-muted">Thành tiền</div>
                                        <div class="fw-medium">980,000đ</div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Order Summary -->
                        <div class="row">
                            <div class="col-md-6 offset-md-6">
                                <div class="card">
                                    <div class="card-body">
                                        <h6 class="card-title mb-3">Thông tin thanh toán</h6>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span>Tạm tính:</span>
                                            <span>31,520,000đ</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span>Giảm giá:</span>
                                            <span>-520,000đ</span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span>Phí vận chuyển:</span>
                                            <span>0đ</span>
                                        </div>
                                        <hr>
                                        <div class="d-flex justify-content-between fw-bold">
                                            <span>Tổng cộng:</span>
                                            <span class="text-primary">31,000,000đ</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Order Timeline -->
                        <h6 class="d-flex align-items-center mt-4 mb-3">
                            <i class="bi bi-clock-history me-2"></i> Lịch sử đơn hàng
                        </h6>
                        <div class="timeline">
                            <div class="timeline-item">
                                <div class="timeline-marker"></div>
                                <div class="timeline-content">
                                    <h6 class="mb-0">Đơn hàng đang được xử lý</h6>
                                    <small class="text-muted">16/08/2023 09:15 - Nhân viên: Trần Văn X</small>
                                    <p class="small mt-1 mb-0">Đơn hàng đang được chuẩn bị để giao cho đơn vị vận
                                        chuyển</p>
                                </div>
                            </div>
                            <div class="timeline-item">
                                <div class="timeline-marker"></div>
                                <div class="timeline-content">
                                    <h6 class="mb-0">Đã xác nhận đơn hàng</h6>
                                    <small class="text-muted">15/08/2023 16:20 - Nhân viên: Trần Văn X</small>
                                    <p class="small mt-1 mb-0">Đơn hàng đã được xác nhận và đang được xử lý</p>
                                </div>
                            </div>
                            <div class="timeline-item">
                                <div class="timeline-marker"></div>
                                <div class="timeline-content">
                                    <h6 class="mb-0">Đơn hàng mới</h6>
                                    <small class="text-muted">15/08/2023 15:30 - Hệ thống</small>
                                    <p class="small mt-1 mb-0">Đơn hàng được tạo thành công</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-success" id="btnNextStatus" data-id="ORD123456">
                            <i class="bi bi-arrow-right"></i> Chuyển trạng thái
                        </button>
                        <button type="button" class="btn btn-primary" id="btnPrintOrder" data-id="ORD123456">
                            <i class="bi bi-printer"></i> In đơn hàng
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Cập nhật trạng thái -->
        <div class="modal fade" id="updateStatusModal" tabindex="-1" aria-labelledby="updateStatusModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateStatusModalLabel">Cập nhật trạng thái đơn hàng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="updateStatusForm">
                            <input type="hidden" id="updateOrderId" value="">
                            <div class="mb-3">
                                <label for="currentStatus" class="form-label">Trạng thái hiện tại</label>
                                <input type="text" class="form-control" id="currentStatus" value="Đang xử lý"
                                       disabled>
                            </div>
                            <div class="mb-3">
                                <label for="newStatus" class="form-label">Trạng thái mới</label>
                                <select class="form-select" id="newStatus">
                                    <option value="shipped">Đang giao hàng</option>
                                    <option value="delivered">Đã giao hàng</option>
                                    <option value="cancelled">Hủy đơn hàng</option>
                                    <option value="returned">Trả hàng</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="statusNote" class="form-label">Ghi chú</label>
                                <textarea class="form-control" id="statusNote" rows="3"
                                          placeholder="Nhập ghi chú về việc thay đổi trạng thái"></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="notifyCustomer" class="form-check-label">
                                    <input type="checkbox" class="form-check-input" id="notifyCustomer" checked>
                                    Thông báo cho khách hàng
                                </label>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="confirmUpdateStatus">
                            <i class="bi bi-check2"></i> Cập nhật
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Tạo đơn hàng mới -->
        <div class="modal fade" id="createOrderModal" tabindex="-1" aria-labelledby="createOrderModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="createOrderModalLabel">Tạo đơn hàng mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="createOrderForm">
                            <div class="row g-4">
                                <!-- Thông tin khách hàng -->
                                <div class="col-md-6">
                                    <div class="card h-100">
                                        <div class="card-header bg-light">
                                            <h6 class="mb-0">Thông tin khách hàng</h6>
                                        </div>
                                        <div class="card-body">
                                            <div class="mb-3">
                                                <label for="customerSearch" class="form-label">Tìm kiếm khách
                                                    hàng</label>
                                                <div class="input-group">
                                                    <input type="text" class="form-control" id="customerSearch"
                                                           placeholder="Nhập tên, số điện thoại, email...">
                                                    <button class="btn btn-outline-secondary" type="button"
                                                            id="searchCustomerBtn">
                                                        <i class="bi bi-search"></i>
                                                    </button>
                                                </div>
                                            </div>
                                            <div class="row mb-3">
                                                <div class="col-md-6 mb-3 mb-md-0">
                                                    <label for="customerName" class="form-label">Họ tên <span
                                                            class="text-danger">*</span></label>
                                                    <input type="text" class="form-control" id="customerName"
                                                           required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="customerPhone" class="form-label">Số điện thoại
                                                        <span class="text-danger">*</span></label>
                                                    <input type="tel" class="form-control" id="customerPhone"
                                                           required>
                                                </div>
                                            </div>
                                            <div class="mb-3">
                                                <label for="customerEmail" class="form-label">Email</label>
                                                <input type="email" class="form-control" id="customerEmail">
                                            </div>
                                            <div class="mb-3">
                                                <label for="customerAddress" class="form-label">Địa chỉ giao hàng
                                                    <span class="text-danger">*</span></label>
                                                <textarea class="form-control" id="customerAddress" rows="3"
                                                          required></textarea>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Thông tin đơn hàng -->
                                <div class="col-md-6">
                                    <div class="card h-100">
                                        <div class="card-header bg-light">
                                            <h6 class="mb-0">Thông tin đơn hàng</h6>
                                        </div>
                                        <div class="card-body">
                                            <div class="row mb-3">
                                                <div class="col-md-6 mb-3 mb-md-0">
                                                    <label for="orderDate" class="form-label">Ngày đặt hàng</label>
                                                    <input type="date" class="form-control" id="orderDate"
                                                           value="2023-08-17">
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="orderPaymentMethod" class="form-label">Phương thức
                                                        thanh toán</label>
                                                    <select class="form-select" id="orderPaymentMethod">
                                                        <option value="cod">COD (Thanh toán khi nhận hàng)</option>
                                                        <option value="bank_transfer">Chuyển khoản ngân hàng
                                                        </option>
                                                        <option value="credit_card">Thẻ tín dụng</option>
                                                        <option value="momo">Ví MoMo</option>
                                                        <option value="other">Khác</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="mb-3">
                                                <label for="orderShipping" class="form-label">Phương thức vận
                                                    chuyển</label>
                                                <select class="form-select" id="orderShipping">
                                                    <option value="standard">Giao hàng tiêu chuẩn (1-3 ngày)
                                                    </option>
                                                    <option value="express">Giao hàng nhanh (24h)</option>
                                                    <option value="same_day">Giao trong ngày</option>
                                                </select>
                                            </div>
                                            <div class="mb-3">
                                                <label for="orderNote" class="form-label">Ghi chú đơn hàng</label>
                                                <textarea class="form-control" id="orderNote" rows="3"
                                                          placeholder="Nhập ghi chú đơn hàng nếu có"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="orderStatus" class="form-label">Trạng thái đơn
                                                    hàng</label>
                                                <select class="form-select" id="orderStatus">
                                                    <option value="pending">Chờ xử lý</option>
                                                    <option value="processing">Đang xử lý</option>
                                                    <option value="shipped">Đang giao hàng</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Danh sách sản phẩm -->
                                <div class="col-12">
                                    <div class="card">
                                        <div
                                                class="card-header bg-light d-flex justify-content-between align-items-center">
                                            <h6 class="mb-0">Danh sách sản phẩm</h6>
                                            <button type="button" class="btn btn-primary btn-sm" id="addProduct">
                                                <i class="bi bi-plus-lg"></i> Thêm sản phẩm
                                            </button>
                                        </div>
                                        <div class="card-body p-0">
                                            <div class="table-responsive">
                                                <table class="table mb-0" id="productTable">
                                                    <thead class="table-light">
                                                    <tr>
                                                        <th width="5%">#</th>
                                                        <th width="40%">Sản phẩm</th>
                                                        <th width="15%" class="text-end">Đơn giá</th>
                                                        <th width="15%" class="text-center">Số lượng</th>
                                                        <th width="15%" class="text-end">Thành tiền</th>
                                                        <th width="10%" class="text-center">Thao tác</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <tr class="text-center">
                                                        <td colspan="6">Chưa có sản phẩm nào</td>
                                                    </tr>
                                                    </tbody>
                                                    <tfoot class="table-light">
                                                    <tr>
                                                        <td colspan="4" class="text-end fw-bold">Tạm tính:</td>
                                                        <td class="text-end fw-bold" id="subtotal">0đ</td>
                                                        <td></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="4" class="text-end">Giảm giá:</td>
                                                        <td class="text-end" id="discount">0đ</td>
                                                        <td></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="4" class="text-end">Phí vận chuyển:</td>
                                                        <td class="text-end" id="shipping">0đ</td>
                                                        <td></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="4" class="text-end fw-bold">Tổng cộng:</td>
                                                        <td class="text-end fw-bold text-primary" id="total">0đ
                                                        </td>
                                                        <td></td>
                                                    </tr>
                                                    </tfoot>
                                                </table>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="button" class="btn btn-primary" id="saveOrder">
                            <i class="bi bi-save"></i> Lưu đơn hàng
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Thêm sản phẩm vào đơn hàng -->
        <div class="modal fade" id="addProductModal" tabindex="-1" aria-labelledby="addProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addProductModalLabel">Thêm sản phẩm</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <div class="input-group">
                                <input type="text" class="form-control" id="productSearch"
                                       placeholder="Tìm kiếm sản phẩm theo tên, mã...">
                                <button class="btn btn-outline-secondary" type="button" id="searchProductBtn">
                                    <i class="bi bi-search"></i>
                                </button>
                            </div>
                        </div>
                        <div class="table-responsive">
                            <table class="table" id="productSearchTable">
                                <thead>
                                <tr>
                                    <th width="10%">Ảnh</th>
                                    <th width="35%">Tên sản phẩm</th>
                                    <th width="15%">SKU</th>
                                    <th width="15%" class="text-end">Giá</th>
                                    <th width="15%" class="text-center">Kho</th>
                                    <th width="10%" class="text-center">Thêm</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td><img src="https://via.placeholder.com/40" class="img-thumbnail"
                                             alt="iPhone"></td>
                                    <td>iPhone 14 Pro Max 256GB</td>
                                    <td>IP14PM-256-BLK</td>
                                    <td class="text-end">29,990,000đ</td>
                                    <td class="text-center">45</td>
                                    <td class="text-center">
                                        <button type="button" class="btn btn-sm btn-primary select-product"
                                                data-id="1" data-name="iPhone 14 Pro Max 256GB"
                                                data-price="29990000">
                                            <i class="bi bi-plus"></i>
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td><img src="https://via.placeholder.com/40" class="img-thumbnail"
                                             alt="Ốp lưng"></td>
                                    <td>Ốp lưng iPhone 14 Pro Max Silicon Case</td>
                                    <td>CASE-IP14PM-SIL-BLK</td>
                                    <td class="text-end">550,000đ</td>
                                    <td class="text-center">120</td>
                                    <td class="text-center">
                                        <button type="button" class="btn btn-sm btn-primary select-product"
                                                data-id="2" data-name="Ốp lưng iPhone 14 Pro Max Silicon Case"
                                                data-price="550000">
                                            <i class="bi bi-plus"></i>
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td><img src="https://via.placeholder.com/40" class="img-thumbnail"
                                             alt="Cường lực"></td>
                                    <td>Kính cường lực iPhone 14 Pro Max</td>
                                    <td>GLASS-IP14PM-FULL</td>
                                    <td class="text-end">490,000đ</td>
                                    <td class="text-center">85</td>
                                    <td class="text-center">
                                        <button type="button" class="btn btn-sm btn-primary select-product"
                                                data-id="3" data-name="Kính cường lực iPhone 14 Pro Max"
                                                data-price="490000">
                                            <i class="bi bi-plus"></i>
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="/common/admin2/footer.jsp"></jsp:include>
    </div>
</div>

<jsp:include page="/common/admin2/scripts-footer.jsp"></jsp:include>

<!-- Custom JS -->
<script src="/app/main/index.js" type=""></script>
<script src="/app/main/order.js" type="module"></script>
</body>

</html>