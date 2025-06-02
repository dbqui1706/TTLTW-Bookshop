<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 9:37 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Quản Lý Nhập/Xuất Kho - Hệ Thống Quản Lý</title>
    <jsp:include page="/common/admin2/head-meta.jsp"/>
    <link rel="stylesheet" href="/app/css/inventory/inventory.css"/>
    <style>
        .tab-container {
            background-color: #f8f9fa;
            border-radius: 0.5rem;
            padding: 15px;
            margin-bottom: 20px;
        }

        .inventory-tab {
            border-radius: 0.25rem;
            cursor: pointer;
            padding: 10px 20px;
            font-weight: 500;
            border: 1px solid transparent;
            transition: all 0.2s ease;
        }

        .inventory-tab.active {
            background-color: #0d6efd;
            color: white;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }

        .inventory-tab:hover:not(.active) {
            background-color: #e9ecef;
            border-color: #dee2e6;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
            animation: fadeIn 0.3s ease;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
            }

            to {
                opacity: 1;
            }
        }

        .action-buttons {
            white-space: nowrap;
        }

        .table-container {
            background-color: #fff;
            border-radius: 0.5rem;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }

        .status-badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: normal;
        }

        .bg-imported {
            background-color: #2979ff;
            color: white;
        }

        .bg-exported {
            background-color: #ff5722;
            color: white;
        }

        .bg-damage {
            background-color: #ff9800;
            color: white;
        }

        .bg-returned {
            background-color: #9c27b0;
            color: white;
        }
    </style>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="sidebar" value="INVENTORY-MANAGEMENT"/>
    </jsp:include>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <jsp:include page="/common/admin2/navbar.jsp"></jsp:include>

        <!-- Dashboard Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="bg-primary text-white rounded p-3">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Quản lý nhập/xuất kho</h2>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <div class="btn-group">
                                <button class="btn btn-light" id="receiptBtn">
                                    <i class="bi bi-plus-circle"></i> Tạo phiếu xuất/nhập kho
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabs Navigation -->
        <div class="row">
            <div class="col-12">
                <div class="tab-container d-flex">
                    <div class="inventory-tab active me-2" id="importTab">
                        <i class="bi bi-box-arrow-in-down"></i> Nhập kho
                    </div>
                    <div class="inventory-tab" id="exportTab">
                        <i class="bi bi-box-arrow-up"></i> Xuất kho
                    </div>
                </div>
            </div>
        </div>

        <!-- IMPORT TAB CONTENT -->
        <div id="importTabContent" class="tab-content">
            <!-- Filter Section - Import -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title mb-3">Lọc phiếu nhập kho</h5>
                            <div class="row g-3">
                                <div class="col-md-3">
                                    <div class="form-floating">
                                        <input type="text" class="form-control" id="importCode"
                                               placeholder="Mã phiếu nhập">
                                        <label for="importCode">Mã phiếu nhập</label>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-floating">
                                        <select class="form-select" id="statusFilter">
                                            <option value="">Tất cả trạng thái</option>
                                            <option value="pending">Chờ duyệt</option>
                                            <option value="completed">Hoàn thành</option>
                                            <option value="cancelled">Hủy</option>
                                        </select>
                                        <label for="statusFilter">Trạng thái</label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="row">
                                        <div class="col-6">
                                            <div class="form-floating">
                                                <input type="date" class="form-control" id="importStartDate">
                                                <label for="importStartDate">Từ ngày</label>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="form-floating">
                                                <input type="date" class="form-control" id="importEndDate">
                                                <label for="importEndDate">Đến ngày</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <div class="d-flex justify-content-center gap-3">
                                        <button class="btn btn-primary h-100 w-100" id="searchImportBtn">
                                            <i class="bi bi-search"></i> Tìm kiếm
                                        </button>
                                        <button class="btn btn-primary h-100 w-100" id="searchImportBtn">
                                            <i class="bi bi-search"></i> Làm mới
                                        </button>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Import Inventory Table -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="table-container p-3">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="importTable">
                                <thead>
                                <tr>
                                    <th>Mã phiếu</th>
                                    <th>Ngày nhập</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Số lượng SP</th>
                                    <th>Tổng SL</th>
                                    <th>Giá trị nhập</th>
                                    <th>Người nhập</th>
                                    <th>Ghi chú</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- EXPORT TAB CONTENT -->
        <div id="exportTabContent" class="tab-content">
            <!-- Filter Section - Export -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title mb-3">Lọc phiếu xuất kho</h5>
                            <div class="row g-3">
                                <div class="col-md-3">
                                    <div class="form-floating">
                                        <input type="text" class="form-control" id="exportCode"
                                               placeholder="Mã phiếu xuất">
                                        <label for="exportCode">Mã phiếu xuất</label>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-floating">
                                        <select class="form-select" id="exportReasonFilter">
                                            <option value="">Tất cả lý do xuất</option>
                                            <option value="order">Bán hàng</option>
                                            <option value="return">Trả nhà cung cấp</option>
                                            <option value="damage">Hư hỏng</option>
                                            <option value="expired">Hết hạn</option>
                                            <option value="other">Khác</option>
                                        </select>
                                        <label for="exportReasonFilter">Lý do xuất</label>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="row">
                                        <div class="col-6">
                                            <div class="form-floating">
                                                <input type="date" class="form-control" id="exportStartDate">
                                                <label for="exportStartDate">Từ ngày</label>
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <div class="form-floating">
                                                <input type="date" class="form-control" id="exportEndDate">
                                                <label for="exportEndDate">Đến ngày</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <button class="btn btn-primary h-100 w-100" id="searchExportBtn">
                                        <i class="bi bi-search"></i> Tìm kiếm
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Export Inventory Table -->
            <div class="row mb-4">
                <div class="col-12">
                    <div class="table-container p-3">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle" id="exportTable">
                                <thead>
                                <tr>
                                    <th>Mã phiếu</th>
                                    <th>Ngày nhập</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Số lượng SP</th>
                                    <th>Tổng SL</th>
                                    <th>Giá trị nhập</th>
                                    <th>Người nhập</th>
                                    <th>Ghi chú</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <!-- Import Detail Modal -->
        <div class="modal fade" id="importDetailModal" tabindex="-1" aria-labelledby="importDetailModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="importDetailModalLabel">Chi tiết phiếu nhập kho</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Mã phiếu nhập:</p>
                                <p class="fw-bold" id="detailImportId">NK00265</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Ngày nhập:</p>
                                <p class="fw-bold" id="detailImportDate">12/03/2025</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Nhà cung cấp:</p>
                                <p class="fw-bold" id="detailSupplier">NXB Trẻ</p>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Người tạo:</p>
                                <p class="fw-bold" id="detailCreatedBy">Nguyễn Văn A</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Ngày tạo:</p>
                                <p class="fw-bold" id="detailCreatedAt">12/03/2025 08:45</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Tổng giá trị:</p>
                                <p class="fw-bold text-primary" id="detailTotalValue">4,750,000 ₫</p>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col-12">
                                <p class="mb-1 text-muted">Ghi chú:</p>
                                <p id="detailNotes">Nhập bổ sung sách cho quý 2/2025.</p>
                            </div>
                        </div>

                        <h5 class="border-bottom pb-2 mb-3">Danh sách sản phẩm</h5>
                        <div class="table-responsive">
                            <table class="table" id="detailProductTable">
                                <thead class="table-light">
                                <tr>
                                    <th>STT</th>
                                    <th>Sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Giá nhập</th>
                                    <th>Thành tiền</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td colspan="2" class="text-end fw-bold">Tổng cộng:</td>
                                    <td class="fw-bold" id="detailTotalQuantity">50</td>
                                    <td></td>
                                    <td class="fw-bold text-primary" id="detailTotalAmount">4,750,000 ₫</td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="printImport">
                            <i class="bi bi-printer"></i> In phiếu
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Export Detail Modal -->
        <div class="modal fade" id="exportDetailModal" tabindex="-1" aria-labelledby="exportDetailModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exportDetailModalLabel">Chi tiết phiếu xuất kho</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Mã phiếu xuất:</p>
                                <p class="fw-bold" id="detailExportId">XK00132</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Ngày xuất:</p>
                                <p class="fw-bold" id="detailExportDate">06/04/2025</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Lý do xuất:</p>
                                <p class="fw-bold" id="detailExportReason">Bán hàng</p>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Người tạo:</p>
                                <p class="fw-bold" id="detailCreatedBy">Nguyễn Văn A</p>
                            </div>
                            <div class="col-md-4">
                                <p class="mb-1 text-muted">Ngày tạo:</p>
                                <p class="fw-bold" id="detailCreatedAt">06/04/2025 14:30</p>
                            </div>
                            <div class="col-md-4" id="detailReferenceContainer">
                                <p class="mb-1 text-muted">Đơn hàng tham chiếu:</p>
                                <p class="fw-bold" id="detailReference">DH00123</p>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col-12">
                                <p class="mb-1 text-muted">Ghi chú:</p>
                                <p id="detailNotes">Xuất hàng cho đơn hàng của khách hàng Nguyễn Văn A.</p>
                            </div>
                        </div>

                        <h5 class="border-bottom pb-2 mb-3">Danh sách sản phẩm</h5>
                        <div class="table-responsive">
                            <table class="table" id="detailProductTable">
                                <thead class="table-light">
                                <tr>
                                    <th>STT</th>
                                    <th>Sản phẩm</th>
                                    <th>Số lượng</th>
                                    <th>Giá trị</th>
                                    <th>Thành tiền</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td colspan="2" class="text-end fw-bold">Tổng cộng:</td>
                                    <td class="fw-bold" id="detailTotalQuantity">3</td>
                                    <td></td>
                                    <td class="fw-bold text-primary" id="detailTotalAmount">350,000 ₫</td>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" id="printExport">
                            <i class="bi bi-printer"></i> In phiếu
                        </button>
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
<script src="/app/main/index.js" type="module"></script>
<script src="/app/main/inventory-management.js" type="module"></script>
<!-- <script src="../temp/management.js"></script> -->

</body>

</html>
