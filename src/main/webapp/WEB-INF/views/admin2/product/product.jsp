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
    <!-- Thêm Select2 CSS và JS -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet"/>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/product/product-manager.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="${pageContext.request.contextPath}/common/admin2/sidebar.jsp">
        <jsp:param name="active" value="PRODUCT"/>
    </jsp:include>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <jsp:include page="${pageContext.request.contextPath}/common/admin2/navbar.jsp"/>

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
                                    <input
                                            type="text"
                                            class="form-control"
                                            id="searchProduct"
                                            placeholder="Tìm kiếm sản phẩm"
                                    />
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
                                <button
                                        id="importExcelBtn"
                                        class="btn btn-success ms-2"
                                        data-bs-toggle="tooltip"
                                        data-bs-placement="top"
                                        title="Thêm hàng loạt sản phẩm từ file Excel"
                                >
                                    <i class="bi bi-file-earmark-arrow-down"></i>
                                </button>
                                <button
                                        id="exportExcelBtn"
                                        class="btn btn-warning ms-2"
                                        data-bs-toggle="tooltip"
                                        data-bs-placement="top"
                                        title="Xuất file Excel danh sách sản phẩm"
                                >
                                    <i class="bi bi-file-earmark-arrow-up"></i>
                                </button>
                                <button
                                        id="exportPdfBtn"
                                        class="btn btn-danger ms-2"
                                        data-bs-toggle="tooltip"
                                        data-bs-placement="top"
                                        title="In file FDF danh sách sản phẩm"
                                >
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
                                <h3 class="mb-0" id="outOfStock">65</h3>
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
                        <div id="loadingOverlay"
                             class="position-absolute top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center"
                             style="z-index: 1000; background-color: rgba(255, 255, 255, 0.8); display: none;">
                            <div class="d-flex flex-column align-items-center">
                                <div class="spinner-border text-primary mb-2" role="status">
                                    <span class="visually-hidden">Loading...</span>
                                </div>
                                <div>Đang tải dữ liệu...</div>
                            </div>
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
<script src="<c:url value="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"/>"></script>

<!-- Froala JS -->
<link href="<c:url value="https://cdn.jsdelivr.net/npm/froala-editor@latest/css/froala_editor.pkgd.min.css"/>" rel="stylesheet"
      type="text/css"/>
<script type="text/javascript"
        src="<c:url value="https://cdn.jsdelivr.net/npm/froala-editor@latest/js/froala_editor.pkgd.min.js"/>"></script>

<!-- Custom JS -->
<script src="<c:url value="/js/admin2/sidebar.js"/>"></script>
<script src="<c:url value="/js/admin2/product/main.js"/>" type="module"></script>

<!-- Select2 JS CSS -->
<style>
    /* Ẩn container của Select2 để không thay đổi giao diện */
    .select2-container--default .select2-selection--single {
        height: 100%;
        border: 1px solid #ced4da; /* Giữ border giống Bootstrap */
        border-radius: 0.375rem; /* Border radius giống Bootstrap */
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
        color: #212529; /* Màu chữ giống Bootstrap */
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
</html>