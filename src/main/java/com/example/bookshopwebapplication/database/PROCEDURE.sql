
-- 1. Procedure thống kê User 
DELIMITER //

CREATE PROCEDURE bookshopdb.GetUserStatistics()
BEGIN
    SELECT
        -- Tổng số người dùng
        COUNT(u.id) AS total_users,
        
        -- Số người dùng đang hoạt động và tỷ lệ
        SUM(CASE WHEN us.is_active = 1 THEN 1 ELSE 0 END) AS active_users,
        ROUND(100.0 * SUM(CASE WHEN us.is_active = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(u.id), 0), 2) AS active_percentage,
        
        -- Số người dùng mới trong tháng này và tháng trước
        SUM(CASE WHEN YEAR(u.created_at) = YEAR(CURRENT_DATE()) AND MONTH(u.created_at) = MONTH(CURRENT_DATE()) THEN 1 ELSE 0 END) AS new_users_this_month,
        SUM(CASE WHEN YEAR(u.created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) AND MONTH(u.created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) AS new_users_last_month,
        
        -- Tài khoản bị khóa và tỷ lệ
        SUM(CASE WHEN us.is_locked = 1 THEN 1 ELSE 0 END) AS locked_accounts,
        ROUND(100.0 * SUM(CASE WHEN us.is_locked = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(u.id), 0), 2) AS locked_percentage
        
    FROM 
        bookshopdb.user u
    LEFT JOIN 
        bookshopdb.user_status us ON u.id = us.user_id;
END //

DELIMITER ;

CALL bookshopdb.GetUserStatistics();

