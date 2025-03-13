<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản Trị Hệ Thống</title>
    <link href="${pageContext.request.contextPath}/img/favicon.ico" rel="shortcut icon" type="image/x-icon">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css" />
</head>
<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="active" value="HOME"/>
    </jsp:include>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <nav class="navbar navbar-expand-lg mb-4">
            <div class="container-fluid">
                <button type="button" id="sidebarCollapse" class="btn btn-primary">
                    <i class="bi bi-list"></i>
                </button>
                <button
                        class="btn btn-dark d-inline-block d-lg-none ms-auto"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarSupportedContent"
                        aria-controls="navbarSupportedContent"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                >
                    <i class="bi bi-three-dots"></i>
                </button>

                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="nav navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="#" title="Thông báo">
                                <i class="bi bi-bell position-relative">
                      <span
                              class="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle"
                      >
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
                            <a
                                    class="nav-link dropdown-toggle d-flex align-items-center"
                                    href="#"
                                    id="navbarDropdown"
                                    role="button"
                                    data-bs-toggle="dropdown"
                                    aria-expanded="false"
                            >
                                <img
                                        src="https://via.placeholder.com/30"
                                        class="rounded-circle me-2"
                                        alt="Admin"
                                />
                                <span>Admin</span>
                            </a>
                            <ul
                                    class="dropdown-menu dropdown-menu-end"
                                    aria-labelledby="navbarDropdown"
                            >
                                <li>
                                    <a class="dropdown-item" href="#"
                                    ><i class="bi bi-person me-2"></i>Hồ sơ</a
                                    >
                                </li>
                                <li>
                                    <a class="dropdown-item" href="#"
                                    ><i class="bi bi-gear me-2"></i>Cài đặt</a
                                    >
                                </li>
                                <li><hr class="dropdown-divider" /></li>
                                <li>
                                    <a class="dropdown-item" href="#"
                                    ><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a
                                    >
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
                <div class="bg-primary text-white rounded p-4">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Xin chào, Admin!</h2>
                            <p class="mb-0">Đây là tổng quan về hoạt động của hệ thống</p>
                        </div>
                        <!-- <div class="col-md-6 text-md-end mt-3 mt-md-0">
                          <div class="btn-group">
                            <button type="button" class="btn btn-outline-light">
                              Hôm nay
                            </button>
                            <button type="button" class="btn btn-light">
                              Tuần này
                            </button>
                            <button type="button" class="btn btn-outline-light">
                              Tháng này
                            </button>
                          </div>
                        </div> -->
                    </div>
                </div>
            </div>
        </div>

        <!-- Stat Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-6 col-xl-3">
                <div class="stat-card p-4">
                    <div class="card-icon bg-primary bg-opacity-10 text-primary">
                        <i class="bi bi-currency-dollar"></i>
                    </div>
                    <h6 class="card-title">Tổng doanh thu</h6>
                    <h3 id="revenue" class="card-value">145,280,000đ</h3>
                    <div class="trend up">
                        <i class="bi bi-arrow-up me-1"></i>
                        <span>13.8% so với tháng trước</span>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xl-3">
                <div class="stat-card p-4">
                    <div class="card-icon bg-success bg-opacity-10 text-success">
                        <i class="bi bi-cart-check"></i>
                    </div>
                    <h6 class="card-title">Tổng đơn hàng</h6>
                    <h3 class="card-value">458</h3>
                    <div class="trend up">
                        <i class="bi bi-arrow-up me-1"></i>
                        <span>8.2% so với tháng trước</span>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xl-3">
                <div class="stat-card p-4">
                    <div class="card-icon bg-info bg-opacity-10 text-info">
                        <i class="bi bi-people"></i>
                    </div>
                    <h6 class="card-title">Khách hàng mới</h6>
                    <h3 class="card-value">64</h3>
                    <div class="trend up">
                        <i class="bi bi-arrow-up me-1"></i>
                        <span>4.6% so với tháng trước</span>
                    </div>
                </div>
            </div>
            <div class="col-md-6 col-xl-3">
                <div class="stat-card p-4">
                    <div class="card-icon bg-warning bg-opacity-10 text-warning">
                        <i class="bi bi-eye"></i>
                    </div>
                    <h6 class="card-title">Lượt truy cập</h6>
                    <h3 class="card-value">12,648</h3>
                    <div class="trend down">
                        <i class="bi bi-arrow-down me-1"></i>
                        <span>2.3% so với tháng trước</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Charts Row -->
        <div class="row g-4 mb-4">
            <div class="col-xl-8">
                <div class="chart-card">
                    <div
                            class="d-flex justify-content-between align-items-center mb-4"
                    >
                        <h5 class="card-title m-0">Biểu đồ doanh thu</h5>
                        <div class="btn-group btn-group-sm">
                            <button class="btn btn-outline-secondary">Ngày</button>
                            <button class="btn btn-outline-secondary active">Tuần</button>
                            <button class="btn btn-outline-secondary">Tháng</button>
                        </div>
                    </div>
                    <div>
                        <canvas id="revenueChart" style="height: 300px"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-xl-4">
                <div class="chart-card">
                    <div
                            class="d-flex justify-content-between align-items-center mb-4"
                    >
                        <h5 class="card-title m-0">Phân bổ sản phẩm</h5>
                        <button class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-three-dots"></i>
                        </button>
                    </div>
                    <div>
                        <canvas id="productPieChart" style="height: 300px"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Recent Orders Table -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="chart-card">
                    <div
                            class="d-flex justify-content-between align-items-center mb-4"
                    >
                        <h5 class="card-title m-0">Đơn hàng gần đây</h5>
                        <a href="#" class="btn btn-sm btn-primary">Xem tất cả</a>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>Mã đơn</th>
                                <th>Khách hàng</th>
                                <th>Sản phẩm</th>
                                <th>Ngày đặt</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>#ORD-7829</td>
                                <td>Nguyễn Văn A</td>
                                <td>iPhone 13 Pro</td>
                                <td>12/03/2025</td>
                                <td>28,990,000đ</td>
                                <td><span class="badge bg-success">Hoàn thành</span></td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <button class="btn btn-outline-secondary">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        <button class="btn btn-outline-primary">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>#ORD-7830</td>
                                <td>Trần Thị B</td>
                                <td>Laptop Dell XPS</td>
                                <td>11/03/2025</td>
                                <td>32,490,000đ</td>
                                <td>
                        <span class="badge bg-warning text-dark"
                        >Đang giao</span
                        >
                                </td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <button class="btn btn-outline-secondary">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        <button class="btn btn-outline-primary">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>#ORD-7831</td>
                                <td>Lê Văn C</td>
                                <td>Samsung Galaxy S24</td>
                                <td>11/03/2025</td>
                                <td>24,990,000đ</td>
                                <td><span class="badge bg-info">Đang xử lý</span></td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <button class="btn btn-outline-secondary">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        <button class="btn btn-outline-primary">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>#ORD-7833</td>
                                <td>Hoàng Văn E</td>
                                <td>MacBook Air M3</td>
                                <td>09/03/2025</td>
                                <td>29,990,000đ</td>
                                <td><span class="badge bg-danger">Đã hủy</span></td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <button class="btn btn-outline-secondary">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        <button class="btn btn-outline-primary">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Top Products and Activity Row -->
        <div class="row g-4 mb-4">
            <div class="col-lg-6">
                <div class="chart-card">
                    <div
                            class="d-flex justify-content-between align-items-center mb-4"
                    >
                        <h5 class="card-title m-0">Top sản phẩm bán chạy</h5>
                        <a href="#" class="btn btn-sm btn-outline-primary"
                        >Xem chi tiết</a
                        >
                    </div>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Đã bán</th>
                                <th>Doanh thu</th>
                                <th>Tồn kho</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>iPhone 13 Pro</td>
                                <td>125</td>
                                <td>3,623,750,000đ</td>
                                <td>
                                    <div class="progress" style="height: 6px">
                                        <div
                                                class="progress-bar bg-success"
                                                role="progressbar"
                                                style="width: 45%"
                                                aria-valuenow="45"
                                                aria-valuemin="0"
                                                aria-valuemax="100"
                                        ></div>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="chart-card">
                    <div
                            class="d-flex justify-content-between align-items-center mb-4"
                    >
                        <h5 class="card-title m-0">Hoạt động gần đây</h5>
                        <button class="btn btn-sm btn-outline-secondary">
                            <i class="bi bi-arrow-repeat"></i> Làm mới
                        </button>
                    </div>
                    <div class="timeline">
                        <div class="timeline-item pb-3 mb-3 border-bottom">
                            <div class="d-flex">
                                <div class="me-3">
                      <span class="badge rounded-circle bg-success p-2">
                        <i class="bi bi-check-lg"></i>
                      </span>
                                </div>
                                <div>
                                    <h6 class="mb-1">Đơn hàng #ORD-7829 hoàn thành</h6>
                                    <p class="text-muted mb-0 small">
                                        Đơn hàng đã được giao thành công cho khách hàng
                                    </p>
                                    <small class="text-muted">15 phút trước</small>
                                </div>
                            </div>
                        </div>
                        <div class="timeline-item pb-3 mb-3 border-bottom">
                            <div class="d-flex">
                                <div class="me-3">
                      <span class="badge rounded-circle bg-primary p-2">
                        <i class="bi bi-person-plus"></i>
                      </span>
                                </div>
                                <div>
                                    <h6 class="mb-1">Khách hàng mới đăng ký</h6>
                                    <p class="text-muted mb-0 small">
                                        Nguyễn Văn F vừa tạo tài khoản mới
                                    </p>
                                    <small class="text-muted">42 phút trước</small>
                                </div>
                            </div>
                        </div>
                        <div class="timeline-item pb-3 mb-3 border-bottom">
                            <div class="d-flex">
                                <div class="me-3">
                      <span
                              class="badge rounded-circle bg-warning p-2 text-dark"
                      >
                        <i class="bi bi-box-seam"></i>
                      </span>
                                </div>
                                <div>
                                    <h6 class="mb-1">Cập nhật kho hàng</h6>
                                    <p class="text-muted mb-0 small">
                                        Đã nhập thêm 20 MacBook Air M3 vào kho
                                    </p>
                                    <small class="text-muted">1 giờ trước</small>
                                </div>
                            </div>
                        </div>
                        <div class="timeline-item pb-3 mb-3 border-bottom">
                            <div class="d-flex">
                                <div class="me-3">
                      <span class="badge rounded-circle bg-info p-2">
                        <i class="bi bi-cart-plus"></i>
                      </span>
                                </div>
                                <div>
                                    <h6 class="mb-1">Đơn hàng mới #ORD-7833</h6>
                                    <p class="text-muted mb-0 small">
                                        Khách hàng Hoàng Văn E đã đặt một đơn hàng mới
                                    </p>
                                    <small class="text-muted">2 giờ trước</small>
                                </div>
                            </div>
                        </div>
                        <div class="timeline-item">
                            <div class="d-flex">
                                <div class="me-3">
                      <span class="badge rounded-circle bg-danger p-2">
                        <i class="bi bi-exclamation-triangle"></i>
                      </span>
                                </div>
                                <div>
                                    <h6 class="mb-1">Cảnh báo hết hàng</h6>
                                    <p class="text-muted mb-0 small">
                                        iPad Pro 12.9" sắp hết hàng trong kho
                                    </p>
                                    <small class="text-muted">3 giờ trước</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <footer class="bg-white text-center py-3 mt-5 rounded">
            <p class="mb-0 text-muted">
                © 2025 Hệ Thống Quản Lý. Thiết kế bởi
                <a href="#" class="text-decoration-none">BookShopTeam</a>
            </p>
        </footer>
    </div>
</div>

<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/js/admin2/home.js"></script>
</body>
</html>

