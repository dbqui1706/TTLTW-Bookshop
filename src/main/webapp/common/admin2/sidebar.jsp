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
            <a href="index.html" class="">
                <i class="bi bi-speedometer2"></i>
                <span>Trang Chủ</span>
            </a>
        </li>
        <li>
            <a href="#productSubmenu" data-bs-toggle="collapse" aria-expanded="false"
               class="dropdown-toggle ${param.sidebar.split("-")[0] == 'PRODUCT' ? 'active' : ''}">
                <i class="bi bi-box-seam"></i>
                <span>Sản Phẩm</span>
            </a>
            <ul id="productSubmenu"
                class="collapse submenu ${param.sidebar.split("-")[0] == 'PRODUCT' ? 'show' : ''}">
                <li>
                    <a href="/admin/product-manager"
                       class="${param.sidebar == 'PRODUCT-MANAGER' ? 'active' : ''}">
                        Danh Sách Sản Phẩm
                    </a>
                </li>
                <li>
                    <a href="product-categories.html"
                       class="${param.sidebar == 'PRODUCT-CATEGORIES' ? 'active' : ''}">
                        Danh Mục Sản Phẩm
                    </a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#inventorySubmenu" data-bs-toggle="collapse" aria-expanded="true"
               class="dropdown-toggle ${param.sidebar.split("-")[0] == 'INVENTORY' ? 'active' : ''}">
                <i class="bi bi-stack"></i>
                <span>Quản Lý Kho</span>
            </a>
            <ul id="inventorySubmenu"
                class="collapse submenu ${param.sidebar.split("-")[0] == 'INVENTORY' ? 'show' : ''}">
                <li>
                    <a href="/admin/inventory-manager/dashboard"
                       class="${param.sidebar == 'INVENTORY-DASHBOARD' ? 'active' : ''}">
                        Tổng Quan Tồn Kho
                    </a>
                </li>
                <li>
                    <a href="/admin/inventory-manager/management"
                       class="${param.sidebar == 'INVENTORY-MANAGEMENT' ? 'active' : ''}">
                        Nhập/Xuất Kho
                    </a>
                </li>
                <li>
                    <a href="/admin/inventory-manager/report"
                       class="${param.sidebar == 'INVENTORY-REPORT' ? 'active' : ''}">
                        Báo Cáo Thống Kê
                    </a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#orderSubmenu" data-bs-toggle="collapse" aria-expanded="false"
               class="dropdown-toggle ${param.sidebar.split("-")[0] == 'ORDER' ? 'active' : ''}">
                <i class="bi bi-cart3"></i>
                <span>Đơn Hàng</span>
                <span class="badge bg-danger rounded-pill">5</span>
            </a>
            <ul id="orderSubmenu"
                class="collapse submenu ${param.sidebar.split("-")[0] == 'ORDER' ? 'show' : ''}">
                <li>
                    <a href="/admin/order-manager/orders"
                       class="${param.sidebar == 'ORDER-ORDERS' ? 'active' : ''}">
                        Tất Cả Đơn Hàng
                    </a>
                </li>
            </ul>
        </li>
        <li>
            <a href="#customerSubmenu" data-bs-toggle="collapse" aria-expanded="false" class="">
                <i class="bi bi-people"></i>
                <span>Khách Hàng</span>
            </a>
            <ul class="collapse submenu" id="customerSubmenu">
                <li>
                    <a href="#" class="">
                        Danh Sách Khách Hàng
                    </a>
                </li>
                <li>
                    <a href="#" class="">
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
            <a href="/admin/permissions" class="${param.sidebar == 'PERMISSIONS' ? 'active' : ''}">
                <i class="bi bi-shield-lock"></i>
                <span>Phân Quyền</span>
            </a>
        </li>
    </ul>
</nav>