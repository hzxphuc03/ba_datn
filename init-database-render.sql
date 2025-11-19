-- Script khởi tạo dữ liệu cho PostgreSQL trên Render
-- Database: ba_datn_db
-- Host: dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com

-- Lưu ý: Script này chỉ tạo dữ liệu mẫu cơ bản
-- DataInitializer trong Spring Boot sẽ tự động tạo dữ liệu khi deploy

-- 1. Tạo Chức vụ
INSERT INTO chuc_vu (ten, ngay_tao, ngay_sua, trang_thai) 
VALUES 
    ('ADMIN', NOW(), NOW(), 1),
    ('NHAN_VIEN', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 2. Tạo Phương thức thanh toán
INSERT INTO phuong_thuc_thanh_toan (ten, ngay_tao, ngay_sua, trang_thai)
VALUES 
    ('Thanh toán khi nhận hàng (COD)', NOW(), NOW(), 1),
    ('Thanh toán online (VNPay)', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 3. Tạo Thể loại mẫu
INSERT INTO the_loai (ten, ngay_tao, ngay_sua, trang_thai)
VALUES 
    ('Giày thể thao', NOW(), NOW(), 1),
    ('Giày chạy bộ', NOW(), NOW(), 1),
    ('Giày da', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 4. Tạo Thương hiệu mẫu
INSERT INTO thuong_hieu (ten, ngay_tao, ngay_sua, trang_thai)
VALUES 
    ('Nike', NOW(), NOW(), 1),
    ('Adidas', NOW(), NOW(), 1),
    ('Puma', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 5. Tạo Màu sắc mẫu
INSERT INTO mau_sac (ten, ma_mau, ngay_tao, ngay_sua, trang_thai)
VALUES 
    ('Đen', '#000000', NOW(), NOW(), 1),
    ('Trắng', '#FFFFFF', NOW(), NOW(), 1),
    ('Đỏ', '#FF0000', NOW(), NOW(), 1),
    ('Xanh dương', '#0000FF', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 6. Tạo Kích cỡ mẫu
INSERT INTO kich_co (ten, ngay_tao, ngay_sua, trang_thai)
VALUES 
    ('36', NOW(), NOW(), 1),
    ('37', NOW(), NOW(), 1),
    ('38', NOW(), NOW(), 1),
    ('39', NOW(), NOW(), 1),
    ('40', NOW(), NOW(), 1),
    ('41', NOW(), NOW(), 1),
    ('42', NOW(), NOW(), 1),
    ('43', NOW(), NOW(), 1)
ON CONFLICT DO NOTHING;

-- 7. Tạo tài khoản Admin
-- Password: admin123 (BCrypt hash)
-- Lưu ý: Cần hash password bằng BCrypt trước khi insert
-- Có thể dùng DataInitializer trong Spring Boot để tạo admin user tự động

-- Kiểm tra dữ liệu đã tạo
SELECT 'Chức vụ' as table_name, COUNT(*) as count FROM chuc_vu
UNION ALL
SELECT 'Phương thức thanh toán', COUNT(*) FROM phuong_thuc_thanh_toan
UNION ALL
SELECT 'Thể loại', COUNT(*) FROM the_loai
UNION ALL
SELECT 'Thương hiệu', COUNT(*) FROM thuong_hieu
UNION ALL
SELECT 'Màu sắc', COUNT(*) FROM mau_sac
UNION ALL
SELECT 'Kích cỡ', COUNT(*) FROM kich_co;

