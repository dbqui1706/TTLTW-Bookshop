<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thành công - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="/bookshop/css/style.css">
    <link rel="stylesheet" href="/bookshop/css/payment-status.css">
</head>

<body>
<!-- Header -->
<jsp:include page="/common/client2/header.jsp" />

<!-- Main Content -->
<main>
    <div class="container">
        <!-- Thanh toán thành công -->
        <div class="payment-success-container d-none">
            <div class="payment-icon-success">
                <i class="fas fa-check-circle"></i>
            </div>

            <h1 class="section-title">Thanh toán thành công!</h1>

            <div class="order-info">
                <div class="order-detail">
                    <span class="label">Mã đơn hàng:</span>
                    <span class="value" id="order-code">ORD-20250405-XXXXX</span>
                </div>
                <div class="order-detail">
                    <span class="label">Phương thức:</span>
                    <span class="value">VNPay</span>
                </div>
                <div class="order-detail">
                    <span class="label">Số tiền:</span>
                    <span class="value" id="order-amount">0 ₫</span>
                </div>
                <div class="order-detail">
                    <span class="label">Thời gian:</span>
                    <span class="value" id="payment-time">05/04/2025 17:30:45</span>
                </div>
                <div class="order-detail">
                    <span class="label">Trạng thái:</span>
                    <span class="value text-success">Đã thanh toán</span>
                </div>
            </div>

            <div class="thank-you-message">
                <p>Cảm ơn bạn đã mua sắm tại BookStore!</p>
                <p>Đơn hàng của bạn đang được xử lý và sẽ được giao sớm nhất.</p>
            </div>

            <div class="action-buttons">
                <a href="/order-info" class="btn btn-view-order">
                    <i class="fas fa-clipboard-list me-1"></i>Xem đơn hàng
                </a>
                <a href="/" class="btn btn-continue-shopping">
                    <i class="fas fa-shopping-bag me-1"></i>Tiếp tục mua sắm
                </a>
            </div>
        </div>
        <!-- Thanh toán thất bại -->
        <div class="payment-failed-container d-none">
            <div class="payment-icon-falied">
                <i class="fas fa-times-circle"></i>
            </div>

            <h1 class="section-title">Thanh toán thất bại!</h1>

            <div class="error-message">
                <h5>Xảy ra lỗi trong quá trình thanh toán</h5>
                <p id="error-description">Giao dịch không thành công. Vui lòng kiểm tra lại thông tin thanh toán
                    hoặc thử lại.</p>
                <p class="mt-2 mb-0">Mã lỗi: <span class="error-code" id="error-code">unknown</span></p>
            </div>

            <div class="order-info">
                <div class="order-detail">
                    <span class="label">Mã đơn hàng:</span>
                    <span class="value" id="order-code">ORD-20250405-XXXXX</span>
                </div>
                <div class="order-detail">
                    <span class="label">Phương thức:</span>
                    <span class="value">VNPay</span>
                </div>
                <div class="order-detail">
                    <span class="label">Số tiền:</span>
                    <span class="value" id="order-amount">0 ₫</span>
                </div>
                <div class="order-detail">
                    <span class="label">Thời gian:</span>
                    <span class="value" id="payment-time">05/04/2025 17:30:45</span>
                </div>
                <div class="order-detail">
                    <span class="label">Trạng thái:</span>
                    <span class="value text-danger">Thanh toán thất bại</span>
                </div>
            </div>

            <div class="action-buttons">
                <a href="javascript:void(0);" class="btn btn-retry" id="btn-retry">
                    <i class="fas fa-sync-alt me-1"></i>Thử lại
                </a>
                <a href="/cart" class="btn btn-back-to-cart" id="btn-back-to-cart">
                    <i class="fas fa-shopping-cart me-1"></i>Quay lại giỏ hàng
                </a>
            </div>

            <div class="contact-support">
                <p>Nếu vẫn gặp vấn đề, vui lòng <a href="/client/support.html">liên hệ hỗ trợ</a> hoặc gọi số
                    hotline <strong>1900 1234</strong>.</p>
            </div>
        </div>
    </div>
</main>

<!-- Footer -->
<jsp:include page="/common/client2/footer.jsp"/>

<!-- Bootstrap JS and Font Awesome -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

<!-- JavaScript -->
<script src="/bookshop/js/app/header.js" type="module"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Lấy thông tin từ URL params
        const urlParams = new URLSearchParams(window.location.search);
        const vnpResponseCode = urlParams.get('vnp_ResponseCode');
        const orderCode = urlParams.get('orderCode');
        const vnpAmount = urlParams.get('vnp_Amount');

        // Lấy reference đến hai container
        const successContainer = document.querySelector('.payment-success-container');
        const failedContainer = document.querySelector('.payment-failed-container');

        // Xác định container nào được hiển thị dựa trên vnp_ResponseCode
        if (vnpResponseCode === '00') {
            // Thanh toán thành công - hiển thị container thành công
            successContainer.classList.remove('d-none');
            failedContainer.classList.add('d-none');

            // Xử lý thông tin thành công
            handleSuccessPayment(orderCode, vnpAmount);
        } else {
            // Thanh toán thất bại - hiển thị container thất bại
            successContainer.classList.add('d-none');
            failedContainer.classList.remove('d-none');

            // Xử lý thông tin thất bại
            handleFailedPayment(orderCode, vnpAmount, vnpResponseCode);
        }

        // Xử lý thanh toán thành công
        function handleSuccessPayment(orderCode, amount) {
            // Hiển thị thông tin đơn hàng
            if (orderCode) {
                const orderCodeElements = document.querySelectorAll('#order-code');
                orderCodeElements.forEach(element => {
                    element.textContent = orderCode;
                });
            }

            // Hiển thị số tiền nếu có
            if (amount) {
                const amountValue = parseInt(amount) / 100; // VNPay trả về số tiền đã nhân với 100
                const orderAmountElements = document.querySelectorAll('#order-amount');
                orderAmountElements.forEach(element => {
                    element.textContent = formatCurrency(amountValue);
                });
            }

            document.querySelectorAll('#payment-time').forEach(element => {
                element.textContent = formatDateTime(new Date());
            });

            // Xóa dữ liệu đơn hàng chờ thanh toán và giỏ hàng
            localStorage.removeItem('pendingOrderId');
            localStorage.removeItem('cart');
        }

        // Xử lý thanh toán thất bại
        function handleFailedPayment(orderCode, amount, errorCode) {
            // Hiển thị thông tin đơn hàng
            if (orderCode) {
                const orderCodeElements = document.querySelectorAll('#order-code');
                orderCodeElements.forEach(element => {
                    element.textContent = orderCode;
                });
            }

            // Hiển thị số tiền nếu có
            if (amount) {
                const amountValue = parseInt(amount) / 100; // VNPay trả về số tiền đã nhân với 100
                const orderAmountElements = document.querySelectorAll('#order-amount');
                orderAmountElements.forEach(element => {
                    element.textContent = formatCurrency(amountValue);
                });
            }

            document.querySelectorAll('#payment-time').forEach(element => {
                element.textContent = formatDateTime(new Date());
            });

            // Hiển thị mã lỗi và mô tả lỗi
            document.getElementById('error-code').textContent = errorCode || 'unknown';
            document.getElementById('error-description').textContent = getErrorDescription(errorCode);

            // Xử lý nút thử lại
            document.getElementById('btn-retry').addEventListener('click', function () {
                // Trong thực tế, bạn sẽ gọi API để tạo giao dịch thanh toán mới
                alert('Tính năng thử lại thanh toán đang được phát triển');
            });
        }

        // Định dạng tiền tệ
        function formatCurrency(amount) {
            return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
        }

        // Định dạng ngày giờ
        function formatDateTime(date) {
            return new Intl.DateTimeFormat('vi-VN', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            }).format(date);
        }

        // Hàm lấy mô tả lỗi dựa theo mã lỗi
        function getErrorDescription(code) {
            const errorMessages = {
                '01': 'Giao dịch đã được xử lý',
                '02': 'Giao dịch bị lỗi',
                '04': 'Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)',
                '05': 'VNPAY đang xử lý giao dịch này (GD hoàn tiền)',
                '06': 'VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)',
                '07': 'Giao dịch bị nghi ngờ gian lận',
                '09': 'GD Hoàn trả bị từ chối',
                '10': 'Đã hết hạn chờ thanh toán',
                '11': 'GD bị hủy',
                '12': 'Giao dịch bị từ chối',
                '13': 'Mã đơn hàng không hợp lệ',
                '24': 'Giao dịch không thành công do: Khách hàng hủy giao dịch',
                '51': 'Số dư tài khoản không đủ để thanh toán',
                '65': 'Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày',
                '75': 'Ngân hàng thanh toán đang bảo trì',
                '79': 'KH nhập sai mật khẩu xác thực giao dịch',
                '99': 'Các lỗi khác',
                'unknown': 'Giao dịch không thành công. Vui lòng kiểm tra lại thông tin thanh toán hoặc thử lại sau.'
            };

            return errorMessages[code] || errorMessages['unknown'];
        }
    });
</script>
</body>

</html>