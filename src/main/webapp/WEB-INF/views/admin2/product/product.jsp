<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Quản Trị Hệ Thống</title>

    <jsp:include page="/common/admin2/head-meta.jsp"></jsp:include>
    <link rel="stylesheet" href="/bookshop/css/product/product.css" />
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="sidebar" value="PRODUCT-MANAGER"/>
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
                            <h2>Quản lý thông tin sản phẩm</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Control Panel -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-3">
                        <div class="row g-2 align-items-center">
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="searchProduct"
                                           placeholder="Tìm kiếm sản phẩm" />
                                    <label for="searchProduct">Tìm kiếm sản phẩm</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="categoryFilter">
                                        <option value="" selected>Tất cả</option>
                                    </select>
                                    <label for="categoryFilter">Danh mục</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="stockFilter">
                                    </select>
                                    <label for="stockFilter">Tình trạng</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="sortOption">
                                    </select>
                                    <label for="sortOption">Sắp xếp theo</label>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-12 text-end">
                                <button id="importExcelBtn" class="btn btn-success ms-2" data-bs-toggle="tooltip"
                                        data-bs-placement="top" title="Thêm hàng loạt sản phẩm từ file Excel">
                                    <i class="bi bi-file-earmark-arrow-down"></i>
                                </button>
                                <button id="exportExcelBtn" class="btn btn-warning ms-2" data-bs-toggle="tooltip"
                                        data-bs-placement="top" title="Xuất file Excel danh sách sản phẩm">
                                    <i class="bi bi-file-earmark-arrow-up"></i>
                                </button>
                                <button id="exportPdfBtn" class="btn btn-danger ms-2" data-bs-toggle="tooltip"
                                        data-bs-placement="top" title="In file FDF danh sách sản phẩm">
                                    <i class="bi bi-filetype-pdf"></i>
                                </button>
                                <button id="addProductBtn" class="btn btn-primary ms-2">
                                    <i class="bi bi-plus-lg"></i> Thêm sản phẩm
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stats Row -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-primary bg-opacity-10 p-3 rounded text-primary">
                                    <i class="bi bi-box-seam fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Tổng sản phẩm</h6>
                                <h3 class="mb-0" id="totalProducts">0</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-success bg-opacity-10 p-3 rounded text-success">
                                    <i class="bi bi-check-circle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Sản phẩm còn hàng</h6>
                                <h3 class="mb-0" id="available">0</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-warning bg-opacity-10 p-3 rounded text-warning">
                                    <i class="bi bi-exclamation-triangle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Sắp hết hàng</h6>
                                <h3 class="mb-0" id="almostOutOfStock">0</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-danger bg-opacity-10 p-3 rounded text-danger">
                                    <i class="bi bi-x-circle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Hết hàng</h6>
                                <h3 class="mb-0" id="outOfStock">0</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Products Table View -->
        <div class="row mb-4 product-container" id="table-view">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0" id="productTable">
                                <thead class="table-view-header">
                                <tr>
                                    <th width="60">ID</th>
                                    <th width="80">Hình ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Danh mục</th>
                                    <th>Giá bán</th>
                                    <th>Giá gốc</th>
                                    <th>Tồn kho</th>
                                    <th>Trạng thái</th>
                                    <th>Đã bán</th>
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

        <!-- Pagination -->
        <div class="row">
            <div class="col-md-6 mb-3">
                <div class="d-flex align-items-center">
                    <span class="me-2">Hiển thị</span>
                    <select class="form-select-sm w-auto" id="itemsPerPage">
                        <option value="10">10</option>
                        <option value="25">25</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                    </select>
                    <span class="ms-2">sản phẩm trên mỗi trang</span>
                </div>
            </div>
            <div class="col-md-6">
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-end"></ul>
                </nav>
            </div>
        </div>

        <!-- Modal Thêm Sản Phẩm -->

        <!-- Modal Xem Chi Tiết Sản Phẩm -->

        <!-- Modal Chỉnh Sửa Sản Phẩm -->

        <!-- Modal Xác Nhận Xóa Sản Phẩm -->

        <!-- Footer -->
        <footer class="bg-white text-center py-3 mt-2 rounded">
            <p class="mb-0 text-muted">
                © 2025 Hệ Thống Quản Lý. Thiết kế bởi
                <a href="#" class="text-decoration-none">BookShopTeam</a>
            </p>
        </footer>
    </div>
</div>
<!-- Select2 JS CSS -->
<style>
    /* Ẩn container của Select2 để không thay đổi giao diện */
    .select2-container--default .select2-selection--single {
        height: 100%;
        border: 1px solid #ced4da;
        /* Giữ border giống Bootstrap */
        border-radius: 0.375rem;
        /* Border radius giống Bootstrap */
        padding: 0.1rem 0.75rem;
    }

    /* Giữ nguyên vị trí và kích thước của mũi tên dropdown */
    .select2-container--default .select2-selection--single .select2-selection__arrow {
        height: 100%;
        right: 10px;
    }

    /* Đảm bảo văn bản được hiển thị đúng vị trí */
    .select2-container--default .select2-selection--single .select2-selection__rendered {
        line-height: 1.5;
        padding-left: 0;
        color: #212529;
        /* Màu chữ giống Bootstrap */
    }

    /* Đảm bảo form-floating hoạt động đúng */
    .form-floating .select2-container {
        height: 100%;
    }

    .form-floating .select2-container--default .select2-selection--single {
        padding-top: 1.62rem;
        padding-bottom: 0.62rem;
    }
</style>
</body>
<jsp:include page="/common/admin2/scripts-footer.jsp"></jsp:include>

<!-- Custom JS -->
<script src="/app/main/product.js" type="module"></script>

</html>