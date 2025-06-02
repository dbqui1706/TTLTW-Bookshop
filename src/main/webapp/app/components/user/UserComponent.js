import { EventBus } from "../../core/eventBus"; 
import { userService } from "../../services/UserService.js";

export class UserComponent {
    constructor() {
        // Services
        this.userService = userService.getInstance();
        this.eventBus = EventBus.getInstance();

        // DataTable instance
        this.dataTable = null;

        // DOM elements
        this.userTable = document.getElementById('userTableBody');
        this.totalUsers = document.getElementById('totalUsers');
        this.totalActiveUsers = document.getElementById('totalActiveUsers');
        this.totalNewUsers = document.getElementById('totalNewUsers');
        this.totalLockedUsers = document.getElementById('totalLockedUsers');
        
        this.searchUser = document.getElementById('searchUser');
        this.roleFilter = document.getElementById('roleFilter');
        this.statusFilter = document.getElementById('statusFilter');
        this.sortOption = document.getElementById('sortOption');
        this.addUserBtn = document.getElementById('addUserBtn');
        this.exportExcelBtn = document.getElementById('exportExcelBtn');
        
        this.confirmDeleteUserBtn = document.getElementById('confirmDeleteUserBtn');
        this.confirmUserDeletion = document.getElementById('confirmUserDeletion');

        // Current user being edited/deleted
        this.currentUserId = null;

        // Initialize
        this.initialize();
    }

    initialize() {
        // Load users
        this.initialUserTable();

        // Event listeners
        this.searchUser.addEventListener('keyup', this.handleFilterChange.bind(this));
        this.roleFilter.addEventListener('change', this.handleFilterChange.bind(this));
        this.statusFilter.addEventListener('change', this.handleFilterChange.bind(this));
        this.sortOption.addEventListener('change', this.handleSortChange.bind(this));
        this.exportExcelBtn.addEventListener('click', this.exportToExcel.bind(this));
        
        // Delete modal events
        this.confirmUserDeletion.addEventListener('change', this.toggleDeleteButton.bind(this));
        this.confirmDeleteUserBtn.addEventListener('click', this.deleteUser.bind(this));
        
        // Subscribe to events
        this.eventBus.subscribe('user:created', this.refreshTable.bind(this));
        this.eventBus.subscribe('user:updated', this.refreshTable.bind(this));
    }

    initialUserTable() {
        // DataTable for user
        this.dataTable = $('#userTableBody').parent('table').DataTable({
            serverSide: true,
            processing: true,
            searching: false, 
            lengthChange: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50, 100],
            ordering: true, 
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.11.3/i18n/vi.json' 
            },
            columns: [
                { 
                    title: 'ID',
                    data: 'id'
                },
                { 
                    title: 'Họ tên',
                    data: 'fullName',
                    render: (data, type, row) => {
                        return `
                            <div class="d-flex align-items-center">
                                <div class="avatar me-2">
                                    <img src="${row.avatar || '/asset/default-avatar.png'}" 
                                         class="rounded-circle" width="40" height="40" alt="${data}">
                                </div>
                                <div>
                                    <h6 class="mb-0">${data}</h6>
                                    <small class="text-muted">${row.email}</small>
                                </div>
                            </div>
                        `;
                    }
                },
                { 
                    title: 'Vai trò',
                    data: 'role',
                    render: (data) => {
                        const badges = {
                            'ADMIN': 'bg-danger',
                            'EMPLOYEE': 'bg-primary',
                            'CUSTOMER': 'bg-success'
                        };
                        const roleNames = {
                            'ADMIN': 'Quản trị viên',
                            'EMPLOYEE': 'Nhân viên',
                            'CUSTOMER': 'Khách hàng'
                        };
                        return `<span class="badge ${badges[data] || 'bg-secondary'}">${roleNames[data] || data}</span>`;
                    }
                },
                { 
                    title: 'Trạng thái',
                    data: 'status',
                    render: (data) => {
                        const badges = {
                            'active': 'bg-success',
                            'inactive': 'bg-warning',
                            'locked': 'bg-danger'
                        };
                        const statusNames = {
                            'active': 'Đang hoạt động',
                            'inactive': 'Không hoạt động',
                            'locked': 'Bị khóa'
                        };
                        return `<span class="badge ${badges[data] || 'bg-secondary'}">${statusNames[data] || data}</span>`;
                    }
                },
                { 
                    title: 'Ngày đăng ký',
                    data: 'createdAt',
                    render: (data) => {
                        const date = new Date(data);
                        return date.toLocaleDateString('vi-VN');
                    }
                },
                { 
                    title: 'Đăng nhập gần đây',
                    data: 'lastLogin',
                    render: (data) => {
                        if (!data) return 'Chưa đăng nhập';
                        const date = new Date(data);
                        return date.toLocaleDateString('vi-VN', { 
                            year: 'numeric', 
                            month: 'numeric', 
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                    }
                },
                { 
                    title: 'Hành động',
                    data: null,
                    orderable: false,
                    render: (data, type, row) => {
                        return `
                            <div class="dropdown">
                                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" 
                                        type="button" data-bs-toggle="dropdown">
                                    <i class="bi bi-three-dots"></i>
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li>
                                        <a class="dropdown-item view-user" data-id="${row.id}" href="#">
                                            <i class="bi bi-eye me-2"></i> Xem chi tiết
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item edit-user" data-id="${row.id}" href="#">
                                            <i class="bi bi-pencil me-2"></i> Chỉnh sửa
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item ${row.status === 'locked' ? 'unlock-user' : 'lock-user'}" 
                                           data-id="${row.id}" href="#">
                                            <i class="bi bi-${row.status === 'locked' ? 'unlock' : 'lock'} me-2"></i> 
                                            ${row.status === 'locked' ? 'Mở khóa' : 'Khóa'} tài khoản
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item text-danger delete-user" 
                                           data-id="${row.id}" 
                                           data-name="${row.fullName}" href="#">
                                            <i class="bi bi-trash me-2"></i> Xóa
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        `;
                    }
                }
            ],
            ajax: (data, callback, settings) => {
                // Prepare parameters for API call
                const params = {
                    page: Math.floor(data.start / data.length) + 1,
                    limit: data.length,
                    search: this.searchUser.value,
                    role: this.roleFilter.value,
                    status: this.statusFilter.value,
                    sort: this.sortOption.value
                };
                
                // Call API to get users with filters, sorting, and pagination
                this.userService.getUsers(params).then(response => {
                    const result = {
                        draw: data.draw,
                        recordsTotal: response.total,
                        recordsFiltered: response.total,
                        data: response.data
                    };
                    
                    // Update statistics
                    this.updateUserStats(response.stats);
                    
                    callback(result);
                    
                    // Add event listeners for action buttons after table is rendered
                    this.addActionEventListeners();
                });
            },
            drawCallback: () => {
                // Re-initialize tooltips after table redraw
                const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                tooltips.forEach(tooltip => {
                    new bootstrap.Tooltip(tooltip);
                });
            }
        });
    }
    
    handleFilterChange() {
        this.dataTable.ajax.reload();
    }
    
    handleSortChange() {
        this.dataTable.ajax.reload();
    }
    
    refreshTable() {
        this.dataTable.ajax.reload();
    }
    
    addActionEventListeners() {
        // View user details
        document.querySelectorAll('.view-user').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const userId = e.currentTarget.dataset.id;
                this.viewUserDetails(userId);
            });
        });
        
        // Edit user
        document.querySelectorAll('.edit-user').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const userId = e.currentTarget.dataset.id;
                this.editUser(userId);
            });
        });
        
        // Lock/unlock user
        document.querySelectorAll('.lock-user, .unlock-user').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const userId = e.currentTarget.dataset.id;
                const action = e.currentTarget.classList.contains('lock-user') ? 'lock' : 'unlock';
                this.toggleUserLock(userId, action);
            });
        });
        
        // Delete user
        document.querySelectorAll('.delete-user').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const userId = e.currentTarget.dataset.id;
                const userName = e.currentTarget.dataset.name;
                this.showDeleteConfirmation(userId, userName);
            });
        });
    }
    
    viewUserDetails(userId) {
        // Implement view user details logic
        // For example, redirect to user detail page or show modal
        window.location.href = `/admin/users/${userId}`;
    }
    
    editUser(userId) {
        // Get user data and populate form
        this.userService.getUserById(userId).then(user => {
            // Populate form with user data
            // ...
            
            // Show edit modal
            const editUserModal = new bootstrap.Modal(document.getElementById('editUserModal'));
            editUserModal.show();
        });
    }
    
    toggleUserLock(userId, action) {
        this.userService.updateUserStatus(userId, action === 'lock' ? 'locked' : 'active')
            .then(() => {
                this.showToast(`Tài khoản đã được ${action === 'lock' ? 'khóa' : 'mở khóa'} thành công`);
                this.refreshTable();
            })
            .catch(error => {
                this.showToast(`Có lỗi xảy ra: ${error.message}`, 'danger');
            });
    }
    
    showDeleteConfirmation(userId, userName) {
        this.currentUserId = userId;
        document.getElementById('deleteUserName').textContent = userName;
        document.getElementById('confirmUserDeletion').checked = false;
        this.toggleDeleteButton();
        
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteUserModal'));
        deleteModal.show();
    }
    
    toggleDeleteButton() {
        this.confirmDeleteUserBtn.disabled = !this.confirmUserDeletion.checked;
    }
    
    deleteUser() {
        if (!this.currentUserId) return;
        
        this.userService.deleteUser(this.currentUserId)
            .then(() => {
                // Close modal
                const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
                deleteModal.hide();
                
                // Show success message
                this.showToast('Người dùng đã được xóa thành công');
                
                // Refresh table
                this.refreshTable();
                
                // Reset current user
                this.currentUserId = null;
            })
            .catch(error => {
                this.showToast(`Có lỗi xảy ra: ${error.message}`, 'danger');
            });
    }
    
    updateUserStats(stats) {
        // Update statistics
        this.totalUsers.textContent = stats.total || 0;
        this.totalActiveUsers.textContent = stats.active || 0;
        this.totalNewUsers.textContent = stats.newThisMonth || 0;
        this.totalLockedUsers.textContent = stats.locked || 0;
    }
    
    exportToExcel() {
        // Get current filters
        const params = {
            search: this.searchUser.value,
            role: this.roleFilter.value,
            status: this.statusFilter.value,
            sort: this.sortOption.value,
            export: 'excel'
        };
        
        // Convert params to query string
        const queryString = Object.keys(params)
            .filter(key => params[key])
            .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
            .join('&');
        
        // Create export URL
        const exportUrl = `/api/users/export?${queryString}`;
        
        // Trigger download
        window.location.href = exportUrl;
    }
    
    showToast(message, type = 'success') {
        // Use EventBus to show toast notification
        this.eventBus.publish('toast:show', {
            message,
            type
        });
    }
}