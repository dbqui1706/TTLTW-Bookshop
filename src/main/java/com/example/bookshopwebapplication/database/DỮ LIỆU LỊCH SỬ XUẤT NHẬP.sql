-- ========== PHẦN 2: DỮ LIỆU LỊCH SỬ XUẤT NHẬP ==========
-- Xóa dữ liệu cũ (nếu có) để tránh trùng lặp
DELETE FROM bookshopdb.inventory_history;

-- Tạo dữ liệu lịch sử NHẬP kho (từ bảng inventory_import)
INSERT INTO bookshopdb.inventory_history
(product_id, quantity_change, previous_quantity, current_quantity, action_type, reason, reference_id, reference_type, notes, created_by, created_at)
SELECT 
    ii.product_id,
    ii.quantity AS quantity_change,
    0 AS previous_quantity, -- Giả định số lượng trước đó là 0 cho đơn giản
    ii.quantity AS current_quantity,
    'import' AS action_type,
    'Nhập kho' AS reason,
    ii.id AS reference_id,
    'inventory_import' AS reference_type,
    ii.notes,
    1 AS created_by,
    ii.import_date AS created_at
FROM 
    bookshopdb.inventory_import ii
ORDER BY 
    ii.import_date;
    
-- Tạo dữ liệu lịch sử XUẤT kho (đơn hàng) cho các sản phẩm
-- Tạo khoảng 1000 đơn hàng từ 11/2024 đến nay
-- Mỗi đơn hàng xuất từ 1-3 sản phẩm

-- Tạo bảng tạm để lưu thông tin đơn hàng
DROP TEMPORARY TABLE IF EXISTS tempdb.temp_orders;
CREATE TEMPORARY TABLE tempdb.temp_orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME
);

-- Thêm 1000 đơn hàng từ 11/2024 đến nay
INSERT INTO tempdb.temp_orders (order_date)
SELECT DATE_ADD('2024-11-01', INTERVAL FLOOR(RAND() * 166) DAY) -- 166 ngày từ 01/11/2024 đến 15/04/2025
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) AS t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) AS t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) AS t3
ORDER BY 1
LIMIT 1000;
SELECT count(*) FROM tempdb.temp_orders;

-- Tạo bảng tạm để lưu chi tiết đơn hàng
DROP TEMPORARY TABLE IF EXISTS tempdb.temp_order_items;
CREATE TEMPORARY TABLE tempdb.temp_order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT,
    order_date DATETIME
);

-- Thêm chi tiết đơn hàng (1-3 sản phẩm mỗi đơn)
INSERT INTO tempdb.temp_order_items (order_id, product_id, quantity, order_date)
SELECT 
    o.order_id,
    p.id AS product_id,
    FLOOR(RAND() * 5) + 1 AS quantity, -- Số lượng từ 1-5
    o.order_date
FROM 
    tempdb.temp_orders o
JOIN 
    bookshopdb.product p ON p.id BETWEEN 1 AND 100
WHERE 
    RAND() < 0.02 -- Xác suất để chọn sản phẩm vào đơn hàng (điều chỉnh để có 1-3 sản phẩm/đơn)
ORDER BY 
    o.order_id;
SELECT * FROM tempdb.temp_order_items;

-- Cập nhật lịch sử tồn kho với các giao dịch xuất hàng
INSERT INTO bookshopdb.inventory_history
(product_id, quantity_change, previous_quantity, current_quantity, action_type, reason, reference_id, reference_type, notes, created_by, created_at)
SELECT 
    oi.product_id,
    -oi.quantity AS quantity_change, -- Số âm biểu thị xuất kho
    100 AS previous_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    100 - oi.quantity AS current_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    'export' AS action_type,
    'Đơn hàng' AS reason,
    oi.order_id AS reference_id,
    'order' AS reference_type,
    CONCAT('Đơn hàng #', oi.order_id) AS notes,
    1 AS created_by,
    oi.order_date AS created_at
FROM 
    tempdb.temp_order_items oi;
SELECT * FROM bookshopdb.inventory_history;

-- 1. Thêm điều chỉnh do kiểm kê (50 sản phẩm random)
INSERT INTO bookshopdb.inventory_history
(product_id, quantity_change, previous_quantity, current_quantity, action_type, reason, reference_id, reference_type, notes, created_by, created_at)
SELECT 
    p.id AS product_id,
    CASE WHEN RAND() > 0.5 THEN FLOOR(RAND() * 5) + 1 ELSE -(FLOOR(RAND() * 3) + 1) END AS quantity_change, -- Điều chỉnh +1 đến +5 hoặc -1 đến -3
    100 AS previous_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    100 + CASE WHEN RAND() > 0.5 THEN FLOOR(RAND() * 5) + 1 ELSE -(FLOOR(RAND() * 3) + 1) END AS current_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    'adjustment' AS action_type,
    'Kiểm kê' AS reason,
    NULL AS reference_id,
    NULL AS reference_type,
    CASE 
        WHEN RAND() > 0.5 THEN 'Điều chỉnh sau kiểm kê - tìm thấy thêm sách'
        ELSE 'Điều chỉnh sau kiểm kê - phát hiện thiếu sách'
    END AS notes,
    1 AS created_by,
    DATE_ADD('2025-01-15', INTERVAL FLOOR(RAND() * 90) DAY) AS created_at
FROM 
    bookshopdb.product p
WHERE 
    p.id BETWEEN 1 AND 100
ORDER BY 
    RAND()
LIMIT 50;

-- 2. Thêm điều chỉnh do hàng hỏng (30 sản phẩm random)
INSERT INTO bookshopdb.inventory_history
(product_id, quantity_change, previous_quantity, current_quantity, action_type, reason, reference_id, reference_type, notes, created_by, created_at)
SELECT 
    p.id AS product_id,
    -(FLOOR(RAND() * 3) + 1) AS quantity_change, -- Giảm từ 1-3 sản phẩm
    100 AS previous_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    100 - (FLOOR(RAND() * 3) + 1) AS current_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    'adjustment' AS action_type,
    'Mất mát/Hư hỏng' AS reason,
    NULL AS reference_id,
    NULL AS reference_type,
    CASE 
        WHEN RAND() > 0.7 THEN 'Sách bị hư hỏng do ẩm mốc'
        WHEN RAND() > 0.4 THEN 'Sách bị rách bìa'
        ELSE 'Sách bị mất trang'
    END AS notes,
    1 AS created_by,
    DATE_ADD('2025-02-01', INTERVAL FLOOR(RAND() * 70) DAY) AS created_at
FROM 
    bookshopdb.product p
WHERE 
    p.id BETWEEN 1 AND 100
ORDER BY 
    RAND()
LIMIT 30;

-- 3. Thêm điều chỉnh do trả hàng (20 sản phẩm random)
INSERT INTO bookshopdb.inventory_history
(product_id, quantity_change, previous_quantity, current_quantity, action_type, reason, reference_id, reference_type, notes, created_by, created_at)
SELECT 
    p.id AS product_id,
    FLOOR(RAND() * 3) + 1 AS quantity_change, -- Tăng từ 1-3 sản phẩm
    100 AS previous_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    100 + (FLOOR(RAND() * 3) + 1) AS current_quantity, -- Giá trị mặc định, sẽ cập nhật sau
    'adjustment' AS action_type,
    'Trả hàng' AS reason,
    1000 + FLOOR(RAND() * 1000) AS reference_id,
    'return' AS reference_type,
    CASE 
        WHEN RAND() > 0.7 THEN 'Khách hàng trả lại do đặt nhầm'
        WHEN RAND() > 0.4 THEN 'Khách hàng trả lại do sách lỗi in'
        ELSE 'Khách hàng trả lại do sách không đúng yêu cầu'
    END AS notes,
    1 AS created_by,
    DATE_ADD('2025-03-01', INTERVAL FLOOR(RAND() * 45) DAY) AS created_at
FROM 
    bookshopdb.product p
WHERE 
    p.id BETWEEN 1 AND 100
ORDER BY 
    RAND()
LIMIT 20;

-- ========== PHẦN 3: DỮ LIỆU TRẠNG THÁI TỒN KHO HIỆN TẠI ==========
-- Xóa dữ liệu cũ (nếu có) để tránh trùng lặp
DELETE FROM bookshopdb.inventory_status;

-- Tính toán số lượng tồn kho thực tế dựa trên lịch sử nhập xuất
INSERT INTO bookshopdb.inventory_status
(product_id, actual_quantity, available_quantity, reserved_quantity, reorder_threshold, last_updated)
SELECT 
    p.id AS product_id,
    -- Tính tổng số lượng hiện có (tổng nhập - tổng xuất + tổng điều chỉnh)
    GREATEST(
        COALESCE((
            SELECT SUM(quantity_change) 
            FROM bookshopdb.inventory_history
            WHERE product_id = p.id
        ), 0),
        0
    ) AS actual_quantity,
    -- Số lượng có thể bán = actual_quantity - reserved_quantity
    GREATEST(
        COALESCE((
            SELECT SUM(quantity_change) 
            FROM bookshopdb.inventory_history
            WHERE product_id = p.id
        ), 0) - FLOOR(RAND() * 3),
        0
    ) AS available_quantity,
    -- Số lượng đã đặt chưa giao
    FLOOR(RAND() * 3) AS reserved_quantity,
    -- Ngưỡng cảnh báo: 5, 8, 10 hoặc 15 tùy theo sản phẩm
    ELT(FLOOR(RAND() * 4) + 1, 5, 8, 10, 15) AS reorder_threshold,
    NOW() AS last_updated
FROM 
    bookshopdb.product p
WHERE 
    p.id BETWEEN 1 AND 100;

-- Đảm bảo available_quantity + reserved_quantity = actual_quantity
SET SQL_SAFE_UPDATES = 0;
UPDATE bookshopdb.inventory_status 
SET available_quantity = actual_quantity - reserved_quantity 
WHERE product_id = product_id AND available_quantity + reserved_quantity != actual_quantity;
SET SQL_SAFE_UPDATES = 1;

-- ========== PHẦN 4: HOÀN THIỆN DỮ LIỆU LỊCH SỬ XUẤT NHẬP ==========
-- Cập nhật lại previous_quantity và current_quantity trong bảng inventory_history
-- để đảm bảo tính nhất quán của dữ liệu

-- Tạo bảng tạm để tính toán số lượng trước và sau mỗi giao dịch
DROP TEMPORARY TABLE IF EXISTS temp_inventory_running_totals;
CREATE TEMPORARY TABLE tempdb.temp_inventory_running_totals AS
SELECT 
    id,
    product_id,
    quantity_change,
    action_type,
    created_at,
    (
        SELECT COALESCE(SUM(ih2.quantity_change), 0)
        FROM bookshopdb.inventory_history ih2
        WHERE ih2.product_id = ih.product_id
        AND (ih2.created_at < ih.created_at OR (ih2.created_at = ih.created_at AND ih2.id < ih.id))
    ) AS previous_quantity,
    (
        SELECT COALESCE(SUM(ih2.quantity_change), 0)
        FROM bookshopdb.inventory_history ih2
        WHERE ih2.product_id = ih.product_id
        AND (ih2.created_at < ih.created_at OR (ih2.created_at = ih.created_at AND ih2.id <= ih.id))
    ) AS current_quantity
FROM 
    bookshopdb.inventory_history ih
ORDER BY 
    product_id, created_at, id;

-- Cập nhật lại bảng inventory_history
SET SQL_SAFE_UPDATES = 0;
UPDATE bookshopdb.inventory_history ih
JOIN tempdb.temp_inventory_running_totals t ON ih.id = t.id
SET 
    ih.previous_quantity = GREATEST(t.previous_quantity, 0),
    ih.current_quantity = GREATEST(t.current_quantity, 0);
SET SQL_SAFE_UPDATES = 1;