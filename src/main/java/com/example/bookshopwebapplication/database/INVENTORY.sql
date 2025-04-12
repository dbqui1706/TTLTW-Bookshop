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

