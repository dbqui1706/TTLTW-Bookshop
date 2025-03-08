document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('forgotPasswordForm');
    const emailInput = document.getElementById('emailInput');
    const emailError = document.getElementById('emailError');

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        // Xóa thông báo lỗi cũ nếu có
        emailInput.classList.remove('is-invalid');
        emailError.textContent = '';

        // Lấy giá trị email
        const email = emailInput.value.trim();

        // Xác thực email
        if (!validateEmail(email)) {
            showError('Vui lòng nhập đúng định dạng email');
            return;
        }

        // Tiếp tục submit form để xử lý ở phía server
        form.submit();
    });

    // Hàm xác thực định dạng email
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Hàm hiển thị lỗi
    function showError(message) {
        emailInput.classList.add('is-invalid');
        emailError.textContent = message;
    }
});