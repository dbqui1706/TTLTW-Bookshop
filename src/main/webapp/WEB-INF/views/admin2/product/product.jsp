<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Quản Trị Hệ Thống</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"/>
    <!-- DataTables CSS -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.5.0/css/responsive.bootstrap5.min.css">

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/product/product-manager.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <nav id="sidebar">
        <div class="sidebar-header">
            <h5 class="mb-0 d-flex align-items-center">
                <i class="bi bi-boxes me-2 text-primary"></i>
                <span>Hệ Thống Quản Lý</span>
            </h5>
        </div>
        <ul class="components">
            <li>
                <a href="index.html">
                    <i class="bi bi-speedometer2"></i>
                    <span>Trang Chủ</span>
                </a>
            </li>
            <li>
                <a href="#productSubmenu" data-bs-toggle="collapse" aria-expanded="false"
                   class="dropdown-toggle active show">
                    <i class="bi bi-box-seam"></i>
                    <span>Sản Phẩm</span>
                </a>
                <ul class="collapse submenu" id="productSubmenu">
                    <li>
                        <a href="#" style="color: rgb(13 110 253)">
                            Danh Sách Sản Phẩm
                        </a>
                    </li>
                    <li>
                        <a href="#">Thêm Sản Phẩm Mới</a>
                    </li>
                    <li>
                        <a href="#">Danh Mục Sản Phẩm</a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="#orderSubmenu" data-bs-toggle="collapse" aria-expanded="false" class="dropdown-toggle">
                    <i class="bi bi-cart3"></i>
                    <span>Đơn Hàng</span>
                    <span class="badge bg-danger rounded-pill">5</span>
                </a>
                <ul class="collapse submenu" id="orderSubmenu">
                    <li>
                        <a href="#">Tất Cả Đơn Hàng</a>
                    </li>
                    <li>
                        <a href="#">Đơn Hàng Mới</a>
                    </li>
                    <li>
                        <a href="#">Đơn Đang Xử Lý</a>
                    </li>
                    <li>
                        <a href="#">Đơn Đã Hoàn Thành</a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="#customerSubmenu" data-bs-toggle="collapse" aria-expanded="false" class="dropdown-toggle">
                    <i class="bi bi-people"></i>
                    <span>Khách Hàng</span>
                </a>
                <ul class="collapse submenu" id="customerSubmenu">
                    <li>
                        <a href="#">Danh Sách Khách Hàng</a>
                    </li>
                    <li>
                        <a href="#">Khách Hàng VIP</a>
                    </li>
                    <li>
                        <a href="#">Phản Hồi Khách Hàng</a>
                    </li>
                </ul>
            </li>
            <div class="sidebar-heading">Báo Cáo</div>
            <li>
                <a href="#">
                    <i class="bi bi-graph-up"></i>
                    <span>Báo Cáo Doanh Thu</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="bi bi-bar-chart"></i>
                    <span>Thống Kê Bán Hàng</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="bi bi-pie-chart"></i>
                    <span>Phân Tích Xu Hướng</span>
                </a>
            </li>
            <div class="sidebar-heading">Hệ Thống</div>
            <li>
                <a href="#">
                    <i class="bi bi-gear"></i>
                    <span>Thiết Lập Hệ Thống</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="bi bi-person-badge"></i>
                    <span>Quản Lý Người Dùng</span>
                </a>
            </li>
            <li>
                <a href="#">
                    <i class="bi bi-shield-lock"></i>
                    <span>Phân Quyền</span>
                </a>
            </li>
        </ul>
    </nav>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <nav class="navbar navbar-expand-lg mb-4">
            <div class="container-fluid">
                <button type="button" id="sidebarCollapse" class="btn btn-primary">
                    <i class="bi bi-list"></i>
                </button>
                <button class="btn btn-dark d-inline-block d-lg-none ms-auto" type="button" data-bs-toggle="collapse"
                        data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                        aria-expanded="false" aria-label="Toggle navigation">
                    <i class="bi bi-three-dots"></i>
                </button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="nav navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="#" title="Thông báo">
                                <i class="bi bi-bell position-relative">
                                        <span class="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle">
                                            <span class="visually-hidden">Thông báo mới</span>
                                        </span>
                                </i>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#" title="Tin nhắn">
                                <i class="bi bi-envelope"></i>
                            </a>
                        </li>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="navbarDropdown"
                               role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <img src="https://via.placeholder.com/30" class="rounded-circle me-2" alt="Admin"/>
                                <span>Admin</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                                <li>
                                    <a class="dropdown-item" href="#"><i class="bi bi-person me-2"></i>Hồ sơ</a>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="#"><i class="bi bi-gear me-2"></i>Cài đặt</a>
                                </li>
                                <li>
                                    <hr class="dropdown-divider"/>
                                </li>
                                <li>
                                    <a class="dropdown-item" href="#"><i class="bi bi-box-arrow-right me-2"></i>Đăng
                                        xuất</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>

        <!-- Dashboard Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="bg-primary text-white rounded p-3">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Quản lý sản phẩm</h2>
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
                                           placeholder="Tìm kiếm sản phẩm"/>
                                    <label for="searchProduct">Tìm kiếm sản phẩm</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="categoryFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="1">Điện thoại</option>
                                        <option value="2">Laptop</option>
                                        <option value="3">Máy tính bảng</option>
                                        <option value="4">Phụ kiện</option>
                                    </select>
                                    <label for="categoryFilter">Danh mục</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="stockFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="in-stock">Còn hàng</option>
                                        <option value="low-stock">Sắp hết</option>
                                        <option value="out-of-stock">Hết hàng</option>
                                    </select>
                                    <label for="stockFilter">Tình trạng</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="sortOption">
                                        <option value="name-asc" selected>Tên A-Z</option>
                                        <option value="name-desc">Tên Z-A</option>
                                        <option value="price-asc">Giá tăng dần</option>
                                        <option value="price-desc">Giá giảm dần</option>
                                        <option value="newest">Mới nhất</option>
                                    </select>
                                    <label for="sortOption">Sắp xếp theo</label>
                                </div>
                            </div>
                            <div class="col-lg-4 col-md-12 text-end">
                                <button class="btn btn-success ms-2" data-bs-toggle="tooltip" data-bs-placement="top"
                                        title="Thêm hàng loạt sản phẩm từ file Excel">
                                    <i class="bi bi-file-earmark-arrow-down"></i> Import Excel
                                </button>
                                <button class="btn btn-success ms-2" data-bs-toggle="tooltip" data-bs-placement="top"
                                        title="Xuất file Excel danh sách sản phẩm">
                                    <i class="bi bi-file-earmark-arrow-up"></i> Export Excel
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
                                <h3 class="mb-0">1,358</h3>
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
                                <h3 class="mb-0">1,208</h3>
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
                                <h3 class="mb-0">85</h3>
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
                                <h3 class="mb-0">65</h3>
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
                            <table class="table table-hover mb-0">
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
                                    <th width="150">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Product Table Row 1 -->
                                <tr>
                                    <td>#SP001</td>
                                    <td>
                                        <img src="https://via.placeholder.com/300x300" alt="Sách Spacewax" width="50"
                                             height="50" class="rounded"/>
                                    </td>
                                    <td>Sách Spacewax</td>
                                    <td>
                                        <span>Báo in</span>
                                    </td>
                                    <td class="fw-bold">29,990đ</td>
                                    <td class="text-muted text-decoration-line-through">32,990đ</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="stock-indicator stock-high me-2"></div>
                                            <span>128</span>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-success">Còn hàng</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Product Table Row 2 -->
                                <tr>
                                    <td>#SP002</td>
                                    <td>
                                        <img src="https://via.placeholder.com/300x300" alt="Sách Ontagene" width="50"
                                             height="50" class="rounded"/>
                                    </td>
                                    <td>Sách Ontagene</td>
                                    <td>
                                        <span> Sách kỹ năng sống</span>
                                    </td>
                                    <td class="fw-bold">35,990đ</td>
                                    <td class="text-muted text-decoration-line-through">39,990đ</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="stock-indicator stock-low me-2"></div>
                                            <span>0</span>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-danger">Hết hàng</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Product Table Row 3 -->
                                <tr>
                                    <td>#SP003</td>
                                    <td>
                                        <img src="https://via.placeholder.com/300x300?text=Samsung+S24"
                                             alt="Samsung Galaxy S24" width="50" height="50" class="rounded"/>
                                    </td>
                                    <td>Samsung Galaxy S24</td>
                                    <td>
                                        <span class="badge bg-primary">Điện thoại</span>
                                    </td>
                                    <td class="fw-bold">25,990,000đ</td>
                                    <td class="text-muted text-decoration-line-through">
                                        27,990,000đ
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="stock-indicator stock-medium me-2"></div>
                                            <span>8</span>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-warning text-dark">Sắp hết</span>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Product Table Row 4 -->
                                <tr>
                                    <td>#SP004</td>
                                    <td>
                                        <img src="https://via.placeholder.com/300x300?text=iPad+Pro" alt="iPad Pro"
                                             width="50" height="50" class="rounded"/>
                                    </td>
                                    <td>iPad Pro 12.9"</td>
                                    <td>
                                        <span class="badge bg-secondary">Máy tính bảng</span>
                                    </td>
                                    <td class="fw-bold">26,990,000đ</td>
                                    <td class="text-muted text-decoration-line-through">
                                        29,990,000đ
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="stock-indicator stock-high me-2"></div>
                                            <span>42</span>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-success">Còn hàng</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Product Table Row 5 -->
                                <tr>
                                    <td>#SP005</td>
                                    <td>
                                        <img src="https://via.placeholder.com/300x300?text=AirPods+Pro"
                                             alt="AirPods Pro" width="100" height="150" class="rounded"/>
                                    </td>
                                    <td>AirPods Pro 2</td>
                                    <td><span class="badge bg-dark">Phụ kiện</span></td>
                                    <td class="fw-bold">6,990,000đ</td>
                                    <td class="text-muted text-decoration-line-through">
                                        7,990,000đ
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="stock-indicator stock-high me-2"></div>
                                            <span>75</span>
                                        </div>
                                    </td>
                                    <td><span class="badge bg-success">Còn hàng</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                <!-- Thêm các hàng sản phẩm khác vào đây -->
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
                    <select class="form-select form-select-sm w-auto">
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
                    <ul class="pagination justify-content-end">
                        <li class="page-item disabled">
                            <a class="page-link" href="#" aria-label="Previous">
                                <span aria-hidden="true">&laquo;</span>
                            </a>
                        </li>
                        <li class="page-item active">
                            <a class="page-link" href="#">1</a>
                        </li>
                        <li class="page-item"><a class="page-link" href="#">2</a></li>
                        <li class="page-item"><a class="page-link" href="#">3</a></li>
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>

        <!-- Modal Thêm Sản Phẩm -->
        <div class="modal fade" id="addProductModal" tabindex="-1" aria-labelledby="addProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="addProductModalLabel">
                            Thêm sản phẩm mới
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addProductForm" method="POST"
                              action="${pageContext.request.contextPath}/admin/productManager/create"
                              enctype="multipart/form-data">
                            <!-- Thông báo thành công/lỗi -->
                            <div id="successMessage" class="alert alert-success mb-3" role="alert"
                                 style="display: none"></div>
                            <div id="errorMessage" class="alert alert-danger mb-3" role="alert"
                                 style="display: none"></div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-name" class="form-label">Tên sách <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="product-name" name="name" required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-category" class="form-label">Thể loại <span class="text-danger">*</span></label>
                                    <select class="form-select" id="product-category" name="category" required>
                                        <option value="" selected disabled>
                                            Chọn một thể loại...
                                        </option>
                                        <!-- Danh sách thể loại sẽ được tải động -->
                                    </select>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-price" class="form-label">Giá gốc <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="product-price" name="price"
                                               min="0" step="500" required/>
                                        <span class="input-group-text">₫</span>
                                        <div class="invalid-feedback"></div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-discount" class="form-label">Khuyến mãi <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="product-discount" name="discount"
                                               min="0" max="100" value="0" required/>
                                        <span class="input-group-text">%</span>
                                        <div class="invalid-feedback"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-quantity" class="form-label">Tồn kho <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="product-quantity" name="quantity"
                                           min="0" required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-totalBuy" class="form-label">Lượt mua <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="product-totalBuy" name="totalBuy"
                                           min="0" value="0" required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-author" class="form-label">Tác giả <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="product-author" name="author" required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-pages" class="form-label">Số trang <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="product-pages" name="pages" min="1"
                                           required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-publisher" class="form-label">Nhà xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="product-publisher" name="publisher"
                                           required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-yearPublishing" class="form-label">Năm xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="product-yearPublishing"
                                           name="yearPublishing" min="1901" max="2099" required/>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="product-startsAt" class="form-label">Ngày bắt đầu khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="product-startsAt"
                                           name="startsAt"/>
                                    <div class="invalid-feedback"></div>
                                </div>
                                <div class="col-md-6">
                                    <label for="product-endsAt" class="form-label">Ngày kết thúc khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="product-endsAt"
                                           name="endsAt"/>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Mô tả sách</label>
                                <div id="editor-container">
                                    <div id="froala-editor"></div>
                                    <!-- <textarea
                                class="form-control"
                                id="product-description"
                                name="description"
                                rows="5"
                                style="display: none"
                                ></textarea> -->
                                </div>
                                <div class="invalid-feedback"></div>
                            </div>
                            <div class="mb-3">
                                <label for="product-imageName" class="form-label">Hình sản phẩm</label>
                                <input type="file" class="form-control" id="product-imageName" name="image"
                                       accept="image/*"/>
                                <div class="mt-2" id="imagePreview" style="display: none">
                                    <img src="" alt="Preview" class="img-thumbnail" style="max-height: 200px"/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label d-block">Cho phép giao dịch?
                                        <span class="text-danger">*</span></label>
                                    <div class="form-check d-inline-block me-4">
                                        <input class="form-check-input" type="radio" name="shop" id="product-shop-yes"
                                               value="1" checked required/>
                                        <label class="form-check-label" for="product-shop-yes">Có</label>
                                    </div>
                                    <div class="form-check d-inline-block">
                                        <input class="form-check-input" type="radio" name="shop" id="product-shop-no"
                                               value="0" required/>
                                        <label class="form-check-label" for="product-shop-no">Không</label>
                                    </div>
                                    <div class="invalid-feedback"></div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-warning" id="resetFormBtn">
                            <i class="bi bi-arrow-counterclockwise me-1"></i> Mặc định
                        </button>
                        <button type="button" class="btn btn-primary" id="saveProductBtn">
                            <i class="bi bi-save me-1"></i> Thêm sản phẩm
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xem Chi Tiết Sản Phẩm -->
        <div class="modal fade" id="viewProductModal" tabindex="-1" aria-labelledby="viewProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="viewProductModalLabel">
                            Chi tiết sản phẩm
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <img id="viewProductImage" src="" alt="Hình sản phẩm" class="img-fluid rounded mb-3"
                                     style="max-height: 200px"/>
                                <div class="mb-3 p-2">
                                    <span class="badge bg-primary" id="viewProductCategory"></span>
                                </div>
                                <div class="d-flex align-items-center justify-content-center gap-3">
                                    <h5 class="mb-0" id="viewProductPrice"></h5>
                                    <small class="text-muted text-decoration-line-through badge bg-primary text-white"
                                           id="viewProductOriginalPrice"></small>
                                </div>
                            </div>
                            <div class="col-md-8">
                                <h4 id="viewProductName" class="mb-3"></h4>
                                <div class="row mb-2">
                                    <div class="col-md-6">
                                        <p class="mb-1">
                                            <strong>ID:</strong> <span id="viewProductId"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Tác giả:</strong>
                                            <span id="viewProductAuthor"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Nhà xuất bản:</strong>
                                            <span id="viewProductPublisher"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Năm xuất bản:</strong>
                                            <span id="viewProductYear"></span>
                                        </p>
                                        <p class="mb-1"></p>
                                        <strong>Thể loại:</strong>
                                        <span id="viewCategory">Bao in</span>
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-1">
                                            <strong>Số trang:</strong>
                                            <span id="viewProductPages"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Tồn kho:</strong>
                                            <span id="viewProductQuantity"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Lượt mua:</strong>
                                            <span id="viewProductTotalBuy"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Trạng thái:</strong>
                                            <span id="viewProductStatus"></span>
                                        </p>
                                    </div>
                                </div>
                                <hr/>
                                <div class="mb-3">
                                    <h5>Mô tả sản phẩm</h5>
                                    <div id="viewProductDescription" class="overflow-auto"
                                         style="max-height: 200px"></div>
                                </div>
                                <div class="row mb-2">
                                    <div class="col-md-6">
                                        <p class="mb-1">
                                            <strong>Khuyến mãi:</strong>
                                            <span id="viewProductDiscount"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Cho phép giao dịch:</strong>
                                            <span id="viewProductShop"></span>
                                        </p>
                                    </div>
                                    <div class="col-md-6">
                                        <p class="mb-1">
                                            <strong>Bắt đầu KM:</strong>
                                            <span id="viewProductStartsAt"></span>
                                        </p>
                                        <p class="mb-1">
                                            <strong>Kết thúc KM:</strong>
                                            <span id="viewProductEndsAt"></span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Đóng
                        </button>
                        <button type="button" class="btn btn-primary" id="editFromViewBtn">
                            <i class="bi bi-pencil me-1"></i> Chỉnh sửa
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Chỉnh Sửa Sản Phẩm -->
        <div class="modal fade" id="editProductModal" tabindex="-1" aria-labelledby="editProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="editProductModalLabel">
                            Chỉnh sửa sản phẩm
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editProductForm" method="POST"
                              action="${pageContext.request.contextPath}/admin/productManager/update"
                              enctype="multipart/form-data">
                            <input type="hidden" id="editProductId" name="id" value=""/>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductName" class="form-label">Tên sách <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductName" name="name" required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductCategory" class="form-label">Thể loại <span
                                            class="text-danger">*</span></label>
                                    <select class="form-select" id="editProductCategory" name="category" required>
                                        <option value="" disabled>Chọn một thể loại...</option>
                                        <!-- Danh sách thể loại sẽ được tải động -->
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductPrice" class="form-label">Giá gốc <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="editProductPrice" name="price"
                                               min="0" step="500" required/>
                                        <span class="input-group-text">₫</span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductDiscount" class="form-label">Khuyến mãi <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="editProductDiscount"
                                               name="discount" min="0" max="100" value="0" required/>
                                        <span class="input-group-text">%</span>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductQuantity" class="form-label">Tồn kho <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductQuantity" name="quantity"
                                           min="0" required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductTotalBuy" class="form-label">Lượt mua <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductTotalBuy" name="totalBuy"
                                           min="0" required/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductAuthor" class="form-label">Tác giả <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductAuthor" name="author"
                                           required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductPages" class="form-label">Số trang <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductPages" name="pages" min="1"
                                           required/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductPublisher" class="form-label">Nhà xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductPublisher" name="publisher"
                                           required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductYearPublishing" class="form-label">Năm xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductYearPublishing"
                                           name="yearPublishing" min="1901" max="2099" required/>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editProductDescription" class="form-label">Mô tả sách</label>
                                <div id="edit-editor-container">
                                    <div id="edit-froala-editor"></div>
                                    <textarea class="form-control" id="editProductDescription" name="description"
                                              rows="5" style="display: none"></textarea>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editProductImage" class="form-label">Hình sản phẩm</label>
                                <input type="file" class="form-control" id="editProductImage" name="image"
                                       accept="image/*"/>
                                <div class="mt-2" id="editImagePreview">
                                    <img src="" alt="Preview" class="img-thumbnail" style="max-height: 150px"/>
                                    <small class="d-block text-muted">Để trống nếu không muốn thay đổi hình ảnh.</small>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label d-block">Cho phép giao dịch?
                                        <span class="text-danger">*</span></label>
                                    <div class="form-check d-inline-block me-4">
                                        <input class="form-check-input" type="radio" name="shop" id="editProductShopYes"
                                               value="1" required/>
                                        <label class="form-check-label" for="editProductShopYes">Có</label>
                                    </div>
                                    <div class="form-check d-inline-block">
                                        <input class="form-check-input" type="radio" name="shop" id="editProductShopNo"
                                               value="0" required/>
                                        <label class="form-check-label" for="editProductShopNo">Không</label>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductStartsAt" class="form-label">Ngày bắt đầu khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="editProductStartsAt"
                                           name="startsAt"/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductEndsAt" class="form-label">Ngày kết thúc khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="editProductEndsAt"
                                           name="endsAt"/>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="updateProductBtn">
                            <i class="bi bi-save me-1"></i> Cập nhật
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xác Nhận Xóa Sản Phẩm -->
        <div class="modal fade" id="deleteProductModal" tabindex="-1" aria-labelledby="deleteProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="deleteProductModalLabel">
                            Xác nhận xóa sản phẩm
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>
                            Bạn có chắc chắn muốn xóa sản phẩm
                            <strong id="deleteProductName"></strong>?
                        </p>
                        <p class="text-danger">
                            Lưu ý: Hành động này không thể hoàn tác.
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
                            <i class="bi bi-trash me-1"></i> Xóa
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Chỉnh Sửa Sản Phẩm -->
        <div class="modal fade" id="editProductModal" tabindex="-1" aria-labelledby="editProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="editProductModalLabel">
                            Chỉnh sửa sản phẩm
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editProductForm" method="POST"
                              action="${pageContext.request.contextPath}/admin/productManager/update"
                              enctype="multipart/form-data">
                            <input type="hidden" id="editProductId" name="id" value=""/>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductName" class="form-label">Tên sách <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductName" name="name" required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductCategory" class="form-label">Thể loại <span
                                            class="text-danger">*</span></label>
                                    <select class="form-select" id="editProductCategory" name="category" required>
                                        <option value="" disabled>Chọn một thể loại...</option>
                                        <!-- Danh sách thể loại sẽ được tải động -->
                                    </select>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductPrice" class="form-label">Giá gốc <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="editProductPrice" name="price"
                                               min="0" step="500" required/>
                                        <span class="input-group-text">₫</span>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductDiscount" class="form-label">Khuyến mãi <span
                                            class="text-danger">*</span></label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="editProductDiscount"
                                               name="discount" min="0" max="100" value="0" required/>
                                        <span class="input-group-text">%</span>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductQuantity" class="form-label">Tồn kho <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductQuantity" name="quantity"
                                           min="0" required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductTotalBuy" class="form-label">Lượt mua <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductTotalBuy" name="totalBuy"
                                           min="0" required/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductAuthor" class="form-label">Tác giả <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductAuthor" name="author"
                                           required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductPages" class="form-label">Số trang <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductPages" name="pages" min="1"
                                           required/>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductPublisher" class="form-label">Nhà xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="editProductPublisher" name="publisher"
                                           required/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductYearPublishing" class="form-label">Năm xuất bản <span
                                            class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="editProductYearPublishing"
                                           name="yearPublishing" min="1901" max="2099" required/>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editProductDescription" class="form-label">Mô tả sách</label>
                                <div id="edit-editor-container">
                                    <div id="edit-froala-editor"></div>
                                    <textarea class="form-control" id="editProductDescription" name="description"
                                              rows="5" style="display: none"></textarea>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editProductImage" class="form-label">Hình sản phẩm</label>
                                <input type="file" class="form-control" id="editProductImage" name="image"
                                       accept="image/*"/>
                                <div class="mt-2" id="editImagePreview">
                                    <img src="" alt="Preview" class="img-thumbnail" style="max-height: 150px"/>
                                    <small class="d-block text-muted">Để trống nếu không muốn thay đổi hình ảnh.</small>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label d-block">Cho phép giao dịch?
                                        <span class="text-danger">*</span></label>
                                    <div class="form-check d-inline-block me-4">
                                        <input class="form-check-input" type="radio" name="shop" id="editProductShopYes"
                                               value="1" required/>
                                        <label class="form-check-label" for="editProductShopYes">Có</label>
                                    </div>
                                    <div class="form-check d-inline-block">
                                        <input class="form-check-input" type="radio" name="shop" id="editProductShopNo"
                                               value="0" required/>
                                        <label class="form-check-label" for="editProductShopNo">Không</label>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="editProductStartsAt" class="form-label">Ngày bắt đầu khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="editProductStartsAt"
                                           name="startsAt"/>
                                </div>
                                <div class="col-md-6">
                                    <label for="editProductEndsAt" class="form-label">Ngày kết thúc khuyến mãi</label>
                                    <input type="datetime-local" class="form-control" id="editProductEndsAt"
                                           name="endsAt"/>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="updateProductBtn">
                            <i class="bi bi-save me-1"></i> Cập nhật
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Xác Nhận Xóa Sản Phẩm -->
        <div class="modal fade" id="deleteProductModal" tabindex="-1" aria-labelledby="deleteProductModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="deleteProductModalLabel">
                            Xác nhận xóa sản phẩm
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>
                            Bạn có chắc chắn muốn xóa sản phẩm
                            <strong id="deleteProductName"></strong>?
                        </p>
                        <p class="text-danger">
                            Lưu ý: Hành động này không thể hoàn tác.
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">
                            <i class="bi bi-trash me-1"></i> Xóa
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <footer class="bg-white text-center py-3 mt-2 rounded">
            <p class="mb-0 text-muted">
                © 2025 Hệ Thống Quản Lý. Thiết kế bởi
                <a href="#" class="text-decoration-none">BookShopTeam</a>
            </p>
        </footer>
    </div>
</div>
<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- Froala JS -->
<link href="https://cdn.jsdelivr.net/npm/froala-editor@latest/css/froala_editor.pkgd.min.css" rel="stylesheet"
      type="text/css"/>
<script type="text/javascript"
        src="https://cdn.jsdelivr.net/npm/froala-editor@latest/js/froala_editor.pkgd.min.js"></script>

<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/js/admin2/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/js/admin2/product/product.js"></script>
<script src="${pageContext.request.contextPath}/js/admin2/product/product2.js"></script>
</body>
</html>