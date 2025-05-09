<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    .account-menu li a {
        display: flex;
        align-items: center;
        text-decoration: none;  /* Loại bỏ gạch chân */
        color: inherit;         /* Giữ nguyên màu chữ */
        width: 100%;            /* Đảm bảo liên kết chiếm toàn bộ không gian */
    }

    .account-menu li {
        display: block;         /* Đảm bảo li vẫn hiển thị như block */
        margin: 0;              /* Loại bỏ margin mặc định */
    }

    .account-menu li.active a {
        /* Giữ nguyên kiểu cho mục đang active */
        color: inherit;         /* Hoặc màu mà bạn muốn cho mục active */
        font-weight: bold;      /* Nếu bạn muốn in đậm cho mục active */
    }
</style>

<div class="col-lg-3 col-md-4">
    <div class="account-sidebar">
        <div class="user-profile">
            <div class="user-avatar">
                <img src="https://avatar.iran.liara.run/public/boy" alt="Avatar">
            </div>
            <div class="user-info">
                <h6>Tài khoản của</h6>
                <p class="user-name"></p>
            </div>
        </div>

        <ul class="account-menu">
            <li class="${param.sidebar == 'ACCOUNT' ? 'active' : ''}">
                <a href="/account-info">
                    <i class="fas fa-user"></i>
                    <span>Thông tin tài khoản</span>
                </a>
            </li>
            <li class="${param.sidebar == 'ORDER' ? 'active' : ''}">
                <a href="/order-info">
                    <i class="fas fa-shopping-bag"></i>
                    <span>Quản lý đơn hàng</span>
                </a>
            </li>
            <li class="${param.sidebar == 'NOTIFICATION' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-bell"></i>
                    <span>Thông báo của tôi</span>
                </a>
            </li>
            <li class="${param.sidebar == 'ADDRESS' ? 'active' : ''}">
                <a href="/addresses-info">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>Sổ địa chỉ</span>
                </a>
            </li>
            <li class="${param.sidebar == 'PAYMENT' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-credit-card"></i>
                    <span>Thông tin thanh toán</span>
                </a>
            </li>
            <li class="${param.sidebar == 'REVIEW' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-star"></i>
                    <span>Đánh giá sản phẩm</span>
                </a>
            </li>
            <li class="${param.sidebar == 'VIEWED' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-eye"></i>
                    <span>Sản phẩm bạn đã xem</span>
                </a>
            </li>
            <li class="${param.sidebar == 'WISHLIST' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-heart"></i>
                    <span>Sản phẩm yêu thích</span>
                </a>
            </li>
            <li class="${param.sidebar == 'COMMENTS' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-thumbs-up"></i>
                    <span>Nhận xét của tôi</span>
                </a>
            </li>
            <li class="${param.sidebar == 'COUPONS' ? 'active' : ''}">
                <a href="#">
                    <i class="fas fa-tags"></i>
                    <span>Mã giảm giá</span>
                </a>
            </li>
        </ul>
    </div>
</div>