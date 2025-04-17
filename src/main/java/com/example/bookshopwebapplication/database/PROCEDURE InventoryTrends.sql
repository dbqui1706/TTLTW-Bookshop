-- ============================================================
-- STORED PROCEDURES CHO BIỂU ĐỒ XU HƯỚNG TỒN KHO
-- ============================================================

DELIMITER //

-- ---------------------------------------------------------------
-- 1. XU HƯỚNG TỒN KHO THEO NGÀY (trong 7 ngày gần nhất)
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS bookshopdb.GetInventoryTrendDaily //
CREATE PROCEDURE bookshopdb.GetInventoryTrendDaily()
BEGIN
    -- Tạo bảng tạm chứa lịch sử giá trị tồn kho theo ngày
    DROP TEMPORARY TABLE IF EXISTS temp_daily_inventory;
    CREATE TEMPORARY TABLE temp_daily_inventory AS
    SELECT 
        DATE(ih.created_at) AS report_date,
        SUM(ih.quantity_change) AS daily_change
    FROM 
        bookshopdb.inventory_history ih
    WHERE 
        ih.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)
    GROUP BY 
        DATE(ih.created_at);

    -- Tạo chuỗi ngày liên tục 7 ngày gần nhất
    DROP TEMPORARY TABLE IF EXISTS bookshopdb.temp_date_range;
    CREATE TEMPORARY TABLE bookshopdb.temp_date_range AS
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
        bookshopdb.temp_date_range dr
    ORDER BY 
        dr.report_date ASC;

    -- Dọn dẹp bảng tạm
    DROP TEMPORARY TABLE IF EXISTS bookshopdb.temp_daily_inventory;
    DROP TEMPORARY TABLE IF EXISTS bookshopdb.temp_date_range;
END //
CALL bookshopdb.GetInventoryTrendDaily();
-- ---------------------------------------------------------------
-- 2. XU HƯỚNG TỒN KHO THEO TUẦN (trong 6 tuần gần nhất)
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS bookshopdb.GetInventoryTrendWeekly //
CREATE PROCEDURE bookshopdb.GetInventoryTrendWeekly()
BEGIN
    -- Lấy giá trị tồn kho vào cuối mỗi tuần
    SELECT 
        CONCAT('Tuần ', WEEK(date_point, 1), '/', YEAR(date_point)) AS label,
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
        ) AS weeks
    ) AS date_points
    ORDER BY 
        date_point ASC;
END //
CALL bookshopdb.GetInventoryTrendWeekly();
-- ---------------------------------------------------------------
-- 3. XU HƯỚNG TỒN KHO THEO THÁNG (trong 6 tháng gần nhất)
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS bookshopdb.GetInventoryTrendMonthly //
CREATE PROCEDURE bookshopdb.GetInventoryTrendMonthly()
BEGIN
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
END //
CALL bookshopdb.GetInventoryTrendMonthly();
-- ---------------------------------------------------------------
-- 4. XU HƯỚNG TỒN KHO THEO QUÝ (trong 4 quý gần nhất)
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS bookshopdb.GetInventoryTrendQuarterly //
CREATE PROCEDURE bookshopdb.GetInventoryTrendQuarterly()
BEGIN
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
            -- Cách tính cuối quý đơn giản hơn, tương thích với nhiều phiên bản MySQL
            LAST_DAY(DATE_ADD(DATE_SUB(CURRENT_DATE(), INTERVAL seq*3 MONTH), INTERVAL 2 - MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL seq*3 MONTH)) % 3 MONTH)) AS quarter_end
        FROM (
            SELECT 0 AS seq UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
        ) AS quarters
    ) AS quarter_ends
    ORDER BY 
        quarter_end ASC;
END //
CALL bookshopdb.GetInventoryTrendQuarterly();

-- ---------------------------------------------------------------
-- 5. PROCEDURE TỔNG HỢP
-- ---------------------------------------------------------------

DROP PROCEDURE IF EXISTS bookshopdb.GetInventoryTrends //
CREATE PROCEDURE bookshopdb.GetInventoryTrends(IN interval_type VARCHAR(10))
BEGIN
    -- Kiểm tra tham số và gọi procedure tương ứng
    IF interval_type = 'day' OR interval_type IS NULL THEN
        CALL bookshopdb.GetInventoryTrendDaily();
    ELSEIF interval_type = 'week' THEN
        CALL bookshopdb.GetInventoryTrendWeekly();
    ELSEIF interval_type = 'month' THEN
        CALL bookshopdb.GetInventoryTrendMonthly();
    ELSEIF interval_type = 'quarter' THEN
        CALL GetInventoryTrendQuarterly();
    ELSEIF interval_type = 'all' THEN
        -- Trả về dữ liệu của tất cả các khoảng thời gian
        -- Dùng bảng tạm để lưu kết quả từ mỗi procedure
        
        -- Tạo bảng tạm để lưu kết quả
        DROP TEMPORARY TABLE IF EXISTS bookshopdb.temp_trend_results;
        CREATE TEMPORARY TABLE bookshopdb.temp_trend_results (
            interval_type VARCHAR(10),
            label VARCHAR(20),
            value DECIMAL(15,2)
        );
        
        -- Thêm dữ liệu xu hướng ngày
        INSERT INTO bookshopdb.temp_trend_results (interval_type, label, value)
        SELECT 
            'day' AS interval_type,
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
                        (
                            SELECT 
                                DATE(ih.created_at) AS report_date,
                                SUM(ih.quantity_change) AS daily_change
                            FROM 
                                bookshopdb.inventory_history ih
                            WHERE 
                                ih.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)
                            GROUP BY 
                                DATE(ih.created_at)
                        ) di
                    JOIN 
                        bookshopdb.inventory_history ih ON DATE(ih.created_at) = di.report_date
                    JOIN 
                        bookshopdb.product p ON ih.product_id = p.id
                    WHERE 
                        di.report_date > dr.report_date
                ), 0
            ) AS value
        FROM 
            (
                SELECT 
                    DATE_SUB(CURRENT_DATE(), INTERVAL (seq-1) DAY) AS report_date
                FROM (
                    SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
                    UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
                ) AS dates
            ) dr;
        
        -- Thêm dữ liệu xu hướng tuần
        INSERT INTO bookshopdb.temp_trend_results (interval_type, label, value)
        SELECT 
            'week' AS interval_type,
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
            ) AS weeks
        ) AS date_points;
        
        -- Thêm dữ liệu xu hướng tháng
        INSERT INTO bookshopdb.temp_trend_results (interval_type, label, value)
        SELECT 
            'month' AS interval_type,
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
        ) AS month_ends;
        
        -- Thêm dữ liệu xu hướng quý (đã sửa lỗi)
        INSERT INTO bookshopdb.temp_trend_results (interval_type, label, value)
        SELECT 
            'quarter' AS interval_type,
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
                -- Cách tính cuối quý đơn giản hơn
                LAST_DAY(DATE_ADD(DATE_SUB(CURRENT_DATE(), INTERVAL seq*3 MONTH), INTERVAL 2 - MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL seq*3 MONTH)) % 3 MONTH)) AS quarter_end
            FROM (
                SELECT 0 AS seq UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
            ) AS quarters
        ) AS quarter_ends;
        
        -- Trả về tất cả kết quả
        SELECT * FROM bookshopdb.temp_trend_results;
        
        -- Dọn dẹp bảng tạm
        DROP TEMPORARY TABLE IF EXISTS bookshopdb.temp_trend_results;
    ELSE
        -- Trường hợp không hợp lệ, trả về thông báo lỗi
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Invalid interval_type. Valid values are: day, week, month, quarter, all';
    END IF;
END //
DELIMITER ;


-- Lấy xu hướng theo ngày
CALL bookshopdb.GetInventoryTrends('day');

-- Lấy xu hướng theo tuần
CALL bookshopdb.GetInventoryTrends('week');

-- Lấy xu hướng theo tháng 
CALL bookshopdb.GetInventoryTrends('month');

-- Lấy xu hướng theo quý
CALL bookshopdb.GetInventoryTrends('quarter');

-- Lấy tất cả xu hướng một lúc
CALL bookshopdb.GetInventoryTrends('all');


-- ============================================================
-- QUERY PHÂN BỐ TỒN KHO THEO DANH MỤC
-- ============================================================

-- 1. Phân bố theo số lượng sản phẩm trong mỗi danh mục
SELECT 
    c.name AS category_name,
    COUNT(DISTINCT p.id) AS product_count,
    SUM(is2.actual_quantity) AS total_quantity,
    ROUND(SUM(is2.actual_quantity * p.price), 0) AS total_value,
    ROUND(SUM(is2.actual_quantity) / (
        SELECT SUM(actual_quantity) FROM bookshopdb.inventory_status
    ) * 100, 1) AS percentage_by_quantity
FROM 
    bookshopdb.category c
JOIN 
    bookshopdb.product_category pc ON c.id = pc.categoryId
JOIN 
    bookshopdb.product p ON pc.productId = p.id
JOIN 
    bookshopdb.inventory_status is2 ON p.id = is2.product_id
-- Có thể thêm điều kiện lọc ở đây, ví dụ:
-- WHERE is2.actual_quantity > 0  -- Chỉ tính những sản phẩm có tồn kho
GROUP BY 
    c.id, c.name
ORDER BY 
    total_quantity DESC;

-- Query tổng hợp cho bảng trạng thái tồn kho với đầy đủ bộ lọc
-- Các tham số:
-- p_stock_status: Trạng thái tồn kho ('normal', 'low', 'out')
-- p_category_id: ID danh mục 
-- p_duration_days: Số ngày tồn kho tối thiểu
-- p_start_date, p_end_date: Khoảng thời gian
-- p_search: Từ khóa tìm kiếm
-- p_offset, p_limit: Phân trang

SELECT 
    p.id AS product_id,
    p.name AS product_name,
    p.author AS product_author,
    p.imageName AS product_image,
    (
        SELECT GROUP_CONCAT(c.name SEPARATOR ', ') 
        FROM bookshopdb.product_category pc 
        JOIN bookshopdb.category c ON pc.categoryId = c.id 
        WHERE pc.productId = p.id
    ) AS categories,
    ist.actual_quantity AS stock_quantity,
    ist.reserved_quantity AS reserved_quantity,
    ist.available_quantity AS available_quantity,
    ist.reorder_threshold AS threshold,
    ist.last_updated AS last_updated,
    DATEDIFF(CURRENT_DATE(), 
        COALESCE(
            (SELECT MIN(ii.import_date) FROM bookshopdb.inventory_import ii WHERE ii.product_id = p.id),
            p.createdAt
        )
    ) AS days_in_inventory,
    CASE 
        WHEN ist.actual_quantity = 0 THEN 'out'
        WHEN ist.actual_quantity <= ist.reorder_threshold THEN 'low'
        ELSE 'normal'
    END AS stock_status,
    p.price AS price,
    (p.price * ist.actual_quantity) AS inventory_value
FROM 
    bookshopdb.product p
JOIN 
    bookshopdb.inventory_status ist ON p.id = ist.product_id
LEFT JOIN 
    bookshopdb.product_category pc ON p.id = pc.productId
-- WHERE 
    -- Lọc theo trạng thái tồn kho
    -- (? IS NULL OR ? = '' OR 
--         CASE 
--             WHEN ? = 'out' THEN ist.actual_quantity = 0
--             WHEN ? = 'low' THEN ist.actual_quantity <= ist.reorder_threshold AND ist.actual_quantity > 0
--             WHEN ? = 'normal' THEN ist.actual_quantity > ist.reorder_threshold
--             ELSE TRUE
--         END
--     )
    
    -- Lọc theo danh mục
    -- AND (? IS NULL OR ? = '' OR EXISTS (
--         SELECT 1 FROM bookshopdb.product_category pc2 
--         WHERE pc2.productId = p.id AND pc2.categoryId = ?
--     ))
    
    -- Lọc theo thời gian tồn kho
    -- AND (? IS NULL OR ? = 0 OR 
--         DATEDIFF(CURRENT_DATE(), 
--             COALESCE(
--                 (SELECT MIN(ii.import_date) FROM bookshopdb.inventory_import ii WHERE ii.product_id = p.id),
--                 p.createdAt
--             )
--         ) >= ?
--     )
    
    -- Lọc theo khoảng thời gian (sử dụng thời gian cập nhật cuối)
    -- AND (? IS NULL OR ist.last_updated >= ?)
--     AND (? IS NULL OR ist.last_updated <= ?)
    
    -- Tìm kiếm theo tên sản phẩm, tác giả hoặc mã sản phẩm
   --  AND (? IS NULL OR ? = '' OR 
--         p.name LIKE CONCAT('%', ?, '%') OR 
--         p.author LIKE CONCAT('%', ?, '%') OR 
--         CAST(p.id AS CHAR) = ?
--     )

GROUP BY 
    p.id
    
-- Sắp xếp theo trạng thái tồn kho và tồn kho thực tế
ORDER BY 
    CASE 
        WHEN ist.actual_quantity = 0 THEN 1
        WHEN ist.actual_quantity <= ist.reorder_threshold THEN 2
        ELSE 3
    END ASC,
    ist.actual_quantity ASC;
    
-- Phân trang
-- LIMIT 0, 10;