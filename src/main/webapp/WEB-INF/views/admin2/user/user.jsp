<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Quản Trị Hệ Thống</title>
  <!-- Bootstrap 5 CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <!-- Bootstrap Icons -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"/>

  <!-- CSS -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css"/>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/user/user.css"/>
</head>

<body>
<div class="wrapper">
  <!-- Sidebar -->
  <jsp:include page="${pageContext.request.contextPath}/common/admin2/sidebar.jsp">
    <jsp:param name="active" value="USER"/>
  </jsp:include>

  <!-- Page Content -->
  <div id="content">

    <!-- Navbar -->
    <jsp:include page="${pageContext.request.contextPath}/common/admin2/navbar.jsp"/>

    <!-- Dashboard Header -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="bg-primary text-white rounded p-3">
          <div class="row align-items-center">
            <div class="col-md-6">
              <h2>Quản lý thông tin người dùng</h2>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Control Panel -->
    <div class="row mb-4">
      <div class="col-md-12">
        <div class="card border-0 shadow-sm">
          <div class="card-body p-3">
            <div class="row g-2 align-items-center">
              <div class="col-lg-3 col-md-6">
                <div class="form-floating">
                  <input type="text" class="form-control" id="searchUser" placeholder="Tìm kiếm người dùng" />
                  <label for="searchUser">Tìm kiếm người dùng</label>
                </div>
              </div>
              <div class="col-lg-2 col-md-6">
                <div class="form-floating">
                  <select class="form-select" id="roleFilter">
                    <option value="" selected>Tất cả</option>
                    <option value="admin">Quản trị viên</option>
                    <option value="staff">Nhân viên</option>
                    <option value="customer">Khách hàng</option>
                  </select>
                  <label for="roleFilter">Vai trò</label>
                </div>
              </div>
              <div class="col-lg-2 col-md-6">
                <div class="form-floating">
                  <select class="form-select" id="statusFilter">
                    <option value="" selected>Tất cả</option>
                    <option value="active">Đang hoạt động</option>
                    <option value="inactive">Không hoạt động</option>
                    <option value="locked">Bị khóa</option>
                  </select>
                  <label for="statusFilter">Trạng thái</label>
                </div>
              </div>
              <div class="col-lg-2 col-md-6">
                <div class="form-floating">
                  <select class="form-select" id="sortOption">
                    <option value="name-asc" selected>Tên A-Z</option>
                    <option value="name-desc">Tên Z-A</option>
                    <option value="date-asc">Ngày tạo (cũ nhất)</option>
                    <option value="date-desc">Ngày tạo (mới nhất)</option>
                    <option value="login-desc">Đăng nhập gần đây</option>
                  </select>
                  <label for="sortOption">Sắp xếp theo</label>
                </div>
              </div>
              <div class="col-lg-3 col-md-12 text-end">
                <button class="btn btn-success ms-2" data-bs-toggle="tooltip" data-bs-placement="top" title="Xuất danh sách người dùng">
                  <i class="bi bi-file-earmark-arrow-up"></i> Export Excel
                </button>
                <button id="addUserBtn" class="btn btn-primary ms-2" data-bs-toggle="modal" data-bs-target="#addUserModal">
                  <i class="bi bi-plus-lg"></i> Thêm người dùng
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Row -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="flex-shrink-0">
                <div class="stat-icon bg-primary bg-opacity-10 p-3 rounded text-primary">
                  <i class="bi bi-people fs-3"></i>
                </div>
              </div>
              <div class="flex-grow-1 ms-3">
                <h6 class="mb-1">Tổng người dùng</h6>
                <h3 class="mb-0">1,245</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="flex-shrink-0">
                <div class="stat-icon bg-success bg-opacity-10 p-3 rounded text-success">
                  <i class="bi bi-person-check fs-3"></i>
                </div>
              </div>
              <div class="flex-grow-1 ms-3">
                <h6 class="mb-1">Đang hoạt động</h6>
                <h3 class="mb-0">1,152</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="flex-shrink-0">
                <div class="stat-icon bg-info bg-opacity-10 p-3 rounded text-info">
                  <i class="bi bi-person-plus fs-3"></i>
                </div>
              </div>
              <div class="flex-grow-1 ms-3">
                <h6 class="mb-1">Mới trong tháng</h6>
                <h3 class="mb-0">48</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="d-flex align-items-center">
              <div class="flex-shrink-0">
                <div class="stat-icon bg-danger bg-opacity-10 p-3 rounded text-danger">
                  <i class="bi bi-person-lock fs-3"></i>
                </div>
              </div>
              <div class="flex-grow-1 ms-3">
                <h6 class="mb-1">Tài khoản bị khóa</h6>
                <h3 class="mb-0">25</h3>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- User Table -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                <tr>
                  <th width="50">ID</th>
                  <th width="60">Ảnh</th>
                  <th>Họ tên</th>
                  <th>Email</th>
                  <th>Vai trò</th>
                  <th>Số điện thoại</th>
                  <th>Đăng nhập gần đây</th>
                  <th>Trạng thái</th>
                  <th width="150">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <!-- User Row 1 -->
                <tr>
                  <td>#U001</td>
                  <td>
                    <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle" />
                  </td>
                  <td>Nguyễn Văn Admin</td>
                  <td>admin@example.com</td>
                  <td>
                    <span class="badge bg-danger">Quản trị viên</span>
                  </td>
                  <td>0123456789</td>
                  <td>Hôm nay, 08:45</td>
                  <td>
                    <span class="badge bg-success">Đang hoạt động</span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewUserModal" data-id="U001">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="U001">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="U001">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <!-- User Row 2 -->
                <tr>
                  <td>#U002</td>
                  <td>
                    <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle" />
                  </td>
                  <td>Trần Thị Nhân Viên</td>
                  <td>staff@example.com</td>
                  <td><span class="badge bg-primary">Nhân viên</span></td>
                  <td>0987654321</td>
                  <td>Hôm qua, 17:30</td>
                  <td>
                    <span class="badge bg-success">Đang hoạt động</span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewUserModal" data-id="U002">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="U002">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="U002">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <!-- User Row 3 -->
                <tr>
                  <td>#U003</td>
                  <td>
                    <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle" />
                  </td>
                  <td>Lê Văn Khách</td>
                  <td>customer@example.com</td>
                  <td>
                    <span class="badge bg-secondary">Khách hàng</span>
                  </td>
                  <td>0912345678</td>
                  <td>12/03/2025, 14:15</td>
                  <td>
                    <span class="badge bg-success">Đang hoạt động</span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewUserModal" data-id="U003">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="U003">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="U003">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <!-- User Row 4 -->
                <tr>
                  <td>#U004</td>
                  <td>
                    <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle" />
                  </td>
                  <td>Phạm Thị Tạm Khóa</td>
                  <td>locked@example.com</td>
                  <td>
                    <span class="badge bg-secondary">Khách hàng</span>
                  </td>
                  <td>0956781234</td>
                  <td>05/02/2025, 09:20</td>
                  <td><span class="badge bg-danger">Bị khóa</span></td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewUserModal" data-id="U004">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="U004">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="U004">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <!-- User Row 5 -->
                <tr>
                  <td>#U005</td>
                  <td>
                    <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle" />
                  </td>
                  <td>Hoàng Văn Không Hoạt Động</td>
                  <td>inactive@example.com</td>
                  <td>
                    <span class="badge bg-secondary">Khách hàng</span>
                  </td>
                  <td>0967812345</td>
                  <td>15/12/2024, 11:45</td>
                  <td>
                    <span class="badge bg-warning text-dark">Không hoạt động</span>
                  </td>
                  <td>
                    <div class="btn-group btn-group-sm">
                      <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewUserModal" data-id="U005">
                        <i class="bi bi-eye"></i>
                      </button>
                      <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#editUserModal" data-id="U005">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-id="U005">
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="row">
      <div class="col-md-6 mb-3">
        <div class="d-flex align-items-center">
          <span class="me-2">Hiển thị</span>
          <select class="form-select form-select-sm w-auto">
            <option value="10">10</option>
            <option value="25" selected>25</option>
            <option value="50">50</option>
            <option value="100">100</option>
          </select>
          <span class="ms-2">thể loại trên mỗi trang</span>
        </div>
      </div>
      <div class="col-md-6">
        <nav aria-label="Page navigation">
          <ul class="pagination justify-content-end">
            <li class="page-item disabled">
              <a class="page-link" href="#" aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
              </a>
            </li>
            <li class="page-item active">
              <a class="page-link" href="#">1</a>
            </li>
            <li class="page-item"><a class="page-link" href="#">2</a></li>
            <li class="page-item">
              <a class="page-link" href="#" aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>

    <!-- Modal Thêm người dùng -->
    <div class="modal fade" id="addUserModal" tabindex="-1" aria-labelledby="addUserModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="addUserModalLabel">
              Thêm người dùng mới
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form id="addUserForm" method="POST" action="#" enctype="multipart/form-data">
              <!-- Thông báo thành công/lỗi -->
              <div id="successMessage" class="alert alert-success mb-3" role="alert" style="display: none"></div>
              <div id="errorMessage" class="alert alert-danger mb-3" role="alert" style="display: none"></div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="user-firstName" class="form-label">Họ <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" id="user-firstName" name="firstName" required />
                  <div class="invalid-feedback"></div>
                </div>
                <div class="col-md-6">
                  <label for="user-lastName" class="form-label">Tên <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" id="user-lastName" name="lastName" required />
                  <div class="invalid-feedback"></div>
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="user-email" class="form-label">Email <span class="text-danger">*</span></label>
                  <input type="email" class="form-control" id="user-email" name="email" required />
                  <div class="invalid-feedback"></div>
                </div>
                <div class="col-md-6">
                  <label for="user-phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                  <input type="tel" class="form-control" id="user-phone" name="phone" required />
                  <div class="invalid-feedback"></div>
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="user-password" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                  <input type="password" class="form-control" id="user-password" name="password" required />
                  <div class="invalid-feedback"></div>
                </div>
                <div class="col-md-6">
                  <label for="user-confirmPassword" class="form-label">Xác nhận mật khẩu
                    <span class="text-danger">*</span></label>
                  <input type="password" class="form-control" id="user-confirmPassword" name="confirmPassword" required />
                  <div class="invalid-feedback"></div>
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="user-role" class="form-label">Vai trò <span class="text-danger">*</span></label>
                  <select class="form-select" id="user-role" name="role" required>
                    <option value="" selected disabled>Chọn vai trò</option>
                    <option value="admin">Quản trị viên</option>
                    <option value="staff">Nhân viên</option>
                    <option value="customer">Khách hàng</option>
                  </select>
                  <div class="invalid-feedback"></div>
                </div>
                <div class="col-md-6">
                  <label for="user-status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                  <select class="form-select" id="user-status" name="status" required>
                    <option value="active" selected>Đang hoạt động</option>
                    <option value="inactive">Không hoạt động</option>
                    <option value="locked">Bị khóa</option>
                  </select>
                  <div class="invalid-feedback"></div>
                </div>
              </div>

              <div class="mb-3">
                <label for="user-address" class="form-label">Địa chỉ</label>
                <textarea class="form-control" id="user-address" name="address" rows="2"></textarea>
              </div>

              <div class="mb-3">
                <label for="user-avatar" class="form-label">Ảnh đại diện</label>
                <input type="file" class="form-control" id="user-avatar" name="avatar" accept="image/*" />
                <div class="mt-2" id="avatarPreview" style="display: none">
                  <img src="" alt="Preview" class="img-thumbnail" style="max-height: 150px" />
                </div>
              </div>

              <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="user-sendWelcomeEmail" name="sendWelcomeEmail" checked />
                <label class="form-check-label" for="user-sendWelcomeEmail">Gửi email chào mừng với thông tin đăng nhập</label>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button type="button" class="btn btn-primary" id="saveUserBtn">
              <i class="bi bi-save me-1"></i> Lưu người dùng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Xem chi tiết -->
    <div class="modal fade" id="viewUserModal" tabindex="-1" aria-labelledby="viewUserModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="viewUserModalLabel">
              Thông tin người dùng
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-4 text-center mb-4">
                <img id="viewUserAvatar" src="https://via.placeholder.com/150" alt="Avatar" class="img-fluid rounded-circle mb-3" style="width: 150px; height: 150px; object-fit: cover" />
                <div id="viewUserStatus" class="badge bg-success mb-2">
                  Đang hoạt động
                </div>
                <h5 id="viewUserName" class="mb-1">Nguyễn Văn Admin</h5>
                <div id="viewUserRole" class="badge bg-danger mb-3">
                  Quản trị viên
                </div>
                <div class="d-grid">
                  <button class="btn btn-outline-primary btn-sm" id="editFromViewBtn" data-bs-toggle="modal" data-bs-target="#editUserModal">
                    <i class="bi bi-pencil me-1"></i> Chỉnh sửa
                  </button>
                </div>
              </div>
              <div class="col-md-8">
                <div class="card mb-3">
                  <div class="card-header">
                    <h6 class="mb-0">Thông tin cá nhân</h6>
                  </div>
                  <div class="card-body">
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">ID:</div>
                      <div class="col-md-8" id="viewUserId">#U001</div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Email:</div>
                      <div class="col-md-8" id="viewUserEmail">
                        admin@example.com
                      </div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Số điện thoại:</div>
                      <div class="col-md-8" id="viewUserPhone">
                        0123456789
                      </div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Địa chỉ:</div>
                      <div class="col-md-8" id="viewUserAddress">
                        123 Đường Quản Trị, Quận 1, TP.HCM
                      </div>
                    </div>
                  </div>
                </div>

                <div class="card mb-3">
                  <div class="card-header">
                    <h6 class="mb-0">Thông tin tài khoản</h6>
                  </div>
                  <div class="card-body">
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Ngày tạo:</div>
                      <div class="col-md-8" id="viewUserCreated">
                        01/01/2025
                      </div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Đăng nhập cuối:</div>
                      <div class="col-md-8" id="viewUserLastLogin">
                        14/03/2025, 08:45
                      </div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Số đơn hàng:</div>
                      <div class="col-md-8" id="viewUserOrders">15</div>
                    </div>
                    <div class="row mb-2">
                      <div class="col-md-4 fw-bold">Tổng chi tiêu:</div>
                      <div class="col-md-8" id="viewUserSpent">
                        12,500,000đ
                      </div>
                    </div>
                  </div>
                </div>

                <div class="card">
                  <div class="card-header">
                    <h6 class="mb-0">Ghi chú</h6>
                  </div>
                  <div class="card-body">
                    <p id="viewUserNotes" class="mb-0">
                      Quản trị viên hệ thống chính với quyền hạn cao nhất.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Chỉnh Sửa -->
    <div class="modal fade" id="editUserModal" tabindex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header bg-primary text-white">
            <h5 class="modal-title" id="editUserModalLabel">
              Chỉnh sửa người dùng
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form id="editUserForm" method="POST" action="#" enctype="multipart/form-data">
              <input type="hidden" id="edit-userId" name="id" value="" />

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="edit-firstName" class="form-label">Họ <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" id="edit-firstName" name="firstName" required />
                </div>
                <div class="col-md-6">
                  <label for="edit-lastName" class="form-label">Tên <span class="text-danger">*</span></label>
                  <input type="text" class="form-control" id="edit-lastName" name="lastName" required />
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="edit-email" class="form-label">Email <span class="text-danger">*</span></label>
                  <input type="email" class="form-control" id="edit-email" name="email" required />
                </div>
                <div class="col-md-6">
                  <label for="edit-phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                  <input type="tel" class="form-control" id="edit-phone" name="phone" required />
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="edit-password" class="form-label">Mật khẩu mới</label>
                  <input type="password" class="form-control" id="edit-password" name="password" placeholder="Để trống nếu không thay đổi" />
                </div>
                <div class="col-md-6">
                  <label for="edit-confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                  <input type="password" class="form-control" id="edit-confirmPassword" name="confirmPassword" placeholder="Để trống nếu không thay đổi" />
                </div>
              </div>

              <div class="row mb-3">
                <div class="col-md-6">
                  <label for="edit-role" class="form-label">Vai trò <span class="text-danger">*</span></label>
                  <select class="form-select" id="edit-role" name="role" required>
                    <option value="" disabled>Chọn vai trò</option>
                    <option value="admin">Quản trị viên</option>
                    <option value="staff">Nhân viên</option>
                    <option value="customer">Khách hàng</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label for="edit-status" class="form-label">Trạng thái <span class="text-danger">*</span></label>
                  <select class="form-select" id="edit-status" name="status" required>
                    <option value="active">Đang hoạt động</option>
                    <option value="inactive">Không hoạt động</option>
                    <option value="locked">Bị khóa</option>
                  </select>
                </div>
              </div>

              <div class="mb-3">
                <label for="edit-address" class="form-label">Địa chỉ</label>
                <textarea class="form-control" id="edit-address" name="address" rows="2"></textarea>
              </div>

              <div class="mb-3">
                <label for="edit-avatar" class="form-label">Ảnh đại diện</label>
                <input type="file" class="form-control" id="edit-avatar" name="avatar" accept="image/*" />
                <div class="mt-2" id="editAvatarPreview">
                  <img src="" alt="Preview" class="img-thumbnail" style="max-height: 150px" />
                  <small class="d-block text-muted">Để trống nếu không muốn thay đổi ảnh đại diện.</small>
                </div>
              </div>

              <div class="mb-3">
                <label for="edit-notes" class="form-label">Ghi chú</label>
                <textarea class="form-control" id="edit-notes" name="notes" rows="3"></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button type="button" class="btn btn-success" id="resetPasswordBtn">
              <i class="bi bi-key me-1"></i> Đặt lại mật khẩu
            </button>
            <button type="button" class="btn btn-primary" id="updateUserBtn">
              <i class="bi bi-save me-1"></i> Cập nhật
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Xác Nhận Xóa Người Dùng -->
    <div class="modal fade" id="deleteUserModal" tabindex="-1" aria-labelledby="deleteUserModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title" id="deleteUserModalLabel">
              Xác nhận xóa người dùng
            </h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>
              Bạn có chắc chắn muốn xóa người dùng
              <strong id="deleteUserName"></strong>?
            </p>
            <p class="text-danger">
              Lưu ý: Hành động này không thể hoàn tác. Tất cả dữ liệu liên
              quan đến người dùng này sẽ bị xóa vĩnh viễn.
            </p>

            <div class="form-check mb-3">
              <input class="form-check-input" type="checkbox" id="confirmUserDeletion" required />
              <label class="form-check-label" for="confirmUserDeletion">
                Tôi hiểu rằng hành động này không thể hoàn tác
              </label>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button type="button" class="btn btn-danger" id="confirmDeleteUserBtn" disabled>
              <i class="bi bi-trash me-1"></i> Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Đặt Lại Mật Khẩu -->
    <div class="modal fade" id="resetPasswordModal" tabindex="-1" aria-labelledby="resetPasswordModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-warning">
            <h5 class="modal-title" id="resetPasswordModalLabel">
              Đặt lại mật khẩu
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>
              Bạn có chắc chắn muốn đặt lại mật khẩu cho người dùng
              <strong id="resetPasswordUserName"></strong>?
            </p>

            <div class="mb-3">
              <label for="newPassword" class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
              <div class="input-group">
                <input type="password" class="form-control" id="newPassword" required />
                <button class="btn btn-outline-secondary" type="button" id="togglePasswordBtn">
                  <i class="bi bi-eye"></i>
                </button>
              </div>
              <div class="form-text">
                Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ
                thường và số.
              </div>
            </div>

            <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="sendPasswordEmail" checked />
              <label class="form-check-label" for="sendPasswordEmail">Gửi email thông báo đến người dùng</label>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Hủy
            </button>
            <button type="button" class="btn btn-warning" id="confirmResetPasswordBtn">
              <i class="bi bi-check-lg me-1"></i> Xác nhận
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <footer class="bg-white text-center py-3 mt-5 rounded">
      <p class="mb-0 text-muted">
        © 2025 Hệ Thống Quản Lý. Thiết kế bởi
        <a href="#" class="text-decoration-none">BookShopTeam</a>
      </p>
    </footer>
  </div>
</div>

<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- Mẫu JavaScript cho xử lý trang Quản lý người dùng -->
<script src="${pageContext.request.contextPath}/js/admin2/user/user.js"></script>
</body>

</html>