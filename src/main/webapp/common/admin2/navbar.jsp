<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<nav class="navbar navbar-expand-lg mb-4">
    <div class="container-fluid">
        <button type="button" id="sidebarCollapse" class="btn btn-primary">
            <i class="bi bi-list"></i>
        </button>
        <button class="btn btn-dark d-inline-block d-lg-none ms-auto" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
            <i class="bi bi-three-dots"></i>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="nav navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#" title="Thông báo">
                        <i class="bi bi-bell position-relative">
                                        <span class="position-absolute top-0 start-100 translate-middle p-1 bg-danger border border-light rounded-circle">
                                            <span class="visually-hidden">Thông báo mới</span>
                                        </span>
                        </i>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" title="Tin nhắn">
                        <i class="bi bi-envelope"></i>
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" id="navbarDropdown"
                       role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <img src="https://via.placeholder.com/30" class="rounded-circle me-2" alt="Admin"/>
                        <span>Admin</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
                        <li>
                            <a class="dropdown-item" href="#"><i class="bi bi-person me-2"></i>Hồ sơ</a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#"><i class="bi bi-gear me-2"></i>Cài đặt</a>
                        </li>
                        <li>
                            <hr class="dropdown-divider"/>
                        </li>
                        <li>
                            <a class="dropdown-item" href="#"><i class="bi bi-box-arrow-right me-2"></i>Đăng
                                xuất</a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>
