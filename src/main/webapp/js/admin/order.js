// Initiate the OrderTable class
class OrderTable {
    constructor(tableSelector) {
        this.table = tableSelector
        this.dataTable = null;
        this.isProcessing = false; // Thêm cờ để kiểm soát việc thực thi
    }

    init() {
        this.dataTable = $(this.table).DataTable({
            processing: true, // Hiển thị trạng thái "Loading..."
            serverSide: true, // Chế độ Server-side Rendering
            searching: true,
            pageLength: 10,
            ajax: {
                url: "/admin/get-orders",
                type: "GET",
                dataSrc: "data",
                data: function (d) {
                    d.search = d.search.value;
                },
                error: function (xhr, error, thrown) {
                    // Xử lý lỗi khi tải dữ liệu
                    alert('Lỗi tải dữ liệu. Vui lòng thử lại.');
                }
            },
            columns: this.getColumns(),
            order: [[0, 'desc']],
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.13.6/i18n/vi.json'
            }
        });
    }
    _formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(price.toFixed());
    }
    // Lấy các columns
    getColumns() {
        // Cấu hình các cột hiển thị
        return [
            {data: "id"},
            {data: "username"},
            {data: "createdAt"},
            {data: "updatedAt"},
            {data: "productCount"},
            {data: "totalPrice"},
            {data: "status"},
            {data: "verify"},
            {data: "actions", orderTable: false},
        ];
    }

    // Hàm xử lý các nút hành động
    handleAction(event) {
        const button = event.target.closest('button');
        const action = button.getAttribute('data-action');
        const orderId = button.getAttribute('data-id');
        console.log("Click button: ", action, orderId);

        if (!action || !orderId) {
            return;
        }

        // Gọi API xử lý hành động
        this.executeAction(action, orderId);
    }

    // Gửi yêu cầu AJAX để thực hiện hành động
    executeAction(actionType, orderId) {
        // Ngăn chặn việc thực thi nhiều lần đồng thời
        if (this.isProcessing) {
            alert('Đang xử lý yêu cầu. Vui lòng đợi!');
            return;
        }

        this.isProcessing = true;

        $.ajax({
            url: '/admin/orders',
            type: 'POST',
            data: {
                id: orderId,
                action: actionType
            },
            success: (response) => {
                console.log('Response: ', response);
                if (response.success) {
                    alert(response.message);

                    // Sử dụng phương thức reload có tham số để giữ nguyên trang hiện tại
                    this.dataTable.ajax.reload(null, false);
                } else {
                    alert('Không thể thực hiện hành động. Vui lòng thử lại sau!');
                }
            },
            error: (err) => {
                alert('Không thể thực hiện hành động. Vui lòng thử lại sau!');
            },
            complete: () => {
                // Đảm bảo reset cờ xử lý sau khi hoàn tất
                this.isProcessing = false;
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const orderTable = new OrderTable("#ordersTable");
    if (!$.fn.DataTable.isDataTable("#ordersTable")) {
        orderTable.init();
    }

    // Thêm sự kiện xử lý các nút hành động
    $('#ordersTable').on('draw.dt', function () {
        // Sử dụng event delegation để xử lý sự kiện click cho tất cả các nút trong bảng
        $('#ordersTable').on('click', '.action-btn', function (event) {
            orderTable.handleAction(event);  // Gọi phương thức handleAction và truyền button
        });
    });
});