<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản Trị Hệ Thống</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"/>

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/product/product-manager.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="${pageContext.request.contextPath}/common/admin2/sidebar.jsp">
        <jsp:param name="active" value="CATEGORY"/>
    </jsp:include>
    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <nav class="navbar navbar-expand-lg mb-4">
            <div class="container-fluid">
                <button type="button" id="sidebarCollapse" class="btn btn-primary">
                    <i class="bi bi-list"></i>
                </button>
                <button class="btn btn-dark d-inline-block d-lg-none ms-auto" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
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
                            <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <img src="https://via.placeholder.com/30" class="rounded-circle me-2" alt="Admin" />
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
                                    <hr class="dropdown-divider" />
                                </li>
                                <li>
                                    <a class="dropdown-item" href="#"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a>
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
                            <h2>Quản lý thể loại</h2>
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
                            <div class="col-lg-4 col-md-6">
                                <div class="form-floating">
                                    <input type="text" class="form-control" id="searchCategory" placeholder="Tìm kiếm thể loại" />
                                    <label for="searchCategory">Tìm kiếm thể loại</label>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="statusFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="active">Đang hoạt động</option>
                                        <option value="inactive">Đã tạm khóa</option>
                                    </select>
                                    <label for="statusFilter">Trạng thái</label>
                                </div>
                            </div>
                            <div class="col-lg-5 col-md-12 text-end">
                                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
                                    <i class="bi bi-plus-lg"></i> Thêm thể loại mới
                                </button>
                                <button class="btn btn-success ms-2" data-bs-toggle="tooltip" data-bs-placement="top" title="Xuất file Excel danh sách thể loại">
                                    <i class="bi bi-file-earmark-arrow-up"></i> Export Excel
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stats Row -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-primary bg-opacity-10 p-3 rounded text-primary">
                                    <i class="bi bi-tags fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Tổng thể loại</h6>
                                <h3 class="mb-0">24</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-success bg-opacity-10 p-3 rounded text-success">
                                    <i class="bi bi-check-circle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Thể loại hoạt động</h6>
                                <h3 class="mb-0">22</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <div class="flex-shrink-0">
                                <div class="stat-icon bg-danger bg-opacity-10 p-3 rounded text-danger">
                                    <i class="bi bi-x-circle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Thể loại tạm khóa</h6>
                                <h3 class="mb-0">2</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Categories Table -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                <tr>
                                    <th width="60">ID</th>
                                    <th>Tên thể loại</th>
                                    <th>Slug</th>
                                    <th>Số sách</th>
                                    <th>Ngày tạo</th>
                                    <th>Cập nhật cuối</th>
                                    <th>Trạng thái</th>
                                    <th width="150">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Category Row 1 -->
                                <tr>
                                    <td>#TL001</td>
                                    <td>Văn học</td>
                                    <td>van-hoc</td>
                                    <td>142</td>
                                    <td>20/02/2025</td>
                                    <td>12/03/2025</td>
                                    <td><span class="badge bg-success">Hoạt động</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="tooltip" data-bs-placement="top" title="Tạm khóa">
                                                <i class="bi bi-lock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 2 -->
                                <tr>
                                    <td>#TL002</td>
                                    <td>Kinh tế</td>
                                    <td>kinh-te</td>
                                    <td>95</td>
                                    <td>20/02/2025</td>
                                    <td>10/03/2025</td>
                                    <td><span class="badge bg-success">Hoạt động</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="tooltip" data-bs-placement="top" title="Tạm khóa">
                                                <i class="bi bi-lock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 3 -->
                                <tr>
                                    <td>#TL003</td>
                                    <td>Tâm lý - Kỹ năng sống</td>
                                    <td>tam-ly-ky-nang-song</td>
                                    <td>78</td>
                                    <td>21/02/2025</td>
                                    <td>05/03/2025</td>
                                    <td><span class="badge bg-success">Hoạt động</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="tooltip" data-bs-placement="top" title="Tạm khóa">
                                                <i class="bi bi-lock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 4 -->
                                <tr>
                                    <td>#TL004</td>
                                    <td>Thiếu nhi</td>
                                    <td>thieu-nhi</td>
                                    <td>112</td>
                                    <td>21/02/2025</td>
                                    <td>08/03/2025</td>
                                    <td><span class="badge bg-success">Hoạt động</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="tooltip" data-bs-placement="top" title="Tạm khóa">
                                                <i class="bi bi-lock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 5 -->
                                <tr>
                                    <td>#TL005</td>
                                    <td>Tiểu sử - Hồi ký</td>
                                    <td>tieu-su-hoi-ky</td>
                                    <td>45</td>
                                    <td>22/02/2025</td>
                                    <td>01/03/2025</td>
                                    <td><span class="badge bg-success">Hoạt động</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="tooltip" data-bs-placement="top" title="Tạm khóa">
                                                <i class="bi bi-lock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 6 (Inactive) -->
                                <tr class="table-danger bg-opacity-25">
                                    <td>#TL006</td>
                                    <td>Comics - Manga (18+)</td>
                                    <td>comics-manga-18-plus</td>
                                    <td>37</td>
                                    <td>22/02/2025</td>
                                    <td>11/03/2025</td>
                                    <td><span class="badge bg-danger">Tạm khóa</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-success" data-bs-toggle="tooltip" data-bs-placement="top" title="Mở khóa">
                                                <i class="bi bi-unlock"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Category Row 7 (Inactive) -->
                                <tr class="table-danger bg-opacity-25">
                                    <td>#TL007</td>
                                    <td>Văn học người lớn</td>
                                    <td>van-hoc-nguoi-lon</td>
                                    <td>21</td>
                                    <td>23/02/2025</td>
                                    <td>09/03/2025</td>
                                    <td><span class="badge bg-danger">Tạm khóa</span></td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-primary" data-bs-toggle="tooltip" data-bs-placement="top" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-outline-success" data-bs-toggle="tooltip" data-bs-placement="top" title="Mở khóa">
                                                <i class="bi bi-unlock"></i>
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
        </div>

        <!-- Pagination -->
        <div class="row">
            <div class="col-md-6 mb-3">
                <div class="d-flex align-items-center">
                    <span class="me-2">Hiển thị</span>
                    <select class="form-select form-select-sm w-auto">
                        <option value="10">10</option>
                        <option value="25" selected>25</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                    </select>
                    <span class="ms-2">thể loại trên mỗi trang</span>
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
                        <li class="page-item">
                            <a class="page-link" href="#" aria-label="Next">
                                <span aria-hidden="true">&raquo;</span>
                            </a>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>

        <!-- Modal Thêm Thể Loại -->
        <div class="modal fade" id="addCategoryModal" tabindex="-1" aria-labelledby="addCategoryModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="addCategoryModalLabel">
                            Thêm thể loại mới
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addCategoryForm">
                            <div class="mb-3">
                                <label for="categoryName" class="form-label">Tên thể loại <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="categoryName" required />
                                <div class="form-text">
                                    Tên thể loại sẽ hiển thị cho người dùng.
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="categorySlug" class="form-label">Slug <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="categorySlug" required />
                                <div class="form-text">
                                    Slug sẽ được sử dụng trong URL (ví dụ: van-hoc,
                                    tam-ly-song).
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="categoryDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="categoryDescription" rows="3"></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label d-block">Trạng thái</label>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="categoryStatus" id="categoryStatusActive" value="active" checked />
                                    <label class="form-check-label" for="categoryStatusActive">Hoạt động</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="categoryStatus" id="categoryStatusInactive" value="inactive" />
                                    <label class="form-check-label" for="categoryStatusInactive">Tạm khóa</label>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="categoryParent" class="form-label">Thể loại cha</label>
                                <select class="form-select" id="categoryParent">
                                    <option value="0" selected>Không có</option>
                                    <option value="1">Văn học</option>
                                    <option value="2">Kinh tế</option>
                                    <option value="3">Tâm lý - Kỹ năng sống</option>
                                    <option value="4">Thiếu nhi</option>
                                    <option value="5">Tiểu sử - Hồi ký</option>
                                </select>
                                <div class="form-text">
                                    Chọn thể loại cha nếu đây là thể loại con.
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="categoryOrder" class="form-label">Thứ tự hiển thị</label>
                                <input type="number" class="form-control" id="categoryOrder" min="0" value="0" />
                                <div class="form-text">
                                    Thể loại có thứ tự cao hơn sẽ hiển thị trước.
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="saveCategoryBtn">
                            <i class="bi bi-save me-1"></i> Lưu thể loại
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Chỉnh Sửa Thể Loại -->
        <div class="modal fade" id="editCategoryModal" tabindex="-1" aria-labelledby="editCategoryModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="editCategoryModalLabel">
                            Chỉnh sửa thể loại
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editCategoryForm">
                            <input type="hidden" id="editCategoryId" value="" />
                            <div class="mb-3">
                                <label for="editCategoryName" class="form-label">Tên thể loại <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editCategoryName" required />
                                <div class="form-text">
                                    Tên thể loại sẽ hiển thị cho người dùng.
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editCategorySlug" class="form-label">Slug <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="editCategorySlug" required />
                                <div class="form-text">
                                    Slug sẽ được sử dụng trong URL (ví dụ: van-hoc,
                                    tam-ly-song).
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editCategoryDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="editCategoryDescription" rows="3"></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label d-block">Trạng thái</label>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="editCategoryStatus" id="editCategoryStatusActive" value="active" />
                                    <label class="form-check-label" for="editCategoryStatusActive">Hoạt động</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="editCategoryStatus" id="editCategoryStatusInactive" value="inactive" />
                                    <label class="form-check-label" for="editCategoryStatusInactive">Tạm khóa</label>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editCategoryParent" class="form-label">Thể loại cha</label>
                                <select class="form-select" id="editCategoryParent">
                                    <option value="0" selected>Không có</option>
                                    <option value="1">Văn học</option>
                                    <option value="2">Kinh tế</option>
                                    <option value="3">Tâm lý - Kỹ năng sống</option>
                                    <option value="4">Thiếu nhi</option>
                                    <option value="5">Tiểu sử - Hồi ký</option>
                                </select>
                                <div class="form-text">
                                    Chọn thể loại cha nếu đây là thể loại con.
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="editCategoryOrder" class="form-label">Thứ tự hiển thị</label>
                                <input type="number" class="form-control" id="editCategoryOrder" min="0" />
                                <div class="form-text">
                                    Thể loại có thứ tự cao hơn sẽ hiển thị trước.
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="updateCategoryBtn">
                            <i class="bi bi-save me-1"></i> Cập nhật
                        </button>
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
<script src="app.js"></script>
<script src="category.js"></script>
<!-- Script khởi tạo tooltip -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Khởi tạo tất cả tooltip trong trang
        var tooltipTriggerList = [].slice.call(
            document.querySelectorAll('[data-bs-toggle="tooltip"]')
        );
        var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });

        // Xử lý sinh tự động slug từ tên thể loại
        const categoryNameInput = document.getElementById("categoryName");
        const categorySlugInput = document.getElementById("categorySlug");

        if (categoryNameInput && categorySlugInput) {
            categoryNameInput.addEventListener("keyup", function() {
                // Chuyển đổi thành slug (loại bỏ dấu, chuyển khoảng trắng thành dấu gạch ngang)
                let slug = this.value
                    .toLowerCase()
                    .normalize("NFD")
                    .replace(/[\u0300-\u036f]/g, "") // Loại bỏ dấu tiếng Việt
                    .replace(/[đĐ]/g, "d")
                    .replace(/[^a-z0-9\s-]/g, "") // Loại bỏ ký tự đặc biệt
                    .replace(/[\s-]+/g, "-") // Thay khoảng trắng và nhiều dấu gạch ngang thành một dấu gạch ngang
                    .trim();

                categorySlugInput.value = slug;
            });
        }

        // Xử lý nút lưu thể loại
        const saveCategoryBtn = document.getElementById("saveCategoryBtn");
        const addCategoryForm = document.getElementById("addCategoryForm");

        if (saveCategoryBtn && addCategoryForm) {
            saveCategoryBtn.addEventListener("click", function() {
                if (addCategoryForm.checkValidity()) {
                    // Giả lập việc lưu thành công
                    alert("Thể loại đã được thêm thành công!");

                    // Đóng modal
                    const modal = bootstrap.Modal.getInstance(
                        document.getElementById("addCategoryModal")
                    );
                    modal.hide();

                    // Reset form
                    addCategoryForm.reset();
                } else {
                    addCategoryForm.reportValidity();
                }
            });
        }

        // Xử lý nút chỉnh sửa
        const editButtons = document.querySelectorAll(".btn-outline-primary");

        editButtons.forEach((button) => {
            button.addEventListener("click", function() {
                // Lấy thông tin từ hàng được chọn
                const row = this.closest("tr");
                const id = row.cells[0].textContent;
                const name = row.cells[1].textContent;
                const slug = row.cells[2].textContent;
                const status =
                    row.cells[6].querySelector(".badge").textContent === "Hoạt động" ?
                        "active" :
                        "inactive";

                // Điền thông tin vào form chỉnh sửa
                document.getElementById("editCategoryId").value = id;
                document.getElementById("editCategoryName").value = name;
                document.getElementById("editCategorySlug").value = slug;

                if (status === "active") {
                    document.getElementById(
                        "editCategoryStatusActive"
                    ).checked = true;
                } else {
                    document.getElementById(
                        "editCategoryStatusInactive"
                    ).checked = true;
                }

                document.getElementById("editCategoryOrder").value = 0; // Giả định

                // Mở modal chỉnh sửa
                const editModal = new bootstrap.Modal(
                    document.getElementById("editCategoryModal")
                );
                editModal.show();
            });
        });

        // Xử lý nút cập nhật thể loại
        const updateCategoryBtn = document.getElementById("updateCategoryBtn");

        if (updateCategoryBtn) {
            updateCategoryBtn.addEventListener("click", function() {
                // Giả lập việc cập nhật thành công
                alert("Thể loại đã được cập nhật thành công!");

                // Đóng modal
                const modal = bootstrap.Modal.getInstance(
                    document.getElementById("editCategoryModal")
                );
                modal.hide();
            });
        }

        // Xử lý nút khóa/mở khóa
        const lockButtons = document.querySelectorAll(".btn-outline-danger");
        const unlockButtons = document.querySelectorAll(".btn-outline-success");

        lockButtons.forEach((button) => {
            button.addEventListener("click", function() {
                if (confirm("Bạn có chắc chắn muốn khóa thể loại này?")) {
                    alert("Đã khóa thể loại thành công!");
                }
            });
        });

        unlockButtons.forEach((button) => {
            button.addEventListener("click", function() {
                if (confirm("Bạn có chắc chắn muốn mở khóa thể loại này?")) {
                    alert("Đã mở khóa thể loại thành công!");
                }
            });
        });
    });
</script>
</body>
</html>