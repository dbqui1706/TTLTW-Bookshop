<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản Trị Hệ Thống</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"/>

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/home.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin/user/feedback.css"/>
</head>

<body>
<div class="wrapper">

    <!-- Sidebar -->
    <jsp:include page="${pageContext.request.contextPath}/common/admin2/sidebar.jsp">
        <jsp:param name="active" value="FEEDBACK"/>
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
                            <h2>Quản lý phản hồi từ người dùng</h2>
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
                                    <input type="text" class="form-control" id="searchFeedback" placeholder="Tìm kiếm phản hồi" />
                                    <label for="searchFeedback">Tìm kiếm phản hồi</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="statusFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="new">Mới</option>
                                        <option value="processing">Đang xử lý</option>
                                        <option value="resolved">Đã giải quyết</option>
                                        <option value="closed">Đã đóng</option>
                                    </select>
                                    <label for="statusFilter">Trạng thái</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="typeFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="question">Câu hỏi</option>
                                        <option value="suggestion">Góp ý</option>
                                        <option value="complaint">Khiếu nại</option>
                                        <option value="praise">Khen ngợi</option>
                                        <option value="bug">Lỗi hệ thống</option>
                                    </select>
                                    <label for="typeFilter">Loại phản hồi</label>
                                </div>
                            </div>
                            <div class="col-lg-2 col-md-6">
                                <div class="form-floating">
                                    <select class="form-select" id="priorityFilter">
                                        <option value="" selected>Tất cả</option>
                                        <option value="high">Cao</option>
                                        <option value="medium">Trung bình</option>
                                        <option value="low">Thấp</option>
                                    </select>
                                    <label for="priorityFilter">Mức độ ưu tiên</label>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-12 text-end">
                                <button class="btn btn-success me-2" data-bs-toggle="tooltip" data-bs-placement="top" title="Xuất danh sách phản hồi">
                                    <i class="bi bi-file-earmark-arrow-up"></i> Export Excel
                                </button>
                                <button id="refreshBtn" class="btn btn-primary">
                                    <i class="bi bi-arrow-clockwise"></i> Làm mới
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
                                    <i class="bi bi-chat-dots fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Tổng phản hồi</h6>
                                <h3 class="mb-0">245</h3>
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
                                    <i class="bi bi-exclamation-triangle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Chưa xử lý</h6>
                                <h3 class="mb-0">42</h3>
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
                                <div class="stat-icon bg-warning bg-opacity-10 p-3 rounded text-warning">
                                    <i class="bi bi-hourglass-split fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Đang xử lý</h6>
                                <h3 class="mb-0">36</h3>
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
                                    <i class="bi bi-check-circle fs-3"></i>
                                </div>
                            </div>
                            <div class="flex-grow-1 ms-3">
                                <h6 class="mb-1">Đã giải quyết</h6>
                                <h3 class="mb-0">167</h3>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Table -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                <tr>
                                    <th width="50">ID</th>
                                    <th width="180">Người gửi</th>
                                    <th>Tiêu đề/Nội dung</th>
                                    <th width="120">Loại</th>
                                    <th width="120">Mức ưu tiên</th>
                                    <th width="120">Trạng thái</th>
                                    <th width="150">Ngày gửi</th>
                                    <th width="150">Thao tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- Phản hồi 1 - Mới -->
                                <tr>
                                    <td>#FB001</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle me-2" />
                                            <div>
                                                <div class="fw-bold">Nguyễn Văn A</div>
                                                <small class="text-muted">nguyenvana@example.com</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="fw-bold">Sự cố khi đặt hàng</div>
                                        <div class="text-muted text-truncate" style="max-width: 300px">
                                            Tôi không thể hoàn tất thanh toán khi đặt hàng. Hệ
                                            thống báo lỗi...
                                        </div>
                                    </td>
                                    <td><span class="badge bg-danger">Khiếu nại</span></td>
                                    <td><span class="badge bg-danger">Cao</span></td>
                                    <td><span class="badge bg-info">Mới</span></td>
                                    <td>14/03/2025, 09:45</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewFeedbackModal" data-id="FB001">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal" data-id="FB001">
                                                <i class="bi bi-reply"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#closeFeedbackModal" data-id="FB001">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Phản hồi 2 - Đang xử lý -->
                                <tr>
                                    <td>#FB002</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle me-2" />
                                            <div>
                                                <div class="fw-bold">Trần Thị B</div>
                                                <small class="text-muted">tranthib@example.com</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="fw-bold">
                                            Góp ý cải thiện tính năng tìm kiếm sách
                                        </div>
                                        <div class="text-muted text-truncate" style="max-width: 300px">
                                            Tôi thấy tính năng tìm kiếm sách hiện tại khá khó sử
                                            dụng...
                                        </div>
                                    </td>
                                    <td><span class="badge bg-success">Góp ý</span></td>
                                    <td>
                                        <span class="badge bg-warning text-dark">Trung bình</span>
                                    </td>
                                    <td>
                                        <span class="badge bg-warning text-dark">Đang xử lý</span>
                                    </td>
                                    <td>12/03/2025, 14:20</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewFeedbackModal" data-id="FB002">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal" data-id="FB002">
                                                <i class="bi bi-reply"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#closeFeedbackModal" data-id="FB002">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Phản hồi 3 - Đã giải quyết -->
                                <tr>
                                    <td>#FB003</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle me-2" />
                                            <div>
                                                <div class="fw-bold">Lê Văn C</div>
                                                <small class="text-muted">levanc@example.com</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="fw-bold">
                                            Câu hỏi về chính sách đổi trả sách
                                        </div>
                                        <div class="text-muted text-truncate" style="max-width: 300px">
                                            Tôi muốn hỏi về chính sách đổi trả sách khi phát
                                            hiện sách bị lỗi...
                                        </div>
                                    </td>
                                    <td><span class="badge bg-primary">Câu hỏi</span></td>
                                    <td><span class="badge bg-secondary">Thấp</span></td>
                                    <td>
                                        <span class="badge bg-success">Đã giải quyết</span>
                                    </td>
                                    <td>10/03/2025, 10:15</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewFeedbackModal" data-id="FB003">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal" data-id="FB003">
                                                <i class="bi bi-reply"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#closeFeedbackModal" data-id="FB003">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Phản hồi 4 - Đã đóng -->
                                <tr class="table-light">
                                    <td>#FB004</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle me-2" />
                                            <div>
                                                <div class="fw-bold">Phạm Thị D</div>
                                                <small class="text-muted">phamthid@example.com</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="fw-bold">
                                            Khen ngợi nhân viên giao hàng
                                        </div>
                                        <div class="text-muted text-truncate" style="max-width: 300px">
                                            Tôi rất hài lòng với thái độ phục vụ của nhân viên
                                            giao hàng...
                                        </div>
                                    </td>
                                    <td><span class="badge bg-info">Khen ngợi</span></td>
                                    <td><span class="badge bg-secondary">Thấp</span></td>
                                    <td><span class="badge bg-secondary">Đã đóng</span></td>
                                    <td>08/03/2025, 16:40</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewFeedbackModal" data-id="FB004">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal" data-id="FB004" disabled>
                                                <i class="bi bi-reply"></i>
                                            </button>
                                            <button class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#reopenFeedbackModal" data-id="FB004">
                                                <i class="bi bi-arrow-counterclockwise"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                <!-- Phản hồi 5 - Đang xử lý (Cao) -->
                                <tr>
                                    <td>#FB005</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <img src="https://via.placeholder.com/40" alt="Avatar" class="rounded-circle me-2" />
                                            <div>
                                                <div class="fw-bold">Hoàng Văn E</div>
                                                <small class="text-muted">hoangvane@example.com</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="fw-bold">
                                            Phát hiện lỗi hiển thị thông tin sách
                                        </div>
                                        <div class="text-muted text-truncate" style="max-width: 300px">
                                            Tôi phát hiện một số thông tin sách bị hiển thị sai,
                                            như giá tiền và tác giả...
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-danger">Lỗi hệ thống</span>
                                    </td>
                                    <td><span class="badge bg-danger">Cao</span></td>
                                    <td>
                                        <span class="badge bg-warning text-dark">Đang xử lý</span>
                                    </td>
                                    <td>07/03/2025, 11:30</td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#viewFeedbackModal" data-id="FB005">
                                                <i class="bi bi-eye"></i>
                                            </button>
                                            <button class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal" data-id="FB005">
                                                <i class="bi bi-reply"></i>
                                            </button>
                                            <button class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#closeFeedbackModal" data-id="FB005">
                                                <i class="bi bi-x-lg"></i>
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

        <!-- Modal Xem Chi Tiết Phản Hồi -->
        <div class="modal fade" id="viewFeedbackModal" tabindex="-1" aria-labelledby="viewFeedbackModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="viewFeedbackModalLabel">
                            Chi tiết phản hồi
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="badge bg-danger">Khiếu nại</span>
                            <span class="text-muted">ID: #FB001</span>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-8">
                                <h5 class="fw-bold">Sự cố khi đặt hàng</h5>
                            </div>
                            <div class="col-md-4 text-md-end">
                                <span class="badge bg-info">Mới</span>
                                <span class="badge bg-danger ms-1">Ưu tiên cao</span>
                            </div>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <div class="d-flex align-items-center">
                                    <img src="https://via.placeholder.com/50" alt="Avatar" class="rounded-circle me-2" />
                                    <div>
                                        <div class="fw-bold">Nguyễn Văn A</div>
                                        <div class="text-muted">nguyenvana@example.com</div>
                                        <div class="text-muted">0987654321</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 text-md-end">
                                <div class="text-muted">Gửi lúc: 14/03/2025, 09:45</div>
                                <div class="text-muted">
                                    Cập nhật cuối: 14/03/2025, 10:30
                                </div>
                            </div>
                        </div>

                        <div class="card mb-4">
                            <div class="card-body bg-light">
                                <h6 class="mb-3">Nội dung phản hồi:</h6>
                                <p>
                                    Tôi không thể hoàn tất thanh toán khi đặt hàng. Hệ thống
                                    báo lỗi "Không thể xử lý giao dịch vào lúc này". Tôi đã
                                    thử nhiều lần trong 2 ngày qua nhưng vẫn gặp lỗi tương tự.
                                </p>
                                <p>
                                    Tôi đã kiểm tra thẻ của mình và đảm bảo rằng thẻ vẫn hoạt
                                    động bình thường trên các website khác. Đây là lỗi từ phía
                                    hệ thống của các bạn.
                                </p>
                                <p>
                                    Mong nhận được phản hồi sớm vì tôi cần đặt những cuốn sách
                                    này gấp.
                                </p>

                                <div class="mt-3">
                                    <h6 class="mb-2">File đính kèm:</h6>
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-file-earmark-image fs-4 me-2"></i>
                                        <a href="#" class="text-decoration-none">loi-thanh-toan.jpg</a>
                                        <span class="ms-2 text-muted">(250KB)</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="border-top pt-3 mb-3">
                            <h6 class="mb-3">Lịch sử phản hồi:</h6>

                            <!-- Phản hồi từ Admin -->
                            <div class="card mb-3 border-primary">
                                <div class="card-header bg-primary bg-opacity-10 d-flex justify-content-between align-items-center">
                                    <div>
                                        <img src="https://via.placeholder.com/30" alt="Avatar" class="rounded-circle me-2" />
                                        <span class="fw-bold">Admin (Nguyễn Thị Hỗ Trợ)</span>
                                    </div>
                                    <small class="text-muted">14/03/2025, 10:30</small>
                                </div>
                                <div class="card-body">
                                    <p>Chào anh Nguyễn Văn A,</p>
                                    <p>
                                        Cảm ơn anh đã gửi phản hồi về vấn đề thanh toán. Chúng
                                        tôi rất tiếc về trải nghiệm không tốt này.
                                    </p>
                                    <p>
                                        Hiện tại chúng tôi đã ghi nhận sự cố và đang tiến hành
                                        kiểm tra hệ thống thanh toán. Đội kỹ thuật sẽ khắc phục
                                        sớm nhất có thể.
                                    </p>
                                    <p>
                                        Trong thời gian chờ đợi, anh có thể thử sử dụng phương
                                        thức thanh toán khác như chuyển khoản ngân hàng hoặc
                                        thanh toán khi nhận hàng.
                                    </p>
                                    <p>
                                        Chúng tôi sẽ cập nhật tình hình xử lý đến anh trong vòng
                                        24 giờ tới.
                                    </p>
                                    <p>
                                        Trân trọng,<br />Nguyễn Thị Hỗ Trợ<br />Bộ phận CSKH
                                    </p>
                                </div>
                            </div>

                            <!-- Phản hồi từ khách hàng -->
                            <div class="card mb-3 border-info">
                                <div class="card-header bg-info bg-opacity-10 d-flex justify-content-between align-items-center">
                                    <div>
                                        <img src="https://via.placeholder.com/30" alt="Avatar" class="rounded-circle me-2" />
                                        <span class="fw-bold">Nguyễn Văn A</span>
                                    </div>
                                    <small class="text-muted">14/03/2025, 11:15</small>
                                </div>
                                <div class="card-body">
                                    <p>
                                        Cảm ơn bạn đã phản hồi nhanh chóng. Tôi sẽ thử phương
                                        thức thanh toán khác và chờ thông tin từ bạn.
                                    </p>
                                    <p>
                                        Tuy nhiên, tôi vẫn muốn được thanh toán qua thẻ nên mong
                                        đội kỹ thuật sẽ khắc phục sớm.
                                    </p>
                                </div>
                            </div>
                        </div>

                        <div class="card mb-3">
                            <div class="card-header">
                                <h6 class="mb-0">Ghi chú nội bộ</h6>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <textarea class="form-control" id="internalNote" rows="3" placeholder="Thêm ghi chú nội bộ..."></textarea>
                                </div>
                                <button class="btn btn-secondary btn-sm">
                                    Lưu ghi chú
                                </button>

                                <div class="mt-3">
                                    <div class="d-flex mb-2">
                                        <div class="fw-bold me-2">Trần Kỹ Thuật</div>
                                        <div class="text-muted me-auto">14/03/2025, 10:15</div>
                                    </div>
                                    <p class="mb-0">
                                        Đã kiểm tra và phát hiện lỗi ở cổng thanh toán. Đang làm
                                        việc với đối tác để khắc phục. Dự kiến hoàn thành trong
                                        2-3 giờ.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div class="d-flex justify-content-between w-100">
                            <div>
                                <button type="button" class="btn btn-outline-secondary me-2" data-bs-dismiss="modal">
                                    Đóng
                                </button>
                                <button type="button" class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#closeFeedbackModal">
                                    <i class="bi bi-x-lg me-1"></i> Đóng phản hồi
                                </button>
                            </div>
                            <div>
                                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#replyFeedbackModal">
                                    <i class="bi bi-reply me-1"></i> Trả lời
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Trả Lời Phản Hồi -->
        <div class="modal fade" id="replyFeedbackModal" tabindex="-1" aria-labelledby="replyFeedbackModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="replyFeedbackModalLabel">
                            Trả lời phản hồi #FB001
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <h6>Tiêu đề: Sự cố khi đặt hàng</h6>
                                <span class="badge bg-info">Mới</span>
                            </div>
                            <div class="card bg-light mb-3">
                                <div class="card-body">
                                    <div class="d-flex align-items-center mb-2">
                                        <img src="https://via.placeholder.com/30" alt="Avatar" class="rounded-circle me-2" />
                                        <div>
                                            <span class="fw-bold">Nguyễn Văn A</span>
                                            <small class="text-muted ms-2">14/03/2025, 09:45</small>
                                        </div>
                                    </div>
                                    <p class="mb-0">
                                        Tôi không thể hoàn tất thanh toán khi đặt hàng. Hệ thống
                                        báo lỗi "Không thể xử lý giao dịch vào lúc này"...
                                    </p>
                                </div>
                            </div>
                        </div>

                        <form id="replyForm">
                            <div class="mb-3">
                                <label for="replyContent" class="form-label">Nội dung trả lời</label>
                                <textarea class="form-control" id="replyContent" rows="6" required>
Kính gửi anh/chị Nguyễn Văn A,

    Cảm ơn anh/chị đã gửi phản hồi về vấn đề gặp phải. Chúng tôi rất tiếc về sự bất tiện này.

    </textarea>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="statusUpdate" class="form-label">Cập nhật trạng thái</label>
                                    <select class="form-select" id="statusUpdate">
                                        <option value="new">Giữ nguyên (Mới)</option>
                                        <option value="processing" selected>Đang xử lý</option>
                                        <option value="resolved">Đã giải quyết</option>
                                        <option value="closed">Đóng phản hồi</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label for="priorityUpdate" class="form-label">Mức ưu tiên</label>
                                    <select class="form-select" id="priorityUpdate">
                                        <option value="high" selected>Cao</option>
                                        <option value="medium">Trung bình</option>
                                        <option value="low">Thấp</option>
                                    </select>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="attachments" class="form-label">Đính kèm tệp (nếu có)</label>
                                <input class="form-control" type="file" id="attachments" multiple />
                            </div>

                            <div class="mb-3 form-check">
                                <input class="form-check-input" type="checkbox" id="sendEmailCheck" checked />
                                <label class="form-check-label" for="sendEmailCheck">Gửi email thông báo đến người dùng</label>
                            </div>

                            <div class="mb-3 form-check">
                                <input class="form-check-input" type="checkbox" id="internalNoteCheck" />
                                <label class="form-check-label" for="internalNoteCheck">Thêm ghi chú nội bộ</label>
                            </div>

                            <div id="internalNoteContainer" class="mb-3" style="display: none">
                                <label for="internalNoteText" class="form-label">Ghi chú nội bộ</label>
                                <textarea class="form-control" id="internalNoteText" rows="3" placeholder="Ghi chú này chỉ hiển thị với quản trị viên"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-outline-primary me-2" id="saveAsDraftBtn">
                            <i class="bi bi-save me-1"></i> Lưu nháp
                        </button>
                        <button type="button" class="btn btn-primary" id="sendReplyBtn">
                            <i class="bi bi-send me-1"></i> Gửi phản hồi
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Đóng Phản Hồi -->
        <div class="modal fade" id="closeFeedbackModal" tabindex="-1" aria-labelledby="closeFeedbackModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="closeFeedbackModalLabel">
                            Đóng phản hồi
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>
                            Bạn có chắc chắn muốn đóng phản hồi <strong>#FB001</strong>?
                        </p>

                        <div class="mb-3">
                            <label for="closeReason" class="form-label">Lý do đóng</label>
                            <select class="form-select" id="closeReason" required>
                                <option value="" selected disabled>Chọn lý do...</option>
                                <option value="resolved">Đã giải quyết</option>
                                <option value="duplicate">
                                    Trùng lặp với phản hồi khác
                                </option>
                                <option value="invalid">Không hợp lệ/Spam</option>
                                <option value="outdated">Không còn áp dụng</option>
                                <option value="other">Khác</option>
                            </select>
                        </div>

                        <div class="mb-3" id="otherReasonContainer" style="display: none">
                            <label for="otherReason" class="form-label">Lý do khác</label>
                            <textarea class="form-control" id="otherReason" rows="2"></textarea>
                        </div>

                        <div class="mb-3">
                            <label for="closingNote" class="form-label">Ghi chú khi đóng</label>
                            <textarea class="form-control" id="closingNote" rows="3" placeholder="Ghi chú về lý do đóng phản hồi..."></textarea>
                        </div>

                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="notifyUserClose" checked />
                            <label class="form-check-label" for="notifyUserClose">
                                Thông báo cho người dùng về việc đóng phản hồi
                            </label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-danger" id="confirmCloseBtn">
                            <i class="bi bi-x-lg me-1"></i> Đóng phản hồi
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Mở Lại Phản Hồi -->
        <div class="modal fade" id="reopenFeedbackModal" tabindex="-1" aria-labelledby="reopenFeedbackModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title" id="reopenFeedbackModalLabel">
                            Mở lại phản hồi
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>
                            Bạn có chắc chắn muốn mở lại phản hồi <strong>#FB004</strong>?
                        </p>

                        <div class="mb-3">
                            <label for="reopenStatus" class="form-label">Trạng thái sau khi mở lại</label>
                            <select class="form-select" id="reopenStatus" required>
                                <option value="new">Mới</option>
                                <option value="processing" selected>Đang xử lý</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="reopenNote" class="form-label">Ghi chú khi mở lại</label>
                            <textarea class="form-control" id="reopenNote" rows="3" placeholder="Ghi chú về lý do mở lại phản hồi..."></textarea>
                        </div>

                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="notifyUserReopen" checked />
                            <label class="form-check-label" for="notifyUserReopen">
                                Thông báo cho người dùng về việc mở lại phản hồi
                            </label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-success" id="confirmReopenBtn">
                            <i class="bi bi-arrow-counterclockwise me-1"></i> Mở lại phản
                            hồi
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Chuyển Phản Hồi -->
        <div class="modal fade" id="assignFeedbackModal" tabindex="-1" aria-labelledby="assignFeedbackModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="assignFeedbackModalLabel">
                            Chuyển phản hồi
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>
                            Chuyển phản hồi <strong>#FB001</strong> đến nhân viên hoặc bộ
                            phận khác:
                        </p>

                        <div class="mb-3">
                            <label for="assignDepartment" class="form-label">Chọn bộ phận</label>
                            <select class="form-select" id="assignDepartment">
                                <option value="" selected disabled>Chọn bộ phận...</option>
                                <option value="support">Hỗ trợ khách hàng</option>
                                <option value="technical">Kỹ thuật</option>
                                <option value="product">Quản lý sản phẩm</option>
                                <option value="sales">Kinh doanh</option>
                                <option value="manager">Ban quản lý</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="assignStaff" class="form-label">Chọn nhân viên</label>
                            <select class="form-select" id="assignStaff">
                                <option value="" selected disabled>
                                    Chọn nhân viên...
                                </option>
                                <option value="staff1">Nguyễn Thị Hỗ Trợ</option>
                                <option value="staff2">Trần Văn Kỹ Thuật</option>
                                <option value="staff3">Lê Thị Kinh Doanh</option>
                                <option value="staff4">Phạm Văn Quản Lý</option>
                            </select>
                            <small class="form-text text-muted">Bạn có thể chọn bộ phận hoặc nhân viên cụ thể</small>
                        </div>

                        <div class="mb-3">
                            <label for="assignNote" class="form-label">Ghi chú khi chuyển</label>
                            <textarea class="form-control" id="assignNote" rows="3" placeholder="Ghi chú về lý do chuyển phản hồi..."></textarea>
                        </div>

                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="notifyAssignee" checked />
                            <label class="form-check-label" for="notifyAssignee">
                                Thông báo cho nhân viên được chuyển phản hồi
                            </label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            Hủy
                        </button>
                        <button type="button" class="btn btn-primary" id="confirmAssignBtn">
                            <i class="bi bi-send me-1"></i> Chuyển phản hồi
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
<script src="${pageContext.request.contextPath}/js/admin2/user/feedback.js"></script>
</body>

</html>