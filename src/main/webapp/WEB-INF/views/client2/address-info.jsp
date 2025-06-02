<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin tài khoản - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/sidebar-account.css">
    <link rel="stylesheet" href="/bookshop/css/address-info.css">
<%--    <script src="/bookshop/js/app/header.js" type="module"></script>--%>
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp"/>

<main>
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#">Trang chủ</a></li>
                <li class="breadcrumb-item active">Địa chỉ</li>
            </ol>
        </nav>

        <!-- Address Info Section -->
        <div class="address-section">
            <div class="row">
                <!-- Sidebar -->
                <jsp:include page="/common/client2/sidebar.jsp">
                    <jsp:param name="sidebar" value="ADDRESS"/>
                </jsp:include>

                <!-- Main Content -->
                <div class="col-lg-9 col-md-8">
                    <div class="address-content">
                        <h3 class="address-title">Thông tin địa chỉ</h3>

                        <div class="address-info-wrapper">
                            <div class="address-info-section">
                                <div class="row address-list" id="address-list">
                                    <!-- Address List -->
                                    <!-- Default Address Card -->
                                    <div class="col-md-6 mb-3">
                                        <div class="address-card default">
                                            <span class="default-badge">Mặc định</span>
                                            <div class="address-info">
                                                <p class="address-type">Nhà riêng</p>
                                                <p class="recipient-name">Quý Đặng</p>
                                                <p class="phone-number">0987 654 321</p>
                                                <p class="address-details">123 Đường Nguyễn Văn Linh, Phường Tân
                                                    Phong, Quận 7, TP Hồ Chí Minh</p>
                                            </div>
                                            <div class="address-actions">
                                                <button class="edit-btn" data-bs-toggle="modal"
                                                        data-bs-target="#editAddressModal">
                                                    <i class="fas fa-edit"></i> Sửa
                                                </button>
                                                <button class="delete-btn" data-bs-toggle="modal"
                                                        data-bs-target="#deleteAddressModal">
                                                    <i class="fas fa-trash-alt"></i> Xoá
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Additional Address Card -->
                                    <div class="col-md-6 mb-3">
                                        <div class="address-card">
                                            <div class="address-info">
                                                <p class="address-type">Công ty</p>
                                                <p class="recipient-name">Quý Đặng</p>
                                                <p class="phone-number">0978 123 456</p>
                                                <p class="address-details">456 Đường Lê Trọng Tấn, Phường Tây Thạnh,
                                                    Quận Tân Phú, TP Hồ Chí Minh</p>
                                            </div>
                                            <div class="address-actions">
                                                <button class="edit-btn" data-bs-toggle="modal"
                                                        data-bs-target="#editAddressModal">
                                                    <i class="fas fa-edit"></i> Sửa
                                                </button>
                                                <button class="delete-btn" data-bs-toggle="modal"
                                                        data-bs-target="#deleteAddressModal">
                                                    <i class="fas fa-trash-alt"></i> Xoá
                                                </button>
                                                <button class="set-default-btn">
                                                    <i class="fas fa-check-circle"></i> Đặt làm mặc định
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Additional Address Card -->
                                    <div class="col-md-6 mb-3">
                                        <div class="address-card">
                                            <div class="address-info">
                                                <p class="address-type">Nhà riêng</p>
                                                <p class="recipient-name">Quý Đặng</p>
                                                <p class="phone-number">0919 876 543</p>
                                                <p class="address-details">789 Đường Trần Hưng Đạo, Phường Cầu Kho,
                                                    Quận 1, TP Hồ Chí Minh</p>
                                            </div>
                                            <div class="address-actions">
                                                <button class="edit-btn" data-bs-toggle="modal"
                                                        data-bs-target="#editAddressModal">
                                                    <i class="fas fa-edit"></i> Sửa
                                                </button>
                                                <button class="delete-btn" data-bs-toggle="modal"
                                                        data-bs-target="#deleteAddressModal">
                                                    <i class="fas fa-trash-alt"></i> Xoá
                                                </button>
                                                <button class="set-default-btn">
                                                    <i class="fas fa-check-circle"></i> Đặt làm mặc định
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Add New Address Card -->
                                    <div class="col-md-6 mb-3">
                                        <div class="add-address-card" data-bs-toggle="modal"
                                             data-bs-target="#addAddressModal">
                                            <div class="add-icon">
                                                <i class="fas fa-plus"></i>
                                            </div>
                                            <p class="add-address-text">Thêm địa chỉ mới</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>


<!-- Footer -->
<jsp:include page="/common/client2/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>
<script src="<c:url value="/bookshop/js/app/address-info.js"/>" type="module"></script>
</body>

</html>