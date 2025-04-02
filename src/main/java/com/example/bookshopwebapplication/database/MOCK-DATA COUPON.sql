-- Mã giảm giá theo phần trăm
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'WELCOME10', 
    'Giảm 10% cho đơn hàng đầu tiên', 
    'percentage', 
    10.00, 
    100000.00, 
    50000.00, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    1000
);

-- Mã giảm giá số tiền cố định
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'SAVE50K', 
    'Giảm 50,000đ cho đơn hàng từ 200,000đ', 
    'fixed', 
    50000.00, 
    200000.00, 
    NULL, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    500
);

-- Mã giảm giá theo phần trăm với giới hạn số lần sử dụng thấp
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'FLASH20', 
    'Flash sale: Giảm 20% tối đa 100,000đ', 
    'percentage', 
    20.00, 
    150000.00, 
    100000.00, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    100
);

-- Mã giảm giá cố định không yêu cầu giá trị đơn hàng tối thiểu
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'FREESHIP', 
    'Miễn phí vận chuyển 30,000đ cho mọi đơn hàng', 
    'fixed', 
    30000.00, 
    0.00, 
    NULL, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    NULL
);

-- Mã giảm giá theo phần trăm với giảm tối đa lớn
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'SUPER30', 
    'Siêu ưu đãi: Giảm 30% tối đa 300,000đ cho đơn hàng từ 500,000đ', 
    'percentage', 
    30.00, 
    500000.00, 
    300000.00, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    200
);

-- Mã giảm giá đã hết hạn (để test)
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'EXPIRED', 
    'Mã giảm giá đã hết hạn', 
    'percentage', 
    15.00, 
    100000.00, 
    50000.00, 
    '2023-01-01 00:00:00', 
    '2023-12-31 23:59:59', 
    100
);

-- Mã giảm giá chưa đến hạn sử dụng (để test)
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'FUTURE25', 
    'Mã giảm giá cho tương lai', 
    'percentage', 
    25.00, 
    200000.00, 
    100000.00, 
    '2025-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    300
);

-- Mã giảm giá đã hết lượt sử dụng (để test)
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit, usage_count
) VALUES (
    'SOLDOUT', 
    'Mã giảm giá đã hết lượt dùng', 
    'fixed', 
    100000.00, 
    300000.00, 
    NULL, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    10,
    10
);

-- Mã giảm giá đặc biệt với giảm phần trăm cao
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'SPECIAL50', 
    'Ưu đãi đặc biệt: Giảm 50% tối đa 500,000đ cho đơn hàng từ 1,000,000đ', 
    'percentage', 
    50.00, 
    1000000.00, 
    500000.00, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    50
);

-- Mã giảm cho Black Friday
INSERT INTO bookshopdb.coupon (
    code, description, discount_type, discount_value, min_order_value, 
    max_discount, start_date, end_date, usage_limit
) VALUES (
    'BLACK40', 
    'Black Friday: Giảm 40% tối đa 400,000đ', 
    'percentage', 
    40.00, 
    300000.00, 
    400000.00, 
    '2023-01-01 00:00:00', 
    '2025-12-31 23:59:59', 
    150
);