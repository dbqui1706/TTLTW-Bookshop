<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm sách</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/product.css">

    <!-- Swiper CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css"/>
    <script src="/bookshop/js/app/header.js"></script>
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<main>
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#">Trang chủ</a></li>
                <li class="breadcrumb-item active" id="breadcrumb-item-active"><a href="#"></a></li>
            </ol>
        </nav>

        <div class="row">
            <!-- Left column - Product Images -->
            <div class="col-md-7">

                <!-- Product Images -->
                <div class="product-container">
                    <div class="product-image">
                        <img src="/asset/images/image.png" alt="COMBO 4 - MINH TRIẾT PHƯƠNG ĐÔNG"
                             id="product-image">
                    </div>
                    <div class="thumbnail-container d-flex justify-content-center">
                        <div class="thumbnail active">
                            <img src="/asset/images/image.png" alt="Thumbnail 1">
                        </div>
                        <div class="thumbnail">
                            <img src="/asset/images/image.png" alt="Thumbnail 2">
                        </div>
                        <div class="thumbnail">
                            <img src="/asset/images/image.png" alt="Thumbnail 3">
                        </div>
                    </div>
                </div>

                <!-- Đặc điểm nổi bật -->
                <div class="product-container">
                    <h3 class="details-title">Đặc điểm nổi bật</h3>
                    <ul class="details-list">
                        <li class="details-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Bộ sách gồm 4 cuốn với nội dung đa dạng và phong phú.</span>
                        </li>
                        <li class="details-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Cuốn sách "Trí tuệ của người xưa" tập trung vào trí thức và mưu kế của người
                                    xưa.</span>
                        </li>
                        <li class="details-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Cuốn sách "Đạo lý người xưa" giới thiệu về đạo lý, luân thường đạo lý trong cuộc
                                    sống.</span>
                        </li>
                        <li class="details-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Cuốn sách "Hiểu người để dùng người" chia sẻ bí quyết hiểu và quản lý con
                                    người.</span>
                        </li>
                        <li class="details-item">
                            <i class="fas fa-check-circle"></i>
                            <span>Cuốn sách "Cổ học tinh hoa" tổng hợp tinh hoa tri thức cổ đại phương Đông.</span>
                        </li>
                    </ul>
                </div>

                <!-- Thông tin vận chuyển -->
                <div class="product-container">
                    <h3 class="details-title">Thông tin vận chuyển</h3>
                    <div class="delivery-info">
                        <div class="delivery-location">
                            <div class="delivery-icon">
                                <i class="fas fa-map-marker-alt"></i>
                            </div>
                            <div>
                                <div class="mb-2">Giao đến Q. Hoàn Kiếm, P. Hàng Trống, Hà Nội</div>
                                <a href="#" class="text-primary">Đổi</a>
                            </div>
                        </div>

                        <div class="delivery-service">
                            <div class="delivery-icon">
                                <i class="fas fa-truck"></i>
                            </div>
                            <div>
                                <div>Giao Thứ Tư</div>
                                <div>Trước 19h, 02/04: <span class="delivery-free">Miễn phí 58.800₫</span></div>
                            </div>
                        </div>

                        <div class="delivery-service">
                            <div class="delivery-icon">
                                <i class="fas fa-shipping-fast"></i>
                            </div>
                            <div>
                                <div class="badge bg-primary">FREESHIP XTRA</div>
                                <div>Freeship 15k đơn từ 45k, Freeship 70k đơn từ 100k</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Thông tin chi tiết -->
                <div class="product-container">
                    <h3 class="details-title">Thông tin chi tiết</h3>
                    <table class="product-details-table">
                        <tbody>
                        <tr>
                            <td class="detail-label">Tác giả</td>
                            <td class="detail-value" id="author">Nhiều tác giả</td>
                        </tr>
                        <tr>
                            <td class="detail-label">Nhà xuất bản</td>
                            <td class="detail-value" id="publisher">NXB Hồng Đức</td>
                        </tr>
                        <tr>
                            <td class="detail-label">Năm xuất bản</td>
                            <td class="detail-value" id="year">2022</td>
                        </tr>
                        <tr>
                            <td class="detail-label">Số trang</td>
                            <td class="detail-value" id="page">984 trang (4 cuốn)</td>
                        </tr>
                        <tr>
                            <td class="detail-label">SKU</td>
                            <td class="detail-value" id="sku">8935235238312</td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Khách hàng đánh giá -->
                <div class="product-container">
                    <h3 class="details-title">Khách hàng đánh giá</h3>
                    <div class="ratings-overview">
                        <div class="rating-summary">
                        </div>
                        <div class="rating-bars" id="rating-bars-container">
                        </div>
                    </div>

                    <!-- Review filters -->
                    <div class="review-filters">
                        <div class="filter-title">Lọc theo</div>
                        <div class="filter-options-simple">
                            <button class="filter-btn-simple active" value="newest">Mới nhất</button>
                            <button class="filter-btn-simple" value="5">5 sao</button>
                            <button class="filter-btn-simple" value="4">4 sao</button>
                            <button class="filter-btn-simple" value="3">3 sao</button>
                            <button class="filter-btn-simple" value="2">2 sao</button>
                            <button class="filter-btn-simple" value="1">1 sao</button>
                        </div>
                    </div>

                    <div class="review-list">
                        <!-- Review item -->

                        <!-- Load more button -->

                    </div>
                </div>
            </div>

            <!-- Right column - Product Info & Actions -->
            <div class="col-md-5">
                <div class="product-sidebar">
                    <div class="product-container">
                        <!-- Badges -->
                        <div class="product-badges">
                            <span class="badge-item badge-deal">TOP DEAL</span>
                            <span class="badge-item badge-return">30 NGÀY ĐỔI TRẢ</span>
                            <span class="badge-item badge-authentic">CHÍNH HÃNG</span>
                        </div>

                        <!-- Product Name -->
                        <h3 class="product-title" id="product-title">COMBO 4 - (TRÍ TUỆ CỦA NGƯỜI XƯA - ĐẠO LÝ NGƯỜI
                            XƯA - HIỂU NGƯỜI
                            để
                            DÙNG
                            NGƯỜI - CỖ HỌC TINH HOA) - MINH TRIẾT PHƯƠNG ĐÔNG</h3>

                        <!-- Rating -->
                        <div class="product-rating">
                            <span id="rating-average" class="rating-average">2</span>
                            <div class="rating-stars" id="rating-stars">
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star-half-alt"></i>
                            </div>
                            <span class="rating-count" id="rating-count">(255)</span>
                            <span class="sold-count" id="sold-count">| Đã bán 3k</span>
                        </div>

                        <!-- Price -->
                        <div class="product-price" id="product-price">
                            <span class="current-price" id="current-price">257.560₫</span>
                            <span class="original-price" id="original-price">582.000₫</span>
                            <span class="discount-percent" id="discount-percent">-56%</span>
                        </div>

                        <!-- Promotion -->
                        <div class="promotion-info">
                            <p style="font-size: 14px; font-weight: bold;">Giá sau áp dụng mã khuyến mãi</p>
                            <div class="promotion-item">
                                <div class="promotion-icon">
                                    <i class="fas fa-tag"></i>
                                </div>
                                <div>Giảm 25.000₫ từ mã khuyến mãi của nhà bán</div>
                            </div>
                            <div class="promotion-item">
                                <div class="promotion-icon">
                                    <i class="fas fa-tag"></i>
                                </div>
                                <div>Giảm 16.440₫ từ mã khuyến mãi của Tiki</div>
                            </div>
                        </div>

                        <!-- Quantity -->
                        <div class="product-quantity">
                            <span class="quantity-label">Số Lượng</span>
                            <div class="quantity-controls" id="quantity-controls">
                                <button class="quantity-btn" id="minus-btn">−</button>
                                <input type="text" class="quantity-input" id="quantity-input" value="1">
                                <button class="quantity-btn" id="plus-btn">+</button>
                            </div>
                        </div>

                        <!-- Total -->
                        <div class="product-total mb-4">
                            <h5>Tạm tính</h5>
                            <div class="current-price" id="total-price">257.560₫</div>
                        </div>

                        <!-- Actions -->
                        <div class="product-actions">
                            <button class="btn-buy-now" id="btn-buy-now">Mua ngay</button>
                            <button class="btn-add-cart" id="btn-add-cart">Thêm vào giỏ</button>
                            <button class="btn-installment" id="btn-wishlist">Yêu thích</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Sản phẩm đã xem -->
        <div class="mt-2">
            <div class="publisher-section mb-4">
                <div class="publisher-header">
                    <div>
                        <h3>Sản Phẩm Đã Xem</h3>
                    </div>
                </div>
                <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                    <div class="col">
                        <div class="book-card">
                            <img src="/asset/images/image.png" alt="Recently Viewed Book">
                            <div class="book-info">
                                <h5 class="book-title">Nexus - Lược Sử Của Những Mạng Lưới Thông Tin Từ Thời Cổ Đại
                                    Đến Nay</h5>
                                <div class="book-price">202.900đ <span class="discount-tag">-38%</span></div>
                                <div class="rating">★★★★★</div>
                                <div class="sold-count">Đã bán 1000+</div>
                            </div>
                        </div>
                    </div>
                    <div class="col">
                        <div class="book-card">
                            <img src="/asset/images/image.png" alt="Recently Viewed Book">
                            <div class="book-info">
                                <h5 class="book-title">Nexus - Lược Sử Của Những Mạng Lưới Thông Tin Từ Thời Cổ Đại
                                    Đến Nay</h5>
                                <div class="book-price">202.900đ <span class="discount-tag">-38%</span></div>
                                <div class="rating">★★★★★</div>
                                <div class="sold-count">Đã bán 1000+</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Khám phá thêm -->
        <div class="mt-2">
            <div class="publisher-section mb-4">
                <div class="publisher-header">
                    <div>
                        <h3>Khám phá thêm</h3>
                    </div>
                </div>

                <!-- Swiper container -->
                <div class="swiper discover-swiper">
                    <div class="swiper-wrapper" id="discover-swiper-wrapper"></div>
                    <!-- <div class="swiper-navigation">
                        <div class="swiper-button-prev discover-prev custom-nav-btn"></div>
                        <div class="swiper-button-next discover-next custom-nav-btn"></div>
                    </div> -->
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
<!-- Swiper JS -->
<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
<!-- Product JS -->
<script src="/bookshop/js/app/product.js" type="module"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>

</body>

</html>