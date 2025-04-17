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

WITH daily_stock_changes AS (
    -- Thu thập tất cả các giao dịch tồn kho theo ngày
    SELECT 
        DATE(ih.created_at) AS transaction_date,
        ih.product_id,
        ih.quantity_change
    FROM 
        bookshopdb.inventory_history ih
    WHERE 
        ih.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)
        -- Lọc theo sản phẩm cụ thể (nếu cần)
        -- AND (? IS NULL OR ih.product_id = ?)
        -- Lọc theo danh mục sản phẩm (nếu cần)
        -- AND (? IS NULL OR EXISTS (
--             SELECT 1 FROM bookshopdb.product_category pc 
--             WHERE pc.productId = ih.product_id AND pc.categoryId = ?
--         ))
),
daily_balance AS (
    -- Tính tổng thay đổi cho mỗi ngày
    SELECT 
        transaction_date,
        SUM(quantity_change) AS daily_change
    FROM 
        daily_stock_changes
    GROUP BY 
        transaction_date
),
dates_series AS (
    -- Tạo chuỗi ngày liền mạch trong 30 ngày gần nhất
    SELECT 
        DATE_SUB(CURRENT_DATE(), INTERVAL (a.a + (10 * b.a) + (100 * c.a)) DAY) AS date_day
    FROM 
        (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS a,
        (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2) AS b,
        (SELECT 0 AS a) AS c
    WHERE 
        DATE_SUB(CURRENT_DATE(), INTERVAL (a.a + (10 * b.a) + (100 * c.a)) DAY) >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)
    ORDER BY 
        date_day
),
daily_running_total AS (
    -- Kết hợp chuỗi ngày với thay đổi tồn kho
    SELECT 
        ds.date_day,
        COALESCE(db.daily_change, 0) AS daily_change,
        @running_total := @running_total + COALESCE(db.daily_change, 0) AS running_total
    FROM 
        dates_series ds
    LEFT JOIN 
        daily_balance db ON ds.date_day = db.transaction_date
    CROSS JOIN 
        (SELECT @running_total := (
            -- Lấy số lượng tồn kho ban đầu (trước khoảng thời gian phân tích)
            SELECT 
                COALESCE(SUM(ih.quantity_change), 0)
            FROM 
                bookshopdb.inventory_history ih
            WHERE 
                ih.created_at < DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY)
                -- Lọc theo sản phẩm cụ thể (nếu cần)
                -- AND (? IS NULL OR ih.product_id = ?)
                -- Lọc theo danh mục sản phẩm (nếu cần)
                -- AND (? IS NULL OR EXISTS (
                --    SELECT 1 FROM bookshopdb.product_category pc 
                --    WHERE pc.productId = ih.product_id AND pc.categoryId = ?
                -- ))
        )) AS vars
    ORDER BY 
        ds.date_day
)
SELECT 
    date_day AS ngay,
    date_day AS ngay_goc,
    daily_change AS thay_doi_trong_ngay,
    running_total AS ton_kho_tich_luy
FROM 
    daily_running_total
ORDER BY 
    date_day;

USE bookshopdb;
-- Tạo bảng tạm chứa lịch sử giá trị tồn kho theo ngày
CREATE TEMPORARY TABLE IF NOT EXISTS temp_daily_inventory AS
SELECT 
    DATE(ih.created_at) AS report_date,
    SUM(ih.quantity_change) AS daily_change
FROM 
    bookshopdb.inventory_history ih
WHERE 
    ih.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)
GROUP BY 
    DATE(ih.created_at);
SELECT * FROM temp_daily_inventory;

-- Tạo chuỗi ngày liên tục 7 ngày gần nhất
CREATE TEMPORARY TABLE IF NOT EXISTS temp_date_range AS
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL (seq-1) DAY) AS report_date
FROM (
    SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) AS dates;

-- Truy vấn chính - Trả về xu hướng giá trị tồn kho theo ngày
SELECT 
    DATE_FORMAT(dr.report_date, '%d/%m') AS label,
    (
        SELECT 
            SUM(p.price * is2.actual_quantity)
        FROM 
            bookshopdb.product p
        JOIN 
            bookshopdb.inventory_status is2 ON p.id = is2.product_id
    ) - 
    COALESCE(
        (
            SELECT 
                SUM(di.daily_change * p.price)
            FROM 
                temp_daily_inventory di
            JOIN 
                bookshopdb.inventory_history ih ON DATE(ih.created_at) = di.report_date
            JOIN 
                bookshopdb.product p ON ih.product_id = p.id
            WHERE 
                di.report_date > dr.report_date
        ), 0
    ) AS value
FROM 
    temp_date_range dr
ORDER BY 
    dr.report_date ASC;

-- Dọn dẹp bảng tạm
DROP TEMPORARY TABLE IF EXISTS temp_daily_inventory;
DROP TEMPORARY TABLE IF EXISTS temp_date_range;

-- ---------------------------------------------------------------
-- 2. XU HƯỚNG TỒN KHO THEO TUẦN (trong 6 tuần gần nhất)
-- ---------------------------------------------------------------

-- Cách đơn giản hơn: Lấy giá trị vào cuối mỗi tuần
SELECT 
    CONCAT('Tuần ', WEEK(date_point, 1)) AS label,
    (
        SELECT 
            SUM(p.price * 
                (
                    COALESCE(
                        (
                            SELECT SUM(ih.quantity_change) 
                            FROM bookshopdb.inventory_history ih 
                            WHERE ih.product_id = p.id 
                            AND ih.created_at <= date_point
                        ), 0
                    )
                )
            )
        FROM 
            bookshopdb.product p
        WHERE 
            p.id BETWEEN 1 AND 100
    ) AS value
FROM (
    SELECT CURRENT_DATE() - INTERVAL (seq-1)*7 DAY AS date_point
    FROM (
        SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 
        UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 
        UNION SELECT 7 UNION SELECT 8 UNION SELECT 9
        UNION SELECT 10 UNION SELECT 11 UNION SELECT 12
    ) AS weeks
) AS date_points
ORDER BY 
    date_point DESC;
    
-- ---------------------------------------------------------------
-- 3. XU HƯỚNG TỒN KHO THEO THÁNG (trong 6 tháng gần nhất)
-- ---------------------------------------------------------------

-- Lấy giá trị tồn kho vào cuối mỗi tháng
SELECT 
    DATE_FORMAT(last_day_of_month, '%m/%Y') AS label,
    (
        SELECT 
            SUM(p.price * 
                (
                    COALESCE(
                        (
                            SELECT SUM(ih.quantity_change) 
                            FROM bookshopdb.inventory_history ih 
                            WHERE ih.product_id = p.id 
                            AND ih.created_at <= last_day_of_month
                        ), 0
                    )
                )
            )
        FROM 
            bookshopdb.product p
        WHERE 
            p.id BETWEEN 1 AND 100
    ) AS value
FROM (
    SELECT 
        LAST_DAY(DATE_SUB(CURRENT_DATE(), INTERVAL seq MONTH)) AS last_day_of_month
    FROM (
        SELECT 0 AS seq UNION SELECT 1 UNION SELECT 2 
        UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    ) AS months
) AS month_ends
ORDER BY 
    last_day_of_month ASC;

-- ---------------------------------------------------------------
-- 4. XU HƯỚNG TỒN KHO THEO QUÝ (trong 4 quý gần nhất)
-- ---------------------------------------------------------------

-- Lấy giá trị tồn kho vào cuối mỗi quý
SELECT 
    CONCAT('Q', QUARTER(quarter_end), '/', YEAR(quarter_end)) AS label,
    (
        SELECT 
            SUM(p.price * 
                (
                    COALESCE(
                        (
                            SELECT SUM(ih.quantity_change) 
                            FROM bookshopdb.inventory_history ih 
                            WHERE ih.product_id = p.id 
                            AND ih.created_at <= quarter_end
                        ), 0
                    )
                )
            )
        FROM 
            bookshopdb.product p
        WHERE 
            p.id BETWEEN 1 AND 100
    ) AS value
FROM (
    SELECT 
        LAST_DAY(DATE_SUB(CURRENT_DATE(), INTERVAL seq*3 MONTH - DAY(CURRENT_DATE()) + 1 DAY)) AS quarter_end
    FROM (
        SELECT 0 AS seq UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
    ) AS quarters
) AS quarter_ends
ORDER BY 
    quarter_end ASC;
    
SELECT * FROM bookshopdb.inventory_status;