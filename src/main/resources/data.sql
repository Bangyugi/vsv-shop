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
-- Thêm địa chỉ cho mỗi user (cấu trúc mới)
-- =================================================================
INSERT INTO addresses (id, full_name, phone_number, email, address, district, province, user_id, note) -- Đảm bảo đủ 10 cột
VALUES
    (1, 'Nguyễn Thị Na', '0972571254', 'nanguyen04@vsvshop.com', '123 Đường ABC',  'Quận 1', 'Thành phố Hồ Chí Minh', 3, 'Giao hàng giờ hành chính'),
    (2, 'Trần Văn Bằng', '0334236824', 'bangtran08@vsvshop.com', '456 Đường LMN', 'Quận 2', 'Thành phố Hồ Chí Minh', 1, NULL),
    (3, 'Nguyễn Trường Tùng', '0918572821', 'truongtung02@vsvshop.com', '789 Đường XYZ', 'Quận 3', 'Thành phố Hồ Chí Minh', 2, 'Gọi trước khi giao'),
    (4, 'Đinh Huy Hoàng', '0918830673', 'hoanghuy@gmail.com', '101 Đường GHI', 'Quận 4', 'Thành phố Hồ Chí Minh', 4, NULL);

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

-- ================================================================
-- TABLE: CATEGORIES (3 LEVELS)
-- ================================================================

-- Level 1
INSERT INTO categories (name, level)
VALUES
    ('Pants', 1),
    ('Shirts', 1),
    ('Shoes', 1),
    ('Accessories', 1);

-- Level 2
INSERT INTO categories (name, level, parent_category_id)
VALUES
    -- Pants
    ('Jeans', 2, 1),
    ('Trousers', 2, 1),
    ('Shorts', 2, 1),
    ('Joggers', 2, 1),
    ('Chinos', 2, 1),

    -- Shirts
    ('T-Shirts', 2, 2),
    ('Dress Shirts', 2, 2),
    ('Jackets', 2, 2),
    ('Polos', 2, 2),
    ('Sweaters', 2, 2),

    -- Shoes
    ('Sneakers', 2, 3),
    ('Formal Shoes', 2, 3),
    ('Loafers', 2, 3),
    ('Boots', 2, 3),
    ('Sandals', 2, 3),

    -- Accessories
    ('Hats', 2, 4),
    ('Bags', 2, 4),
    ('Belts', 2, 4),
    ('Sunglasses', 2, 4),
    ('Jewelry', 2, 4);

-- Level 3
INSERT INTO categories (name, level, parent_category_id)
VALUES
    -- Jeans
    ('Skinny Jeans', 3, 5),
    ('Ripped Jeans', 3, 5),
    ('Straight Jeans', 3, 5),
    ('High-Waist Jeans', 3, 5),
    ('Wide-Leg Jeans', 3, 5),

    -- Trousers
    ('Slim Fit Trousers', 3, 6),
    ('Straight Fit Trousers', 3, 6),
    ('High-Waist Trousers', 3, 6),
    ('Classic Fit Trousers', 3, 6),
    ('Plain Trousers', 3, 6),

    -- T-Shirts
    ('Crew Neck T-Shirt', 3, 11),
    ('V-Neck T-Shirt', 3, 11),
    ('Oversized T-Shirt', 3, 11),
    ('Half Sleeve T-Shirt', 3, 11),
    ('Graphic T-Shirt', 3, 11),

    -- Sneakers
    ('Running Shoes', 3, 16),
    ('White Sneakers', 3, 16),
    ('High-Top Sneakers', 3, 16),
    ('Canvas Sneakers', 3, 16),
    ('Training Shoes', 3, 16),

    -- Bags
    ('Tote Bag', 3, 21),
    ('Crossbody Bag', 3, 21),
    ('Handbag', 3, 21),
    ('Mini Bag', 3, 21),
    ('Backpack', 3, 21);

-- ================================================================
-- TABLE: PRODUCTS (10 SAMPLE ITEMS, USD PRICES)
-- ================================================================

INSERT INTO products (title, description, price, selling_price, discount_percent, num_ratings, category_id, seller_id, created_at, updated_at)
VALUES
    ('Men Skinny Jeans', 'Stretch denim skinny jeans with a modern fit.', 45.00, 36.90, 18, 250, 25, 3, NOW(), NOW()),
    ('Oversized Graphic T-Shirt', 'Oversized cotton t-shirt with printed logo.', 25.00, 19.50, 22, 300, 39, 3, NOW(), NOW()),
    ('White Basic Sneakers', 'Minimalist white sneakers with rubber soles.', 65.00, 52.00, 20, 400, 41, 3, NOW(), NOW()),
    ('Canvas Tote Bag', 'Durable canvas tote bag with stylish print.', 20.00, 15.00, 25, 120, 45, 3, NOW(), NOW()),
    ('Men Kaki Jacket', 'Stylish kaki jacket with zip closure.', 55.00, 46.50, 15, 180, 12, 3, NOW(), NOW()),
    ('Running Sports Shoes', 'Breathable running shoes for daily training.', 75.00, 63.00, 16, 270, 40, 3, NOW(), NOW()),
    ('Classic Polo Shirt', 'Cotton polo shirt with collar, business casual style.', 38.00, 31.50, 17, 140, 13, 3, NOW(), NOW()),
    ('Mini Crossbody Bag', 'Compact mini bag with adjustable strap.', 30.00, 24.90, 17, 90, 46, 3, NOW(), NOW()),
    ('Unisex Jogger Pants', 'Comfortable unisex jogger pants, elastic waistband.', 35.00, 28.00, 20, 230, 8, 3, NOW(), NOW()),
    ('Leather Formal Shoes', 'Premium leather formal shoes with anti-slip sole.', 90.00, 74.00, 18, 150, 16, 3, NOW(), NOW());
-- ================================================================
-- TABLE: PRODUCT_VARIANTS (COLOR, SIZE, QUANTITY)
-- ================================================================

INSERT INTO product_variants (product_id, sku, color, size, quantity)
VALUES
    -- Men Skinny Jeans
    (1, 'JE001-BLUE-30', 'Blue', '30', 50),
    (1, 'JE001-BLUE-31', 'Blue', '31', 50),
    (1, 'JE001-BLACK-30', 'Black', '30', 40),

    -- Oversized Graphic T-Shirt
    (2, 'TS001-WHITE-M', 'White', 'M', 60),
    (2, 'TS001-BLACK-L', 'Black', 'L', 60),
    (2, 'TS001-GREEN-L', 'Green', 'L', 60),

    -- White Basic Sneakers
    (3, 'SH001-WHITE-40', 'White', '40', 40),
    (3, 'SH001-WHITE-41', 'White', '41', 40),


    -- Canvas Tote Bag
    (4, 'BG001-BEIGE-ONESIZE', 'Beige', 'One Size', 100),

    -- Men Kaki Jacket
    (5, 'JK001-OLIVE-L', 'Olive', 'L', 30),
    (5, 'JK001-BLACK-M', 'Black', 'M', 30),

    -- Running Sports Shoes
    (6, 'RS001-NAVY-41', 'Navy', '41', 35),
    (6, 'RS001-BLACK-42', 'Black', '42', 35),

    -- Classic Polo Shirt
    (7, 'PO001-WHITE-M', 'White', 'M', 40),
    (7, 'PO001-GRAY-L', 'Gray', 'L', 40),

    -- Mini Crossbody Bag
    (8, 'BG002-PINK-ONESIZE', 'Pink', 'One Size', 60),

    -- Unisex Jogger Pants
    (9, 'JG001-BLACK-M', 'Black', 'M', 80),
    (9, 'JG001-GRAY-L', 'Gray', 'L', 60),

    -- Leather Formal Shoes
    (10, 'FS001-BROWN-42', 'Brown', '42', 25),
    (10, 'FS001-BLACK-41', 'Black', '41', 25);


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

-- =================================================================
-- BẢNG PRODUCT_IMAGES
-- Thêm ảnh cho các sản phẩm đã có
-- =================================================================

-- Thêm nhiều ảnh cho 'Men Skinny Jeans' (product_id = 1)
INSERT INTO product_images (product_id, image_url)
VALUES
    (1, 'https://cdn.boo.vn/media/catalog/product/1/_/1.2.21.2.23.001.124.01.60600034_1__4.webp'),
    (1, 'https://cdn.boo.vn/media/catalog/product/1/_/1.2.21.2.23.001.124.01.60600034_1__4.webp'),
    (1, 'https://vulcano.vn/media/catalog/product/cache/4867bdff41445f787f46fb72bd107e23/q/u/quan-jeans-nam-cao-cap-den-tron-3004b-base.jpg');

-- Thêm ảnh cho 'Oversized Graphic T-Shirt' (product_id = 2)
INSERT INTO product_images (product_id, image_url)
VALUES
    (2, 'https://cdn.vuahanghieu.com/unsafe/0x0/left/top/smart/filters:quality(90)/https://admin.vuahanghieu.com/upload/news/content/2023/09/ao-phong-burberry-logo-print-cotton-t-shirt-mau-trang-jpg-1694661542-14092023101902.jpg'),
(2, 'https://blankroom.co/cdn/shop/files/gucci-original-print-oversize-t-shirt-616036-XJCOQ-1082-a.jpg?v=1730710915&width=1445'),
(2, 'https://hourscollection.com/cdn/shop/files/CroppedT-shirt-ForestGreen-ProductPhoto.png?v=1739485867');

-- Thêm ảnh cho 'White Basic Sneakers' (product_id = 3)
INSERT INTO product_images (product_id, image_url)
VALUES
    (3, 'https://mensfashioner.com/media/pages/articles/ultimate-guide-to-mens-white-tennis-shoes-sneakers/bc106c4a52-1689327659/product-items-12-1408x.png');

-- Thêm ảnh cho 'Canvas Tote Bag' (product_id = 4)
INSERT INTO product_images (product_id, image_url)
VALUES
    (4, 'https://chus.vn/images/detailed/271/10799_04_F1.jpg');

-- Thêm ảnh cho 'Men Kaki Jacket' (product_id = 5)
INSERT INTO product_images (product_id, image_url)
VALUES
    (5, 'https://fttleather.com/uploads/1026/product/2023/10/28/ftt5-2-medium--1698509531.jpg?v=1.0.01'),
    (5, 'https://bizweb.dktcdn.net/100/348/395/products/o21-ak15-pp-web-thumbnail-11.jpg?v=1746160563597');

-- Thêm ảnh cho 'Running Sports Shoes' (product_id = 6)
INSERT INTO product_images (product_id, image_url)
VALUES
    (6, 'https://i5.walmartimages.com/seo/Boys-Girls-Sneakers-Kids-Shoes-Tennis-Running-Shoes-Athletic-Non-Slip-Sneakers-Navy-Blue-Big-Kid-Size-4_605a3297-de24-4219-a9e3-88405f411fa1.9d3863924edc6ffd434dbc73afcea5c0.jpeg?odnHeight=768&odnWidth=768&odnBg=FFFFFF'),
(6, 'https://m.media-amazon.com/images/I/71B-AakjjlL.jpg');

-- Thêm ảnh cho 'Classic Polo Shirt' (product_id = 7)
INSERT INTO product_images (product_id, image_url)
VALUES
    (7, 'https://freshcleantees.com/cdn/shop/files/GhostMannequinsPOLOWedgewood_737x980.jpg?v=1715102597');

-- Thêm ảnh cho 'Mini Crossbody Bag' (product_id = 8)
INSERT INTO product_images (product_id, image_url)
VALUES
    (8, 'https://www.converse.vn/media/catalog/product/0/8/0882-CON20540-A01-3.jpg');

-- Thêm ảnh cho 'Unisex Jogger Pants' (product_id = 9)
INSERT INTO product_images (product_id, image_url)
VALUES
    (9, 'https://jogger.com.vn/wp-content/uploads/z3623185822484_32a308bf3d54a85396e172f95e7ab87b.jpg');

-- Thêm ảnh cho 'Leather Formal Shoes' (product_id = 10)
INSERT INTO product_images (product_id, image_url)
VALUES
    (10, 'https://hitz.co.in/cdn/shop/files/1680-BROWN_604262e9-1998-4507-8ae2-41863abf099f.jpg?v=1755618701');