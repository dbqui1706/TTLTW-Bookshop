-- ========== BẢNG VAI TRÒ (ROLES) ==========
/*
 * Bảng roles: Lưu trữ các vai trò trong hệ thống
 * - id: Khóa chính, định danh duy nhất cho mỗi vai trò
 * - name: Tên vai trò, phải duy nhất trong hệ thống (ADMIN, CUSTOMER, EMPLOYEE, SHIPPING)
 * - description: Mô tả chi tiết về vai trò
 * - is_system: Đánh dấu vai trò hệ thống (1) không được phép xóa, hoặc vai trò tùy chỉnh (0)
 * - created_at: Thời điểm tạo vai trò
 * - updated_at: Thời điểm cập nhật vai trò gần nhất
 */
CREATE TABLE bookshopdb.roles (
    id BIGINT NOT NULL AUTO_INCREMENT,        -- Định danh duy nhất cho vai trò
    name VARCHAR(50) NOT NULL,                -- Tên vai trò: ADMIN, CUSTOMER, EMPLOYEE, SHIPPING
    description TEXT NULL,                    -- Mô tả chi tiết về vai trò và phạm vi quyền hạn
    is_system BIT NOT NULL DEFAULT 0,         -- Đánh dấu vai trò hệ thống (1) hoặc tùy chỉnh (0)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm tạo
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,    -- Thời điểm cập nhật
    PRIMARY KEY (id),
    UNIQUE INDEX uq_role_name (name)          -- Đảm bảo tên vai trò duy nhất
);

-- ========== BẢNG QUYỀN HẠN (PERMISSIONS) ==========
/*
 * Bảng permissions: Lưu trữ các quyền hạn trong hệ thống
 * - id: Khóa chính, định danh duy nhất cho mỗi quyền
 * - name: Tên quyền dễ đọc cho giao diện người dùng
 * - code: Mã quyền, sử dụng trong code để kiểm tra quyền (format: module.action)
 * - module: Phân nhóm quyền theo module chức năng (user, product, order, shipping, report, system)
 * - description: Mô tả chi tiết về quyền
 * - is_system: Đánh dấu quyền hệ thống (1) không được phép xóa, hoặc quyền tùy chỉnh (0)
 * - created_at: Thời điểm tạo quyền
 * - updated_at: Thời điểm cập nhật quyền gần nhất
 */
CREATE TABLE bookshopdb.permissions (
    id BIGINT NOT NULL AUTO_INCREMENT,        -- Định danh duy nhất cho quyền
    name VARCHAR(100) NOT NULL,               -- Tên quyền hiển thị cho người dùng
    code VARCHAR(100) NOT NULL,               -- Mã quyền dùng trong code (vd: product.create)
    module VARCHAR(50) NOT NULL,              -- Module chức năng (user, product, order...)
    description TEXT NULL,                    -- Mô tả chi tiết về quyền
    is_system BIT NOT NULL DEFAULT 0,         -- Đánh dấu quyền hệ thống (1) hoặc tùy chỉnh (0)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm tạo
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,    -- Thời điểm cập nhật
    PRIMARY KEY (id),
    UNIQUE INDEX uq_permission_code (code)    -- Đảm bảo mã quyền duy nhất
);

-- ========== BẢNG LIÊN KẾT VAI TRÒ - QUYỀN (ROLE_PERMISSIONS) ==========
/*
 * Bảng role_permissions: Liên kết giữa vai trò và quyền (quan hệ nhiều-nhiều)
 * - role_id: Khóa ngoại tham chiếu đến bảng roles
 * - permission_id: Khóa ngoại tham chiếu đến bảng permissions
 * - created_at: Thời điểm gán quyền cho vai trò
 * 
 * Mỗi bản ghi trong bảng này biểu thị một quyền được gán cho một vai trò.
 * Ví dụ: Vai trò ADMIN có tất cả các quyền, vai trò CUSTOMER chỉ có một số quyền giới hạn.
 * Khi xóa vai trò hoặc quyền, tất cả các liên kết liên quan cũng bị xóa (ON DELETE CASCADE).
 */
CREATE TABLE bookshopdb.role_permissions (
    role_id BIGINT NOT NULL,                  -- Tham chiếu đến vai trò
    permission_id BIGINT NOT NULL,            -- Tham chiếu đến quyền
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm gán quyền
    PRIMARY KEY (role_id, permission_id),     -- Khóa chính kết hợp
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
            REFERENCES bookshopdb.roles (id)
            ON DELETE CASCADE                 -- Khi xóa vai trò, xóa tất cả liên kết
            ON UPDATE CASCADE,
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
            REFERENCES bookshopdb.permissions (id)
            ON DELETE CASCADE                 -- Khi xóa quyền, xóa tất cả liên kết
            ON UPDATE CASCADE
);

-- ========== BẢNG LIÊN KẾT NGƯỜI DÙNG - VAI TRÒ (USER_ROLES) ==========
/*
 * Bảng user_roles: Liên kết giữa người dùng và vai trò (quan hệ nhiều-nhiều)
 * - user_id: Khóa ngoại tham chiếu đến bảng user
 * - role_id: Khóa ngoại tham chiếu đến bảng roles
 * - created_at: Thời điểm gán vai trò cho người dùng
 * 
 * Mỗi bản ghi trong bảng này biểu thị một vai trò được gán cho một người dùng.
 * Ví dụ: User1 được gán vai trò ADMIN, User2 được gán vai trò CUSTOMER.
 * Một người dùng có thể có nhiều vai trò, ví dụ: vừa là EMPLOYEE vừa là CUSTOMER.
 * Khi xóa người dùng hoặc vai trò, tất cả các liên kết liên quan cũng bị xóa (ON DELETE CASCADE).
 */
CREATE TABLE bookshopdb.user_roles (
    user_id BIGINT NOT NULL,                  -- Tham chiếu đến người dùng
    role_id BIGINT NOT NULL,                  -- Tham chiếu đến vai trò
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm gán vai trò
    PRIMARY KEY (user_id, role_id),           -- Khóa chính kết hợp
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
            REFERENCES bookshopdb.user (id)
            ON DELETE CASCADE                 -- Khi xóa người dùng, xóa tất cả liên kết
            ON UPDATE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
            REFERENCES bookshopdb.roles (id)
            ON DELETE CASCADE                 -- Khi xóa vai trò, xóa tất cả liên kết
            ON UPDATE CASCADE
);

-- ========== BẢNG QUYỀN ĐẶC BIỆT CHO NGƯỜI DÙNG (USER_PERMISSIONS) ==========
/*
 * Bảng user_permissions: Gán quyền đặc biệt trực tiếp cho người dùng
 * - user_id: Khóa ngoại tham chiếu đến bảng user
 * - permission_id: Khóa ngoại tham chiếu đến bảng permissions
 * - is_granted: Cấp (1) hoặc từ chối (0) quyền cho người dùng
 * - created_at: Thời điểm gán quyền đặc biệt
 * - updated_at: Thời điểm cập nhật quyền đặc biệt gần nhất
 * 
 * Bảng này cho phép cấp hoặc từ chối các quyền cụ thể cho người dùng, bất kể vai trò của họ.
 * Ví dụ: 
 * - User1 là EMPLOYEE nhưng được cấp thêm quyền đặc biệt mà thông thường EMPLOYEE không có.
 * - User2 là ADMIN nhưng bị từ chối một quyền cụ thể dù vai trò ADMIN có quyền đó.
 * 
 * Điều này tạo ra tính linh hoạt cao trong phân quyền:
 * 1. Nếu is_granted = 1: Người dùng được cấp quyền này, bất kể vai trò của họ có quyền hay không.
 * 2. Nếu is_granted = 0: Người dùng bị từ chối quyền này, bất kể vai trò của họ có quyền hay không.
 * 
 * Khi kiểm tra quyền, hệ thống sẽ kiểm tra cả quyền từ vai trò và quyền đặc biệt, với quyền đặc biệt
 * có mức ưu tiên cao hơn.
 */
CREATE TABLE bookshopdb.user_permissions (
    user_id BIGINT NOT NULL,                  -- Tham chiếu đến người dùng
    permission_id BIGINT NOT NULL,            -- Tham chiếu đến quyền
    is_granted BIT NOT NULL DEFAULT 1,        -- 1: Cấp quyền, 0: Từ chối quyền
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm gán quyền
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,    -- Thời điểm cập nhật
    PRIMARY KEY (user_id, permission_id),     -- Khóa chính kết hợp
    CONSTRAINT fk_user_permissions_user
        FOREIGN KEY (user_id)
            REFERENCES bookshopdb.user (id)
            ON DELETE CASCADE                 -- Khi xóa người dùng, xóa tất cả liên kết
            ON UPDATE CASCADE,
    CONSTRAINT fk_user_permissions_permission
        FOREIGN KEY (permission_id)
            REFERENCES bookshopdb.permissions (id)
            ON DELETE CASCADE                 -- Khi xóa quyền, xóa tất cả liên kết
            ON UPDATE CASCADE
);

-- ========== INSERT MOCK DATA ==========

-- ========== BẢNG VAI TRÒ (ROLES) ==========
-- Tạo 4 vai trò: ADMIN, CUSTOMER, EMPLOYEE, SHIPPING
INSERT INTO bookshopdb.roles (name, description, is_system) VALUES
('ADMIN', 'Quản trị viên hệ thống có toàn quyền truy cập', 1),
('CUSTOMER', 'Khách hàng đã đăng ký tài khoản', 1),
('EMPLOYEE', 'Nhân viên cửa hàng sách', 1),
('SHIPPING', 'Nhân viên vận chuyển/giao hàng', 1);

-- ========== BẢNG QUYỀN HẠN (PERMISSIONS) ==========
-- 1. Module User (Quản lý người dùng)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Xem danh sách người dùng', 'user.view_list', 'user', 'Xem danh sách tất cả người dùng'),
('Xem chi tiết người dùng', 'user.view_detail', 'user', 'Xem thông tin chi tiết của người dùng'),
('Tạo người dùng mới', 'user.create', 'user', 'Tạo tài khoản người dùng mới'),
('Chỉnh sửa người dùng', 'user.edit', 'user', 'Chỉnh sửa thông tin người dùng'),
('Xóa người dùng', 'user.delete', 'user', 'Xóa tài khoản người dùng'),
('Khóa/mở khóa tài khoản', 'user.lock_unlock', 'user', 'Khóa hoặc mở khóa tài khoản người dùng'),
('Đặt lại mật khẩu', 'user.reset_password', 'user', 'Đặt lại mật khẩu cho người dùng'),
('Xem lịch sử đăng nhập', 'user.view_login_history', 'user', 'Xem lịch sử đăng nhập của người dùng'),
('Cập nhật thông tin cá nhân', 'user.update_profile', 'user', 'Cập nhật thông tin cá nhân của tài khoản'),
('Quản lý địa chỉ', 'user.manage_addresses', 'user', 'Quản lý địa chỉ của người dùng'),
('Quản lý vai trò', 'user.manage_roles', 'user', 'Gán hoặc hủy vai trò cho người dùng');

-- 2. Module Product (Quản lý sản phẩm/sách)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Xem danh sách sản phẩm', 'product.view_list', 'product', 'Xem danh sách tất cả sản phẩm'),
('Xem chi tiết sản phẩm', 'product.view_detail', 'product', 'Xem thông tin chi tiết của sản phẩm'),
('Tạo sản phẩm mới', 'product.create', 'product', 'Thêm sản phẩm mới vào hệ thống'),
('Chỉnh sửa sản phẩm', 'product.edit', 'product', 'Chỉnh sửa thông tin sản phẩm'),
('Xóa sản phẩm', 'product.delete', 'product', 'Xóa sản phẩm khỏi hệ thống'),
('Quản lý kho', 'product.manage_inventory', 'product', 'Quản lý số lượng tồn kho của sản phẩm'),
('Quản lý giá', 'product.manage_price', 'product', 'Quản lý giá bán và giảm giá của sản phẩm'),
('Quản lý danh mục', 'product.manage_categories', 'product', 'Quản lý phân loại danh mục sản phẩm'),
('Đánh giá sản phẩm', 'product.review', 'product', 'Đánh giá và bình luận về sản phẩm'),
('Quản lý đánh giá', 'product.manage_reviews', 'product', 'Quản lý các đánh giá của khách hàng'),
('Thêm vào giỏ hàng', 'product.add_to_cart', 'product', 'Thêm sản phẩm vào giỏ hàng'),
('Xóa khỏi giỏ hàng', 'product.delete_to_cart', 'product', 'Xóa sản phẩm khỏi giỏ hàng'),
('Thêm vào danh sách yêu thích', 'product.add_to_wishlist', 'product', 'Thêm sản phẩm vào danh sách yêu thích'),
('Xóa khỏi danh sách yêu thích', 'product.delete_to_wishlist', 'product', 'Xóa sản phẩm khỏi danh sách yêu thích');

-- 3. Module Order (Quản lý đơn hàng)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Xem danh sách đơn hàng', 'order.view_list', 'order', 'Xem danh sách tất cả đơn hàng'),
('Xem chi tiết đơn hàng', 'order.view_detail', 'order', 'Xem thông tin chi tiết của đơn hàng'),
('Tạo đơn hàng', 'order.create', 'order', 'Tạo đơn hàng mới'),
('Hủy đơn hàng', 'order.cancel', 'order', 'Hủy đơn hàng đã tạo'),
('Cập nhật trạng thái đơn hàng', 'order.update_status', 'order', 'Cập nhật trạng thái xử lý đơn hàng'),
('Xác nhận đơn hàng', 'order.confirm', 'order', 'Xác nhận đơn hàng mới'),
('Xử lý đơn hàng', 'order.process', 'order', 'Xử lý đơn hàng đã xác nhận'),
('Xem lịch sử đơn hàng', 'order.view_history', 'order', 'Xem lịch sử các đơn hàng đã đặt'),
('Xuất hóa đơn', 'order.export_invoice', 'order', 'Xuất hóa đơn cho đơn hàng'),
('Quản lý thanh toán', 'order.manage_payment', 'order', 'Quản lý thông tin thanh toán đơn hàng'),
('Quản lý hoàn tiền', 'order.manage_refund', 'order', 'Quản lý việc hoàn tiền cho đơn hàng');

-- 4. Module Shipping (Quản lý vận chuyển)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Xem danh sách vận chuyển', 'shipping.view_list', 'shipping', 'Xem danh sách tất cả đơn vận chuyển'),
('Xem chi tiết vận chuyển', 'shipping.view_detail', 'shipping', 'Xem thông tin chi tiết của đơn vận chuyển'),
('Tạo đơn vận chuyển', 'shipping.create', 'shipping', 'Tạo đơn vận chuyển mới'),
('Cập nhật trạng thái vận chuyển', 'shipping.update_status', 'shipping', 'Cập nhật trạng thái vận chuyển'),
('Giao hàng', 'shipping.deliver', 'shipping', 'Xác nhận giao hàng thành công'),
('Quản lý đối tác vận chuyển', 'shipping.manage_partners', 'shipping', 'Quản lý các đơn vị vận chuyển đối tác'),
('Theo dõi vận chuyển', 'shipping.track', 'shipping', 'Theo dõi trạng thái vận chuyển đơn hàng'),
('Quản lý phí vận chuyển', 'shipping.manage_fees', 'shipping', 'Quản lý chi phí vận chuyển');

-- 5. Module Report (Báo cáo)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Xem báo cáo doanh thu', 'report.revenue', 'report', 'Xem báo cáo về doanh thu'),
('Xem báo cáo bán hàng', 'report.sales', 'report', 'Xem báo cáo về tình hình bán hàng'),
('Xem báo cáo kho', 'report.inventory', 'report', 'Xem báo cáo về tình trạng kho'),
('Xem báo cáo khách hàng', 'report.customer', 'report', 'Xem báo cáo về khách hàng'),
('Xuất báo cáo', 'report.export', 'report', 'Xuất báo cáo ra các định dạng khác nhau'),
('Xem biểu đồ thống kê', 'report.view_charts', 'report', 'Xem biểu đồ thống kê dữ liệu');

-- 6. Module System (Hệ thống)
INSERT INTO bookshopdb.permissions (name, code, module, description) VALUES
('Quản lý cấu hình hệ thống', 'system.manage_settings', 'system', 'Quản lý các cài đặt và cấu hình hệ thống'),
('Quản lý bảo mật', 'system.manage_security', 'system', 'Quản lý các thiết lập bảo mật hệ thống'),
('Xem nhật ký hệ thống', 'system.view_logs', 'system', 'Xem nhật ký hoạt động của hệ thống'),
('Sao lưu dữ liệu', 'system.backup', 'system', 'Thực hiện sao lưu dữ liệu hệ thống'),
('Khôi phục dữ liệu', 'system.restore', 'system', 'Khôi phục dữ liệu từ bản sao lưu'),
('Quản lý phân quyền', 'system.manage_permissions', 'system', 'Quản lý quyền hạn trong hệ thống'),
('Quản lý tích hợp', 'system.manage_integrations', 'system', 'Quản lý tích hợp với các dịch vụ bên thứ ba');

-- Quản lý Vai trò (Roles)
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Xem danh sách vai trò', 'system.view_roles', 'system', 'Cho phép xem danh sách và thông tin chi tiết của tất cả các vai trò trong hệ thống', 1),
('Tạo vai trò mới', 'system.create_role', 'system', 'Cho phép tạo vai trò mới trong hệ thống', 1),
('Chỉnh sửa vai trò', 'system.edit_role', 'system', 'Cho phép chỉnh sửa thông tin của vai trò đã tồn tại', 1),
('Xóa vai trò', 'system.delete_role', 'system', 'Cho phép xóa vai trò khỏi hệ thống (chỉ áp dụng cho vai trò không phải hệ thống)', 1);

-- Quản lý Quyền hạn (Permissions)
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Xem danh sách quyền', 'system.view_permissions', 'system', 'Cho phép xem danh sách và thông tin chi tiết của tất cả các quyền trong hệ thống', 1),
('Tạo quyền mới', 'system.create_permission', 'system', 'Cho phép tạo quyền mới trong hệ thống', 1),
('Chỉnh sửa quyền', 'system.edit_permission', 'system', 'Cho phép chỉnh sửa thông tin của quyền đã tồn tại', 1),
('Xóa quyền', 'system.delete_permission', 'system', 'Cho phép xóa quyền khỏi hệ thống (chỉ áp dụng cho quyền không phải hệ thống)', 1);

-- Gán Quyền cho Vai trò (Role-Permissions)
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Gán quyền cho vai trò', 'system.assign_permission_to_role', 'system', 'Cho phép gán quyền cụ thể cho vai trò trong hệ thống', 1),
('Hủy quyền từ vai trò', 'system.remove_permission_from_role', 'system', 'Cho phép hủy bỏ quyền đã gán cho vai trò', 1);

-- Gán Vai trò cho Người dùng (User-Roles)
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Xem vai trò người dùng', 'system.view_user_roles', 'system', 'Cho phép xem các vai trò đã được gán cho từng người dùng', 1),
('Gán vai trò cho người dùng', 'system.assign_role_to_user', 'system', 'Cho phép gán vai trò cụ thể cho người dùng', 1),
('Hủy vai trò từ người dùng', 'system.remove_role_from_user', 'system', 'Cho phép hủy bỏ vai trò đã gán cho người dùng', 1);

-- Quyền Đặc biệt cho Người dùng (User-Permissions)
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Xem quyền đặc biệt của người dùng', 'system.view_user_permissions', 'system', 'Cho phép xem danh sách quyền đặc biệt đã gán trực tiếp cho người dùng', 1),
('Cấp quyền đặc biệt cho người dùng', 'system.grant_permission_to_user', 'system', 'Cho phép cấp quyền đặc biệt trực tiếp cho người dùng, bất kể vai trò của họ', 1),
('Từ chối quyền đặc biệt cho người dùng', 'system.deny_permission_to_user', 'system', 'Cho phép từ chối quyền đặc biệt cho người dùng, kể cả khi vai trò của họ có quyền đó', 1),
('Xóa quyền đặc biệt từ người dùng', 'system.remove_special_permission', 'system', 'Cho phép xóa cấp/từ chối quyền đặc biệt từ người dùng, để trở về trạng thái mặc định theo vai trò', 1);

-- Kiểm tra Quyền
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Kiểm tra quyền', 'system.check_permission', 'system', 'Cho phép kiểm tra quyền của người dùng trong hệ thống', 1);

-- Quyền cho quản lý tồn kho
INSERT INTO bookshopdb.permissions (name, code, module, description, is_system) VALUES
('Xem tồn kho', 'inventory.view_list', 'inventory', 'Cho phép xem các tính năng liên quan đến tồn kho', 1),
('Thực hiện nhập/xuất tồn kho cho các sản phẩm', 'inventory.transaction', 'inventory', 'Cho phép tạo xuất/nhập các phiếu tồn kho', 1);

-- ========== BẢNG LIÊN KẾT VAI TRÒ - QUYỀN (ROLE_PERMISSIONS) ==========

INSERT INTO bookshopdb.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM bookshopdb.roles WHERE name = 'ADMIN'), 
    id 
FROM bookshopdb.permissions WHERE module = 'inventory';

SELECT p.* 
FROM bookshopdb.permissions p 
JOIN bookshopdb.role_permissions rp on p.id = rp.permission_id
JOIN bookshopdb.roles r on rp.role_id = r.id
WHERE r.name = 'ADMIN';


-- 1. Phân quyền cho ADMIN (Có tất cả quyền)
INSERT INTO bookshopdb.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM bookshopdb.roles WHERE name = 'ADMIN'), 
    id 
FROM bookshopdb.permissions;

-- 2. Phân quyền cho CUSTOMER
INSERT INTO bookshopdb.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM bookshopdb.roles WHERE name = 'CUSTOMER'), 
    id 
FROM bookshopdb.permissions 
WHERE code IN (
    -- Quyền xem sản phẩm
    'product.view_list', 
    'product.view_detail',
    -- Quyền giỏ hàng và wishlist
    'product.add_to_cart',
    'product.delete_to_cart',
    'product.add_to_wishlist',
    'product.delete_to_wishlist'
    -- Quyền đánh giá sản phẩm
    'product.review',
    -- Quyền đặt hàng
    'order.create',
    'order.cancel',
    'order.view_history',
    'order.view_detail',
    -- Quyền cập nhật thông tin cá nhân
    'user.update_profile',
    'user.manage_addresses',
    -- Quyền theo dõi vận chuyển
    'shipping.track'
);

-- 3. Phân quyền cho EMPLOYEE
INSERT INTO bookshopdb.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM bookshopdb.roles WHERE name = 'EMPLOYEE'), 
    id 
FROM bookshopdb.permissions 
WHERE code IN (
    -- Quyền quản lý sản phẩm
    'product.view_list', 
    'product.view_detail',
    'product.create',
    'product.edit',
    'product.manage_inventory',
    'product.manage_price',
    'product.manage_categories',
    'product.manage_reviews',
    -- Quyền quản lý đơn hàng
    'order.view_list',
    'order.view_detail',
    'order.confirm',
    'order.process',
    'order.update_status',
    'order.export_invoice',
    'order.manage_payment',
    -- Quyền xem báo cáo
    'report.inventory',
    'report.sales',
    'report.view_charts',
    -- Quyền xem thông tin khách hàng
    'user.view_list',
    'user.view_detail'
);

-- 4. Phân quyền cho SHIPPING
INSERT INTO bookshopdb.role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM bookshopdb.roles WHERE name = 'SHIPPING'), 
    id 
FROM bookshopdb.permissions 
WHERE code IN (
    -- Quyền quản lý vận chuyển
    'shipping.view_list',
    'shipping.view_detail',
    'shipping.update_status',
    'shipping.deliver',
    'shipping.track',
    -- Quyền xem đơn hàng
    'order.view_list',
    'order.view_detail',
    -- Quyền xem thông tin giao hàng
    'user.view_detail'
);

-- ========== DỮ LIỆU MẪU USER VÀ USER_ROLES ==========
-- Chỉnh sửa bảng user thêm trường is_email_verified
ALTER TABLE bookshopdb.user
ADD COLUMN is_email_verified BIT NOT NULL DEFAULT 0 AFTER role;

-- Tạo người dùng mẫu
INSERT INTO bookshopdb.user (username, password, fullname, email, phoneNumber, gender, address, role, is_email_verified)
VALUES
('admin', '202CB962AC59075B964B07152D234B70', 'Admin User', 'admin@bookshop.com', '0987654321', 1, '123 Admin St, Admin City', 'ADMIN', 1),
('customer1', '202CB962AC59075B964B07152D234B70', 'Customer One', 'customer1@example.com', '0123456789', 0, '456 Customer Ave, City', 'CUSTOMER', 1),
('employee1', '202CB962AC59075B964B07152D234B70', 'Employee One', 'employee1@bookshop.com', '0345678901', 1, '789 Staff St, Work City', 'EMPLOYEE', 1),
('shipper1', '202CB962AC59075B964B07152D234B70', 'Shipper One', 'shipper1@bookshop.com', '0567890123', 1, '321 Delivery Rd, Ship City', 'SHIPPING', 1);

-- Gán vai trò cho người dùng mẫu
INSERT INTO bookshopdb.user_roles (user_id, role_id)
VALUES
((SELECT id FROM bookshopdb.user WHERE username = 'admin'), (SELECT id FROM bookshopdb.roles WHERE name = 'ADMIN')),
((SELECT id FROM bookshopdb.user WHERE username = 'customer1'), (SELECT id FROM bookshopdb.roles WHERE name = 'CUSTOMER')),
((SELECT id FROM bookshopdb.user WHERE username = 'employee1'), (SELECT id FROM bookshopdb.roles WHERE name = 'EMPLOYEE')),
((SELECT id FROM bookshopdb.user WHERE username = 'shipper1'), (SELECT id FROM bookshopdb.roles WHERE name = 'SHIPPING'));

-- ========== GÁN QUYỀN ĐẶC BIỆT CHO MỘT SỐ NGƯỜI DÙNG ==========

-- Gán quyền đặc biệt cho Employee1 (có thể hủy đơn hàng - thông thường chỉ Customer và Admin có quyền này)
-- INSERT INTO bookshopdb.user_permissions (user_id, permission_id, is_granted)
-- VALUES (
--     (SELECT id FROM bookshopdb.user WHERE username = 'employee1'),
--     (SELECT id FROM bookshopdb.permissions WHERE code = 'order.cancel'),
--     1
-- );

-- Thu hồi quyền quản lý kho từ Employee1
-- INSERT INTO bookshopdb.user_permissions (user_id, permission_id, is_granted)
-- VALUES (
--     (SELECT id FROM bookshopdb.user WHERE username = 'employee1'),
--     (SELECT id FROM bookshopdb.permissions WHERE code = 'product.manage_inventory'),
--     0
-- );