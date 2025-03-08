document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('resetPasswordForm');
    if (form) {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const submitBtn = document.getElementById('submitBtn');
        const passwordStrength = document.getElementById('passwordStrength');
        const passwordFeedback = document.getElementById('passwordFeedback');
        const confirmFeedback = document.getElementById('confirmFeedback');

        // Yêu cầu mật khẩu
        const lengthReq = document.getElementById('length');
        const uppercaseReq = document.getElementById('uppercase');
        const lowercaseReq = document.getElementById('lowercase');
        const numberReq = document.getElementById('number');
        const specialReq = document.getElementById('special');

        // Các regex kiểm tra mật khẩu
        const lengthRegex = /.{8,}/;
        const uppercaseRegex = /[A-Z]/;
        const lowercaseRegex = /[a-z]/;
        const numberRegex = /[0-9]/;
        const specialRegex = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;

        // Hàm cập nhật trạng thái yêu cầu mật khẩu
        function updateRequirement(element, regex, value) {
            const isValid = regex.test(value);
            const icon = element.querySelector('i');

            if (isValid) {
                element.classList.add('valid');
                element.classList.remove('invalid');
                icon.className = 'fas fa-check-circle';
            } else {
                element.classList.add('invalid');
                element.classList.remove('valid');
                icon.className = 'fas fa-times-circle';
            }

            return isValid;
        }

        // Hàm tính độ mạnh của mật khẩu
        function calculatePasswordStrength(password) {
            let strength = 0;

            if (lengthRegex.test(password)) strength += 20;
            if (uppercaseRegex.test(password)) strength += 20;
            if (lowercaseRegex.test(password)) strength += 20;
            if (numberRegex.test(password)) strength += 20;
            if (specialRegex.test(password)) strength += 20;

            return strength;
        }

        // Hàm kiểm tra mật khẩu
        function checkPassword() {
            const value = password.value;
            let allValid = true;

            // Cập nhật từng yêu cầu
            allValid = updateRequirement(lengthReq, lengthRegex, value) && allValid;
            allValid = updateRequirement(uppercaseReq, uppercaseRegex, value) && allValid;
            allValid = updateRequirement(lowercaseReq, lowercaseRegex, value) && allValid;
            allValid = updateRequirement(numberReq, numberRegex, value) && allValid;
            allValid = updateRequirement(specialReq, specialRegex, value) && allValid;

            // Tính độ mạnh mật khẩu
            const strength = calculatePasswordStrength(value);

            // Cập nhật thanh độ mạnh
            passwordStrength.style.width = strength + '%';

            // Đặt màu và thông báo dựa trên độ mạnh
            if (strength === 0) {
                passwordStrength.className = 'password-strength bg-secondary';
                passwordFeedback.textContent = '';
            } else if (strength < 40) {
                passwordStrength.className = 'password-strength bg-danger';
                passwordFeedback.textContent = 'Mật khẩu yếu';
                passwordFeedback.className = 'password-feedback text-danger';
            } else if (strength < 80) {
                passwordStrength.className = 'password-strength bg-warning';
                passwordFeedback.textContent = 'Mật khẩu trung bình';
                passwordFeedback.className = 'password-feedback text-warning';
            } else {
                passwordStrength.className = 'password-strength bg-success';
                passwordFeedback.textContent = 'Mật khẩu mạnh';
                passwordFeedback.className = 'password-feedback text-success';
            }

            return allValid;
        }

        // Hàm kiểm tra mật khẩu xác nhận
        function checkConfirmPassword() {
            const passwordValue = password.value;
            const confirmValue = confirmPassword.value;

            if (confirmValue === '') {
                confirmFeedback.style.display = 'none';
                return false;
            }

            if (passwordValue === confirmValue) {
                confirmFeedback.style.display = 'none';
                return true;
            } else {
                confirmFeedback.style.display = 'block';
                return false;
            }
        }

        // Hàm cập nhật trạng thái nút submit
        function updateSubmitButton() {
            const isPasswordValid = checkPassword();
            const isConfirmValid = checkConfirmPassword();

            submitBtn.disabled = !(isPasswordValid && isConfirmValid);
        }

        // Sự kiện input cho mật khẩu
        password.addEventListener('input', function() {
            checkPassword();
            if (confirmPassword.value !== '') {
                checkConfirmPassword();
            }
            updateSubmitButton();
        });

        // Sự kiện input cho xác nhận mật khẩu
        confirmPassword.addEventListener('input', function() {
            checkConfirmPassword();
            updateSubmitButton();
        });

        // Sự kiện submit form - dùng AJAX thay vì submit form thông thường
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Ngăn form submit thông thường

            const isPasswordValid = checkPassword();
            const isConfirmValid = checkConfirmPassword();

            if (!isPasswordValid || !isConfirmValid) {
                Swal.fire({
                    title: 'Lỗi!',
                    text: 'Vui lòng điền mật khẩu hợp lệ và xác nhận mật khẩu.',
                    icon: 'error',
                    confirmButtonText: 'Đồng ý'
                });
                return;
            }

            // Thu thập dữ liệu form
            const formData =new URLSearchParams();
            for (const pair of new FormData(form)) {
                formData.append(pair[0], pair[1]);
            }

            // Hiển thị loading
            Swal.fire({
                title: 'Đang xử lý...',
                text: 'Vui lòng đợi trong giây lát',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            // Gửi request AJAX
            console.log("URL: " + form.action);
            fetch(form.action, {
                method: 'POST',
                body: formData,
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Nếu thành công
                        Swal.fire({
                            title: 'Thành công!',
                            text: data.message,
                            icon: 'success',
                            confirmButtonText: 'Đến trang đăng nhập'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                // Chuyển hướng đến trang đăng nhập
                                window.location.href = window.location.origin + '/signin';
                            }
                        });

                        // Ẩn form
                        form.style.display = 'none';

                        // Tạo nút đăng nhập
                        const loginButton = document.createElement('div');
                        loginButton.className = 'text-center mb-4';
                        loginButton.innerHTML = '<a href="' + window.location.origin + '/signin" class="btn btn-primary">Đến trang đăng nhập</a>';
                        form.parentNode.insertBefore(loginButton, form);
                    } else {
                        // Nếu có lỗi
                        Swal.fire({
                            title: 'Lỗi!',
                            text: data.message || 'Đã xảy ra lỗi khi đặt lại mật khẩu.',
                            icon: 'error',
                            confirmButtonText: 'Thử lại'
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        title: 'Lỗi!',
                        text: 'Đã xảy ra lỗi khi kết nối đến server.',
                        icon: 'error',
                        confirmButtonText: 'Thử lại'
                    });
                });
        });
    }
});