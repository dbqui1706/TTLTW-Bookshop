<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header>
    <div class="container">
        <!-- Logo và thanh tìm kiếm -->
        <div class="row py-3 align-items-center">
            <div class="col-md-2 col-sm-4 col-5 mb-2 mb-md-0">
                <a class="navbar-brand" href="/">
                    <img src="/asset/logo.jpg" alt="BookStore Logo" class="navbar-logo img-fluid">
                </a>
            </div>
            <div class="col-md-7 col-sm-8 col-12 mb-2 mb-md-0">
                <div class="search-bar">
                    <div class="input-group">
                        <input type="text" class="form-control search-input"
                               placeholder="Tìm kiếm sách, tác giả, thể loại...">
                        <button class="btn search-button" type="button" id="searchBtn">
                            <i class="fas fa-search me-1 d-none d-sm-inline-block"></i> Tìm kiếm
                        </button>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-12">
                <div class="user-menu">
                    <a href="/" title="Trang chủ" class="menu-item">
                        <i class="fas fa-home"></i>
                        <span>Trang chủ</span>
                    </a>
                    <div class="account-dropdown">
                        <a href="/login" class="menu-item dropdown-toggle" id="accountMenu">
                            <i class="fas fa-user-circle"></i>
                            <span>Tài khoản</span>
                        </a>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="/account-info">Thông tin tài khoản</a>
                            <a class="dropdown-item" href="/order-info">Đơn hàng của tôi</a>
                            <a class="dropdown-item" href="#">Trung tâm hỗ trợ</a>
                            <div class="dropdown-divider"></div>
                            <a class="dropdown-item" href="#">Đăng xuất</a>
                        </div>
                    </div>
                    <!-- Giỏ hàng -->
                    <div class="cart-container">
                        <a href="/cart" class="cart-icon menu-item" title="Giỏ hàng">
                            <i class="fas fa-shopping-cart"></i>
                            <span class="cart-count" id="cart-count">0</span>
                            <span>Giỏ hàng</span>
                        </a>

                        <!-- Thông báo thêm vào giỏ hàng thành công -->
                        <div class="cart-success-message" id="cart-success-message">
                            <i class="fas fa-check-circle"></i>
                            <span>Thêm vào giỏ hàng thành công!</span>
                            <div class="message-arrow"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Commitment Bar -->
    <div class="commitment-bar">
        <div class="container">
            <div class="row">
                <div class="col-lg col-md-4 col-6 commitment-item-wrapper">
                    <div class="commitment-item">
                        <i class="fas fa-shield-alt"></i>
                        <span>100% hàng thật</span>
                    </div>
                </div>
                <div class="col-lg col-md-4 col-6 commitment-item-wrapper">
                    <div class="commitment-item">
                        <i class="fas fa-truck"></i>
                        <span>Freeship mọi đơn</span>
                    </div>
                </div>
                <div class="col-lg col-md-4 col-6 commitment-item-wrapper">
                    <div class="commitment-item">
                        <i class="fas fa-undo"></i>
                        <span>Hoàn 200% nếu hàng giả</span>
                    </div>
                </div>
                <div class="col-lg col-md-4 col-6 commitment-item-wrapper">
                    <div class="commitment-item">
                        <i class="fas fa-calendar-alt"></i>
                        <span>30 ngày đổi trả</span>
                    </div>
                </div>
                <div class="col-lg col-md-4 col-6 commitment-item-wrapper">
                    <div class="commitment-item">
                        <i class="fas fa-bolt"></i>
                        <span>Giao nhanh 2h</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>