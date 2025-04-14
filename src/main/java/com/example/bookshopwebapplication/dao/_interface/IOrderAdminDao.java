package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.http.response_admin.orders.*;

import java.util.List;
import java.util.Map;

public interface IOrderAdminDao {
    /**
     * Lấy danh sách đơn hàng với các tùy chọn lọc và phân trang
     *
     * @param params Map chứa các tham số lọc và phân trang
     *               - search: Từ khóa tìm kiếm trong mã đơn hàng, tên khách hàng, số điện thoại
     *               - status: Trạng thái đơn hàng
     *               - paymentMethod: ID phương thức thanh toán
     *               - fromDate: Ngày bắt đầu (yyyy-MM-dd)
     *               - toDate: Ngày kết thúc (yyyy-MM-dd)
     *               - page: Số trang hiện tại
     *               - limit: Số bản ghi trên mỗi trang
     *               - sortBy: Trường cần sắp xếp
     *               - sortDir: Hướng sắp xếp (asc/desc)
     * @return OrderListResponse Đối tượng chứa danh sách đơn hàng và thông tin phân trang
     */
    OrderListResponse getOrdersWithPagination(Map<String, String> params);

    /**
     * Lấy chi tiết đơn hàng theo ID
     *
     * @param orderId ID đơn hàng
     * @return OrderDetailResponse Đối tượng chứa thông tin chi tiết đơn hàng
     */
    public OrderDetailResponse getOrderDetail(Long orderId);

    /**
     * Lấy chi tiết đơn hàng theo code
     *
     * @param code Mã đơn hàng
     * @return OrderDetailResponse Đối tượng chứa thông tin chi tiết đơn hàng
     */
    public OrderDetailResponse getOrderDetailByCode(String code) ;

    /**
     * Cập nhật trạng thái đơn hàng và ghi lịch sử
     *
     * @param orderId ID đơn hàng
     * @param status  Trạng thái mới
     * @param note    Ghi chú khi thay đổi trạng thái
     * @param userId  ID người dùng thực hiện thay đổi
     * @return boolean Kết quả cập nhật
     */
    boolean updateOrderStatus(Long orderId, String status, String note, Long userId);

    /**
     * Kiểm tra xem chuyển đổi trạng thái có hợp lệ không
     * @param oldStatus Trạng thái cũ
     * @param newStatus Trạng thái mới
     * @return boolean Kết quả kiểm tra
     */
    boolean isValidStatusTransition(String oldStatus, String newStatus);

    /**
     * Kiểm tra xem đơn hàng có thể hủy không
     * @param status Trạng thái đơn hàng
     * @return boolean Kết quả kiểm tra
     */
    boolean canCancelOrder(String status);

    /**
     * Xây dựng timeline cho đơn hàng từ lịch sử trạng thái
     * @param statusHistory Lịch sử trạng thái đơn hàng
     * @param orderInfo Thông tin đơn hàng
     * @return List<OrderTimelineDTO> Danh sách các mốc thời gian
     */
    List<OrderTimelineDTO> buildOrderTimeline(List<OrderStatusHistoryDTO> statusHistory, OrderInfoDTO orderInfo);

    /**
     * Lấy thống kê đơn hàng
     * @return OrderStatisticsDTO Đối tượng chứa thống kê đơn hàng
     */
    public OrderStatisticsDTO getOrderStatistics();

    /**
     * Tìm kiếm sản phẩm để thêm vào đơn hàng
     * @param keyword Từ khóa tìm kiếm
     * @return List<ProductSearchDTO> Danh sách sản phẩm tìm thấy
     */
    List<ProductSearchDTO> searchProducts(String keyword);

    /**
     * Tìm kiếm khách hàng để thêm vào đơn hàng
     * @param keyword Từ khóa tìm kiếm
     * @return List<CustomerSearchDTO> Danh sách khách hàng tìm thấy
     */
    public List<CustomerSearchDTO> searchCustomers(String keyword);

}
