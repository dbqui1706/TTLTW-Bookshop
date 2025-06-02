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
    <link rel="stylesheet" href="/bookshop/css/account-info.css">
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
                <li class="breadcrumb-item active">Thông tin tài khoản</li>
            </ol>
        </nav>

        <!-- Account Info Section -->
        <div class="account-section">
            <div class="row">
                <!-- Sidebar -->
                <jsp:include page="/common/client2/sidebar.jsp">
                    <jsp:param name="sidebar" value="ACCOUNT"/>
                </jsp:include>

                <!-- Main Content -->
                <div class="col-lg-9 col-md-8">
                    <div class="account-content">
                        <h3 class="account-title">Thông tin tài khoản</h3>

                        <div class="account-info-wrapper">
                            <!-- Personal Information -->
                            <div class="account-info-section">
                                <h4 class="section-title">Thông tin cá nhân</h4>

                                <div class="row">
                                    <div class="col-md-7">
                                        <div class="personal-info">
                                            <div class="user-avatar-large">
                                                <img src="https://avatar.iran.liara.run/public/boy" alt="Avatar">
                                                <!-- <span class="edit-avatar"><i class="fas fa-camera"></i></span> -->
                                            </div>

                                            <div class="info-form">
                                                <div class="form-group">
                                                    <label>Họ & Tên</label>
                                                    <input type="text" class="form-control" value=""
                                                           id="fullName">
                                                </div>

                                                <div class="form-group">
                                                    <label>User name</label>
                                                    <input type="text" class="form-control"
                                                           placeholder="Thêm nickname" id="nickname">
                                                </div>

                                                <!-- <div class="form-group">
                                                    <label>Ngày sinh</label>
                                                    <div class="dob-selects">
                                                        <select class="form-select" id="day">
                                                            <option>Ngày</option>
                                                        </select>
                                                        <select class="form-select" id="month">
                                                            <option>Tháng</option>
                                                        </select>
                                                        <select class="form-select" id="year">
                                                            <option>Năm</option>
                                                        </select>
                                                    </div>
                                                </div> -->

                                                <div class="form-group">
                                                    <label>Giới tính</label>
                                                    <div class="gender-options">
                                                        <label class="gender-option">
                                                            <input type="radio" name="gender" value="male"
                                                                   id="male">
                                                            <span>Nam</span>
                                                        </label>
                                                        <label class="gender-option">
                                                            <input type="radio" name="gender" value="female"
                                                                   id="female">
                                                            <span>Nữ</span>
                                                        </label>
                                                    </div>
                                                </div>

                                                <div class="save-button">
                                                    <button type="button" class="btn btn-primary"
                                                            id="saveButton">Lưu thay đổi</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-md-5">
                                        <div class="contact-info">
                                            <h4 class="section-title">Số điện thoại và Email</h4>

                                            <!-- Phần cập nhật số điện thoại -->
                                            <div class="contact-item">
                                                <div class="contact-icon">
                                                    <i class="fas fa-phone-alt"></i>
                                                </div>
                                                <div class="contact-detail">
                                                    <div class="contact-label">Số điện thoại</div>
                                                    <div class="contact-value" id="phoneDisplay"></div>
                                                </div>
                                                <button class="btn btn-sm btn-outline-primary"
                                                        id="updatePhoneBtn">Cập nhật</button>
                                            </div>

                                            <!-- Phần cập nhật email -->
                                            <div class="contact-item">
                                                <div class="contact-icon">
                                                    <i class="fas fa-envelope"></i>
                                                </div>
                                                <div class="contact-detail">
                                                    <div class="contact-label">Địa chỉ email</div>
                                                    <div class="contact-value" id="emailDisplay"></div>
                                                </div>
                                                <button class="btn btn-sm btn-outline-primary"
                                                        id="updateEmailBtn">Cập nhật</button>
                                            </div>

                                            <!-- Phần bảo mật -->
                                            <div class="contact-item">
                                                <div class="contact-icon">
                                                    <i class="fas fa-lock"></i>
                                                </div>
                                                <div class="contact-detail">
                                                    <div class="contact-label">Thiết lập mật khẩu</div>
                                                </div>
                                                <button class="btn btn-sm btn-outline-primary"
                                                        id="updatePasswordBtn">Cập nhật</button>
                                            </div>

<%--                                            <div class="contact-item">--%>
<%--                                                <div class="contact-icon">--%>
<%--                                                    <i class="fas fa-key"></i>--%>
<%--                                                </div>--%>
<%--                                                <div class="contact-detail">--%>
<%--                                                    <div class="contact-label">Thiết lập mã PIN</div>--%>
<%--                                                </div>--%>
<%--                                                <button class="btn btn-sm btn-outline-primary"--%>
<%--                                                        id="setupPinBtn">Thiết lập</button>--%>
<%--                                            </div>--%>

                                            <div class="contact-item">
                                                <div class="contact-icon">
                                                    <i class="fas fa-trash-alt"></i>
                                                </div>
                                                <div class="contact-detail">
                                                    <div class="contact-label">Yêu cầu xóa tài khoản</div>
                                                </div>
                                                <button class="btn btn-sm btn-outline-primary"
                                                        id="deleteAccountBtn">Yêu cầu</button>
                                            </div>

                                            <!-- Phần liên kết mạng xã hội -->
<%--                                            <div class="contact-item">--%>
<%--                                                <div class="contact-icon">--%>
<%--                                                    <i class="fab fa-facebook-f"></i>--%>
<%--                                                </div>--%>
<%--                                                <div class="contact-detail">--%>
<%--                                                    <div class="contact-label">Facebook</div>--%>
<%--                                                </div>--%>
<%--                                                <button class="btn btn-sm btn-outline-primary"--%>
<%--                                                        id="linkFacebookBtn">Liên kết</button>--%>
<%--                                            </div>--%>

<%--                                            <div class="contact-item">--%>
<%--                                                <div class="contact-icon">--%>
<%--                                                    <i class="fab fa-google"></i>--%>
<%--                                                </div>--%>
<%--                                                <div class="contact-detail">--%>
<%--                                                    <div class="contact-label">Google</div>--%>
<%--                                                </div>--%>
<%--                                                <button class="btn btn-sm btn-outline-primary" id="linkGoogleBtn">Đã--%>
<%--                                                    liên kết</button>--%>
<%--                                            </div>--%>
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
<script src="/bookshop/js/app/account-info.js" type="module"></script>
</body>

</html>