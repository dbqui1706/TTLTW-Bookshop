<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ hàng - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/cart.css">

</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content -->
<main class="container p-2">
    <div class="row no-gutters">
        <!-- Giỏ hàng trống -->
        <div class="col-lg-12 cart-col d-none">
            <div class="cart-empty">
                <i class="fas fa-shopping-cart"></i>
                <h2>Giỏ hàng của bạn đang trống</h2>
                <p>Thêm sản phẩm vào giỏ hàng để bắt đầu một đơn hàng</p>
            </div>
        </div>

        <!-- Left Column - Cart Items (Khi có sản phẩm trong giỏ hàng)-->
        <div class="col-lg-9 cart-col">
            <div class="cart-main-container">
                <div class="cart-header">
                    <h2>GIỎ HÀNG</h2>
                </div>
                <!-- Cart main -->
                <div class="cart-main" id="cart-main">
                    <!-- Cart header -->
                    <div class="cart-grid-header">
                        <div class="select-all">
                            <input type="checkbox" id="select-all-checkbox" checked>
                            <label for="select-all-checkbox" id="select-all-label">Tất cả (0 sản phẩm)</label>
                        </div>
                        <div class="grid-head-price">Đơn giá</div>
                        <div class="grid-head-quantity">Số lượng</div>
                        <div class="grid-head-total">Thành tiền</div>
                        <div class="grid-head-actions">
                            <i class="fas fa-trash-alt"></i>
                        </div>
                    </div>

                    <!-- Seller group -->
                    <div class="seller-group" id="seller-group">
                        <!-- Cart Item - Cấu trúc 4 cột với item-checkbox và item-content đã gộp -->
                    </div>

                    <!-- Promotion banner -->
                    <div class="promotion-banner">
                        <div class="promotion-icon">
                            <i class="fas fa-tag"></i>
                        </div>
                        <div class="promotion-text">
                            <div>Đã giảm 25k</div>
                            <div class="promotion-more">Mua thêm để giảm 40k cho đơn từ 399k <i
                                    class="fas fa-chevron-right"></i></div>
                        </div>
                    </div>

                    <!-- Freeship info -->
                    <div class="freeship-info">
                        <div class="freeship-icon">FREESHIP XTRA</div>
                        <div class="freeship-text">Freeship 15k đơn từ 45k, Freeship 70k đơn từ 100k</div>
                        <div class="freeship-info-icon">
                            <i class="fas fa-info-circle"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Right Column - Checkout Summary -->
        <div class="col-lg-3 cart-col">
            <div class="checkout-summary">
                <!-- Checkout summary details -->
                <div class="checkout-summary-details">
                    <div class="summary-row">
                        <div class="summary-label">Tổng tiền hàng</div>
                        <div class="summary-value" id="total-price"><sup>đ</sup></div>
                    </div>
                    <div class="summary-row discount">
                        <div class="summary-label">Giảm giá trực tiếp</div>
                        <div class="summary-value" id="discount-price"></div>
                    </div>
                    <div class="summary-row total">
                        <div class="summary-label">Tổng tiền thanh toán</div>
                        <div class="summary-value total-value" id="total-payment"></div>
                    </div>
                    <div class="summary-note">(Đã bao gồm VAT nếu có)</div>
                </div>
                <button class="checkout-button">Mua Hàng (0)</button>
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
<script src="/bookshop/js/app/cart.js" type="module"></script>
</body>

</html>