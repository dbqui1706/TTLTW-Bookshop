<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 9:35 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Quản Lý Tồn Kho - Hệ Thống Quản Lý</title>
    <jsp:include page="/common/admin2/head-meta.jsp"/>
    <link rel="stylesheet" href="/app/css/inventory/inventory.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp" >
        <jsp:param name="sidebar" value="INVENTORY-DASHBOARD"/>
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
                            <h2>Tổng quan tồn kho</h2>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <div class="btn-group" role="group">
                                <button type="button" class="btn btn-light" id="filterDay">Ngày</button>
                                <button type="button" class="btn btn-outline-light" id="filterWeek">Tuần</button>
                                <button type="button" class="btn btn-outline-light" id="filterMonth">Tháng</button>
                                <button type="button" class="btn btn-outline-light" id="filterQuarter">Quý</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Inventory Stats Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-primary-subtle text-primary">
                        <i class="bi bi-box-seam"></i>
                    </div>
                    <h3 class="card-value" id="totalProducts">1,245</h3>
                    <p class="card-title">Tổng sản phẩm</p>
                    <div class="trend up">
                        <i class="bi bi-arrow-up-short"></i> 5.2% so với hôm qua
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-success-subtle text-success">
                        <i class="bi bi-graph-up-arrow"></i>
                    </div>
                    <h3 class="card-value" id="stockValue">2.3 tỷ</h3>
                    <p class="card-title">Giá trị tồn kho</p>
                    <div class="trend up">
                        <i class="bi bi-arrow-up-short"></i> 3.7% so với hôm qua
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-warning-subtle text-warning">
                        <i class="bi bi-exclamation-triangle"></i>
                    </div>
                    <h3 class="card-value" id="lowStockCount">28</h3>
                    <p class="card-title">Sắp hết hàng</p>
                    <div class="trend down">
                        <i class="bi bi-arrow-down-short"></i> 2.1% so với hôm qua
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-danger-subtle text-danger">
                        <i class="bi bi-x-circle"></i>
                    </div>
                    <h3 class="card-value" id="outOfStock">15</h3>
                    <p class="card-title">Hết hàng</p>
                    <div class="trend down">
                        <i class="bi bi-arrow-down-short"></i> 4.5% so với hôm qua
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row mb-4">
            <div class="col-md-7">
                <div class="chart-card mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="mb-0">Xu hướng tồn kho</h5>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-primary active" data-period="day">Ngày</button>
                            <button class="btn btn-outline-primary" data-period="week">Tuần</button>
                            <button class="btn btn-outline-primary" data-period="month">Tháng</button>
                            <button class="btn btn-outline-primary" data-period="quarter">Quý</button>
                        </div>
                    </div>
                    <div>
                        <canvas id="inventoryTrendChart" height="300"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-md-5">
                <div class="chart-card ">
                    <h5 class="mb-3">Phân bố tồn kho</h5>
                    <div>
                        <canvas id="inventoryDistributionChart" height="300"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Inventory Status Table -->
        <div class="row mb-4">
            <div class="col-12">
                <!-- Container cho bảng tồn kho -->
                <div class="table-container p-3">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="mb-0">Trạng thái tồn kho</h5>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-outline-primary btn-sm" id="exportInventoryBtn">
                                <i class="bi bi-download"></i> Xuất dữ liệu
                            </button>
                        </div>
                    </div>

                    <!-- Bộ lọc nâng cao -->
                    <div class="card mb-3">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <span>Bộ lọc tồn kho</span>
                            <button class="btn btn-sm btn-link" type="button" data-bs-toggle="collapse"
                                    data-bs-target="#filterCollapse">
                                <i class="bi bi-funnel"></i> Hiển thị/Ẩn bộ lọc
                            </button>
                        </div>
                        <div class="collapse" id="filterCollapse">
                            <div class="card-body">
                                <form id="inventoryFilterForm">
                                    <div class="row g-3">
                                        <div class="col-md-6 col-lg-3">
                                            <label class="form-label">Trạng thái</label>
                                            <select class="form-select" id="stockFilter">
                                                <option value="">Tất cả</option>
                                                <option value="normal">Bình thường</option>
                                                <option value="low">Sắp hết hàng</option>
                                                <option value="out">Hết hàng</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6 col-lg-3">
                                            <label class="form-label">Danh mục</label>
                                            <select class="form-select" id="categoryFilter">
                                                <option value="">Tất cả danh mục</option>
                                                <!-- Danh mục sẽ được tải động -->
                                            </select>
                                        </div>
                                        <div class="col-md-6 col-lg-3">
                                            <label class="form-label">Thời gian tồn kho</label>
                                            <select class="form-select" id="durationFilter">
                                                <option value="">Tất cả</option>
                                                <option value="7">Trên 7 ngày</option>
                                                <option value="30">Trên 30 ngày</option>
                                                <option value="90">Trên 90 ngày</option>
                                                <option value="180">Trên 180 ngày</option>
                                                <option value="365">Trên 1 năm</option>
                                                <option value="custom">Tùy chỉnh...</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6 col-lg-3" id="customDurationDiv" style="display:none;">
                                            <label class="form-label">Số ngày tùy chỉnh</label>
                                            <input type="number" class="form-control" id="customDuration" min="1"
                                                   placeholder="Nhập số ngày">
                                        </div>

                                        <div class="col-md-6 col-lg-3">
                                            <label class="form-label">Khoảng thời gian</label>
                                            <select class="form-select" id="timeRangeFilter">
                                                <option value="all">Tất cả thời gian</option>
                                                <option value="day">Hôm nay</option>
                                                <option value="week">Tuần này</option>
                                                <option value="month">Tháng này</option>
                                                <option value="quarter">Quý này</option>
                                                <option value="custom">Tùy chỉnh...</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6 col-lg-3" id="dateRangeDiv" style="display:none;">
                                            <label class="form-label">Từ ngày</label>
                                            <input type="date" class="form-control" id="startDate">
                                        </div>
                                        <div class="col-md-6 col-lg-3" id="dateRangeEndDiv" style="display:none;">
                                            <label class="form-label">Đến ngày</label>
                                            <input type="date" class="form-control" id="endDate">
                                        </div>
                                        <div class="col-md-6 col-lg-3">
                                            <label class="form-label">Tìm kiếm</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="bi bi-search"></i></span>
                                                <input type="text" class="form-control" placeholder="Tìm kiếm..."
                                                       id="searchProduct">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="d-flex justify-content-end mt-3">
                                        <button type="button" class="btn btn-outline-secondary me-2"
                                                id="resetFilterBtn">
                                            <i class="bi bi-x-circle"></i> Đặt lại
                                        </button>
                                        <button type="button" class="btn btn-primary" id="applyFilterBtn">
                                            <i class="bi bi-funnel"></i> Áp dụng bộ lọc
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Bảng DataTable -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="inventoryTable" width="100%">
                            <thead>
                            <tr>
                                <th>Mã sản phẩm</th>
                                <th>Sản phẩm</th>
                                <th>Tồn kho</th>
                                <th>Đã đặt</th>
                                <th>Có thể bán</th>
                                <th>Ngưỡng</th>
                                <th>Ngày tồn kho</th>
                                <th>Bán lần cuối</th>
                                <th>Ngày cập nhập</th>
                                <th>Giá</th>
                                <th>Giá tồn</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <!-- Dữ liệu sẽ được tải bằng DataTable -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Activity -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="table-container p-3">
                    <h5 class="mb-3">Nhập kho gần đây</h5>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                            <tr>
                                <th>Mã phiếu</th>
                                <th>Sản phẩm</th>
                                <th>Số lượng</th>
                                <th>Ngày nhập</th>
                                <th>Người nhập</th>
                            </tr>
                            </thead>
                            <tbody id="recentImportTbody">
                            <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                            </tbody>
                        </table>
                    </div>
                    <div class="text-end mt-3">
                        <a href="/admin/inventory-manager/management" class="btn btn-sm btn-outline-primary">Xem tất cả <i
                                class="bi bi-arrow-right"></i></a>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="table-container p-3">
                    <h5 class="mb-3">Xuất kho gần đây</h5>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                            <tr>
                                <th>Mã phiếu</th>
                                <th>Sản phẩm</th>
                                <th>Số lượng</th>
                                <th>Ngày xuất</th>
                                <th>Lý do</th>
                            </tr>
                            </thead>
                            <tbody id="recentExportTbody">
                            <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                            </tbody>
                        </table>
                    </div>
                    <div class="text-end mt-3">
                        <a href="/admin/inventory-manager/management" class="btn btn-sm btn-outline-primary">Xem tất cả <i
                                class="bi bi-arrow-right"></i></a>
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
<script src="/app/main/index.js"></script>
<script src="/app/main/inventory-dashboard.js" type="module"></script>
</body>
</html>