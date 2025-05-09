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
    <script src="/bookshop/js/app/header.js" type="module"></script>
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

<!-- Add Address Modal -->
<div class="modal fade" id="addAddressModal" tabindex="-1" aria-labelledby="addAddressModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addAddressModalLabel">Thêm địa chỉ mới</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="addAddressForm">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="recipientName" class="form-label">Tên người nhận</label>
                            <input type="text" class="form-control" id="recipientName" placeholder="Nhập tên người nhận">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="phoneNumber" class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control" id="phoneNumber" placeholder="Nhập số điện thoại">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="addressType" class="form-label">Loại địa chỉ</label>
                        <select class="form-select" id="addressType">
                            <option value="HOME">Nhà riêng</option>
                            <option value="COMPANY">Công ty</option>
                        </select>
                    </div>
                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label for="province" class="form-label">Tỉnh/Thành phố</label>
                            <select class="form-select" id="province">
                                <option value="" selected disabled>Chọn Tỉnh/Thành phố</option>
                                <option value="79">TP Hồ Chí Minh</option>
                                <option value="01">Hà Nội</option>
                                <option value="48">Đà Nẵng</option>
                                <option value="92">Cần Thơ</option>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="district" class="form-label">Quận/Huyện</label>
                            <select class="form-select" id="district">
                                <option value="" selected disabled>Chọn Quận/Huyện</option>
                                <option value="760">Quận 1</option>
                                <option value="761">Quận 3</option>
                                <option value="764">Quận 7</option>
                                <option value="765">Quận 10</option>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="ward" class="form-label">Phường/Xã</label>
                            <select class="form-select" id="ward">
                                <option value="" selected disabled>Chọn Phường/Xã</option>
                                <option value="26734">Phường Tân Phong</option>
                                <option value="26737">Phường Tân Thuận Đông</option>
                                <option value="26740">Phường Bến Nghé</option>
                                <option value="26743">Phường Bến Thành</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="addressLine1" class="form-label">Địa chỉ</label>
                        <input type="text" class="form-control" id="addressLine1" placeholder="Số nhà, tên đường">
                    </div>
                    <div class="mb-3">
                        <label for="addressLine2" class="form-label">Địa chỉ phụ (tùy chọn)</label>
                        <input type="text" class="form-control" id="addressLine2" placeholder="Căn hộ, tòa nhà, ...">
                    </div>
                    <div class="mb-3">
                        <label for="notes" class="form-label">Ghi chú (tùy chọn)</label>
                        <textarea class="form-control" id="notes" rows="2" placeholder="Ghi chú thêm thông tin..."></textarea>
                    </div>
                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="setAsDefault">
                        <label class="form-check-label" for="setAsDefault">Đặt làm địa chỉ mặc định</label>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-primary">Lưu địa chỉ</button>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<footer class="modern-footer">
    <div class="container">
        <!-- Footer Top - Logo và Newsletter -->
        <div class="footer-top">
            <div class="row align-items-center">
                <div class="col-lg-4 col-md-6 mb-4 mb-md-0">
                    <div class="footer-brand d-flex align-items-center">
                        <div class="footer-logo-container me-3">
                            <img src="/asset/logo.jpg" alt="BookStore Logo" class="footer-logo">
                        </div>
                        <p class="mb-0 footer-description">BookStore - Nơi tri thức gặp gỡ đam mê đọc sách của bạn.
                            Cung cấp hàng nghìn đầu sách chất lượng với dịch vụ giao hàng nhanh chóng.</p>
                    </div>
                </div>
                <div class="col-lg-5 offset-lg-3 col-md-6">
                    <div class="newsletter-box">
                        <h5>Đăng ký nhận thông tin</h5>
                        <p>Nhận thông báo về sách mới và ưu đãi đặc biệt</p>
                        <div class="newsletter-form">
                            <div class="input-group">
                                <input type="email" class="form-control" placeholder="Email của bạn">
                                <button class="btn btn-primary" type="button">Đăng ký</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <hr class="footer-divider">

        <!-- Footer Main - Menu Links -->
        <div class="footer-main">
            <div class="row">
                <div class="col-lg-3 col-md-6 col-sm-6 mb-2 mb-lg-0">
                    <div class="footer-widget">
                        <h5 class="widget-title">Hỗ Trợ Khách Hàng</h5>
                        <ul class="footer-links">
                            <li><a href="#">Trung tâm trợ giúp</a></li>
                            <li><a href="#">Hướng dẫn mua hàng</a></li>
                            <li><a href="#">Phương thức vận chuyển</a></li>
                            <li><a href="#">Chính sách đổi trả</a></li>
                            <li><a href="#">Câu hỏi thường gặp</a></li>
                        </ul>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6 mb-2 mb-lg-0">
                    <div class="footer-widget">
                        <h5 class="widget-title">Về BookStore</h5>
                        <ul class="footer-links">
                            <li><a href="#">Giới thiệu</a></li>
                            <li><a href="#">Tuyển dụng</a></li>
                            <li><a href="#">Điều khoản sử dụng</a></li>
                            <li><a href="#">Chính sách bảo mật</a></li>
                            <li><a href="#">Liên hệ</a></li>
                        </ul>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6 mb-2 mb-lg-0">
                    <div class="footer-widget">
                        <h5 class="widget-title">Hợp tác & Liên kết</h5>
                        <ul class="footer-links">
                            <li><a href="#">Bán hàng cùng BookStore</a></li>
                            <li><a href="#">Đối tác giao hàng</a></li>
                            <li><a href="#">Đối tác thanh toán</a></li>
                            <li><a href="#">Chương trình affiliate</a></li>
                            <li><a href="#">Hợp tác xuất bản</a></li>
                        </ul>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6 mb-2 mb-lg-0">
                    <div class="footer-widget">
                        <h5 class="widget-title">Kết nối với chúng tôi</h5>
                        <div class="social-links">
                            <a href="#" class="social-link facebook"><i class="fab fa-facebook-f"></i></a>
                            <a href="#" class="social-link instagram"><i class="fab fa-instagram"></i></a>
                            <a href="#" class="social-link youtube"><i class="fab fa-youtube"></i></a>
                            <a href="#" class="social-link twitter"><i class="fab fa-twitter"></i></a>
                            <a href="#" class="social-link tiktok"><i class="fab fa-tiktok"></i></a>
                        </div>

                        <h5 class="widget-title mt-4">Phương thức thanh toán</h5>
                        <div class="payment-methods">
                            <div class="payment-icon visa"><i class="fab fa-cc-visa"></i></div>
                            <div class="payment-icon mastercard"><i class="fab fa-cc-mastercard"></i></div>
                            <div class="payment-icon paypal"><i class="fab fa-cc-paypal"></i></div>
                            <div class="payment-icon jcb"><i class="fab fa-cc-jcb"></i></div>
                            <div class="payment-icon cash"><i class="fas fa-money-bill-wave"></i></div>
                            <div class="payment-icon momo">MoMo</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <hr class="footer-divider">

        <!-- Footer Bottom - App Download & Copyright -->
        <div class="footer-bottom">
            <div class="row align-items-center">

                <div class="col-lg-6 col-md-6">
                    <div class="copyright">
                        <p>© 2025 BookStore. Tất cả các quyền được bảo lưu.</p>
                        <p class="address">Địa chỉ: 123 Đường Sách, Phường Văn Chương, Quận Đống Đa, Hà Nội</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>
<script src="/bookshop/js/app/address-info.js" type="module"></script>
</body>

</html>