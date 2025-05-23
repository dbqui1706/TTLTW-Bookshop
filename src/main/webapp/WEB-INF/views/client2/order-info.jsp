<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đơn hàng của tôi - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/sidebar-account.css">
    <link rel="stylesheet" href="/bookshop/css/order-info.css">
    <script src="/bookshop/js/app/header.js" type="module"></script>
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<main>
    <div class="container py-4">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#">Trang chủ</a></li>
                <li class="breadcrumb-item active">Đơn hàng của tôi</li>
            </ol>
        </nav>

        <!-- Order Info Section -->
        <div class="account-section">
            <div class="row">
                <!-- Sidebar -->
                <jsp:include page="/common/client2/sidebar.jsp">
                    <jsp:param name="sidebar" value="ORDER"/>
                </jsp:include>

                <!-- Main Content -->
                <div class="col-lg-9 col-md-8">
                    <div class="order-content">
                        <h3 class="order-title">Đơn hàng của tôi</h3>
                        <div class="order-wrapper">
                            <!-- Order Tabs -->
                            <div class="order-tabs">
                                <ul class="nav-tabs">
                                    <li class="tab-item active" id="all-orders">
                                        <a href="#">Tất cả đơn</a>
                                    </li>
                                    <li class="tab-item" id="pending-orders">
                                        <a href="#">Chờ xác nhận</a>
                                    </li>
                                    <li class="tab-item" id="processing-orders">
                                        <a href="#">Đang xử lý</a>
                                    </li>
                                    <li class="tab-item" id="shipping-orders">
                                        <a href="#">Đang vận chuyển</a>
                                    </li>
                                    <li class="tab-item" id="delivered-orders">
                                        <a href="#">Đã giao</a>
                                    </li>
                                    <li class="tab-item" id="cancelled-orders">
                                        <a href="#">Đã hủy</a>
                                    </li>
                                </ul>
                            </div>

                            <!-- Order Search Bar -->
                            <div class="order-search">
                                <div class="search-input-wrapper">
                                    <i class="fas fa-search search-icon"></i>
                                    <input type="text" class="search-input" id="search-input"
                                           placeholder="Tìm đơn hàng theo Mã đơn hàng, Nhà bán hoặc Tên sản phẩm">
                                </div>
                                <button class="search-button" id="search-button">Tìm đơn hàng</button>
                            </div>

                            <!-- Empty Order List Message -->
                            <div class="empty-order" style="display: none;">
                                <div class="empty-order-image">
                                    <i class="fas fa-box-open"></i>
                                </div>
                                <p class="empty-order-text">Chưa có đơn hàng</p>
                            </div>

                            <!-- Example Order List (Initially Hidden) -->
                            <div class="order-list" id="order-list">
                                <!-- Order Item 1 -->
                                <div class="order-item" id="order-item-1">
                                    <div class="order-header">
                                        <div class="order-info">
                                            <span class="order-id">Đơn hàng: #2345678</span>
                                            <span class="order-date">Ngày đặt: 15/03/2025</span>
                                        </div>
                                        <div class="order-status">
                                            <span class="status-badge delivered">Đã giao</span>
                                        </div>
                                    </div>
                                    <!-- Product 1 -->
                                    <div class="order-body">
                                        <div class="product-info">
                                            <div class="product-image">
                                                <img src="/asset/images/image.png" alt="Sách">
                                            </div>
                                            <div class="product-details">
                                                <h4 class="product-title">Combo Sách Kỹ Năng Sống (Bộ 3 Cuốn)</h4>
                                                <p class="product-variant">Phiên bản: Bìa mềm</p>
                                                <p class="product-quantity">Số lượng: 1</p>
                                            </div>
                                        </div>
                                        <div class="order-price">
                                            <p class="price-label">Thành tiền:</p>
                                            <p class="price-value">359.000đ</p>
                                        </div>
                                    </div>
                                    <!-- Product 2 -->
                                    <div class="order-body">
                                        <div class="product-info">
                                            <div class="product-image">
                                                <img src="/asset/images/image.png" alt="Sách">
                                            </div>
                                            <div class="product-details">
                                                <h4 class="product-title">Combo Sách Kỹ Năng Sống (Bộ 3 Cuốn)</h4>
                                                <p class="product-variant">Phiên bản: Bìa mềm</p>
                                                <p class="product-quantity">Số lượng: 1</p>
                                            </div>
                                        </div>
                                        <div class="order-price">
                                            <p class="price-label">Thành tiền:</p>
                                            <p class="price-value">359.000đ</p>
                                        </div>
                                    </div>
                                    <!-- Span to show the number of products remaining -->
                                    <div class="remaining-products">
                                        <span>+2 sản phẩm khác</span>
                                    </div>

                                    <div class="order-footer">
                                        <button class="btn-detail">Xem chi tiết</button>
                                        <button class="btn-rebuy">Mua lại</button>
                                        <button class="btn-review">Đánh giá</button>
                                    </div>
                                </div>

                                <!-- Order Item 2 -->
                                <div class="order-item" id="order-item-2">
                                    <div class="order-header">
                                        <div class="order-info">
                                            <span class="order-id">Đơn hàng: #2335597</span>
                                            <span class="order-date">Ngày đặt: 27/02/2025</span>
                                        </div>
                                        <div class="order-status">
                                            <span class="status-badge cancelled">Đã hủy</span>
                                        </div>
                                    </div>
                                    <div class="order-body">
                                        <div class="product-info">
                                            <div class="product-image">
                                                <img src="/asset/images/image.png" alt="Sách">
                                            </div>
                                            <div class="product-details">
                                                <h4 class="product-title">Nghệ Thuật Tư Duy Chiến Lược - Lý Thuyết
                                                    Trò Chơi Trong Cuộc Sống</h4>
                                                <p class="product-variant">Phiên bản: Bìa cứng</p>
                                                <p class="product-quantity">Số lượng: 2</p>
                                            </div>
                                        </div>
                                        <div class="order-price">
                                            <p class="price-label">Thành tiền:</p>
                                            <p class="price-value">428.000đ</p>
                                        </div>
                                    </div>
                                    <div class="order-footer">
                                        <button class="btn-detail">Xem chi tiết</button>
                                        <button class="btn-rebuy">Mua lại</button>
                                    </div>
                                </div>

                                <!-- Order Item 3 -->
                                <div class="order-item" id="order-item-3">
                                    <div class="order-header">
                                        <div class="order-info">
                                            <span class="order-id">Đơn hàng: #2324456</span>
                                            <span class="order-date">Ngày đặt: 15/02/2025</span>
                                        </div>
                                        <div class="order-status">
                                            <span class="status-badge shipping">Đang vận chuyển</span>
                                        </div>
                                    </div>
                                    <div class="order-body">
                                        <div class="product-info">
                                            <div class="product-image">
                                                <img src="/asset/images/image.png" alt="Sách">
                                            </div>
                                            <div class="product-details">
                                                <h4 class="product-title">Thao Túng Tâm Lý - Nhận Diện & Chống Lại
                                                    Thủ Đoạn Thao Túng Tâm Lý</h4>
                                                <p class="product-variant">Phiên bản: Bìa mềm</p>
                                                <p class="product-quantity">Số lượng: 1</p>
                                            </div>
                                        </div>
                                        <div class="order-price">
                                            <p class="price-label">Thành tiền:</p>
                                            <p class="price-value">189.000đ</p>
                                        </div>
                                    </div>
                                    <div class="order-footer">
                                        <button class="btn-detail">Xem chi tiết</button>
                                        <button class="btn-track">Theo dõi đơn</button>
                                    </div>
                                </div>
                            </div>

                            <!-- Pagination -->
                            <div class="pagination justify-content-center">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination d-none" id="pagination">
                                    </ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

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
<script src="/bookshop/js/app/order-info.js" type="module"> </script>
</body>

</html>
