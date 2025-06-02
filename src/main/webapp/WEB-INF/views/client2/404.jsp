<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Trang không tìm thấy | BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <style>
        .error-page {
            padding: 50px 0;
            background-color: #f8f9fa;
            min-height: 320px;
            display: flex;
            align-items: center;
        }

        .error-content {
            text-align: center;
        }

        .error-number {
            font-size: 100px;
            font-weight: 700;
            color: #2e86de;
            line-height: 1.2;
            margin-bottom: 10px;
            text-shadow: 2px 2px 8px rgba(46, 134, 222, 0.2);
        }

        .error-title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
        }

        .error-description {
            font-size: 15px;
            color: #666;
            margin-bottom: 15px;
            max-width: 600px;
            margin-left: auto;
            margin-right: auto;
        }

        .home-btn {
            display: inline-block;
            background-color: #2e86de;
            color: #fff;
            padding: 12px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            margin-top: 20px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 8px rgba(46, 134, 222, 0.25);
        }

        .home-btn:hover {
            background-color: #1c75c8;
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(46, 134, 222, 0.3);
            color: #fff;
        }

        .animated {
            animation-duration: 1s;
            animation-fill-mode: both;
        }

        @keyframes bounce {
            0%, 20%, 50%, 80%, 100% {
                transform: translateY(0);
            }
            40% {
                transform: translateY(-20px);
            }
            60% {
                transform: translateY(-10px);
            }
        }

        .bounce {
            animation-name: bounce;
            animation-delay: 0.5s;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
            }
            to {
                opacity: 1;
            }
        }

        .fadeIn {
            animation-name: fadeIn;
        }
    </style>
</head>

<body>
<!-- Header (sử dụng lại từ mẫu) -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content - 404 Error Page -->
<main>
    <section class="error-page">
        <div class="container">
            <div class="error-content animated fadeIn">
                <div class="error-number animated bounce">404</div>
                <h1 class="error-title">Trang không tìm thấy!</h1>
                <p class="error-description">Rất tiếc, trang bạn đang tìm kiếm không tồn tại hoặc đã bị di chuyển.
                    Có thể bạn đã nhập sai địa chỉ hoặc trang này đã bị xóa.</p>
                <a href="/" class="home-btn">
                    <i class="fas fa-home me-2"></i> Quay lại trang chủ
                </a>
            </div>
        </div>
    </section>
</main>

<!-- Footer -->
<jsp:include page="/common/client2/footer.jsp"/>

<!-- Help Widget -->
<div class="help-widget">
    <div class="help-button">
        <i class="fas fa-headset"></i>
    </div>
</div>

<!-- Bootstrap JS and Font Awesome -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- Library -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- JavaScript -->
<script src="/bookshop/js/app/header.js" type="module"></script>
</body>

</html>
