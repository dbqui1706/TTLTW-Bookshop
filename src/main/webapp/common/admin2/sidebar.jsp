<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<nav id="sidebar">
    <div class="sidebar-header">
        <h5 class="mb-0 d-flex align-items-center">
            <i class="bi bi-boxes me-2 text-primary"></i>
            <span>Hệ Thống Quản Lý</span>
        </h5>
    </div>

    <ul class="components">
        <li>
            <a href="${pageContext.request.contextPath}/admin2" class="${param.active == 'HOME' ? 'active' : ''}">
                <i class="bi bi-speedometer2"></i>
                <span>Trang Chủ</span>
            </a>
        </li>
        <li>
            <a href="#productSubmenu" data-bs-toggle="collapse" aria-expanded="false"
               class="dropdown-toggle ${param.active == 'PRODUCT' || param.active == 'CATEGORY' ?
                    'active': ''}">
                <i class="bi bi-box-seam"></i>
                <span>Sản Phẩm</span>
            </a>
            <ul class="collapse submenu submenu" id="productSubmenu">
                <li>
                    <a href="${pageContext.request.contextPath}/admin2/product-manager" class="${param.active == 'PRODUCT' ? 'active' : ''}">
                        Danh Sách Sản Phẩm
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin2/category-manager" class="${param.active == 'CATEGORY' ? 'active' : ''}">
                        Danh Mục Sản Phẩm
                    </a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#orderSubmenu" data-bs-toggle="collapse" aria-expanded="false" class="dropdown-toggle">
                <i class="bi bi-cart3"></i>
                <span>Đơn Hàng</span>
                <span class="badge bg-danger rounded-pill">5</span>
            </a>
            <ul class="collapse submenu" id="orderSubmenu">
                <li>
                    <a href="#">Tất Cả Đơn Hàng</a>
                </li>
                <li>
                    <a href="#">Đơn Hàng Mới</a>
                </li>
                <li>
                    <a href="#">Đơn Đang Xử Lý</a>
                </li>
                <li>
                    <a href="#">Đơn Đã Hoàn Thành</a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#customerSubmenu" data-bs-toggle="collapse" aria-expanded="false"
               class="dropdown-toggle ${param.active == 'USER' || param.active == 'FEEDBACK' ?
                    'active': ''}">
                <i class="bi bi-people"></i>
                <span>Khách Hàng</span>
            </a>
            <ul class="collapse submenu" id="customerSubmenu">
                <li>
                    <a href="${pageContext.request.contextPath}/admin2/user-manager" class="${param.active == 'USER' ? 'active' : ''}">
                        Danh Sách Khách Hàng
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/admin2/user-manager" class="${param.active == 'FEEDBACK' ? 'active' : ''}">
                        Phản Hồi Khách Hàng
                    </a>
                </li>
            </ul>
        </li>

        <div class="sidebar-heading">Báo Cáo</div>
        <li>
            <a href="#">
                <i class="bi bi-graph-up"></i>
                <span>Báo Cáo Doanh Thu</span>
            </a>
        </li>
        <li>
            <a href="#">
                <i class="bi bi-bar-chart"></i>
                <span>Thống Kê Bán Hàng</span>
            </a>
        </li>
        <li>
            <a href="#">
                <i class="bi bi-pie-chart"></i>
                <span>Phân Tích Xu Hướng</span>
            </a>
        </li>

        <div class="sidebar-heading">Hệ Thống</div>
        <li>
            <a href="#">
                <i class="bi bi-gear"></i>
                <span>Thiết Lập Hệ Thống</span>
            </a>
        </li>
        <li>
            <a href="#">
                <i class="bi bi-person-badge"></i>
                <span>Quản Lý Người Dùng</span>
            </a>
        </li>
        <li>
            <a href="#">
                <i class="bi bi-shield-lock"></i>
                <span>Phân Quyền</span>
            </a>
        </li>
    </ul>
</nav>