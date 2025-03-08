<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">
<script src="
https://cdn.jsdelivr.net/npm/sweetalert2@11.14.5/dist/sweetalert2.all.min.js
"></script>
<link href="
https://cdn.jsdelivr.net/npm/sweetalert2@11.14.5/dist/sweetalert2.min.css
" rel="stylesheet">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <title>Đổi mật khẩu</title>
</head>

<body>
<jsp:include page="/common/client/header.jsp"/>

<section class="section py-2 bg-light">
    <div class="container">
        <h2 class="title-page">Sinh khóa</h2>
    </div> <!-- container.// -->
</section> <!-- section-pagetop.// -->

<section class="section-content padding-y">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${empty sessionScope.currentUser}">
                    <p>
                        Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng nhập</a> để sử dụng chức năng
                        thiết đặt.
                    </p>
                </c:when>
                <c:otherwise>
                    <jsp:include page="/common/client/navPanel.jsp">
                        <jsp:param name="active" value="KEY"/>
                    </jsp:include>
                    <main class="col-md-9">
                        <article class="card">
                            <div class="card-body">
                                    <%--Tạo giúp tôi các alert thông báo cho người dùng được không --%>
                                <c:if test="${not empty requestScope.INFO}">
                                    <div class="alert alert-info" role="alert">${requestScope.INFO}</div>
                                </c:if>
                                <c:if test="${not empty requestScope.error}">
                                    <div class="alert alert-info" role="alert">${requestScope.error}</div>
                                </c:if>

                                <div class="d-flex flex-column mb-3">
                                    <p><label for="public-key">Khóa công khai:</label></p>
                                    <textarea id="public-key" rows="10" cols="40" name="public-key"><c:if
                                            test="${not empty requestScope.publicKey}"><c:out
                                            value="${requestScope.publicKey}"/></c:if></textarea>
                                </div>
                                <div class="d-flex flex-row mb-3">
                                    <form action="${pageContext.request.contextPath}/key-generate" method="post">
                                        <button type="submit" class="btn btn-primary me-2 mb-3">Tạo khóa</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/key-import" method="post"
                                          id="import-key-form">
                                        <input type="hidden" id="key-import" name="publicKey">
                                        <button type="submit" class="btn btn-primary me-2 mb-3">Nhập khóa</button>
                                    </form>
                                    <form id="cancelKeyForm" action="${pageContext.request.contextPath}/key-cancel"
                                          method="post">
                                        <button type="button" id="cancelKeyButton" class="btn btn-danger mb-3">Khóa bị
                                            lộ
                                        </button>
                                    </form>
                                    <script>
                                        document.getElementById("cancelKeyButton").addEventListener("click", function () {
                                            const button = document.getElementById("cancelKeyButton"); // Lấy button
                                            button.disabled = true; // Khóa button

                                            // Lấy form và URL
                                            const form = document.getElementById("cancelKeyForm");
                                            const formData = new FormData(form);
                                            const actionUrl = form.action;

                                            // Gửi form bằng AJAX
                                            fetch(actionUrl, {
                                                method: 'POST',
                                                body: formData
                                            })
                                                .then(response => {
                                                    if (response.ok) {
                                                        // Hiển thị thông báo thành công
                                                        Swal.fire({
                                                            title: 'Thành công!',
                                                            text: 'Gửi email thành công, vui lòng kiểm tra!',
                                                            icon: 'success',
                                                            timer: 2000,
                                                            showConfirmButton: false
                                                        });
                                                    } else {
                                                        throw new Error('Có lỗi xảy ra!');
                                                    }
                                                })
                                                .catch(error => {
                                                    Swal.fire({
                                                        title: 'Lỗi!',
                                                        text: 'Không thể gửi email. Vui lòng thử lại!',
                                                        icon: 'error',
                                                        confirmButtonText: 'OK'
                                                    });
                                                })
                                                .finally(() => {
                                                    button.disabled = false; // Mở lại button
                                                });
                                        });
                                    </script>
                                </div>
                            </div> <!-- card-body.// -->
                        </article>
                    </main>
                    <!-- col.// -->
                    <script>
                        const importForm = document.getElementById('import-key-form');
                        importForm.addEventListener('submit', (event) => {
                            const publicKey = document.getElementById('public-key').value;
                            event.preventDefault();
                            if (publicKey.trim() === "") {
                                alert("Vui lòng nhập dữ liệu trước khi gửi!");
                            } else {
                                document.getElementById('key-import').value = publicKey;
                                importForm.submit();
                            }
                        });
                    </script>
                </c:otherwise>
            </c:choose>
        </div> <!-- row.// -->
    </div> <!-- container.// -->
</section> <!-- section-content.// -->
<jsp:include page="/common/client/footer.jsp"/>
</body>
</html>
