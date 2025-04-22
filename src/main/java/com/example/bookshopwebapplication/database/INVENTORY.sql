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
SELECT * FROM bookshopdb.inventory_import;
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
ALTER TABLE bookshopdb.inventory_history 
ADD COLUMN receipt_id BIGINT NULL,
ADD FOREIGN KEY (receipt_id) REFERENCES bookshopdb.inventory_receipts(id); -- receipt_id để liên kết với phiếu

CREATE TABLE bookshopdb.inventory_receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_code VARCHAR(20) NOT NULL,  -- Mã phiếu, ví dụ: NK-20250422-001
    receipt_type ENUM('import', 'export') NOT NULL,  -- Loại phiếu: nhập/xuất
    supplier VARCHAR(100) NULL,  -- ID nhà cung cấp (cho phiếu nhập)
    customer_id BIGINT NULL,  -- ID khách hàng (cho phiếu xuất)
    order_id BIGINT NULL,  -- ID đơn hàng liên quan (nếu có)
    total_items INT NOT NULL,  -- Tổng số mặt hàng
    total_quantity INT NOT NULL,  -- Tổng số lượng
    notes TEXT NULL,  -- Ghi chú
    status ENUM('draft', 'pending', 'completed', 'cancelled') NOT NULL DEFAULT 'draft',
    created_by BIGINT NOT NULL,  -- Người tạo
    approved_by BIGINT NULL,  -- Người duyệt
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME NULL  -- Thời gian hoàn thành
);
-- Index cho tìm kiếm theo mã phiếu (thường xuyên được sử dụng để tra cứu)
CREATE INDEX idx_inventory_receipts_receipt_code ON bookshopdb.inventory_receipts(receipt_code);

-- Index cho lọc theo loại phiếu (nhập/xuất)
CREATE INDEX idx_inventory_receipts_receipt_type ON bookshopdb.inventory_receipts(receipt_type);

-- Index cho trạng thái phiếu (draft, pending, completed, cancelled)
CREATE INDEX idx_inventory_receipts_status ON bookshopdb.inventory_receipts(status);

-- Index cho thời gian tạo (tìm kiếm theo khoảng thời gian)
CREATE INDEX idx_inventory_receipts_created_at ON bookshopdb.inventory_receipts(created_at);

-- Index phức hợp cho tìm kiếm theo nhà cung cấp và loại phiếu
CREATE INDEX idx_inventory_receipts_supplier_type ON bookshopdb.inventory_receipts(supplier_id, receipt_type);

-- Index phức hợp cho tìm kiếm theo đơn hàng
CREATE INDEX idx_inventory_receipts_order_id ON bookshopdb.inventory_receipts(order_id);

-- Index cho người tạo và người duyệt (phục vụ tìm kiếm theo người)
CREATE INDEX idx_inventory_receipts_created_by ON bookshopdb.inventory_receipts(created_by);
CREATE INDEX idx_inventory_receipts_approved_by ON bookshopdb.inventory_receipts(approved_by);

CREATE TABLE bookshopdb.inventory_receipt_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_id BIGINT NOT NULL,  -- ID phiếu
    product_id BIGINT NOT NULL,  -- ID sản phẩm
    quantity INT NOT NULL,  -- Số lượng
    unit_price FLOAT NULL,  -- Đơn giá (có thể null với xuất kho)
    notes TEXT NULL,  -- Ghi chú cho từng sản phẩm
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receipt_id) REFERENCES inventory_receipts(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);
-- Index chính để tìm kiếm tất cả các mục thuộc một phiếu
CREATE INDEX idx_inventory_receipt_items_receipt_id ON bookshopdb.inventory_receipt_items(receipt_id);

-- Index để tìm tất cả các phiếu có chứa một sản phẩm cụ thể
CREATE INDEX idx_inventory_receipt_items_product_id ON bookshopdb.inventory_receipt_items(product_id);

-- Index phức hợp cho cả phiếu và sản phẩm
CREATE INDEX idx_inventory_receipt_items_receipt_product ON bookshopdb.inventory_receipt_items(receipt_id, product_id);

-- Index cho thời gian tạo
CREATE INDEX idx_inventory_receipt_items_created_at ON bookshopdb.inventory_receipt_items(created_at);

SELECT * FROM bookshopdb.inventory_receipt_items;
SELECT * FROM bookshopdb.inventory_receipts;
SELECT * FROM bookshopdb.inventory_history ORDER BY created_at desc;
SELECT * FROM bookshopdb.inventory_import ORDER BY created_at desc;