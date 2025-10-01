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


-- =================================================================
-- BẢNG USER_ROLE
-- Phân quyền cho từng user
-- =================================================================
INSERT INTO user_role(user_id, role_id)
VALUES
    -- Superadmin có mọi quyền
    (1, 1), (1, 2), (1, 3), (1, 4),
    -- Admin có các quyền quản lý
    (2, 1), (2, 2), (2, 3);



-- BẢNG CARTS
-- Mỗi user sẽ có một cart
-- =================================================================
INSERT INTO carts (user_id)
VALUES (1), (2);


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


