// JavaScript cho Ajax và xử lý tab trong trang đơn hàng
document.addEventListener('DOMContentLoaded', function () {
    // Constants - Các biến không đổi
    const contextPathMetaTag = document.querySelector("meta[name='contextPath']");
    const API_URL = contextPathMetaTag.content + "/orders-api";
    console.log(API_URL);
    const INITIAL_STATUS = 'all';
    const INITIAL_PAGE = 1;

    // DOM Elements - Các phần tử DOM
    const elements = {
        tabButtons: document.querySelectorAll('.history-tab'),
        orderTableContainer: document.getElementById('order-table-container'),
        paginationContainer: document.getElementById('pagination-container'),
        loadingSpinner: document.getElementById('loading-spinner')
    };

    // State - Trạng thái
    const state = {
        currentStatus: INITIAL_STATUS,
        currentPage: INITIAL_PAGE
    };

    // Formatters - Các hàm định dạng
    const formatters = {
        // Định dạng số tiền
        currency: function (number) {
            return new Intl.NumberFormat('vi-VN').format(number) + '₫';
        },

        // Lấy badge HTML cho trạng thái đơn hàng
        statusBadge: function (status) {
            const statusMap = {
                0: {class: 'bg-info', text: 'Đơn hàng mới'},
                1: {class: 'bg-primary', text: 'Đã xác nhận'},
                2: {class: 'bg-warning text-dark', text: 'Đang giao hàng'},
                3: {class: 'bg-success', text: 'Giao hàng thành công'},
                4: {class: 'bg-danger', text: 'Hủy đơn hàng'}
            };

            const statusInfo = statusMap[status] || {class: 'bg-secondary', text: 'Không xác định'};
            return `<span class="badge ${statusInfo.class}">${statusInfo.text}</span>`;
        },

        // Lấy badge HTML cho trạng thái xác thực
        verifyBadge: function (status) {
            const badgeMap = {
                'NONE': {class: 'bg-warning text-dark', text: 'Không'},
                'GOOD': {class: 'bg-success', text: 'Đã xác thực'},
                'BAD': {class: 'bg-danger', text: 'Đã bị thay đổi'}
            };

            const badgeInfo = badgeMap[status] || {class: 'bg-secondary', text: 'Không xác định'};
            return `<span class="badge ${badgeInfo.class}">${badgeInfo.text}</span>`;
        }
    };

    // HTML Generators - Các hàm tạo HTML
    const htmlGenerators = {
        // Tạo HTML cho bảng đơn hàng
        orderTable: function (orders) {
            if (!orders || orders.length === 0) {
                return `
                    <div class="empty-state">
                        <i class="bi bi-basket"></i>
                        <h4>Không có đơn hàng nào</h4>
                        <p>Bạn chưa có đơn hàng nào trong mục này.</p>
                    </div>
                `;
            }

            let html = `
                <div class="table-responsive-xxl">
                    <table class="table table-bordered table-striped table-hover align-middle">
                        <thead>
                            <tr>
                                <th scope="col" >Mã</th>
                                <th scope="col" >Ngày mua</th>
                                <th scope="col" >Sản phẩm</th>
                                <th scope="col">Tổng tiền</th>
                                <th scope="col">Trạng thái</th>
                                <th scope="col" >Xác thực</th>
                                <th scope="col">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            orders.forEach(function (order) {
                html += `
                    <tr>
                        <th scope="row">${order.id}</th>
                        <td>${order.createdAt}</td>
                        <td>${order.name}</td>
                        <td>${formatters.currency(order.total)}</td>
                        <td>${formatters.statusBadge(order.status)}</td>
                        <td>${formatters.verifyBadge(order.verifyStatus)}</td>
                        <td class="text-center text-nowrap">
                            <a class="btn btn-primary me-2"
                               href="${contextPathMetaTag.content}/orderDetail?id=${order.id}&statusTab=${state.currentStatus}"
                               role="button">
                                Xem đơn hàng
                            </a>
                        </td>
                    </tr>
                `;
            });

            html += `
                        </tbody>
                    </table>
                </div>
            `;

            return html;
        },

        // Tạo HTML cho phân trang
        pagination: function (currentPage, totalPages) {
            if (totalPages <= 1) {
                return '';
            }

            let html = `
                <nav class="mt-4">
                    <ul class="pagination">
                        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                            <a class="page-link" href="#" data-page="${currentPage - 1}">
                                Trang trước
                            </a>
                        </li>
            `;

            for (let i = 1; i <= totalPages; i++) {
                html += `
                    <li class="page-item ${currentPage === i ? 'active' : ''}">
                        <a class="page-link" href="#" data-page="${i}">${i}</a>
                    </li>
                `;
            }

            html += `
                        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="#" data-page="${currentPage + 1}">
                                Trang sau
                            </a>
                        </li>
                    </ul>
                </nav>
            `;

            return html;
        },

        // Tạo HTML cho thông báo lỗi
        errorMessage: function (message) {
            return `
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    <strong>Lỗi:</strong> ${message}
                </div>
            `;
        }
    };

    // API - Hàm gọi API
    const api = {
        // Lấy danh sách đơn hàng
        fetchOrders: async function (status, page) {
            try {
                // Xây dựng URL API
                let url = `${API_URL}?page=${page}`;
                if (status !== 'all') {
                    url += `&status=${status}`;
                }

                console.log(`Fetching orders from: ${url}`);

                // Gửi request
                const response = await fetch(url);

                // Kiểm tra response
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Server returned ${response.status}: ${errorText}`);
                }

                // Chuyển đổi response sang JSON
                const data = await response.json();
                console.log('Orders data received:', data);

                return data;
            } catch (error) {
                console.error('Error fetching orders:', error);
                throw error;
            }
        }
    };

    // UI Operations - Các thao tác UI
    const ui = {
        // Hiển thị loading spinner
        showLoading: function () {
            elements.loadingSpinner.style.display = 'block';
            elements.orderTableContainer.innerHTML = '';
            elements.paginationContainer.innerHTML = '';
        },

        // Ẩn loading spinner
        hideLoading: function () {
            elements.loadingSpinner.style.display = 'none';
        },

        // Cập nhật tab active
        updateActiveTab: function (status) {
            elements.tabButtons.forEach(btn => btn.classList.remove('active'));
            const activeTab = Array.from(elements.tabButtons).find(tab => tab.getAttribute('data-status') === status);
            if (activeTab) {
                activeTab.classList.add('active');
            }
        },

        // Hiển thị dữ liệu đơn hàng
        displayOrders: function (data) {
            elements.orderTableContainer.innerHTML = htmlGenerators.orderTable(data.orders);
            elements.paginationContainer.innerHTML = htmlGenerators.pagination(data.currentPage, data.totalPages);

            // Thêm event listener cho các nút phân trang
            const paginationLinks = document.querySelectorAll('#pagination-container .page-link');
            paginationLinks.forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const page = parseInt(this.getAttribute('data-page'));
                    handlers.changePage(page);
                });
            });
        },

        // Hiển thị thông báo lỗi
        displayError: function (error) {
            elements.orderTableContainer.innerHTML = htmlGenerators.errorMessage(
                error.message || 'Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại sau.'
            );
        }
    };

    // Event Handlers - Các xử lý sự kiện
    const handlers = {
        // Xử lý khi thay đổi tab
        changeTab: function (status) {
            state.currentStatus = status;
            state.currentPage = 1;

            ui.updateActiveTab(status);
            operations.loadOrders(status, 1);
        },

        // Xử lý khi thay đổi trang
        changePage: function (page) {
            state.currentPage = page;
            operations.loadOrders(state.currentStatus, page);
        }
    };

    // Operations - Các thao tác tổng hợp
    const operations = {
        // Tải dữ liệu đơn hàng
        loadOrders: async function (status, page) {
            ui.showLoading();

            try {
                const data = await api.fetchOrders(status, page);
                ui.displayOrders(data);
            } catch (error) {
                ui.displayError(error);
            } finally {
                ui.hideLoading();
            }
        },

        // Khởi tạo trang
        init: function () {
            // Thêm event listener cho các tab
            elements.tabButtons.forEach(tab => {
                tab.addEventListener('click', function () {
                    handlers.changeTab(this.getAttribute('data-status'));
                });
            });

            // Tải đơn hàng ban đầu
            operations.loadOrders(INITIAL_STATUS, INITIAL_PAGE);
        }
    };

    // Khởi tạo trang
    operations.init();
});