<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bookstore Homepage</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/style.css">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content -->
<main>
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#">Trang chủ</a></li>
                <li class="breadcrumb-item active"><a href="#">Nhà Sách BookStore</a></li>
            </ol>
        </nav>

        <div class="row">
            <!-- Sidebar -->
            <div class="col-lg-3">
                <form id="filter-form" method="GET" enctype="multipart/form-data">
                    <div class="filter-sidebar">
                        <h3 class="filter-sidebar-title">Bộ lọc tìm kiếm</h3>

                        <!-- Thể loại sách -->
                        <div class="filter-group">
                            <!-- Tiêu đề với thuộc tính data-bs-toggle -->
                            <p class="filter-group-title" data-bs-toggle="collapse"
                               data-bs-target="#category-collapse" aria-expanded="true">
                                Thể loại sách
                                <i class="fas fa-chevron-down"></i>
                            </p>
                            <!-- Phần nội dung sử dụng collapse của Bootstrap -->
                            <div class="collapse show" id="category-collapse">
                                <div class="filter-options" id="category-container-filter">
                                </div>
                                <a href="#" class="filter-more">Xem thêm <i class="fas fa-chevron-down"></i></a>
                            </div>
                        </div>

                        <!-- Nhà xuất bản -->
                        <div class="filter-group">
                            <p class="filter-group-title" data-bs-toggle="collapse"
                               data-bs-target="#publisher-collapse" aria-expanded="true">
                                Nhà xuất bản
                                <i class="fas fa-chevron-down"></i>
                            </p>
                            <div class="collapse show" id="publisher-collapse">
                                <div class="filter-options" id="publisher-container-filter">
                                </div>
                                <a href="#" class="filter-more">Xem thêm <i class="fas fa-chevron-down"></i></a>
                            </div>

                        </div>

                        <!-- Khoảng giá -->
                        <div class="filter-group">
                            <p class="filter-group-title" data-bs-toggle="collapse"
                               data-bs-target="#price-range-collapse" aria-expanded="true">
                                Khoảng giá
                                <i class="fas fa-chevron-down"></i>
                            </p>
                            <div class="collapse show" id="price-range-collapse">
                                <div class="filter-options">
                                    <label class="filter-option">
                                        <input type="radio" name="price-range" value="0-50000"> Dưới 50.000₫
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="price-range" value="50000-100000"> 50.000₫ -
                                        100.000₫
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="price-range" value="100000-200000"> 100.000₫ -
                                        200.000₫
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="price-range" value="200000-300000"> 200.000₫ -
                                        300.000₫
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="price-range" value="300000-max"> Trên 300.000₫
                                    </label>
                                </div>
                                <div class="price-range-inputs">
                                    <div class="input-group">
                                        <input type="text" class="form-control price-input" placeholder="₫ Từ"
                                               id="priceFrom">
                                        <span class="price-separator">-</span>
                                        <input type="text" class="form-control price-input" placeholder="₫ Đến"
                                               id="priceTo">
                                        <button type="submit" class="price-apply-btn">Áp dụng</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Đánh giá -->
                        <div class="filter-group">
                            <p class="filter-group-title" data-bs-toggle="collapse"
                               data-bs-target="#rating-collapse" aria-expanded="true">
                                Đánh giá
                                <i class="fas fa-chevron-down"></i>
                            </p>
                            <div class="collapse show" id="rating-collapse">
                                <div class="filter-options mb-3">
                                    <label class="filter-option">
                                        <input type="radio" name="rating" value="5">
                                        <div class="stars">
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                        </div>
                                        <span class="rating-text">từ 5 sao</span>
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="rating" value="4">
                                        <div class="stars">
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="far fa-star"></i>
                                        </div>
                                        <span class="rating-text">từ 4 sao</span>
                                    </label>
                                    <label class="filter-option">
                                        <input type="radio" name="rating" value="3">
                                        <div class="stars">
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i>
                                            <i class="far fa-star"></i>
                                            <i class="far fa-star"></i>
                                        </div>
                                        <span class="rating-text">từ 3 sao</span>
                                    </label>
                                </div>
                            </div>
                        </div>

                        <!-- Dịch vụ & Khuyến mãi -->
                        <div class="filter-group">
                            <p class="filter-group-title" data-bs-toggle="collapse"
                               data-bs-target="#service-promotion-collapse" aria-expanded="true">
                                Dịch vụ & Khuyến mãi
                                <i class="fas fa-chevron-down"></i>
                            </p>
                            <div class="collapse show" id="service-promotion-collapse">
                                <div class="filter-options mb-3">
                                    <label class="filter-option">
                                        <input type="checkbox" name="service" value="freeship"> Miễn phí vận chuyển
                                    </label>
                                    <label class="filter-option">
                                        <input type="checkbox" name="service" value="sale"> Đang giảm giá
                                    </label>
                                    <label class="filter-option">
                                        <input type="checkbox" name="service" value="installment"> Trả góp 0%
                                    </label>
                                    <label class="filter-option">
                                        <input type="checkbox" name="service" value="promotion"> Khuyến mãi
                                    </label>
                                </div>
                            </div>
                        </div>
                        <!-- Nút áp dụng bộ lọc -->
                        <div class="filter-actions">
                            <button type="reset" class="btn-clear-filter" id="clearFilterBtn">Xóa tất cả</button>
                            <button type="submit" class="btn-apply-filter" id="applyFilterBtn">Áp dụng</button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Main Content Area -->
            <div class="col-lg-9">
                <div class="title-section mb-2">
                    <h1 class="section-title"><i class="fas fa-book me-2"></i>BookStore</h1>
                </div>

                <!-- Publisher Section 1 -->
                <div class="publisher-section mb-2">
                    <div class="publisher-header">
                        <div>
                            <h3>BookStore - Top sách bán chạy - Giảm 40%</h3>
                            <div>Tài trợ bởi BookStore <span class="text-warning">5/5 ★</span></div>
                        </div>
                        <img src="/api/placeholder/150x150" alt="Fahasa Logo" class="publisher-logo">
                    </div>
                    <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3" id="">
                        <div class="col">
                            <div class="book-card">
                                <img src="/asset/images/image.png" alt="Book 1">
                                <div class="book-info">
                                    <span class="sponsored-tag">Tài trợ</span>
                                    <div class="shipping-badge fast-shipping">Freeship</div>
                                    <div class="shipping-badge authentic-badge">Chính hãng</div>
                                    <h5 class="book-title">The Psychology of Money</h5>
                                    <div class="book-author">Morgan Housel</div>
                                    <div class="book-price">159.000đ <span class="discount-tag">-20%</span></div>
                                    <div class="rating">★★★★★</div>
                                    <div class="sold-count">Đã bán 1.2k</div>
                                </div>
                            </div>
                        </div>
                        <div class="col">
                            <div class="book-card">
                                <img src="/asset/images/image.png" alt="Book 2">
                                <div class="book-info">
                                    <div class="shipping-badge fast-shipping">Freeship</div>
                                    <div class="shipping-badge authentic-badge">Chính hãng</div>
                                    <h5 class="book-title">Sapiens: A Brief History of Humankind</h5>
                                    <div class="book-author">Yuval Noah Harari</div>
                                    <div class="book-price">249.000đ <span class="discount-tag">-15%</span></div>
                                    <div class="rating">★★★★★</div>
                                    <div class="sold-count">Đã bán 956</div>
                                </div>
                            </div>
                        </div>
                        <div class="col">
                            <div class="book-card">
                                <img src="" alt="Book 3">
                                <div class="book-info">
                                    <div class="shipping-badge fast-shipping">Freeship</div>
                                    <div class="shipping-badge authentic-badge">Chính hãng</div>
                                    <h5 class="book-title">Atomic Habits: An Easy & Proven Way</h5>
                                    <div class="book-author">James Clear</div>
                                    <div class="book-price">363.100đ <span class="discount-tag">-26%</span></div>
                                    <div class="rating">★★★★★</div>
                                    <div class="sold-count">Đã bán 2.3k</div>
                                </div>
                            </div>
                        </div>
                        <div class="col">
                            <div class="book-card">
                                <img src="" alt="Book 4">
                                <div class="book-info">
                                    <div class="shipping-badge fast-shipping">Freeship</div>
                                    <div class="shipping-badge authentic-badge">Chính hãng</div>
                                    <h5 class="book-title">Rich Dad Poor Dad</h5>
                                    <div class="book-author">Robert T. Kiyosaki</div>
                                    <div class="book-price">169.000đ <span class="discount-tag">-10%</span></div>
                                    <div class="rating">★★★★★</div>
                                    <div class="sold-count">Đã bán 1.8k</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Book Grid -->
                <div class="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3" id="productGrid">
                </div>

                <!-- View More Button -->
                <div class="text-center d-flex justify-content-center mt-4 mb-4">
                    <button class="btn btn-outline-primary" id="loadMoreBtn">Xem thêm</button>
                </div>

                <!-- Recently Viewed -->
                <div class="viewed-section mt-5">
                    <h2 class="section-title">Sản Phẩm Đã Xem</h2>
                    <div class="row row-cols-2 row-cols-md-4 g-3" id="recentlyViewed">
                        <div class="col">
                            <div class="book-card">
                                <img src="" alt="Recently Viewed Book">
                                <div class="book-info">
                                    <h5 class="book-title">Nexus - Lược Sử Của Những Mạng Lưới Thông Tin Từ Thời Cổ
                                        Đại
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

<!-- Help Widget -->
<div class="help-widget">
    <div class="help-button">
        <i class="fas fa-headset"></i>
    </div>
</div>

<!-- Bootstrap JS and Font Awesome -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<!-- JavaScript -->
<script src="<c:url value="/bookshop/js/app/home.js"/>" type="module"></script>
<script src="<c:url value="/bookshop/js/app/header.js"/>" type="module"></script>
</body>

</html>
