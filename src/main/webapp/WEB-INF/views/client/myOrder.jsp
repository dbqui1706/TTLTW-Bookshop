<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="/common/meta.jsp"/>
    <title>Đơn hàng</title>
    <style>
        /* Tab bar styles */
        .history-tabs {
            display: flex;
            background-color: #4285f4;
            border-radius: 4px;
            overflow: hidden;
            margin-bottom: 20px;
        }

        .history-tab {
            padding: 12px 24px;
            color: rgba(255, 255, 255, 0.8);
            text-decoration: none;
            font-weight: 500;
            text-align: center;
            flex: 1;
            transition: background-color 0.3s;
            white-space: nowrap;
            cursor: pointer;
        }

        .history-tab:hover {
            background-color: rgba(255, 255, 255, 0.1);
            color: white;
        }

        .history-tab.active {
            background-color: rgba(255, 255, 255, 0.2);
            color: white;
            font-weight: 600;
        }

        /* Responsive styling */
        @media (max-width: 768px) {
            .history-tabs {
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }

            .history-tab {
                padding: 12px 15px;
            }
        }

        /* Loading spinner */
        #loading-spinner {
            display: none;
            text-align: center;
            padding: 20px;
        }

        .spinner-border {
            width: 3rem;
            height: 3rem;
        }

        /* Empty state */
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .empty-state i {
            font-size: 48px;
            color: #adb5bd;
            margin-bottom: 16px;
        }

        .empty-state h4 {
            margin-bottom: 8px;
            color: #495057;
        }

        .empty-state p {
            color: #6c757d;
            max-width: 400px;
            margin: 0 auto;
        }
    </style>
</head>

<body>
<jsp:include page="/common/client/header.jsp"/>

<section class="section py-2 bg-light">
    <div class="container">
        <h2 class="title-page">Đơn hàng</h2>
    </div> <!-- container.// -->
</section> <!-- section-pagetop.// -->

<section class="section-content padding-y">
    <div class="container">
        <div class="row">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <jsp:include page="/common/client/navPanel.jsp">
                        <jsp:param name="active" value="ORDER"/>
                    </jsp:include>

                    <main class="col-md-9">
                        <!-- Tab bar lịch sử mua hàng -->
                        <div class="history-tabs">
                            <div class="history-tab active" data-status="all">
                                Tất cả
                            </div>
                            <div class="history-tab" data-status="new">
                                Đơn hàng mới
                            </div>
                            <div class="history-tab" data-status="confirmed">
                                Đã xác nhận
                            </div>
                            <div class="history-tab" data-status="shipping">
                                Đang vận chuyển
                            </div>
                            <div class="history-tab" data-status="completed">
                                Hoàn thành
                            </div>
                            <div class="history-tab" data-status="canceled">
                                Đã hủy
                            </div>
                        </div>

                        <!-- Loading spinner -->
                        <div id="loading-spinner">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Đang tải...</span>
                            </div>
                            <p class="mt-2">Đang tải dữ liệu...</p>
                        </div>

                        <!-- Bảng danh sách đơn hàng -->
                        <div id="order-table-container">
                            <!-- Nội dung bảng đơn hàng sẽ được nạp qua Ajax -->
                        </div>

                        <!-- Phân trang -->
                        <div id="pagination-container" class="d-flex justify-content-center">
                            <!-- Phân trang sẽ được nạp qua Ajax -->
                        </div>
                    </main>
                    <!-- col.// -->
                </c:when>
                <c:otherwise>
                    <p>
                        Vui lòng <a href="${pageContext.request.contextPath}/signin">đăng nhập</a> để sử dụng trang này.
                    </p>
                </c:otherwise>
            </c:choose>
        </div> <!-- row.// -->
    </div> <!-- container.// -->
</section> <!-- section-content.// -->

<jsp:include page="/common/client/footer.jsp"/>

<!-- JavaScript cho Ajax và xử lý tab -->
<script src="${pageContext.request.contextPath}/js/my-order.js" type="module"></script>
</body>
</html>