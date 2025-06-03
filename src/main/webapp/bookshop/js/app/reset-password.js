// import { API_URL } from '../constants/api-urls.js';

export class ResetPasswordContainer {
    constructor() {
        this.resetPasswordForm = null;
        this.newPassword = null;
        this.confirmPassword = null;
        this.submitBtn = null;
        this.progressBar = null;
        this.strengthText = null;
        this.matchIndicator = null;
        this.matchText = null;

        // URL parameters
        this.email = null;
        this.code = null;
    }

    init() {
        this.initElements();
        this.parseUrlParams();
        this.validateResetLink();
        this.bindEvents();
    }

    initElements() {
        // Form và input elements
        this.resetPasswordForm = document.getElementById('resetPasswordForm');
        this.newPassword = document.getElementById('new-password');
        this.confirmPassword = document.getElementById('confirm-password');
        this.submitBtn = document.getElementById('submit-btn');

        // Hidden fields
        this.resetEmail = document.getElementById('reset-email');
        this.resetCode = document.getElementById('reset-code');

        // Password strength elements
        this.progressBar = document.querySelector('.password-strength .progress-bar');
        this.strengthText = document.querySelector('.strength-text');

        // Password match elements
        this.matchIndicator = document.querySelector('.password-match');
        this.matchText = document.querySelector('.match-text');

        // Display elements
        this.userEmailDisplay = document.getElementById('user-email');
        this.statusMessage = document.getElementById('status-message');
        this.statusIcon = document.getElementById('status-icon');

        // Form containers
        this.resetForm = document.getElementById('reset-password-form');
        this.successMessage = document.getElementById('success-message');
        this.errorMessage = document.getElementById('error-message');
        this.errorText = document.getElementById('error-text');

        // Toggle password buttons
        this.togglePasswordButtons = document.querySelectorAll('.toggle-password');
    }

    parseUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        this.email = urlParams.get('email');
        this.code = urlParams.get('code');

        console.log('Reset parameters:', { email: this.email, code: this.code });
    }

    async validateResetLink() {
        if (!this.email || !this.code) {
            this.showError('Liên kết không hợp lệ. Thiếu thông tin xác thực.');
            return;
        }

        try {
            // Verify reset token với backend
            const response = await fetch(
                `/api/auth/verify-reset-token?email=${encodeURIComponent(this.email)}&code=${encodeURIComponent(this.code)}`,
                {
                    method: 'GET',
                }
            );

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    this.setupValidForm();
                } else {
                    this.showError(data.message || 'Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.');
                }
            } else {
                this.showError('Không thể xác thực liên kết. Vui lòng thử lại.');
            }
        } catch (error) {
            console.error('Error validating reset link:', error);
            this.showError('Có lỗi xảy ra khi xác thực liên kết.');
        }
    }

    setupValidForm() {
        // Hiển thị email trong form
        this.userEmailDisplay.textContent = this.email;

        // Set hidden fields
        this.resetEmail.value = this.email;
        this.resetCode.value = this.code;

        // Update status
        this.statusIcon.className = 'fas fa-key';
        this.statusMessage.innerHTML = `Nhập mật khẩu mới cho tài khoản: <strong>${this.email}</strong>`;

        console.log('Reset form is ready');
    }

    showError(message) {
        this.resetForm.classList.add('d-none');
        this.errorText.textContent = message;
        this.errorMessage.classList.remove('d-none');
        this.errorMessage.classList.add('fade-in');
    }

    showSuccess() {
        this.resetForm.classList.add('d-none');
        this.successMessage.classList.remove('d-none');
        this.successMessage.classList.add('fade-in');
    }

    bindEvents() {
        // Form submission
        if (this.resetPasswordForm) {
            this.resetPasswordForm.addEventListener('submit', this.handleResetPassword.bind(this));
        }

        // Password strength monitoring
        if (this.newPassword) {
            this.newPassword.addEventListener('input', this.updatePasswordStrength.bind(this));
        }

        // Password match monitoring
        if (this.confirmPassword) {
            this.confirmPassword.addEventListener('input', this.checkPasswordMatch.bind(this));
        }

        // Toggle password visibility
        this.togglePasswordButtons.forEach(button => {
            button.addEventListener('click', this.togglePasswordVisibility.bind(this));
        });
    }

    togglePasswordVisibility(e) {
        const button = e.currentTarget;
        const targetId = button.getAttribute('data-target');
        const passwordInput = document.getElementById(targetId);

        if (passwordInput) {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            const icon = button.querySelector('i');
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        }
    }

    updatePasswordStrength() {
        const password = this.newPassword.value;
        const strength = this.calculatePasswordStrength(password);

        // Update progress bar
        this.progressBar.style.width = strength.score + '%';
        this.progressBar.className = `progress-bar ${strength.class}`;

        // Update text
        this.strengthText.textContent = strength.text;
        this.strengthText.className = `strength-text ${strength.textClass}`;
    }

    calculatePasswordStrength(password) {
        let score = 0;
        let feedback = [];

        // Length check
        if (password.length >= 8) {
            score += 20;
        } else {
            feedback.push('ít nhất 8 ký tự');
        }

        // Uppercase check
        if (/[A-Z]/.test(password)) {
            score += 20;
        } else {
            feedback.push('chữ hoa');
        }

        // Lowercase check
        if (/[a-z]/.test(password)) {
            score += 20;
        } else {
            feedback.push('chữ thường');
        }

        // Number check
        if (/\d/.test(password)) {
            score += 20;
        } else {
            feedback.push('số');
        }

        // Special character check
        if (/[^a-zA-Z0-9]/.test(password)) {
            score += 20;
        } else {
            feedback.push('ký tự đặc biệt');
        }

        // Determine strength level
        let result = {
            score: Math.min(100, score),
            class: '',
            text: '',
            textClass: ''
        };

        if (score < 40) {
            result.class = 'bg-danger';
            result.text = feedback.length > 0 ? `Yếu - Cần thêm: ${feedback.join(', ')}` : 'Mật khẩu yếu';
            result.textClass = 'text-danger';
        } else if (score < 80) {
            result.class = 'bg-warning';
            result.text = feedback.length > 0 ? `Trung bình - Cần thêm: ${feedback.join(', ')}` : 'Mật khẩu trung bình';
            result.textClass = 'text-warning';
        } else {
            result.class = 'bg-success';
            result.text = 'Mạnh - Mật khẩu an toàn';
            result.textClass = 'text-success';
        }

        return result;
    }

    checkPasswordMatch() {
        const newPass = this.newPassword.value;
        const confirmPass = this.confirmPassword.value;

        if (confirmPass.length === 0) {
            this.matchIndicator.classList.remove('show');
            return;
        }

        this.matchIndicator.classList.add('show');

        if (newPass === confirmPass) {
            this.matchText.textContent = '✓ Mật khẩu khớp';
            this.matchText.className = 'match-text match';
        } else {
            this.matchText.textContent = '✗ Mật khẩu không khớp';
            this.matchText.className = 'match-text no-match';
        }
    }

    validateForm() {
        let isValid = true;

        // Check new password
        if (!this.newPassword.value || this.newPassword.value.length < 8) {
            this.showFieldError(this.newPassword, 'Mật khẩu phải có ít nhất 8 ký tự');
            isValid = false;
        } else {
            this.clearFieldError(this.newPassword);
        }

        // Check password confirmation
        if (this.newPassword.value !== this.confirmPassword.value) {
            this.showFieldError(this.confirmPassword, 'Mật khẩu xác nhận không khớp');
            isValid = false;
        } else {
            this.clearFieldError(this.confirmPassword);
        }

        // Check password strength
        const strength = this.calculatePasswordStrength(this.newPassword.value);
        if (strength.score < 60) {
            this.showFieldError(this.newPassword, 'Mật khẩu chưa đủ mạnh');
            isValid = false;
        }

        return isValid;
    }

    showFieldError(field, message) {
        field.classList.add('is-invalid');

        // Tìm hoặc tạo error element
        let errorElement = field.parentNode.parentNode.querySelector('.invalid-feedback');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'invalid-feedback';
            field.parentNode.parentNode.appendChild(errorElement);
        }

        errorElement.textContent = message;
    }

    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorElement = field.parentNode.parentNode.querySelector('.invalid-feedback');
        if (errorElement) {
            errorElement.textContent = '';
        }
    }

    async handleResetPassword(e) {
        e.preventDefault();

        if (!this.validateForm()) {return;
        }

        try {
            // Set loading state
            this.setLoadingState(true);

            const formData = new FormData();
            formData.append('email', this.email);
            formData.append('code', this.code);
            formData.append('password', CryptoJS.MD5(this.newPassword.value).toString().toUpperCase());

            const response = await fetch("/api/auth/reset-password", {
                method: 'POST',
                body: formData
            });

            const data = await response.json();

            if (response.ok && data.success) {
                // Show success message
                this.showSuccess();

                // Optional: Show success notification
                this.showNotification('Mật khẩu đã được đặt lại thành công!', 'success');

            } else {
                this.showNotification(data.message || 'Đặt lại mật khẩu thất bại', 'error');
            }

        } catch (error) {
            console.error('Reset password error:', error);
            this.showNotification('Có lỗi xảy ra khi đặt lại mật khẩu', 'error');
        } finally {
            this.setLoadingState(false);
        }
    }

    setLoadingState(isLoading) {
        if (isLoading) {
            this.submitBtn.classList.add('loading');
            this.submitBtn.disabled = true;
        } else {
            this.submitBtn.classList.remove('loading');
            this.submitBtn.disabled = false;
        }
    }

    showNotification(message, type = 'info') {
        // Create notification toast
        const notification = document.createElement('div');
        notification.className = `alert alert-${type} notification-toast position-fixed`;
        notification.style.cssText = `
       top: 20px;
       right: 20px;
       z-index: 9999;
       min-width: 300px;
       opacity: 0;
       transform: translateX(100%);
       transition: all 0.3s ease;
   `;
        notification.innerHTML = `
       <div class="d-flex align-items-center">
           <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-triangle'} me-2"></i>
           <div>${message}</div>
       </div>
   `;

        document.body.appendChild(notification);

        // Show notification
        setTimeout(() => {
            notification.style.opacity = '1';
            notification.style.transform = 'translateX(0)';
        }, 10);

        // Auto hide after 4 seconds
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 4000);
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    const resetPasswordContainer = new ResetPasswordContainer();
    resetPasswordContainer.init();
});