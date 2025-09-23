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
-- Mật khẩu cho tất cả user mẫu đều là: password123
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
        'FEMALE', TRUE, 'ACTIVE', NOW(), NOW());


-- =================================================================
-- BẢNG SELLERS
-- Chỉ chứa các thông tin mở rộng cho user là seller
-- `id` của seller phải khớp với `id` của user tương ứng
-- =================================================================
-- Giả sử id của 'seller_pending' là 5 và 'seller_active' là 6
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
        'Active Seller', '6677889900', 'Vietcombank', 'BFTVVNVX');


-- =================================================================
-- BẢNG USER_ROLE
-- Phân quyền cho từng user
-- user_id và role_id phải khớp với id trong bảng users và roles
-- =================================================================
INSERT INTO user_role(user_id, role_id)
VALUES
    -- Superadmin có mọi quyền (giả sử)
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    -- Admin có các quyền quản lý
    (2, 1),
    (2, 2),
    (2, 3),
    -- User thông thường
    (3, 1),
    (4, 1),
    -- Sellers (có cả vai trò USER và SELLER)
    (5, 1),
    (5, 2),
    (6, 1),
    (6, 2);
