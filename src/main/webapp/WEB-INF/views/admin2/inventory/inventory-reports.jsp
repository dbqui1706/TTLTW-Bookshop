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
    <title>Báo Cáo Thống Kê - Hệ Thống Quản Lý</title>
    <jsp:include page="/common/admin2/head-meta.jsp"/>
    <link rel="stylesheet" href="/app/css/inventory/inventory.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="sidebar" value="INVENTORY-REPORT"/>
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
                            <h2>Báo cáo thống kê tồn kho</h2>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <div class="btn-group" role="group">
                                <button type="button" class="btn btn-light" id="reportDay">Ngày</button>
                                <button type="button" class="btn btn-outline-light" id="reportWeek">Tuần</button>
                                <button type="button" class="btn btn-outline-light" id="reportMonth">Tháng</button>
                                <button type="button" class="btn btn-outline-light" id="reportQuarter">Quý</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Reports Stats Cards -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-primary-subtle text-primary">
                        <i class="bi bi-currency-dollar"></i>
                    </div>
                    <h3 class="card-value" id="totalInventoryValue">2.34 tỷ</h3>
                    <p class="card-title">Tổng giá trị tồn kho</p>
                    <div class="trend up">
                        <i class="bi bi-arrow-up-short"></i> 3.2% so với kỳ trước
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-success-subtle text-success">
                        <i class="bi bi-arrow-repeat"></i>
                    </div>
                    <h3 class="card-value" id="inventoryTurnover">4.5</h3>
                    <p class="card-title">Vòng quay tồn kho</p>
                    <div class="trend up">
                        <i class="bi bi-arrow-up-short"></i> 0.8 lần so với kỳ trước
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-warning-subtle text-warning">
                        <i class="bi bi-calendar3"></i>
                    </div>
                    <h3 class="card-value" id="inventoryDays">21</h3>
                    <p class="card-title">Số ngày tồn kho</p>
                    <div class="trend down">
                        <i class="bi bi-arrow-down-short"></i> 3 ngày so với kỳ trước
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card p-3">
                    <div class="card-icon bg-danger-subtle text-danger">
                        <i class="bi bi-exclamation-triangle"></i>
                    </div>
                    <h3 class="card-value" id="stockOutRate">2.5%</h3>
                    <p class="card-title">Tỷ lệ hết hàng</p>
                    <div class="trend down">
                        <i class="bi bi-arrow-down-short"></i> 1.2% so với kỳ trước
                    </div>
                </div>
            </div>
        </div>

        <!-- Filter Section -->
        <!-- Bộ lọc cho bảng lịch sử kho -->
        <div class="card mb-4">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="card-title mb-0">Bộ lọc lịch sử kho</h5>
                    <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="collapse"
                            data-bs-target="#advancedFiltersCollapse" aria-expanded="false"
                            aria-controls="advancedFiltersCollapse">
                        <i class="bi bi-chevron-down"></i>
                    </button>
                </div>

                <div class="collapse show" id="advancedFiltersCollapse">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <div class="form-floating">
                                <select class="form-select" id="productFilterAdvanced">
                                    <option value="">Tất cả sản phẩm</option>
                                    <option value="1">Đắc Nhân Tâm</option>
                                    <option value="2">Nhà Giả Kim</option>
                                    <option value="3">Tư Duy Phản Biện</option>
                                    <option value="4">Điều Kỳ Diệu Của Thói Quen</option>
                                    <option value="5">Khéo Ăn Nói Sẽ Có Được Thiên Hạ</option>
                                </select>
                                <label for="productFilterAdvanced">Sản phẩm</label>
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-floating">
                                <select class="form-select" id="actionTypeFilterAdvanced">
                                    <option value="">Tất cả hành động</option>
                                    <option value="import">Nhập kho</option>
                                    <option value="export">Xuất kho</option>
                                    <option value="adjustment">Điều chỉnh</option>
                                </select>
                                <label for="actionTypeFilterAdvanced">Loại hành động</label>
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-floating">
                                <select class="form-select" id="quantityChangeFilter">
                                    <option value="">Tất cả thay đổi</option>
                                    <option value="increase">Tăng số lượng</option>
                                    <option value="decrease">Giảm số lượng</option>
                                </select>
                                <label for="quantityChangeFilter">Thay đổi số lượng</label>
                            </div>
                        </div>

                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="referenceFilter"
                                       placeholder="Mã tham chiếu">
                                <label for="referenceFilter">Mã tham chiếu</label>
                            </div>
                        </div>
                    </div>

                    <div class="row g-3 mt-2">

                        <div class="col-md-3">
                            <div class="form-floating">
                                <input type="text" class="form-control" id="reasonFilter" placeholder="Lý do">
                                <label for="reasonFilter">Lý do</label>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="row">
                                <div class="col-6">
                                    <div class="form-floating">
                                        <input type="date" class="form-control" id="startDateAdvanced">
                                        <label for="startDateAdvanced">Từ ngày</label>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="form-floating">
                                        <input type="date" class="form-control" id="endDateAdvanced">
                                        <label for="endDateAdvanced">Đến ngày</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-3 d-flex justify-content-end align-items-end">
                            <button class="btn btn-primary me-2" id="applyAdvancedFilter">
                                <i class="bi bi-filter"></i> Áp dụng
                            </button>
                            <button class="btn btn-outline-secondary" id="resetAdvancedFilter">
                                <i class="bi bi-x-circle"></i> Đặt lại
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thanh tìm kiếm nhanh và điều khiển -->
        <div class="d-flex justify-content-end align-items-center mb-3">
            <div>
                <div class="btn-group">
                    <button class="btn btn-outline-secondary" id="refreshTable">
                        <i class="bi bi-arrow-clockwise"></i> Làm mới
                    </button>
                    <button class="btn btn-outline-primary" id="exportTableData">
                        <i class="bi bi-download"></i> Xuất Excel
                    </button>
                    <button class="btn btn-outline-success" id="printTable">
                        <i class="bi bi-printer"></i> In
                    </button>
                    <button class="btn btn-outline-info" id="columnToggle" data-bs-toggle="dropdown">
                        <i class="bi bi-columns-gap"></i> Cột
                    </button>
                    <div class="dropdown-menu p-3" id="columnToggleMenu">
                        <h6 class="dropdown-header">Hiển thị cột</h6>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleId"
                                   data-column="0" checked>
                            <label class="form-check-label" for="toggleId">ID</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleTime"
                                   data-column="1" checked>
                            <label class="form-check-label" for="toggleTime">Thời gian</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleProduct"
                                   data-column="2" checked>
                            <label class="form-check-label" for="toggleProduct">Sản phẩm</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleActionType"
                                   data-column="3" checked>
                            <label class="form-check-label" for="toggleActionType">Loại hành động</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="togglePrevQty"
                                   data-column="4" checked>
                            <label class="form-check-label" for="togglePrevQty">SL trước</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleChange"
                                   data-column="5" checked>
                            <label class="form-check-label" for="toggleChange">Thay đổi</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleAfterQty"
                                   data-column="6" checked>
                            <label class="form-check-label" for="toggleAfterQty">SL sau</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleRef"
                                   data-column="7" checked>
                            <label class="form-check-label" for="toggleRef">Tham chiếu</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleReason"
                                   data-column="8" checked>
                            <label class="form-check-label" for="toggleReason">Lý do</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleUser"
                                   data-column="9" checked>
                            <label class="form-check-label" for="toggleUser">Người thực hiện</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input column-toggle" type="checkbox" id="toggleActions"
                                   data-column="10" checked>
                            <label class="form-check-label" for="toggleActions">Hành động</label>
                        </div>
                        <div class="mt-2">
                            <button class="btn btn-sm btn-outline-primary w-100" id="resetColumns">
                                Khôi phục mặc định
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thông tin tóm tắt -->
        <div class="row mb-3 text-center">
            <div class="col-md-3">
                <div class="card bg-white">
                    <div class="card-body py-2">
                        <h6 class="card-title mb-0">Tổng số bản ghi</h6>
                        <p class="card-text fs-5 fw-bold text-primary" id="totalRecords">0</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-white">
                    <div class="card-body py-2">
                        <h6 class="card-title mb-0">Tổng nhập kho</h6>
                        <p class="card-text fs-5 fw-bold text-success" id="totalImport">0</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-white">
                    <div class="card-body py-2">
                        <h6 class="card-title mb-0">Tổng xuất kho</h6>
                        <p class="card-text fs-5 fw-bold text-danger" id="totalExport">0</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card bg-white">
                    <div class="card-body py-2">
                        <h6 class="card-title mb-0">Tổng điều chỉnh</h6>
                        <p class="card-text fs-5 fw-bold text-warning" id="totalAdjustment">0</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Inventory History Table -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="table-container p-3">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="historyTable">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Thời gian</th>
                                <th>Sản phẩm</th>
                                <th>Loại hành động</th>
                                <th>SL trước</th>
                                <th>Thay đổi</th>
                                <th>SL sau</th>
                                <th>Tham chiếu</th>
                                <th>Lý do</th>
                                <th>Người thực hiện</th>
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
        <!-- Inventory Value by Category Chart -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="chart-card">
                    <h5 class="mb-3">Giá trị tồn kho theo danh mục</h5>
                    <div>
                        <canvas id="inventoryValueByCategory" height="300"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="chart-card">
                    <h5 class="mb-3">Top 10 sản phẩm có giá trị tồn kho cao nhất</h5>
                    <div>
                        <canvas id="topInventoryValueProducts" height="300"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Inventory Movement & Turnover -->
        <div class="row mb-4">
            <div class="chart-card">
                <div class="row align-items-center mb-3">
                    <div class="col-md-4">
                        <h5>Biến động nhập/xuất kho</h5>
                    </div>
                    <div class="col-md-8 text-md-end">
                        <div class="d-flex justify-content-md-end">
                            <!-- Khoảng thời gian -->
                            <div class="btn-group me-3" role="group" aria-label="Period Selector" id="periodMovement">
                                <button type="button" class="btn btn-outline-primary btn-sm period-selector active"
                                        data-period="day">Ngày
                                </button>
                                <button type="button" class="btn btn-outline-primary btn-sm period-selector"
                                        data-period="week">Tuần
                                </button>
                                <button type="button" class="btn btn-outline-primary btn-sm period-selector"
                                        data-period="month">Tháng
                                </button>
                                <button type="button" class="btn btn-outline-primary btn-sm period-selector"
                                        data-period="quarter">Quý
                                </button>
                            </div>

                            <!-- Ngày bắt đầu và kết thúc -->
                            <div class="input-group input-group-sm me-2" style="width: 200px;">
                                <span class="input-group-text">Từ</span>
                                <input type="date" class="form-control" id="movementStartDate">
                            </div>
                            <div class="input-group input-group-sm" style="width: 200px;">
                                <span class="input-group-text">Đến</span>
                                <input type="date" class="form-control" id="movementEndDate">
                            </div>
                        </div>
                    </div>
                </div>
                <div>
                    <canvas id="inventoryMovementChart" height="300"></canvas>
                </div>
            </div>
        </div>

        <!-- Top Slow Moving and Fast Moving Products -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <ul class="nav nav-tabs" id="productMovementTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="slow-moving-tab" data-bs-toggle="tab"
                                        data-bs-target="#slow-moving" type="button" role="tab"
                                        aria-controls="slow-moving" aria-selected="true">Hàng chậm luân chuyển
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="fast-moving-tab" data-bs-toggle="tab"
                                        data-bs-target="#fast-moving" type="button" role="tab"
                                        aria-controls="fast-moving" aria-selected="false">Hàng nhanh luân
                                    chuyển
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="stock-out-tab" data-bs-toggle="tab"
                                        data-bs-target="#stock-out" type="button" role="tab" aria-controls="stock-out"
                                        aria-selected="false">Hàng thường xuyên hết
                                </button>
                            </li>
                        </ul>
                        <div class="tab-content mt-3" id="productMovementTabContent">
                            <div class="mb-3 border rounded p-3 bg-light">
                                <div class="row g-2 mb-2">
                                    <div class="col-12">
                                        <h6 class="mb-2">Bộ lọc chung</h6>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label small">Danh mục</label>
                                        <select class="form-select form-select-sm common-filter"
                                                id="categoryFilter">
                                            <option value="">Tất cả danh mục</option>
                                            <!-- Danh mục sẽ được tải động -->
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label small">Khoảng giá</label>
                                        <div class="input-group input-group-sm">
                                            <input type="number" class="form-control common-filter"
                                                   id="minPriceFilter" placeholder="Từ">
                                            <span class="input-group-text">-</span>
                                            <input type="number" class="form-control common-filter"
                                                   id="maxPriceFilter" placeholder="Đến">
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label small">Tìm kiếm</label>
                                        <input type="text" class="form-control form-control-sm common-filter"
                                               id="searchFilter" placeholder="Tên, mã, tác giả...">
                                    </div>
                                    <div class="col-md-3 d-flex align-items-end">
                                        <button class="btn btn-sm btn-primary" id="applyCommonFilter">
                                            <i class="bi bi-funnel"></i> Lọc
                                        </button>
                                        <button class="btn btn-sm btn-outline-secondary ms-2"
                                                id="resetCommonFilter">
                                            <i class="bi bi-x-circle"></i> Đặt lại
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane fade show active" id="slow-moving" role="tabpanel"
                                 aria-labelledby="slow-moving-tab">
                                <!-- Lọc cho luân chuyển chậm -->
                                <div id="slowMovingFilters" class="tab-filters mb-3 border rounded p-3 bg-light">
                                    <div class="row g-2">
                                        <div class="col-md-3">
                                            <label class="form-label small">Số ngày tồn kho tối thiểu</label>
                                            <select class="form-select form-select-sm" id="minDaysInStockFilter">
                                                <option value="1">Tất cả</option>
                                                <option value="30">Trên 30 ngày</option>
                                                <option value="60">Trên 60 ngày</option>
                                                <option value="90" selected>Trên 90 ngày</option>
                                                <option value="180">Trên 180 ngày</option>
                                                <option value="365">Trên 1 năm</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Vòng quay tồn kho tối đa</label>
                                            <select class="form-select form-select-sm" id="maxTurnoverFilter">
                                                <option value="">Tất cả</option>
                                                <option value="0.5" selected>Dưới 0.5</option>
                                                <option value="1">Dưới 1</option>
                                                <option value="2">Dưới 2</option>
                                                <option value="3">Dưới 3</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3 d-flex align-items-end">
                                            <button class="btn btn-sm btn-primary" id="applySlowMovingFilter">
                                                <i class="bi bi-funnel"></i> Áp dụng
                                            </button>
                                            <button class="btn btn-sm btn-outline-secondary ms-2"
                                                    id="resetSlowMovingFilter">
                                                <i class="bi bi-x-circle"></i> Đặt lại
                                            </button>
                                            <button class="btn btn-sm btn-outline-secondary ms-2"
                                                    id="refreshSlowMovingTable">
                                                <i class="bi bi-arrow-clockwise"></i> Làm mới
                                        </div>
                                    </div>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle" id="slowMovingTable">
                                        <thead>
                                        <tr>
                                            <th>Mã SP</th>
                                            <th>Sản phẩm</th>
                                            <th>Danh mục</th>
                                            <th>Giá</th>
                                            <th>Tồn kho</th>
                                            <th>Số ngày không bán</th>
                                            <th>Vòng quay</th>
                                            <th>Giá trị tồn</th>
                                            <th>Đề xuất</th>
                                        </tr>
                                        </thead>
                                        <tbody id="slowMovingTbody">
                                        <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="fast-moving" role="tabpanel"
                                 aria-labelledby="fast-moving-tab">
                                <!-- Bộ lọc riêng cho tab Hàng nhanh luân chuyển -->
                                <div id="fastMovingFilters" class="tab-filters mb-3 border rounded p-3 bg-light">
                                    <div class="row g-2">
                                        <div class="col-12">
                                            <h6 class="mb-2">Bộ lọc hàng nhanh luân chuyển</h6>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Vòng quay tồn kho tối thiểu</label>
                                            <select class="form-select form-select-sm" id="minTurnoverFilter">
                                                <option value="">Tất cả</option>
                                                <option value="3">Trên 3</option>
                                                <option value="5" selected>Trên 5</option>
                                                <option value="10">Trên 10</option>
                                                <option value="20">Trên 20</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Xu hướng bán</label>
                                            <select class="form-select form-select-sm" id="salesTrendFilter">
                                                <option value="">Tất cả</option>
                                                <option value="increasing">Tăng</option>
                                                <option value="stable">Ổn định</option>
                                                <option value="decreasing">Giảm</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Sắp xếp theo</label>
                                            <select class="form-select form-select-sm" id="fastMovingSortBy">
                                                <option value="turnover_desc" selected>Vòng quay (cao→thấp)</option>
                                                <option value="trend_desc">Xu hướng bán (tăng→giảm)</option>
                                                <option value="stock_asc">Tồn kho (thấp→cao)</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3 d-flex align-items-end">
                                            <button class="btn btn-sm btn-primary" id="applyFastMovingFilter">
                                                <i class="bi bi-funnel"></i> Áp dụng
                                            </button>
                                            <button class="btn btn-sm btn-outline-secondary ms-2"
                                                    id="resetFastMovingFilter">
                                                <i class="bi bi-x-circle"></i> Đặt lại
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead>
                                        <tr>
                                            <th>Mã SP</th>
                                            <th>Sản phẩm</th>
                                            <th>Danh mục</th>
                                            <th>Tồn kho</th>
                                            <th>Số ngày tồn kho</th>
                                            <th>Vòng quay</th>
                                            <th>Xu hướng bán</th>
                                            <th>Đề xuất</th>
                                        </tr>
                                        </thead>
                                        <tbody id="fastMovingTbody">
                                        <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="tab-pane fade" id="stock-out" role="tabpanel"
                                 aria-labelledby="stock-out-tab">
                                <!-- Bộ lọc riêng cho tab Hàng thường xuyên hết -->
                                <div id="stockOutFilters" class="tab-filters mb-3 border rounded p-3 bg-light">
                                    <div class="row g-2">
                                        <div class="col-12">
                                            <h6 class="mb-2">Bộ lọc hàng thường xuyên hết</h6>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Số lần hết hàng tối thiểu</label>
                                            <select class="form-select form-select-sm" id="minStockOutTimesFilter">
                                                <option value="">Tất cả</option>
                                                <option value="1">Ít nhất 1 lần</option>
                                                <option value="2" selected>Ít nhất 2 lần</option>
                                                <option value="3">Ít nhất 3 lần</option>
                                                <option value="5">Ít nhất 5 lần</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Khung thời gian phân tích</label>
                                            <select class="form-select form-select-sm" id="stockOutTimeFrameFilter">
                                                <option value="30">30 ngày qua</option>
                                                <option value="90" selected>90 ngày qua</option>
                                                <option value="180">180 ngày qua</option>
                                                <option value="365">365 ngày qua</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label small">Sắp xếp theo</label>
                                            <select class="form-select form-select-sm" id="stockOutSortBy">
                                                <option value="times_desc" selected>Số lần hết hàng (cao→thấp)
                                                </option>
                                                <option value="days_desc">Tổng ngày hết hàng (cao→thấp)</option>
                                                <option value="safety_diff">Chênh lệch với mức an toàn</option>
                                            </select>
                                        </div>
                                        <div class="col-md-3 d-flex align-items-end">
                                            <button class="btn btn-sm btn-primary" id="applyStockOutFilter">
                                                <i class="bi bi-funnel"></i> Áp dụng
                                            </button>
                                            <button class="btn btn-sm btn-outline-secondary ms-2"
                                                    id="resetStockOutFilter">
                                                <i class="bi bi-x-circle"></i> Đặt lại
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle">
                                        <thead>
                                        <tr>
                                            <th>Mã SP</th>
                                            <th>Sản phẩm</th>
                                            <th>Danh mục</th>
                                            <th>Tồn kho</th>
                                            <th>Số lần hết hàng</th>
                                            <th>Tổng ngày hết hàng</th>
                                            <th>Mức dự trữ an toàn</th>
                                            <th>Đề xuất</th>
                                        </tr>
                                        </thead>
                                        <tbody id="stockOutTbody">
                                        <!-- Dữ liệu sẽ được tải bằng JavaScript -->
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Export Reports -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title mb-3">Xuất báo cáo</h5>
                        <div class="row g-3">
                            <div class="col-md-4">
                                <div class="form-floating">
                                    <select class="form-select" id="exportReportType">
                                        <option value="inventory_value">Báo cáo giá trị tồn kho</option>
                                        <option value="inventory_turnover">Báo cáo vòng quay tồn kho</option>
                                        <option value="inventory_days">Báo cáo số ngày tồn kho</option>
                                        <option value="stock_movement">Báo cáo biến động tồn kho</option>
                                        <option value="out_of_stock">Báo cáo tình trạng hết hàng</option>
                                        <option value="all">Tất cả báo cáo</option>
                                    </select>
                                    <label for="exportReportType">Loại báo cáo</label>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-floating">
                                    <select class="form-select" id="exportFormat">
                                        <option value="excel">Excel (.xlsx)</option>
                                        <option value="pdf">PDF (.pdf)</option>
                                        <option value="csv">CSV (.csv)</option>
                                    </select>
                                    <label for="exportFormat">Định dạng</label>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <button class="btn btn-success h-100 w-100">
                                    <i class="bi bi-download"></i> Xuất báo cáo
                                </button>
                            </div>
                            <div class="col-md-2">
                                <button class="btn btn-info h-100 w-100">
                                    <i class="bi bi-envelope"></i> Gửi email
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer
        <jsp:include page="/common/admin2/footer.jsp"></jsp:include>
    </div>
</div>

<jsp:include page="/common/admin2/scripts-footer.jsp"></jsp:include>

<!-- Custom JS -->
<script src="/app/main/index.js"></script>
<script src="/app/main/inventory-report.js" type="module"></script>
</body>

</html>
