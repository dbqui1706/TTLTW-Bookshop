<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/checkout-style.css">
    <link rel="stylesheet" href="/bookshop/css/cart-popup-modal.css">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content -->
<main class="container p-2">
    <div class="row no-gutters">

        <!-- Left Column - Product Items -->
        <div class="col-lg-9">
            <div class="checkout-container">
                <div class="checkout-header">
                    <h2>Thanh toán</h2>
                </div>

                <!-- Product Items -->
                <div class="product-section">
                    <div class="section-header">
                        <h3 class="section-title">Sản phẩm</h3>
                    </div>

                    <div class="product-list" id="product-list">
                    </div>
                </div>

                <!-- Hình thức giao hàng -->
                <div class="shipping-section">
                    <div class="section-header">
                        <h3 class="section-title">Chọn hình thức giao hàng</h3>
                    </div>

                    <div class="shipping-options" id="shipping-options"></div>
                </div>

                <!-- Hình thức thanh toán -->
                <div class="payment-section">
                    <div class="section-header">
                        <h3 class="section-title">Chọn hình thức thanh toán</h3>
                    </div>

                    <div class="payment-options" id="payment-options"></div>
                </div>
            </div>
        </div>

        <!-- Right Column - Checkout Summary -->
        <div class="col-lg-3">
            <div class="checkout-summary">

                <!-- Delivery Address -->
                <div class="delivery-address">
                    <div class="delivery-title">Giao tới</div>
                    <div class="delivery-content">
                        <div class="delivery-customer">
                            <span class="customer-name" id="customer-name">Quý Đặng</span>
                            <span class="customer-phone" id="customer-phone">0975688272</span>
                        </div>
                        <div class="delivery-email">
                            <p id="delivery-email">admin@gmail.com</p>
                        </div>
                        <div class="address-tag">Nhà</div>
                        <div class="delivery-address-text" id="delivery-address-text">
                            110/5 đông ánh, hạ nội, Phường Cửa Đông, Quận Hoàn Kiếm, Hà Nội
                        </div>
                        <div class="change-address">
                            <a href="#" id="change-address">Thay đổi</a>
                        </div>
                    </div>
                </div>

                <!-- Coupon -->
                <div class="checkout-coupon">
                    <div class="coupon-header">
                        <div class="tiki-promotion">Khuyến Mãi</div>
                        <div class="coupon-selection">Có thể chọn 1 <i class="fas fa-info-circle"></i></div>
                    </div>
                    <div class="coupon-options" id="coupon-options">
                        <!-- Coupon empty -->
                    </div>
                    <div class="more-coupons">
                        <i class="fas fa-ticket-alt"></i>
                        <span>Danh sách mã khuyến mãi</span>
                        <i class="fas fa-chevron-right d-flex justify-content-end px-3"></i>
                    </div>
                </div>

                <!-- Price Details -->
                <div class="checkout-summary-details">
                    <div class="summary-row">
                        <div class="summary-label">Tổng tiền hàng</div>
                        <div class="summary-value" id="total-price">680.000<sup>đ</sup></div>
                    </div>
                    <div class="summary-row discount">
                        <div class="summary-label">Giảm giá trực tiếp</div>
                        <div class="summary-value" id="discount-price">-381.000<sup>đ</sup></div>
                    </div>
                    <div class="summary-row discount">
                        <div class="summary-label">Mã khuyến mãi từ nhà bán</div>
                        <div class="summary-value" id="promotion-price">-25.000<sup>đ</sup></div>
                    </div>
                    <div class="summary-row discount">
                        <div class="summary-label">Phí vận chuyển</div>
                        <div class="summary-value" id="shipping-price">+32.200<sup>đ</sup></div>
                    </div>
                    <div class="summary-row total">
                        <div class="summary-label">Tổng tiền thanh toán</div>
                        <div class="summary-value" id="total-payment">274.000<sup>đ</sup></div>
                    </div>
                    <div class="summary-row saving">
                        <div class="summary-value" id="saving-price">Tiết kiệm 406.000<sup>đ</sup></div>
                    </div>
                    <div class="summary-note">(Đã bao gồm VAT nếu có)</div>
                </div>

                <!-- Checkout Button -->
                <button class="checkout-button" id="checkout-button">Đặt Hàng (1)</button>
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
<script src="/bookshop/js/app/checkout.js" type="module"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>
</body>

</html>