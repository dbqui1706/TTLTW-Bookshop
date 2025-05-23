<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/sidebar-account.css">
    <link rel="stylesheet" href="/bookshop/css/order-detail.css">
    <link rel="stylesheet" href="/bookshop/css/review-form.css">
    <link rel="stylesheet" href="/bookshop/js/app/header.js">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp"/>

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
                    <div class="order-detail-content" id="orderDetailContainer">
                        <!-- Loading indicator -->
                        <div id="orderLoadingIndicator" class="text-center py-5">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Đang tải...</span>
                            </div>
                            <p class="mt-2">Đang tải thông tin đơn hàng...</p>
                        </div>

                        <!-- Error message -->
                        <div id="orderLoadError" class="alert alert-danger my-3 d-none">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <span id="errorMessage">Không thể tải thông tin đơn hàng.</span>
                        </div>

                        <!-- Main order content (initially hidden) -->
                        <div id="orderContent" class="d-none">
                            <!-- Detail Order -->
                            <div class="order-header">
                                <div>
                                    <h1 class="order-title">Chi tiết đơn hàng</h1>
                                    <p class="order-subtitle">Mã đơn hàng: <span class="order-id"
                                                                                 id="displayOrderCode"></span></p>
                                    <p class="order-subtitle">Đặt ngày: <span id="displayOrderDate"></span></p>
                                </div>
                                <div class="text-end">
                                    <div class="order-status" id="displayOrderStatus"></div>
                                    <div class="order-actions">
                                        <button class="btn-cancel-order" id="btnCancelOrder">Hủy đơn hàng</button>
                                        <button class="btn-contact-support">Liên hệ hỗ trợ</button>
                                    </div>
                                </div>
                            </div>

                            <!-- Shipping Timeline -->
                            <div class="shipping-timeline">
                                <h2 class="section-title">Trạng thái đơn hàng</h2>
                                <div class="timeline" id="orderTimeline">
                                    <!-- Timeline items will be generated dynamically -->
                                </div>
                            </div>

                            <!-- Order Info Split -->
                            <div class="row">
                                <!-- Shipping Info -->
                                <div class="col-md-6">
                                    <h2 class="section-title">Thông tin giao hàng</h2>
                                    <div class="info-box shipping-info">
                                        <p><strong id="shippingName"></strong></p>
                                        <p id="shippingPhone"></p>
                                        <p id="shippingEmail"></p>
                                        <p id="shippingAddress"></p>
                                        <p><span class="info-label">Phương thức giao hàng:</span> <span
                                                id="shippingMethod"></span></p>
                                    </div>
                                </div>

                                <!-- Payment Info -->
                                <div class="col-md-6">
                                    <h2 class="section-title">Thông tin thanh toán</h2>
                                    <div class="info-box payment-info">
                                        <p><span class="info-label">Phương thức thanh toán:</span> <span
                                                id="paymentMethod"></span></p>
                                        <p><span class="info-label">Trạng thái thanh toán:</span> <span
                                                id="paymentStatus"></span></p>
                                        <p><span class="info-label">Mã giảm giá:</span> <span
                                                id="discountInfo"></span></p>
                                    </div>
                                </div>
                            </div>

                            <!-- Order Items -->
                            <h2 class="section-title">Sản phẩm đã đặt</h2>
                            <table class="items-table">
                                <thead>
                                <tr>
                                    <th style="width: 55%;">Sản phẩm</th>
                                    <th style="width: 15%;">Đơn giá</th>
                                    <th style="width: 15%;">Số lượng</th>
                                    <th style="width: 15%;">Thành tiền</th>
                                </tr>
                                </thead>
                                <tbody id="orderItemsContainer">
                                <!-- Order items will be generated dynamically -->
                                </tbody>
                            </table>

                            <!-- Order Summary -->
                            <div class="order-summary">
                                <div class="summary-row">
                                    <span class="summary-label">Tạm tính:</span>
                                    <span class="summary-value" id="summarySubtotal"></span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">Giảm giá:</span>
                                    <span class="summary-value" id="summaryDiscount"></span>
                                </div>
                                <div class="summary-row">
                                    <span class="summary-label">Phí vận chuyển:</span>
                                    <span class="summary-value" id="summaryShipping"></span>
                                </div>
                                <div class="total-row">
                                    <span class="total-label">Tổng cộng:</span>
                                    <span class="total-value" id="summaryTotal"></span>
                                </div>
                            </div>

                            <!-- Reorder Button -->
                            <!-- <div class="text-end mt-4">
                                <button class="btn-order-again" id="btnOrderAgain">Đặt hàng lại</button>
                            </div> -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Form đánh giá sản phẩm -->
    <div class="review-modal-overlay" id="reviewModalOverlay">
        <div class="review-modal">
            <div class="review-modal-header">
                <h3>Đánh giá sản phẩm</h3>
                <button class="review-close-btn" id="closeReviewModal">&times;</button>
            </div>
            <div class="review-modal-body">
                <div class="review-layout">
                    <!-- Cột trái: Thông tin sản phẩm và đánh giá chính -->
                    <div class="review-main-content">
                        <div class="review-product-info">
                            <img src="https://placehold.co/120x160" alt="Sản phẩm" class="review-product-image"
                                 id="reviewProductImage">
                            <div>
                                <h4 class="review-product-title" id="reviewProductTitle">Tên sản phẩm</h4>
                                <p class="review-product-author" id="reviewProductAuthor">Tác giả</p>
                                <!-- Rating stars inline -->
                                <div class="star-rating">
                                    <input type="radio" id="star5" name="rating" value="5"/><label for="star5"
                                                                                                   title="Rất tốt"></label>
                                    <input type="radio" id="star4" name="rating" value="4"/><label for="star4"
                                                                                                   title="Tốt"></label>
                                    <input type="radio" id="star3" name="rating" value="3"/><label for="star3"
                                                                                                   title="Bình thường"></label>
                                    <input type="radio" id="star2" name="rating" value="2"/><label for="star2"
                                                                                                   title="Không tốt"></label>
                                    <input type="radio" id="star1" name="rating" value="1"/><label for="star1"
                                                                                                   title="Rất tệ"></label>
                                </div>
                            </div>
                        </div>

                        <form id="reviewForm">
                            <div class="form-group">
                                <input type="text" id="reviewTitle" class="form-control"
                                       placeholder="Tiêu đề đánh giá">
                            </div>

                            <div class="form-group">
                                    <textarea id="reviewContent" class="form-control" rows="3"
                                              placeholder="Chia sẻ cảm nhận của bạn về sản phẩm này..."></textarea>
                            </div>

                            <div class="review-actions">
                                <button type="button" class="btn-cancel" id="cancelReview">Hủy</button>
                                <button type="submit" class="btn-submit" id="submitReview">Gửi đánh giá</button>
                            </div>
                        </form>
                    </div>

                    <!-- Cột phải: Tùy chọn và ảnh -->
                    <div class="review-options">
                        <div class="review-photos">
                            <p>Thêm hình ảnh:</p>
                            <div class="photo-upload-container">
                                <div class="photo-upload-btn">
                                    <i class="fas fa-camera"></i>
                                    <span>Tải ảnh</span>
                                    <input type="file" accept="image/*" multiple id="photoUpload"
                                           class="photo-input">
                                </div>
                                <div class="photo-preview-container" id="photoPreviewContainer">
                                    <!-- Previews will be added here -->
                                </div>
                            </div>
                        </div>

                        <div class="review-tags">
                            <p>Những gì bạn thích:</p>
                            <div class="tag-options">
                                <label class="tag-option">
                                    <input type="checkbox" name="tags" value="Nội dung hay">
                                    <span>Nội dung hay</span>
                                </label>
                                <label class="tag-option">
                                    <input type="checkbox" name="tags" value="Chất lượng in ấn tốt">
                                    <span>Chất lượng in tốt</span>
                                </label>
                                <label class="tag-option">
                                    <input type="checkbox" name="tags" value="Đóng gói đẹp">
                                    <span>Đóng gói đẹp</span>
                                </label>
                                <label class="tag-option">
                                    <input type="checkbox" name="tags" value="Giá trị tốt">
                                    <span>Giá trị tốt</span>
                                </label>
                                <label class="tag-option">
                                    <input type="checkbox" name="tags" value="Giao hàng nhanh">
                                    <span>Giao hàng nhanh</span>
                                </label>
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
<script src="/bookshop/js/app/order-detail.js" type="module"></script>
<script src="/bookshop/js/review-form.js"></script>
</body>

</html>