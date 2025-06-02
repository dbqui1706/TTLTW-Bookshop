<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 10:59 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Tạo Phiếu Nhập/Xuất Kho - Hệ Thống Quản Lý</title>

    <jsp:include page="/common/admin2/head-meta.jsp"/>
    <link rel="stylesheet" href="/app/css/inventory/inventory.css"/>
    <style>
        .tab-container {
            background-color: #f8f9fa;
            border-radius: 0.5rem;
            padding: 15px;
            margin-bottom: 20px;
        }

        .action-buttons {
            white-space: nowrap;
        }

        .table-container {
            background-color: #fff;
            border-radius: 0.5rem;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }

        .select2-container {
            width: 100% !important;
        }

        .form-section {
            display: none;
        }

        .form-section.active {
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
                            <h2 id="page-title">Tạo phiếu nhập/xuất kho</h2>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <div class="btn-group">
                                <a class="btn btn-light" href="/admin/inventory-manager/management">
                                    <i class="bi bi-arrow-left"></i> Quay lại danh sách
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Form Type Selection -->
        <div class="row mb-3">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="formType" id="importType"
                                       value="import" checked>
                                <label class="form-check-label" for="importType">
                                    <i class="bi bi-box-arrow-in-down"></i> Phiếu nhập kho
                                </label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="formType" id="exportType"
                                       value="export">
                                <label class="form-check-label" for="exportType">
                                    <i class="bi bi-box-arrow-up"></i> Phiếu xuất kho
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Form Container -->
        <div class="row">
            <div class="col-12">
                <div class="card mb-4">
                    <div class="card-body">
                        <!-- Form -->
                        <div id="importForm" class="form-section active">
                            <form>
                                <h5 class="mb-3" id="table-tile">Danh sách Nhập kho</h5>
                                <div class="table-responsive mb-3">
                                    <table class="table table-bordered" id="productTable">
                                        <thead class="table-light">
                                        <tr>
                                            <th style="width: 30%">Sản phẩm</th>
                                            <th style="width: 15%">Tồn kho</th>
                                            <th style="width: 15%">Số lượng</th>
                                            <th style="width: 20%">Giá nhập</th>
                                            <th style="width: 10%">Thành tiền</th>
                                            <th style="width: 5%"></th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        </tbody>
                                        <tfoot>
                                        <tr>
                                            <td colspan="5">
                                                <button type="button" class="btn btn-primary"
                                                        data-bs-toggle="modal" data-bs-target="#productSearchModal">
                                                    Tìm kiếm sản phẩm
                                                </button>
                                            </td>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>

                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-floating mb-3">
                                                <textarea class="form-control" id="importNotes"
                                                          style="height: 100px"></textarea>
                                            <label for="importNotes">Ghi chú</label>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="card">
                                            <div class="card-body">
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span>Tổng số lượng:</span>
                                                    <span id="totalQuantity">1</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span>Số lượng sản phẩm:</span>
                                                    <span id="totalProducts">1</span>
                                                </div>
                                                <div class="d-flex justify-content-between">
                                                    <h5 class="fw-bold">Tổng giá trị:</h5>
                                                    <h5 class="fw-bold text-primary" id="totalAmount">120,000 ₫</h5>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="row mt-4">
                                    <div class="col-12 text-end">
                                        <a href="inventory-management.html" class="btn btn-secondary">Hủy</a>
                                        <button type="button" class="btn btn-primary" id="saveBtn">Lưu phiếu Nhập kho</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Giao diện phần modal tìm kiếm -->
        <div class="modal fade" id="productSearchModal" tabindex="-1" aria-labelledby="productSearchModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="productSearchModalLabel">
                            <i class="bi bi-search me-2"></i>Tìm kiếm sản phẩm nâng cao
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <!-- Phần tìm kiếm và bộ lọc -->
                        <div class="search-header pb-2">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                                        <input type="text" class="form-control" id="searchKeyword"
                                               placeholder="Tìm theo mã, tên sản phẩm...">
                                        <button class="btn btn-primary" type="button" id="searchButton">Tìm</button>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <select class="form-select" id="stockFilter">
                                        <option value="">-- Trạng thái tồn kho --</option>
                                        <option value="high">Tồn nhiều (>100)</option>
                                        <option value="medium">Tồn vừa (51-100)</option>
                                        <option value="low">Tồn ít (<=50)</option>
                                        <option value="out">Hết hàng</option>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <button class="btn btn-outline-secondary w-100" id="resetFilters">
                                        <i class="bi bi-x-circle me-1"></i>Xóa lọc
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Bảng kết quả tìm kiếm -->
                        <div class="table-container p-3">
                            <table class="table table-hover align-middle" id="table-products">
                                <thead class="table-light sticky-top">
                                <tr>
                                    <th scope="col">Mã SP</th>
                                    <th scope="col">Tên sản phẩm</th>
                                    <th scope="col">Danh mục</th>
                                    <th scope="col">Đơn giá</th>
                                    <th scope="col">Tồn kho</th>
                                    <th scope="col">Đặt trước</th>
                                    <th scope="col">Trạng thái</th>
                                    <th scope="col">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
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
<script src="/app/main/inventory-receipts.js" type="module"></script>
</body>

</html>