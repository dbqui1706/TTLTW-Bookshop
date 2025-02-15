<%--
  Created by IntelliJ IDEA.
  User: DELL
  Date: 12/2/2024
  Time: 2:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <c:if test="${not empty sessionScope.orderInfo}">
        <meta name="orderInfo" content="${sessionScope.orderInfo}"/>
    </c:if>

    <title>Trang chủ</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/axios/1.7.8/axios.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/toast.js" type="module"></script>
</head>
<body>

<jsp:include page="/common/client/header.jsp"/>
<section class="section-content padding-y">
    <div class="container py-4">
        <c:choose>
        <c:when test="${empty sessionScope.currentUser}">
            <div class="container">
                <div class="row">
                    <p>
                        Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng nhập</a> để sử dụng trang này.
                    </p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
        <div class="row">
            <!-- Left Column - Form -->
            <div class="col-md-8">
                <div class="card">
                    <div class="card-body">
                        <div
                                class="d-flex justify-content-between align-items-center mb-4"
                        >
                            <h5 class="mb-0">THÔNG TIN GIAO HÀNG</h5>
                        </div>

                        <form id="id-order-form">
                            <div class="mb-3">
                                <label class="form-label text-muted small">HỌ VÀ TÊN</label>
                                <input type="text" class="form-control" id="full-name-input" name="fullName"/>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted small">EMAIL</label>
                                    <input type="email" class="form-control" id="email-input" name="email"/>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted small"
                                    >SỐ ĐIỆN THOẠI</label
                                    >
                                    <input type="tel" class="form-control" id="phone" name="phone"/>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-muted small">ĐỊA CHỈ</label>
                                <input type="text" class="form-control" id="address"/>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted small"
                                    >TỈNH / THÀNH</label
                                    >
                                    <select class="form-select" id="city" name="city">
                                        <option>Chọn Tỉnh/TP</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted small"
                                    >QUẬN / HUYỆN</label
                                    >
                                    <select class="form-select" id="district" name="district">
                                        <option>Chọn Quận/Huyện</option>
                                    </select>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-muted small">PHƯỜNG / XÃ</label>
                                <select class="form-select" id="ward" name="ward">
                                    <option>Chọn Phường/Xã</option>
                                </select>
                            </div>

                            <div class="card mb-4">
                                <div class="card-body">
                                    <h6>PHƯƠNG THỨC VẬN CHUYỂN</h6>
                                    <div class="form-check">
                                        <input
                                                class="form-check-input"
                                                type="radio"
                                                name="delivery"
                                                value="1"
                                                checked
                                        />
                                        <label
                                                class="form-check-label d-flex justify-content-between w-100"
                                        >
                                            <span>Giao hàng tiêu chuẩn</span>
                                            <span>15.000đ</span>
                                        </label>
                                    </div>
                                    <div class="form-check">
                                        <input
                                                class="form-check-input"
                                                type="radio"
                                                name="delivery"
                                                value="2"
                                        />
                                        <label
                                                class="form-check-label d-flex justify-content-between w-100"
                                        >
                                            <span>Giao hàng nhanh</span>
                                            <span>50.000đ</span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="d-flex justify-content-center mb-2 mt-4">
                                <div class="p-2">
                                    <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-dark"
                                    >QUAY LẠI GIỎ HÀNG</a
                                    >
                                </div>
                                <div class="p-2">
                                    <button id="btn-complete-order" class="btn btn-dark">
                                        HOÀN TẤT ĐƠN HÀNG
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Right Column - Order Summary -->
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex flex-row align-items-center mb-3">
                            <h5 class="pe-2">THÔNG TIN ĐƠN HÀNG</h5>
                            <div id="show-detail">
                                <svg
                                        xmlns="http://www.w3.org/2000/svg"
                                        width="16"
                                        height="16"
                                        fill="currentColor"
                                        class="bi bi-chevron-compact-down"
                                        viewBox="0 0 16 16"
                                >
                                    <path
                                            fill-rule="evenodd"
                                            d="M1.553 6.776a.5.5 0 0 1 .67-.223L8 9.44l5.776-2.888a.5.5 0 1 1 .448.894l-6 3a.5.5 0 0 1-.448 0l-6-3a.5.5 0 0 1-.223-.67"
                                    />
                                </svg>
                            </div>
                        </div>

                        <div class="card mb-3">
                            <div id="id-card-products" class="card-body">
                            </div>
                            <div class="border-top p-3" id="summary">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/js/order_info.js"></script>
<jsp:include page="/common/client/footer.jsp"/>

<div class="toast-container position-fixed bottom-0 start-0 p-3"></div> <!-- toast-container.// -->
</body>

</html>