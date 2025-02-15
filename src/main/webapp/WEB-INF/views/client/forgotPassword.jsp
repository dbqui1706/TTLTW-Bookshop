<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <title>Đăng nhập</title>
</head>

<body>
<jsp:include page="/common/client/header.jsp"/>

<section class="section-content" style="margin: 100px 0;">
    <div class="card mx-auto" style="max-width: 380px">
        <div class="card-body">
            <h4 class="card-title mb-4">Quên mật khẩu</h4>
            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="mb-3">
                    <input name="username"
                           class="form-control"
                           placeholder="Example: xxxx@gmail.com"
                           type="text"
                           autocomplete="off"
                           value="${requestScope.values.username}">
                    <c:if test="${not empty requestScope.violations.usernameViolations}">
                        <div class="invalid-feedback">
                            <ul class="list-unstyled">
                                <c:forEach var="violation" items="${requestScope.violations.usernameViolations}">
                                    <li>${violation}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                </div>
                <button type="submit" class="btn btn-primary w-100">Gửi</button>
            </form>

        </div> <!-- card-body.// -->
    </div> <!-- card .// -->
    <p class="text-center mt-4">Không có tài khoản? <a href="${pageContext.request.contextPath}/signup">Đăng ký</a></p>
</section> <!-- section-content.// -->


<jsp:include page="/common/client/footer.jsp"/>

</body>

</html>
