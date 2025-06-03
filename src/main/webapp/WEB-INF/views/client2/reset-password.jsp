<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/2/2025
  Time: 5:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu | BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/login.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/bookshop/css/reset-password.css">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp"/>

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
                                <div class="login-image reset-password-image">
                                    <div class="overlay reset-overlay"></div>
                                    <div class="login-image-content">
                                        <div class="reset-icon">
                                            <i class="fas fa-key"></i>
                                        </div>
                                        <h3 id="sidebar-title">Đặt lại mật khẩu</h3>
                                        <p id="sidebar-description">Tạo mật khẩu mới cho tài khoản của bạn</p>
                                        <div class="security-tips">
                                            <h5><i class="fas fa-shield-alt"></i> Lời khuyên bảo mật:</h5>
                                            <ul>
                                                <li><i class="fas fa-check"></i> Ít nhất 8 ký tự</li>
                                                <li><i class="fas fa-check"></i> Bao gồm chữ hoa và thường</li>
                                                <li><i class="fas fa-check"></i> Có số và ký tự đặc biệt</li>
                                                <li><i class="fas fa-check"></i> Không sử dụng thông tin cá nhân</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Phần form -->
                            <div class="col-md-7 login-form-container">
                                <!-- FORM ĐẶT LẠI MẬT KHẨU -->
                                <div class="login-form-content" id="reset-password-form">
                                    <!-- Header với status -->
                                    <div class="text-center mb-4">
                                        <div class="reset-status-icon">
                                            <i class="fas fa-lock" id="status-icon"></i>
                                        </div>
                                        <h2 class="login-title">Đặt lại mật khẩu</h2>
                                        <p class="login-subtitle" id="status-message">
                                            Nhập mật khẩu mới cho tài khoản: <strong id="user-email"></strong>
                                        </p>
                                    </div>

                                    <!-- Form đặt lại mật khẩu -->
                                    <form id="resetPasswordForm">
                                        <!-- Hidden fields để lưu thông tin xác thực -->
                                        <input type="hidden" id="reset-email" name="email">
                                        <input type="hidden" id="reset-code" name="code">

                                        <div class="mb-3">
                                            <label for="new-password" class="form-label">
                                                <i class="fas fa-key text-primary"></i> Mật khẩu mới
                                            </label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="new-password"
                                                       placeholder="Nhập mật khẩu mới" autocomplete="new-password">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button" data-target="new-password">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <!-- Password strength indicator -->
                                            <div class="password-strength mt-2">
                                                <div class="progress" style="height: 6px;">
                                                    <div class="progress-bar" role="progressbar" style="width: 0%;"
                                                         aria-valuenow="0" aria-valuemin="0" aria-valuemax="100">
                                                    </div>
                                                </div>
                                                <small class="text-muted strength-text">
                                                    Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt
                                                </small>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <label for="confirm-password" class="form-label">
                                                <i class="fas fa-check-double text-success"></i> Xác nhận mật khẩu
                                            </label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control" id="confirm-password"
                                                       placeholder="Nhập lại mật khẩu mới" autocomplete="new-password">
                                                <button class="btn btn-outline-secondary toggle-password"
                                                        type="button" data-target="confirm-password">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <div class="password-match mt-2 d-none">
                                                <small class="match-text"></small>
                                            </div>
                                        </div>

                                        <!-- Security notice -->
                                        <div class="alert alert-info border-0 mb-3">
                                            <div class="d-flex align-items-center">
                                                <i class="fas fa-info-circle me-2"></i>
                                                <small>
                                                    Sau khi đặt lại mật khẩu, bạn sẽ cần đăng nhập lại trên tất cả thiết bị.
                                                </small>
                                            </div>
                                        </div>

                                        <div class="d-grid gap-2 mb-4">
                                            <button type="submit" class="btn btn-primary btn-login" id="submit-btn">
                                                <i class="fas fa-save me-2"></i>
                                                <span class="btn-text">Đặt lại mật khẩu</span>
                                            </button>
                                        </div>
                                    </form>

                                    <!-- Link quay về đăng nhập -->
                                    <div class="back-to-login text-center">
                                        <p>
                                            <i class="fas fa-arrow-left me-1"></i>
                                            <a href="${pageContext.request.contextPath}/login" id="back-to-login">
                                                Quay về trang đăng nhập
                                            </a>
                                        </p>
                                    </div>
                                </div>

                                <!-- THÔNG BÁO THÀNH CÔNG -->
                                <div class="login-form-content d-none" id="success-message">
                                    <div class="text-center">
                                        <div class="success-icon">
                                            <i class="fas fa-check-circle"></i>
                                        </div>
                                        <h2 class="login-title text-success">Thành công!</h2>
                                        <p class="login-subtitle">
                                            Mật khẩu của bạn đã được đặt lại thành công.
                                        </p>
                                        <div class="d-grid gap-2 mb-4">
                                            <a href="${pageContext.request.contextPath}/login"
                                               class="btn btn-success btn-login">
                                                <i class="fas fa-sign-in-alt me-2"></i>
                                                Đăng nhập ngay
                                            </a>
                                        </div>
                                    </div>
                                </div>

                                <!-- THÔNG BÁO LỖI -->
                                <div class="login-form-content d-none" id="error-message">
                                    <div class="text-center">
                                        <div class="error-icon">
                                            <i class="fas fa-exclamation-triangle"></i>
                                        </div>
                                        <h2 class="login-title text-danger">Liên kết không hợp lệ</h2>
                                        <p class="login-subtitle" id="error-text">
                                            Liên kết đặt lại mật khẩu đã hết hạn hoặc không hợp lệ.
                                        </p>
                                        <div class="d-grid gap-2 mb-4">
                                            <a href="${pageContext.request.contextPath}/login"
                                               class="btn btn-outline-primary btn-login">
                                                <i class="fas fa-arrow-left me-2"></i>
                                                Quay về đăng nhập
                                            </a>
                                        </div>
                                        <div class="text-center">
                                            <a href="${pageContext.request.contextPath}/login" class="text-primary">
                                                Yêu cầu đặt lại mật khẩu mới
                                            </a>
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

<!-- Help Widget -->
<div class="help-widget">
    <div class="help-button">
        <i class="fas fa-headset"></i>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- Libraries -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- JavaScript -->
<script src="/bookshop/js/app/reset-password.js" type="module"></script>
<script src="/bookshop/js/app/header.js" type="module"></script>
</body>
</html>
