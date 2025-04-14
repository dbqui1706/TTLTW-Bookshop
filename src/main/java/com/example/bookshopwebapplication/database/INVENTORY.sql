-- Bảng quản lý nhập kho
CREATE TABLE bookshopdb.inventory_import
(
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    product_id        BIGINT          NOT NULL,
    quantity          INT             NOT NULL, -- Số lượng nhập
    cost_price        FLOAT           NOT NULL, -- Giá nhập
    supplier          VARCHAR(100)    NOT NULL, -- Nhà cung cấp
    import_date       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Ngày nhập
    notes             TEXT            NULL,     -- Ghi chú
    created_by        BIGINT          NOT NULL, -- Người tạo phiếu nhập
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_inventory_import_product (product_id),
    INDEX idx_inventory_import_date (import_date),
    CONSTRAINT fk_inventory_import_product
        FOREIGN KEY (product_id)
        REFERENCES bookshopdb.product (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Bảng quản lý tồn kho hiện tại của sản phẩm
CREATE TABLE bookshopdb.inventory_status
(
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    product_id        BIGINT          NOT NULL UNIQUE, -- Mỗi sản phẩm chỉ có 1 bản ghi tồn kho
    actual_quantity   INT             NOT NULL, -- Số lượng thực tế trong kho
    available_quantity INT            NOT NULL, -- Số lượng có thể bán (trừ đi đã đặt)
    reserved_quantity INT             NOT NULL DEFAULT 0, -- Số lượng đã đặt chưa giao
    reorder_threshold INT             NOT NULL DEFAULT 5, -- Ngưỡng cảnh báo
    last_updated      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_inventory_status_product (product_id),
    CONSTRAINT fk_inventory_status_product
        FOREIGN KEY (product_id)
        REFERENCES bookshopdb.product (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Bảng lịch sử thay đổi số lượng
CREATE TABLE bookshopdb.inventory_history
(
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    product_id        BIGINT          NOT NULL,
    quantity_change   INT             NOT NULL, -- Số lượng thay đổi (dương: tăng, âm: giảm)
    previous_quantity INT             NOT NULL, -- Số lượng trước thay đổi
    current_quantity  INT             NOT NULL, -- Số lượng sau thay đổi
    action_type       ENUM('import', 'export', 'adjustment') NOT NULL, -- Loại hành động
    reason            VARCHAR(255)    NOT NULL, -- Lý do (nhập hàng, bán hàng, kiểm kê, hư hỏng...)
    reference_id      BIGINT          NULL,     -- ID tham chiếu (đơn hàng, phiếu nhập...)
    reference_type    VARCHAR(50)     NULL,     -- Loại tham chiếu (order, import)
    notes             TEXT            NULL,     -- Ghi chú bổ sung
    created_by        BIGINT          NOT NULL, -- ID người thực hiện
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_inventory_history_product (product_id),
    INDEX idx_inventory_history_date (created_at),
    INDEX idx_inventory_history_reference (reference_id, reference_type),
    CONSTRAINT fk_inventory_history_product
        FOREIGN KEY (product_id)
        REFERENCES bookshopdb.product (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- Lệnh INSERT để khởi tạo dữ liệu cho bảng inventory_status từ bảng product
INSERT INTO bookshopdb.inventory_status 
(product_id, actual_quantity, available_quantity, reserved_quantity, reorder_threshold, last_updated)
SELECT 
    id,                   -- product_id
    quantity,             -- actual_quantity (lấy từ số lượng hiện tại)
    quantity,             -- available_quantity (ban đầu bằng với số lượng thực tế)
    0,                    -- reserved_quantity (ban đầu chưa có đơn hàng đặt)
    5,                    -- reorder_threshold (ngưỡng cảnh báo tái đặt hàng mặc định)
    CURRENT_TIMESTAMP     -- last_updated
FROM 
    bookshopdb.product;
    
-- Kiểm tra dữ liệu
SELECT 
    p.id, 
    p.name, 
    p.quantity AS product_quantity, 
    ist.actual_quantity, 
    ist.available_quantity, 
    ist.reserved_quantity, 
    ist.reorder_threshold
FROM 
    bookshopdb.product p
JOIN 
    bookshopdb.inventory_status ist ON p.id = ist.product_id
ORDER BY 
    p.id;

SELECT * FROM bookshopdb.inventory_status WHERE product_id = 51;
UPDATE bookshopdb.inventory_status SET available_quantity = available_quantity - 1,  
reserved_quantity = 1 WHERE product_id = 51;

SELECT * FROM bookshopdb.inventory_import;

SELECT * FROM bookshopdb.orders;

-- Truy vấn lấy thông tin đơn hàng dựa trên mã đơn hàng
SELECT 
    o.id,
    o.order_code AS code,
    o.created_at AS orderDate,
    o.status,
    CASE 
        WHEN EXISTS (SELECT 1 FROM bookshopdb.payment_transaction pt WHERE pt.order_id = o.id AND pt.status = 'completed') 
        THEN 'Đã thanh toán'
        WHEN EXISTS (SELECT 1 FROM bookshopdb.payment_transaction pt WHERE pt.order_id = o.id AND pt.status = 'processing') 
        THEN 'Đang xử lý thanh toán'
        ELSE 'Chưa thanh toán'
    END AS paymentStatus,
    pm.name AS paymentMethod,
    o.note,
    -- Thông tin khách hàng
    u.fullname AS customer_name,
    u.phoneNumber AS customer_phone,
    u.email AS customer_email,
    os.address_line1 AS customer_address_line1,
    os.address_line2 AS customer_address_line2,
    dm.name AS name_shipping,
    dm.description as description_shipping,
    CONCAT(os.address_line1, 
           CASE WHEN os.address_line2 IS NOT NULL THEN CONCAT(', ', os.address_line2) ELSE '' END,
           ', ', os.ward, 
           ', ', os.district, 
           ', ', os.city) AS customer_full_address,
    -- Tổng hợp đơn hàng
    o.subtotal,
    o.discount_amount AS discount,
    o.delivery_price AS shipping,
    o.total_amount AS total
FROM 
    bookshopdb.orders o
JOIN 
    bookshopdb.user u ON o.user_id = u.id
JOIN 
    bookshopdb.order_shipping os ON o.id = os.order_id
JOIN 
    bookshopdb.payment_method pm ON o.payment_method_id = pm.id
JOIN 
	bookshopdb.delivery_method dm on o.delivery_method_id = dm.id
LEFT JOIN 
    bookshopdb.payment_transaction pt ON o.id = pt.order_id
WHERE 
    o.order_code = 'ORD-2565e296';

-- Truy vấn lấy các mặt hàng trong đơn hàng
SELECT 
    oi.id,
    p.imageName AS image,
    oi.product_name AS name,
    p.id AS sku,
    oi.price,
    oi.base_price,
    oi.quantity,
    oi.subtotal AS total
FROM 
    bookshopdb.order_item oi
JOIN 
    bookshopdb.product p ON oi.product_id = p.id
JOIN 
    bookshopdb.orders o ON oi.order_id = o.id
WHERE 
    o.order_code = 'ORD-2565e296';

-- Truy vấn lấy lịch sử trạng thái đơn hàng
SELECT 
    osh.created_at AS date,
    osh.status as status_code,
    CASE 
        WHEN osh.status = 'pending' THEN 'Đơn hàng đã đặt'
        WHEN osh.status = 'waiting_payment' THEN 'Chờ thanh toán'
        WHEN osh.status = 'payment_failed' THEN 'Thanh toán thất bại'
        WHEN osh.status = 'processing' THEN 'Đã xác nhận'
        WHEN osh.status = 'shipping' THEN 'Đang giao hàng'
        WHEN osh.status = 'delivered' THEN 'Đã giao hàng'
        WHEN osh.status = 'cancelled' THEN 'Đã hủy'
        WHEN osh.status = 'refunded' THEN 'Đã hoàn tiền'
        ELSE osh.status
    END AS status,
    osh.note AS description,
    CASE 
        WHEN osh.changed_by IS NOT NULL THEN 
            (SELECT fullname FROM bookshopdb.user WHERE id = osh.changed_by)
        ELSE NULL
    END AS changed_by_name
FROM 
    bookshopdb.order_status_history osh
JOIN 
    bookshopdb.orders o ON osh.order_id = o.id
WHERE 
    o.order_code = 'ORD-2565e296'
ORDER BY 
    osh.created_at ASC;