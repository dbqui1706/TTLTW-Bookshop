-- ========== PHẦN 1: DỮ LIỆU NHẬP KHO ==========
-- Tạo dữ liệu cho bảng inventory_import (lịch sử nhập kho)
-- Giả định mỗi sản phẩm từ ID 1-100 đều có ít nhất 1 lần nhập kho

-- Xóa dữ liệu cũ (nếu có) để tránh trùng lặp
DELETE FROM bookshopdb.inventory_import;

-- Danh sách nhà cung cấp logic
SET @supplier_list = 'NXB Kim Đồng|NXB Trẻ|NXB Văn Học|NXB Thanh Niên|NXB Giáo Dục|NXB Hội Nhà Văn|NXB Tổng Hợp|NXB Phụ Nữ|Nhà sách Phương Nam|Fahasa|First News|Alpha Books';

-- Nhập kho ban đầu cho tất cả sản phẩm từ ID 1-100 (từ tháng 9-11/2024)
INSERT INTO bookshopdb.inventory_import 
(product_id, quantity, cost_price, supplier, import_date, notes, created_by)
SELECT 
    p.id AS product_id,
    FLOOR(RAND() * 30) + 20 AS quantity, -- Số lượng nhập từ 20-50
    CASE 
        WHEN p.price > 0 THEN p.price * 0.7 -- Giá nhập = 70% giá bán
        ELSE FLOOR(RAND() * 100000) + 50000 -- Nếu không có giá bán thì random
    END AS cost_price,
    ELT(FLOOR(RAND() * 12) + 1, 
        'NXB Kim Đồng', 'NXB Trẻ', 'NXB Văn Học', 'NXB Thanh Niên', 
        'NXB Giáo Dục', 'NXB Hội Nhà Văn', 'NXB Tổng Hợp', 'NXB Phụ Nữ', 
        'Nhà sách Phương Nam', 'Fahasa', 'First News', 'Alpha Books') AS supplier,
    DATE_ADD('2024-09-01', INTERVAL FLOOR(RAND() * 90) DAY) AS import_date, -- Ngày nhập từ 01/09-30/11/2024
    'Nhập kho ban đầu' AS notes,
    1 AS created_by
FROM 
    bookshopdb.product p
WHERE 
    p.id BETWEEN 1 AND 100;

-- Nhập kho bổ sung cho các sản phẩm bán chạy (khoảng 40% sản phẩm, 2 lần nhập trong tháng 12/2024-01/2025)
INSERT INTO bookshopdb.inventory_import 
(product_id, quantity, cost_price, supplier, import_date, notes, created_by)
WITH random_products AS (
    SELECT 
        id 
    FROM 
        bookshopdb.product 
    WHERE 
        id BETWEEN 1 AND 100
    ORDER BY 
        RAND()
    LIMIT 40
)
SELECT 
    rp.id AS product_id,
    FLOOR(RAND() * 20) + 10 AS quantity, -- Số lượng nhập từ 10-30
    CASE 
        WHEN p.price > 0 THEN p.price * 0.7 -- Giá nhập = 70% giá bán
        ELSE FLOOR(RAND() * 100000) + 50000 -- Nếu không có giá bán thì random
    END AS cost_price,
    (SELECT supplier FROM bookshopdb.inventory_import WHERE product_id = rp.id ORDER BY import_date DESC LIMIT 1) AS supplier, -- Giữ nguyên nhà cung cấp
    DATE_ADD('2024-12-01', INTERVAL FLOOR(RAND() * 60) DAY) AS import_date, -- Ngày nhập từ 01/12/2024-30/01/2025
    'Nhập kho bổ sung' AS notes,
    1 AS created_by
FROM 
    random_products rp
JOIN 
    bookshopdb.product p ON rp.id = p.id;

-- Nhập kho bổ sung cho các sản phẩm bán chạy (khoảng 30% sản phẩm, 1 lần nhập trong tháng 02-03/2025)
INSERT INTO bookshopdb.inventory_import 
(product_id, quantity, cost_price, supplier, import_date, notes, created_by)
WITH random_products AS (
    SELECT 
        id 
    FROM 
        bookshopdb.product 
    WHERE 
        id BETWEEN 1 AND 100
    ORDER BY 
        RAND()
    LIMIT 30
)
SELECT 
    rp.id AS product_id,
    FLOOR(RAND() * 20) + 10 AS quantity, -- Số lượng nhập từ 10-30
    CASE 
        WHEN p.price > 0 THEN p.price * 0.7 -- Giá nhập = 70% giá bán
        ELSE FLOOR(RAND() * 100000) + 50000 -- Nếu không có giá bán thì random
    END AS cost_price,
    (SELECT supplier FROM bookshopdb.inventory_import WHERE product_id = rp.id ORDER BY import_date DESC LIMIT 1) AS supplier, -- Giữ nguyên nhà cung cấp
    DATE_ADD('2025-02-01', INTERVAL FLOOR(RAND() * 60) DAY) AS import_date, -- Ngày nhập từ 01/02-01/04/2025
    'Nhập kho bổ sung' AS notes,
    1 AS created_by
FROM 
    random_products rp
JOIN 
    bookshopdb.product p ON rp.id = p.id;

-- Nhập kho bổ sung gần đây (khoảng 20% sản phẩm, trong tháng 04/2025)
INSERT INTO bookshopdb.inventory_import 
(product_id, quantity, cost_price, supplier, import_date, notes, created_by)
WITH random_products AS (
    SELECT 
        id 
    FROM 
        bookshopdb.product 
    WHERE 
        id BETWEEN 1 AND 100
    ORDER BY 
        RAND()
    LIMIT 20
)
SELECT 
    rp.id AS product_id,
    FLOOR(RAND() * 20) + 10 AS quantity, -- Số lượng nhập từ 10-30
    CASE 
        WHEN p.price > 0 THEN p.price * 0.7 -- Giá nhập = 70% giá bán
        ELSE FLOOR(RAND() * 100000) + 50000 -- Nếu không có giá bán thì random
    END AS cost_price,
    (SELECT supplier FROM bookshopdb.inventory_import WHERE product_id = rp.id ORDER BY import_date DESC LIMIT 1) AS supplier, -- Giữ nguyên nhà cung cấp
    DATE_ADD('2025-04-01', INTERVAL FLOOR(RAND() * 14) DAY) AS import_date, -- Ngày nhập từ 01/04-15/04/2025
    'Nhập kho bổ sung' AS notes,
    1 AS created_by
FROM 
    random_products rp
JOIN 
    bookshopdb.product p ON rp.id = p.id;
    
SELECT * FROM bookshopdb.inventory_import;
