-- Script để hash các mật khẩu hiện có trong database
-- QUAN TRỌNG: Backup database trước khi chạy script này!

-- Tạm thời disable constraints
ALTER TABLE khach_hang NOCHECK CONSTRAINT ALL;
ALTER TABLE nhan_vien NOCHECK CONSTRAINT ALL;

-- Cập nhật mật khẩu khách hàng (ví dụ hash của "123456")
-- Trong thực tế, bạn cần chạy code Java để hash từng mật khẩu
UPDATE khach_hang 
SET mat_khau = '$2a$12$LQv3c1yqBw2/HZkgO/8Lqu2oQQ1QdHvFBp.1WJb2p0x4gvw1cLvGe' -- BCrypt hash của "123456"
WHERE mat_khau = '123456';

UPDATE khach_hang 
SET mat_khau = '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' -- BCrypt hash của "password"
WHERE mat_khau = 'password';

-- Tương tự cho nhân viên
UPDATE nhan_vien 
SET mat_khau = '$2a$12$LQv3c1yqBw2/HZkgO/8Lqu2oQQ1QdHvFBp.1WJb2p0x4gvw1cLvGe'
WHERE mat_khau = '123456';

UPDATE nhan_vien 
SET mat_khau = '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'
WHERE mat_khau = 'password';

-- Enable lại constraints
ALTER TABLE khach_hang CHECK CONSTRAINT ALL;
ALTER TABLE nhan_vien CHECK CONSTRAINT ALL;

-- Kiểm tra kết quả
SELECT COUNT(*) as total_khach_hang, 
       COUNT(CASE WHEN LEN(mat_khau) > 50 THEN 1 END) as hashed_passwords
FROM khach_hang;

SELECT COUNT(*) as total_nhan_vien,
       COUNT(CASE WHEN LEN(mat_khau) > 50 THEN 1 END) as hashed_passwords  
FROM nhan_vien;