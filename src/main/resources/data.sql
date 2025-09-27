-- =================================================================
-- BẢNG ROLES
-- =================================================================
-- role_id sẽ tự động tăng từ 1
INSERT INTO roles (name, description)
VALUES ('ROLE_USER', 'Vai trò người dùng cơ bản'),
       ('ROLE_SELLER', 'Vai trò người bán hàng'),
       ('ROLE_ADMIN', 'Vai trò quản trị viên'),
       ('ROLE_SUPERADMIN', 'Vai trò quản trị viên cấp cao');

-- =================================================================
-- BẢNG USERS
-- Mật khẩu cho tất cả user mẫu đều là: password123 (đã được mã hóa)
-- =================================================================
-- user_id sẽ tự động tăng từ 1
-- Người dùng Admin/Superadmin
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled,
                   account_status, created_at, updated_at)
VALUES ('superadmin', '$2a$10$.KiBYco9zfWcyy5JoA7IhurV1.saoZJtLOOHT5ETZevESLQHmRMZm', 'superadmin@vsvshop.com',
        '0111111111', 'Super', 'Admin', '1990-01-01', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png',
        'OTHER', TRUE, 'ACTIVE', NOW(), NOW()),
       ('admin', '$2a$10$r5/k18lS0z2WqkZbLu34XeX0sTr64W9Exn.Rio9WozMuJFjxSUl.e', 'admin@vsvshop.com', '0222222222',
        'Main', 'Admin', '1992-05-10', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,
        'ACTIVE', NOW(), NOW());

-- Người dùng thông thường (chỉ có vai trò USER)
INSERT INTO users (username, password, email, phone, first_name, last_name, birth_date, avatar, gender, enabled,
                   account_status, created_at, updated_at)
VALUES ('johndoe', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'johndoe@email.com', '0333333333',
        'John', 'Doe', '1995-08-22', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE',
        NOW(), NOW()),
       ('janesmith', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'janesmith@email.com',
        '0444444444', 'Jane', 'Smith', '1998-11-30', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png',
        'FEMALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('seller_pending', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'seller.pending@shop.com',
        '0555555555', 'Pending', 'Seller', '1988-03-15', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png',
        'MALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('seller_active', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'seller.active@shop.com',
        '0666666666', 'Active', 'Seller', '1991-07-20', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png',
        'FEMALE', TRUE, 'ACTIVE', NOW(), NOW()),
       ('tranvanbang', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'bang@email.com', '0777777777',
        'Bang', 'Tran', '2003-08-02', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE, 'ACTIVE',
        NOW(), NOW()),
       ('seller_new', '$2a$10$RiSxd2.3IZW1A.IqRWBiNexVYpbW3rPd9KWgxS1j5phGSn3Tc3U8O', 'seller.new@shop.com',
        '0888888888', 'New', 'Seller', '1999-01-01', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png',
        'MALE', TRUE, 'ACTIVE', NOW(), NOW());


-- =================================================================
-- BẢNG SELLERS
-- Chỉ chứa các thông tin mở rộng cho user là seller
-- `id` của seller phải khớp với `id` của user tương ứng
-- =================================================================
-- Giả sử id của 'seller_pending' là 5, 'seller_active' là 6, và 'seller_new' là 8
INSERT INTO sellers (id, is_email_verified, gstin,
                     business_name, business_email, business_mobile, business_address, logo, banner,
                     account_holder_name, account_number, bank_name, ifsc_code)
VALUES (5, FALSE,  'GSTIN12345XYZ',
        'Pending Electronics Store', 'contact@pendingstore.com', '0555555555', '123 Pending St, Hanoi', 'logo_url',
        'banner_url',
        'Pending Seller', '1122334455', 'Techcombank', 'TCBKVNVX'),
       (6, TRUE, 'GSTIN67890ABC',
        'Active Fashion Hub', 'support@activehub.com', '0666666666', '456 Active Ave, HCMC', 'logo_url_2',
        'banner_url_2',
        'Active Seller', '6677889900', 'Vietcombank', 'BFTVVNVX'),
       (8, TRUE, 'GSTINNEWSELLER',
        'New Seller Gadgets', 'contact@newgadgets.com', '0888888888', '789 New St, Da Nang', 'logo_url_3',
        'banner_url_3',
        'New Seller', '1234567890', 'ACB', 'ASCBVNVX');


-- =================================================================
-- BẢNG USER_ROLE
-- Phân quyền cho từng user
-- user_id và role_id phải khớp với id trong bảng users và roles
-- =================================================================
INSERT INTO user_role(user_id, role_id)
VALUES
    -- Superadmin có mọi quyền
    (1, 1), (1, 2), (1, 3), (1, 4),
    -- Admin có các quyền quản lý
    (2, 1), (2, 2), (2, 3),
    -- User thông thường
    (3, 1), (4, 1), (7, 1),
    -- Sellers (có cả vai trò USER và SELLER)
    (5, 1), (5, 2),
    (6, 1), (6, 2),
    (8, 1), (8, 2);


-- =================================================================
-- BẢNG CATEGORIES
-- =================================================================
INSERT INTO categories (name, level)
VALUES ('Electronics', 1),
       ('Fashion', 1),
       ('Home Appliances', 1);

INSERT INTO categories (name, level, parent_category_id)
VALUES ('Smartphones', 2, 1),
       ('Laptops', 2, 1),
       ('T-Shirts', 2, 2),
       ('Jeans', 2, 2);

-- =================================================================
-- BẢNG PRODUCTS
-- =================================================================
INSERT INTO products (title, description, price, selling_price, quantity, color, category_id, seller_id, sizes, in_stock, created_at, updated_at)
VALUES ('iPhone 15 Pro Max', 'The latest iPhone with A17 Bionic chip.', 29990000, 27990000, 50, 'Titanium Blue', 4, 6, '256GB, 512GB', TRUE, NOW(), NOW()),
       ('MacBook Air M2', 'Apple M2 chip, 13.6-inch Liquid Retina display.', 24990000, 23990000, 30, 'Space Gray', 5, 6, '8GB RAM, 256GB SSD', TRUE, NOW(), NOW()),
       ('Basic Cotton T-Shirt', 'A comfortable and stylish 100% cotton t-shirt.', 250000, 199000, 100, 'White', 6, 8, 'S, M, L, XL', TRUE, NOW(), NOW()),
       ('Slim-Fit Denim Jeans', 'Modern slim-fit jeans for everyday wear.', 750000, 599000, 80, 'Classic Blue', 7, 8, '30, 32, 34', TRUE, NOW(), NOW()),
       ('Samsung Galaxy S23 Ultra', 'Experience the new standard of premium smartphones.', 31990000, 28990000, 40, 'Phantom Black', 4, 5, '256GB, 512GB', TRUE, NOW(), NOW()),
       ('Dell XPS 15', 'Powerful laptop with a stunning InfinityEdge display.', 45000000, 42500000, 25, 'Silver', 5, 6, '16GB RAM, 512GB SSD', TRUE, NOW(), NOW()),
       ('Graphic Print T-Shirt', 'Express yourself with this cool graphic print t-shirt.', 350000, 299000, 120, 'Black', 6, 8, 'S, M, L, XL', TRUE, NOW(), NOW()),
       ('Cargo Pants', 'Utility-style cargo pants with multiple pockets.', 900000, 750000, 60, 'Olive Green', 7, 8, '30, 32, 34', TRUE, NOW(), NOW()),
       ('Google Pixel 8', 'The latest Google phone with an amazing camera.', 18990000, 17990000, 35, 'Obsidian', 4, 5, '128GB', TRUE, NOW(), NOW()),
       ('Robot Vacuum Cleaner', 'Smart vacuum cleaner with mopping function.', 8000000, 6990000, 15, 'White', 3, 5, 'N/A', TRUE, NOW(), NOW());

-- =================================================================
-- BẢNG CARTS
-- Mỗi user sẽ có một cart
-- =================================================================
INSERT INTO carts (user_id)
VALUES (1), (2), (3), (4), (5), (6), (7), (8);