INSERT INTO roles ( name, description) VALUES
   ('ROLE_USER', 'US-01'),
   ('ROLE_MANAGER', 'US-02'),
   ( 'ROLE_ADMIN', 'US-03'),
   ( 'ROLE_SUPERADMIN', 'US-04');


INSERT INTO users (username,password,email,phone,first_name,last_name,birth_date,avatar,gender,enabled,created_at,updated_at)
VALUES
    ( 'bangvan', '$2a$10$Pd3nrs9SFkTAxbe2a4ebDuPWDR/rn4Sro4QSZbOCwP064j7H2X5Ai', 'bangtranvan08@gmail.com', '0334236824', 'Trần', 'Văn Bằng', '2004-08-22', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,   NOW(), NOW()),
    ( 'hoangduy', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'lehoangduy@gmail.com', '0341234567', 'Lê', 'Hoàng Duy', '1978-03-15', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,   NOW(), NOW()),
    ( 'thanhlam', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'nguyenthanhlam@gmail.com', '0352345678', 'Nguyễn', 'Thanh Lâm', '1980-07-22', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,  NOW(), NOW()),
    ('hoapham', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'phamthihoa@gmail.com', '0363456789', 'Phạm', 'Thị Hoa', '1982-11-05', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE,   NOW(), NOW()),
    ( 'thuytran', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'tranthithuy@gmail.com', '0374567890', 'Trần', 'Thị Thủy', '1985-02-28', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE,  NOW(), NOW()),
    ( 'minhthuan', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'dangminhthuan@gmail.com', '0385678901', 'Đặng', 'Minh Thuận', '1979-09-09', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,   NOW(), NOW()),
    ( 'quocanh', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'voquocanh@gmail.com', '0396789012', 'Võ', 'Quốc Anh', '1983-04-17', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,  NOW(), NOW()),
    ( 'lantran', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'tranthilan@gmail.com', '0347890123', 'Trần', 'Thị Lan', '1986-08-30', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE,  NOW(), NOW()),
    ( 'hoanguyen', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'nguyenthihoa@gmail.com', '0358901234', 'Nguyễn', 'Thị Hoa', '1981-12-12', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE, NOW(), NOW()),
    ( 'lehoang', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'lethihoang@gmail.com', '0369012345', 'Lê', 'Thị Hoàng', '1984-03-03', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'FEMALE', TRUE,   NOW(), NOW()),
    ('vanquang', '$2a$10$9hWPtTSDFsBhJB5fblv9tOuuVDUuw9e3c4BBHoJSwtP5/Q0eQ/Qw2', 'dangvanquang@gmail.com', '0370123456', 'Đặng', 'Văn Quang', '1977-06-21', 'https://cdn-icons-png.flaticon.com/512/3607/3607444.png', 'MALE', TRUE,   NOW(), NOW());


INSERT INTO user_role(user_id,role_id) VALUES
    (2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(11,1),
    (1,3),
    (1,4);