<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <title>Đặt lại mật khẩu</title>
    <style>
        .password-strength {
            margin-top: 5px;
            height: 5px;
            border-radius: 3px;
        }

        .password-feedback {
            margin-top: 5px;
            font-size: 0.8rem;
        }

        .requirements {
            list-style: none;
            padding-left: 0;
            margin-top: 8px;
            font-size: 0.8rem;
        }

        .requirements li {
            margin-bottom: 3px;
            color: #777;
        }

        .requirements li i {
            margin-right: 5px;
        }

        .requirements li.valid {
            color: #198754;
        }

        .requirements li.invalid {
            color: #dc3545;
        }
    </style>
</head>

<body>
<jsp:include page="/common/client/header.jsp"/>

<section class="section-content" style="margin: 100px 0;">
    <div class="card mx-auto" style="max-width: 450px">
        <div class="card-body">
            <h4 class="card-title mb-4">Đặt lại mật khẩu</h4>

            <!-- Hiển thị alert nếu có -->
            <c:if test="${not empty requestScope.alertMessage}">
                <div class="alert alert-${requestScope.alertType} alert-dismissible fade show mb-4">
                        ${requestScope.alertMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>

                <!-- Nếu đặt lại mật khẩu thành công, hiển thị nút đăng nhập -->
                <c:if test="${requestScope.alertType eq 'success'}">
                    <div class="text-center mb-4">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đến trang đăng nhập</a>
                    </div>
                </c:if>
            </c:if>

            <!-- Nếu đặt lại mật khẩu thành công, ẩn form -->
            <c:if test="${empty requestScope.alertType or requestScope.alertType ne 'success'}">
                <form action="${pageContext.request.contextPath}/reset-password" method="post" id="resetPasswordForm">
                    <input type="hidden" name="email" value="${requestScope.email}">
                    <input type="hidden" name="code" value="${requestScope.code}">

                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu mới</label>
                        <input name="password"
                               id="password"
                               class="form-control"
                               type="password"
                               required>
                        <div class="password-strength bg-secondary" id="passwordStrength"></div>
                        <small class="password-feedback" id="passwordFeedback"></small>

                        <ul class="requirements" id="passwordRequirements">
                            <li id="length"><i class="fas fa-times-circle"></i> Ít nhất 8 ký tự</li>
                            <li id="uppercase"><i class="fas fa-times-circle"></i> Ít nhất 1 chữ hoa</li>
                            <li id="lowercase"><i class="fas fa-times-circle"></i> Ít nhất 1 chữ thường</li>
                            <li id="number"><i class="fas fa-times-circle"></i> Ít nhất 1 số</li>
                            <li id="special"><i class="fas fa-times-circle"></i> Ít nhất 1 ký tự đặc biệt</li>
                        </ul>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                        <input name="confirmPassword"
                               id="confirmPassword"
                               class="form-control"
                               type="password"
                               required>
                        <small id="confirmFeedback" class="form-text text-danger" style="display: none;">
                            Mật khẩu xác nhận không khớp
                        </small>
                    </div>
                    <button type="submit" id="submitBtn" class="btn btn-primary w-100" disabled>Đặt lại mật khẩu</button>
                </form>
            </c:if>
        </div> <!-- card-body.// -->
    </div> <!-- card .// -->
</section> <!-- section-content.// -->

<jsp:include page="/common/client/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/js/reset-password.js"></script>
</body>
</html>