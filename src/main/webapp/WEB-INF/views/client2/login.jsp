<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/login.css">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content -->
<main>
    <div class="login-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-10 col-lg-8">
                    <div class="login-wrapper">
                        <div class="row g-0">
                            <!-- Phần hình ảnh bên trái -->
                            <div class="col-md-5 login-image-container d-none d-md-block">
                                <div class="login-image">
                                    <div class="overlay"></div>
                                    <div class="login-image-content">
                                        <h3 id="sidebar-title">Chào mừng trở lại</h3>
                                        <p id="sidebar-description">Khám phá thế giới sách cùng BookStore</p>
                                    </div>
                                </div>
                            </div>

                            <!-- Phần form -->
                            <div class="col-md-7 login-form-container">
                                <!-- FORM ĐĂNG NHẬP -->
                                <div class="login-form-content" id="login-form">
                                    <div class="text-center mb-4">
                                        <h2 class="login-title">Đăng nhập</h2>
                                        <p class="login-subtitle">Vui lòng đăng nhập để tiếp tục</p>
                                    </div>

                                    <!-- Form đăng nhập -->
                                    <form id="loginForm">
                                        <div class="mb-3">
                                            <label for="login-email" class="form-label">Email hoặc Số điện
                                                thoại</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-user"></i></span>
                                                <input type="text" class="form-control" id="login-email"
                                                       placeholder="Nhập email hoặc số điện thoại">
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <div class="d-flex justify-content-between">
                                                <label for="login-password" class="form-label">Mật khẩu</label>
                                                <a href="#" class="forgot-password" id="show-forgot-password">Quên
                                                    mật khẩu?</a>
                                            </div>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="login-password"
                                                       placeholder="Nhập mật khẩu">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                        </div>

                                        <div class="mb-3 form-check">
                                            <input type="checkbox" class="form-check-input" id="rememberMe">
                                            <label class="form-check-label" for="rememberMe">Ghi nhớ đăng
                                                nhập</label>
                                        </div>

                                        <div class="d-grid gap-2 mb-4">
                                            <button type="submit" class="btn btn-primary btn-login">Đăng
                                                nhập</button>
                                        </div>
                                    </form>

                                    <!-- Đăng nhập bằng mạng xã hội -->
                                    <div class="social-login">
                                        <p class="text-center mb-3">Hoặc đăng nhập với</p>
                                        <div class="d-flex justify-content-center gap-3 mb-4">
                                            <button class="btn btn-outline-primary social-btn" id="facebook-login">
                                                <i class="fab fa-facebook-f"></i>
                                            </button>
                                            <button class="btn btn-outline-danger social-btn" id="google-login">
                                                <i class="fab fa-google"></i>
                                            </button>
                                            <button class="btn btn-outline-dark social-btn">
                                                <i class="fab fa-apple"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <!-- Đăng ký mới -->
                                    <div class="register-link text-center">
                                        <p>Chưa có tài khoản? <a href="#" id="show-register">Đăng ký ngay</a></p>
                                    </div>
                                </div>

                                <!-- FORM QUÊN MẬT KHẨU -->
                                <div class="login-form-content d-none" id="forgot-password-form">
                                    <div class="text-center mb-4">
                                        <h2 class="login-title">Quên mật khẩu</h2>
                                        <p class="login-subtitle">Nhập email của bạn để lấy lại mật khẩu</p>
                                    </div>

                                    <form id="forgotPasswordForm">
                                        <div class="mb-4">
                                            <label for="forgot-email" class="form-label">Email</label>
                                            <div class="input-group">
                                                    <span class="input-group-text"><i
                                                            class="fas fa-envelope"></i></span>
                                                <input type="email" class="form-control" id="forgot-email"
                                                       placeholder="Nhập email đã đăng ký">
                                            </div>
                                        </div>

                                        <div class="d-grid gap-2 mb-4">
                                            <button type="submit" class="btn btn-primary btn-login">Gửi yêu
                                                cầu</button>
                                        </div>
                                    </form>

                                    <div class="back-to-login text-center">
                                        <p>Đã nhớ mật khẩu? <a href="#" id="back-to-login-from-forgot">Quay lại đăng
                                            nhập</a></p>
                                    </div>
                                </div>

                                <!-- FORM ĐĂNG KÝ -->
                                <div class="login-form-content d-none" id="register-form">
                                    <div class="text-center mb-4">
                                        <h2 class="login-title">Đăng ký tài khoản</h2>
                                        <p class="login-subtitle">Tạo tài khoản để khám phá BookStore</p>
                                    </div>

                                    <form id="registerForm">
                                        <div class="mb-3">
                                            <label for="register-fullname" class="form-label">Họ và tên</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-user"></i></span>
                                                <input type="text" class="form-control" id="register-fullname"
                                                       placeholder="Nhập họ và tên">
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="register-email" class="form-label">Email</label>
                                            <div class="input-group">
                                                    <span class="input-group-text"><i
                                                            class="fas fa-envelope"></i></span>
                                                <input type="email" class="form-control" id="register-email"
                                                       placeholder="Nhập email">
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="register-phone" class="form-label">Số điện thoại</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                                <input type="tel" class="form-control" id="register-phone"
                                                       placeholder="Nhập số điện thoại">
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="register-gender" class="form-label">Giới tính</label>
                                            <div class="d-flex gap-4">
                                                <div class="form-check">
                                                    <input type="radio" class="form-check-input" name="gender"
                                                           id="male" value="1" checked>
                                                    <label class="form-check-label" for="male">Nam</label>
                                                </div>
                                                <div class="form-check">
                                                    <input type="radio" class="form-check-input" name="gender"
                                                           id="female" value="0">
                                                    <label class="form-check-label" for="female">Nữ</label>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="register-password" class="form-label">Mật khẩu</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="register-password"
                                                       placeholder="Nhập mật khẩu">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <div class="password-strength mt-2">
                                                <div class="progress" style="height: 5px;">
                                                    <div class="progress-bar" role="progressbar" style="width: 0%;"
                                                         aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                                                    </div>
                                                </div>
                                                <small class="text-muted">Mật khẩu phải có ít nhất 8 ký tự, bao gồm
                                                    chữ hoa, chữ thường và số</small>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="register-confirm-password" class="form-label">Xác nhận mật
                                                khẩu</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control"
                                                       id="register-confirm-password" placeholder="Nhập lại mật khẩu">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                        </div>

                                        <div class="mb-3 form-check">
                                            <input type="checkbox" class="form-check-input" id="agreeTerms">
                                            <label class="form-check-label" for="agreeTerms">Tôi đồng ý với <a
                                                    href="#" class="text-primary">Điều khoản sử dụng</a> và <a
                                                    href="#" class="text-primary">Chính sách bảo mật</a></label>
                                        </div>

                                        <div class="d-grid gap-2 mb-4">
                                            <button type="submit" class="btn btn-primary btn-login">Đăng ký</button>
                                        </div>
                                    </form>

                                    <!-- Đăng nhập nếu đã có tài khoản -->
                                    <div class="back-to-login text-center">
                                        <p>Đã có tài khoản? <a href="#" id="back-to-login-from-register">Đăng
                                            nhập</a></p>
                                    </div>
                                </div>

                                <!-- FORM ĐẶT LẠI MẬT KHẨU -->
                                <div class="login-form-content d-none" id="reset-password-form">
                                    <div class="text-center mb-4">
                                        <h2 class="login-title">Đặt lại mật khẩu</h2>
                                        <p class="login-subtitle">Nhập mật khẩu mới</p>
                                    </div>

                                    <form id="resetPasswordForm">
                                        <div class="mb-3">
                                            <label for="reset-password" class="form-label">Mật khẩu</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="reset-password"
                                                       placeholder="Nhập mật khẩu">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <div class="reset-password-strength mt-2">
                                                <div class="progress" style="height: 5px;">
                                                    <div class="progress-bar" role="progressbar" style="width: 0%;"
                                                         aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                                                    </div>
                                                </div>
                                                <small class="text-muted">Reset Mật khẩu phải có ít nhất 8 ký tự,
                                                    bao gồm
                                                    chữ hoa, chữ thường và số</small>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="reset-confirm-password" class="form-label">Xác nhận mật
                                                khẩu</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control"
                                                       id="reset-confirm-password" placeholder="Nhập lại mật khẩu">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                        </div>
                                    </form>
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

<!-- Library -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- JavaScript -->
<script src="/bookshop/js/app/login.js" type="module"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>
</body>

</html>