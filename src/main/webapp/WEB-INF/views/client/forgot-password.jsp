<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <title>Quên mật khẩu</title>
</head>

<body>
<jsp:include page="/common/client/header.jsp"/>

<section class="section-content" style="margin: 100px 0;">
    <div class="card mx-auto" style="max-width: 500px">
        <div class="card-body">
            <h4 class="card-title mb-4">Quên mật khẩu</h4>


            <form action="${pageContext.request.contextPath}/forgot-password" method="post" id="forgotPasswordForm">
                <div class="mb-3">
                    <input name="email"
                           id="emailInput"
                           class="form-control"
                           placeholder="example@gmail.com"
                           type="text"
                           autocomplete="off">
                    <div class="invalid-feedback" id="emailError"></div>
                </div>

                <!-- Hiển thị thông báo thành công nếu có -->
                <c:if test="${not empty requestScope.message}">
                    <div class="alert alert-success mb-4">
                            ${requestScope.message}
                    </div>
                </c:if>

                <!-- Hiển thị thông báo lỗi nếu có -->
                <c:if test="${not empty requestScope.error}">
                    <div class="alert alert-danger mb-4">
                            ${requestScope.error}
                    </div>
                </c:if>

                <button type="submit" class="btn btn-primary w-100">Lấy lại mật khẩu</button>
            </form>


        </div> <!-- card-body.// -->
    </div> <!-- card .// -->
    <p class="text-center mt-4">Không có tài khoản? <a href="${pageContext.request.contextPath}/signup">Đăng ký</a></p>
</section> <!-- section-content.// -->

<jsp:include page="/common/client/footer.jsp"/>
<script src="${pageContext.request.contextPath}/js/forgot-password.js" type="module"></script>
</body>
</html>