<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css"/>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin/order.js"></script>
    <title>Quản lý đơn hàng</title>
</head>

<body>
<jsp:include page="/common/admin/header.jsp"/>

<section class="section-content">
    <div class="container">
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success mb-0 mt-4" role="alert">
                    ${sessionScope.successMessage}
            </div>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="alert alert-danger mb-0 mt-4" role="alert">
                    ${sessionScope.errorMessage}
            </div>
        </c:if>
        <c:remove var="successMessage" scope="session"/>
        <c:remove var="errorMessage" scope="session"/>

        <header class="section-heading py-4">
            <h3 class="section-title">Quản lý đơn hàng</h3>
        </header> <!-- section-heading.// -->
        <!-- Vùng chứa các thẻ <a> lọc ra các đơn hàng chưa verify, thay đổi, chưa xác thực -->
        <div class="mb-3">
            <a target="_blank" href="${pageContext.request.contextPath}/admin/orders/filter?status=0" class="btn btn-outline-primary">Đã xác thực</a>
            <a target="_blank" href="${pageContext.request.contextPath}/admin/orders/filter?status=1" class="btn btn-outline-primary">Chưa xác thực</a>
        </div>
        <main class="table-responsive-xl mb-5">
            <table id="ordersTable" class="table table-bordered table-striped table-hover align-middle">
                <thead>
                <tr>
                    <th>Mã</th>
                    <th>Người dùng</th>
                    <th>Ngày tạo</th>
                    <th>Ngày cập nhật</th>
                    <th>Số sản phẩm</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                    <th>Xác thực</th>
                    <th style="width: 220px;">Thao tác</th>
                </tr>
                </thead>
                <tbody></tbody>
            </table>
        </main>

    </div> <!-- container.// -->
</section> <!-- section-content.// -->

<jsp:include page="/common/admin/footer.jsp"/>

</body>

</html>
