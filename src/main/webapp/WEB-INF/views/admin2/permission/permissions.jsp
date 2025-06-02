<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 4/20/2025
  Time: 4:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<!DOCTYPE html>
<html lang="vi">

<head>
    <title>Quản Trị Hệ Thống</title>
    <jsp:include page="/common/admin2/head-meta.jsp"></jsp:include>
    <link rel="stylesheet" href="/app/css/permission/permission.css"/>
</head>

<body>
<div class="wrapper">
    <!-- Sidebar -->
    <jsp:include page="/common/admin2/sidebar.jsp">
        <jsp:param name="sidebar" value="PERMISSIONS"/>
    </jsp:include>

    <!-- Page Content -->
    <div id="content">
        <!-- Navbar -->
        <jsp:include page="/common/admin2/navbar.jsp"></jsp:include>

        <!-- Dashboard Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="bg-primary text-white rounded p-3">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h2>Phân Quyền Hệ Thống</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabs Navigation -->
        <ul class="nav nav-tabs" id="permissionTabs" role="tablist">
            <li class="nav-item" role="presentation">
                <button class="nav-link active" id="roles-tab" data-bs-toggle="tab" data-bs-target="#roles-content"
                        type="button" role="tab" aria-controls="roles-content" aria-selected="true">
                    <i class="bi bi-person-badge me-2"></i>Vai Trò
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="permissions-tab" data-bs-toggle="tab"
                        data-bs-target="#permissions-content" type="button" role="tab"
                        aria-controls="permissions-content" aria-selected="false">
                    <i class="bi bi-key me-2"></i>Quyền Hạn
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="role-permissions-tab" data-bs-toggle="tab"
                        data-bs-target="#role-permissions-content" type="button" role="tab"
                        aria-controls="role-permissions-content" aria-selected="false">
                    <i class="bi bi-link me-2"></i>Gán Quyền Cho Vai Trò
                </button>
            </li>
            <li class="nav-item" role="presentation">
                <button class="nav-link" id="user-roles-tab" data-bs-toggle="tab"
                        data-bs-target="#user-roles-content" type="button" role="tab" aria-controls="user-roles-content"
                        aria-selected="false">
                    <i class="bi bi-people me-2"></i>Gán Vai Trò Cho Người Dùng
                </button>
            </li>
        </ul>

        <!-- Tab Content -->
        <div class="tab-content bg-white rounded-bottom shadow-sm p-4" id="permissionTabsContent">
            <!-- Roles Tab -->
            <div class="tab-pane fade show active" id="roles-content" role="tabpanel" aria-labelledby="roles-tab">
                <!-- Nội dung tab Vai Trò sẽ được nạp động từ roles.html -->
                <div id="roles-container"></div>
            </div>

            <!-- Permissions Tab -->
            <div class="tab-pane fade" id="permissions-content" role="tabpanel" aria-labelledby="permissions-tab">
                <!-- Nội dung tab Quyền Hạn sẽ được nạp động từ permissions.html -->
                <div id="permissions-container"></div>
            </div>

            <!-- Role-Permissions Tab -->
            <div class="tab-pane fade" id="role-permissions-content" role="tabpanel"
                 aria-labelledby="role-permissions-tab">
                <!-- Nội dung tab Gán Quyền Cho Vai Trò sẽ được nạp động từ role-permissions.html -->
                <div id="role-permissions-container"></div>
            </div>

            <!-- User-Roles Tab -->
            <div class="tab-pane fade" id="user-roles-content" role="tabpanel" aria-labelledby="user-roles-tab">
                <!-- Nội dung tab Gán Vai Trò Cho Người Dùng sẽ được nạp động từ user-roles.html -->
                <div id="user-roles-container"></div>
            </div>
        </div>

        <!-- Footer -->
        <jsp:include page="/common/admin2/footer.jsp"></jsp:include>
    </div>
</div>
<style>
    /* Ẩn container của Select2 để không thay đổi giao diện */
    .select2-container--default .select2-selection--single {
        height: 100%;
        border: 1px solid #ced4da;
        /* Giữ border giống Bootstrap */
        border-radius: 0.375rem;
        /* Border radius giống Bootstrap */
        padding: 0.1rem 0.75rem;
    }

    /* Giữ nguyên vị trí và kích thước của mũi tên dropdown */
    .select2-container--default .select2-selection--single .select2-selection__arrow {
        height: 100%;
        right: 10px;
    }

    /* Đảm bảo văn bản được hiển thị đúng vị trí */
    .select2-container--default .select2-selection--single .select2-selection__rendered {
        line-height: 1.5;
        padding-left: 0;
        color: #212529;
        /* Màu chữ giống Bootstrap */
    }

    /* Đảm bảo form-floating hoạt động đúng */
    .form-floating .select2-container {
        height: 100%;
    }

    .form-floating .select2-container--default .select2-selection--single {
        padding-top: 1.62rem;
        padding-bottom: 0.62rem;
    }
</style>

<jsp:include page="/common/admin2/scripts-footer.jsp"></jsp:include>

<!-- Custom JS -->
<script src="/app/main/permissions.js" type="module"></script>
</body>

</html>