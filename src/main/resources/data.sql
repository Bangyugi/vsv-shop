-- =================================================================
-- BẢNG ROLES
-- =================================================================
INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Vai trò người dùng cơ bản'),
       ('ROLE_SELLER', 'Vai trò người bán hàng/chủ shop'),
       ('ROLE_ADMIN', 'Vai trò quản trị viên'),
       ('ROLE_SUPERADMIN', 'Vai trò quản trị viên cấp cao');

-- =================================================================
-- BẢNG USERS
-- Mật khẩu cho tất cả user mẫu đều là: password123 (đã được mã hóa)
-- =================================================================
-- user_id sẽ tự động tăng từ 1
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled, account_status, created_at, updated_at)
VALUES ('bangtran', '$2a$10$tFd/sNlZuPL.FNL898ihnuSkNl4y9YbDVi1qtA7btiTkgrpkbbZjq', 'bangtran08@vsvshop.com', '0334236824', 'Trần', 'Văn Bằng', '1990-01-01', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('truongtung', '$2a$10$wPtCn17ymboK2o/BMfr1AeIns/J0oftrCStE/w3vhgEfTSNqfdYq6', 'truongtung02@vsvshop.com', '0918572821', 'Nguyễn', 'Trường Tùng', '1995-08-15', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('nanguyen', '$2a$10$Sps/X/n.jtW4kr60DBhBQ.eyXlgqk/7JfbRJE/0lQgxDlER5lC/bS', 'nanguyen04@vsvshop.com', '0972571254', 'Nguyễn', 'Thị Na', '1992-05-10', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('huyhoang', '$2a$10$CbYskRKD2dJRJLqjNd6VhuSWzzV8H1QILRyU/saE3rqj2TJzPfocC', 'hoanghuy@gmail.com', '0918830673', 'Đinh', 'Huy Hoàng', '1995-09-22', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW());



-- =================================================================
-- BẢNG USER_ROLE
-- Phân quyền cho từng user
-- =================================================================
INSERT INTO user_role(user_id, role_id)
VALUES
    -- Superadmin có mọi quyền
    (1, 1), (1, 2), (1, 3), (1, 4),
    -- Admin có các quyền quản lý
    (2, 1), (2, 2), (2, 3),
    -- Seller có quyền người bán và người dùng
    (3, 1), (3, 2),
    -- User có quyền người dùng
    (4, 1);

-- =================================================================
-- BẢNG ADDRESSES
-- Thêm địa chỉ cho mỗi user
-- =================================================================
INSERT INTO addresses (id, address_line_1, ward, district, province, country, user_id)
VALUES (1, '123 Đường ABC', 'Phường XYZ', 'Quận 1', 'Thành phố Hồ Chí Minh', 'Việt Nam', 3),
       (2, '456 Đường LMN', 'Phường UVW', 'Quận 2', 'Thành phố Hồ Chí Minh', 'Việt Nam', 1),
       (3, '789 Đường XYZ', 'Phường OPQ', 'Quận 3', 'Thành phố Hồ Chí Minh', 'Việt Nam', 2),
       (4, '101 Đường GHI', 'Phường JKL', 'Quận 4', 'Thành phố Hồ Chí Minh', 'Việt Nam', 4);

SELECT setval('addresses_id_seq', (SELECT MAX(id) FROM addresses));
-- =================================================================
-- BẢNG SELLERS
-- Thêm người bán tương ứng với user có id = 3
-- =================================================================

INSERT INTO sellers (id, business_name, business_email, business_mobile, business_address, account_number, account_holder_name, bank_name, ifsc_code, pickup_address_id, gstin, is_email_verified, account_status, created_at, updated_at)
VALUES (3, 'Na Boutique', 'nanguyen04@vsvshop.com', '0972571254', '123 Đường ABC, Phường XYZ, Quận 1, TP.HCM', '1234567890', 'NGUYEN THI NA', 'Vietcombank', 'BFTVVNVX', 1, 'GSTIN123456789', TRUE, 'ACTIVE', NOW(), NOW());


-- BẢNG CARTS
-- Mỗi user sẽ có một cart
-- =================================================================
INSERT INTO carts (user_id)
VALUES (1), (2), (3),(4);


-- =================================================================
-- BẢNG CATEGORIES - Dành cho thời trang
-- =================================================================
-- Cấp 1
INSERT INTO categories (name, level)
VALUES ('Thời Trang Nam', 1),
       ('Thời Trang Nữ', 1),
       ('Phụ Kiện', 1);

-- Cấp 2
INSERT INTO categories (name, level, parent_category_id)
VALUES ('Áo Nam', 2, 1),
       ('Quần Nam', 2, 1),
       ('Áo Nữ', 2, 2),
       ('Váy Nữ', 2, 2),
       ('Túi Xách', 2, 3);

-- Cấp 3
INSERT INTO categories (name, level, parent_category_id)
VALUES ('Áo Thun Nam', 3, 4),
       ('Áo Sơ Mi Nam', 3, 4),
       ('Quần Jeans Nữ', 3, 7),
       ('Chân Váy', 3, 7);

-- =================================================================
-- BẢNG PRODUCTS - Khoảng 10 sản phẩm về quần áo
-- =================================================================
INSERT INTO products (title, description, price, selling_price, discount_percent, num_ratings, category_id, seller_id, created_at, updated_at)
VALUES
    ('Áo Thun Cổ Tròn In Hình', 'Áo thun cotton thoáng mát, in hình độc đáo', 250000, 199000, 20, 150, 9, 3, NOW(), NOW()),
    ('Áo Sơ Mi Dài Tay Kẻ Caro', 'Áo sơ mi nam phong cách Hàn Quốc, chất vải kate', 450000, 399000, 11, 200, 10, 3, NOW(), NOW()),
    ('Quần Jeans Skinny Rách Gối', 'Quần jeans nữ tôn dáng, rách gối cá tính', 550000, 450000, 18, 300, 11, 3, NOW(), NOW()),
    ('Chân Váy Chữ A Xếp Ly', 'Chân váy ngắn trẻ trung, dễ phối đồ', 350000, 299000, 15, 120, 12, 3, NOW(), NOW()),
    ('Váy Hoa Nhí Vintage', 'Váy voan hoa nhí, phong cách retro nhẹ nhàng', 650000, 550000, 15, 80, 7, 3, NOW(), NOW()),
    ('Quần Kaki Nam Dáng Suông', 'Quần kaki nam ống đứng, thoải mái vận động', 400000, 350000, 13, 180, 5, 3, NOW(), NOW()),
    ('Áo Croptop Tay Phồng', 'Áo croptop nữ tính, tay phồng điệu đà', 280000, 220000, 21, 250, 6, 3, NOW(), NOW()),
    ('Túi Tote Vải Canvas', 'Túi tote tiện dụng, in slogan ý nghĩa', 150000, 99000, 34, 400, 8, 3, NOW(), NOW()),
    ('Áo Khoác Bomber Kaki', 'Áo khoác bomber nam, chất liệu kaki dày dặn', 750000, 650000, 13, 95, 4, 3, NOW(), NOW()),
    ('Đầm Maxi Đi Biển', 'Đầm maxi hai dây, họa tiết nhiệt đới', 850000, 699000, 18, 60, 7, 3, NOW(), NOW());

-- =================================================================
-- BẢNG PRODUCT_VARIANTS - Thêm các biến thể cho sản phẩm
-- =================================================================
INSERT INTO product_variants (product_id, sku, color, size, quantity)
VALUES
    -- Áo Thun Cổ Tròn In Hình
    (1, 'AT001-TRANG-S', 'Trắng', 'S', 50),
    (1, 'AT001-TRANG-M', 'Trắng', 'M', 50),
    (1, 'AT001-DEN-M', 'Đen', 'M', 100),
    -- Áo Sơ Mi Dài Tay Kẻ Caro
    (2, 'SM001-XANH-L', 'Xanh', 'L', 30),
    (2, 'SM001-DO-L', 'Đỏ', 'L', 30),
    -- Quần Jeans Skinny Rách Gối
    (3, 'QJ001-XANH-28', 'Xanh', '28', 70),
    (3, 'QJ001-XANH-29', 'Xanh', '29', 70),
    -- Chân Váy Chữ A Xếp Ly
    (4, 'CV001-DEN-S', 'Đen', 'S', 40),
    (4, 'CV001-BE-S', 'Be', 'S', 40),
    -- Váy Hoa Nhí Vintage
    (5, 'VH001-VANG-M', 'Vàng', 'M', 25),
    -- Quần Kaki Nam Dáng Suông
    (6, 'QK001-BE-30', 'Be', '30', 60),
    (6, 'QK001-XANH-31', 'Xanh rêu', '31', 60),
    -- Áo Croptop Tay Phồng
    (7, 'AC001-TRANG-M', 'Trắng', 'M', 80),
    -- Túi Tote Vải Canvas
    (8, 'TT001-KEM-ONESIZE', 'Kem', 'One Size', 150),
    -- Áo Khoác Bomber Kaki
    (9, 'AK001-DEN-XL', 'Đen', 'XL', 45),
    -- Đầm Maxi Đi Biển
    (10, 'DM001-HOATTIET-S', 'Họa tiết', 'S', 20);




INSERT INTO coupons (code, discount_percentage, start_date, end_date, min_order_value, is_active)
VALUES
    -- Mã còn hiệu lực, không yêu cầu giá trị tối thiểu
    ('GIAM10', 10.00, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '7 day', 0, TRUE),
    -- Mã đã hết hạn
    ('SALEHETHAN', 20.00, '2023-01-01', '2023-01-31', 0, TRUE),
    -- Mã chưa có hiệu lực
    ('SAPTOI', 15.00, CURRENT_DATE + INTERVAL '7 day', CURRENT_DATE + INTERVAL '14 day', 0, TRUE),
    -- Mã yêu cầu giá trị đơn hàng tối thiểu 500k
    ('GIAM50K', 10.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 day', 500000, TRUE),
    -- Mã không hoạt động
    ('KHOA', 50.00, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 day', 0, FALSE);
