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
-- Quản trị viên
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled, account_status, created_at, updated_at)
VALUES ('superadmin', '$2a$10$.KiBYco9zfWcyy5JoA7IhurV1.saoZJtLOOHT5ETZevESLQHmRMZm', 'superadmin@vsvshop.com', '0111111111', 'Super', 'Admin', '1990-01-01', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'OTHER', TRUE, 'ACTIVE', NOW(), NOW()),
       ('admin', '$2a$10$r5/k18lS0z2WqkZbLu34XeX0sTr64W9Exn.Rio9WozMuJFjxSUl.e', 'admin@vsvshop.com', '0222222222', 'Main', 'Admin', '1992-05-10', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW());

-- Người dùng thông thường (khách hàng)
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled, account_status, created_at, updated_at)
VALUES ('johndoe', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'johndoe@email.com', '0333333333', 'John', 'Doe', '1995-08-22', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('janesmith', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'janesmith@email.com', '0444444444', 'Jane', 'Smith', '1998-11-30', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('tranvanbang', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'bang@email.com', '0777777777', 'Bang', 'Tran', '2003-08-02', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW());

-- Các chủ shop (Sellers)
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled, account_status, created_at, updated_at)
VALUES ('seller_chic', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'chic.boutique@shop.com', '0555555555', 'Chic', 'Boutique', '1991-07-20', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('seller_urban', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'urban.threads@shop.com', '0666666666', 'Urban', 'Threads', '1988-03-15', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE', NOW(), NOW());


-- =================================================================
-- BẢNG SELLERS
-- Thông tin chi tiết của các shop bán hàng
-- =================================================================
-- Giả sử id của 'seller_chic' là 6, và 'seller_urban' là 7
INSERT INTO sellers (id, is_email_verified, gstin,
                     business_name, business_email, business_mobile, business_address, logo, banner,
                     account_holder_name, account_number, bank_name, ifsc_code)
VALUES (6, TRUE, 'GSTIN67890FASHION',
        'Chic Boutique', 'support@chicboutique.com', '0555555555', '123 Fashion Ave, HCMC', 'logo_url_chic', 'banner_url_chic',
        'Chic Boutique Owner', '6677889900', 'Vietcombank', 'BFTVVNVX'),
       (7, TRUE,  'GSTIN12345URBAN',
        'Urban Threads', 'contact@urbanthreads.com', '0666666666', '456 Style St, Hanoi', 'logo_url_urban', 'banner_url_urban',
        'Urban Threads Owner', '1122334455', 'Techcombank', 'TCBKVNVX');

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
    -- User thông thường
    (3, 1), (4, 1), (5, 1),
    -- Sellers (có cả vai trò USER và SELLER)
    (6, 1), (6, 2),
    (7, 1), (7, 2);

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
-- BẢNG PRODUCTS
-- =================================================================
INSERT INTO products (title, description, price, selling_price, quantity, color, category_id, seller_id, sizes, in_stock, created_at, updated_at)
VALUES
    -- Sản phẩm của shop Urban Threads (seller_id = 7)
    ('Áo Thun Cotton Basic', 'Áo thun nam 100% cotton, thoáng mát, dễ phối đồ.', 250000, 199000, 100, 'Trắng', 9, 7, 'S, M, L, XL', TRUE, NOW(), NOW()),
    ('Áo Sơ Mi Oxford', 'Áo sơ mi nam dài tay, chất liệu oxford cao cấp.', 550000, 449000, 80, 'Xanh da trời', 10, 7, 'M, L, XL', TRUE, NOW(), NOW()),

    -- Sản phẩm của shop Chic Boutique (seller_id = 6)
    ('Đầm Hoa Vintage', 'Đầm maxi hoa nhí, phong cách vintage nhẹ nhàng.', 750000, 599000, 60, 'Vàng', 7, 6, 'S, M, L', TRUE, NOW(), NOW()),
    ('Chân Váy Chữ A', 'Chân váy công sở chữ A, tôn dáng, thanh lịch.', 450000, 349000, 90, 'Đen', 12, 6, 'S, M', TRUE, NOW(), NOW()),
    ('Túi Tote Da PU', 'Túi xách tote da PU cao cấp, không gian rộng rãi.', 600000, 499000, 50, 'Nâu', 8, 6, 'One Size', TRUE, NOW(), NOW());

-- =================================================================
-- BẢNG CARTS
-- Mỗi user sẽ có một cart
-- =================================================================
INSERT INTO carts (user_id)
VALUES (1), (2), (3), (4), (5), (6), (7);