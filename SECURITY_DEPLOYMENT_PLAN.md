# Kế hoạch triển khai bảo mật từng bước

## Bước 1: Chuẩn bị JWT Filter (ĐANG CẦN LÀM)
- [ ] Tạo JwtAuthenticationFilter
- [ ] Tạo JwtAuthenticationEntryPoint  
- [ ] Test JWT filter hoạt động

## Bước 2: Cập nhật Authentication Logic
- [ ] Sửa KhachHangService.getAccount() để sử dụng BCrypt
- [ ] Sửa NhanVienService.getAccount() để sử dụng BCrypt
- [ ] Hash tất cả mật khẩu hiện có trong DB

## Bước 3: Enable Security từng phần
- [ ] Enable authentication cho /api/admin/** trước
- [ ] Test và debug
- [ ] Enable cho /api/nhan-vien/**
- [ ] Enable cho /api/khach-hang/**
- [ ] Cuối cùng enable cho tất cả

## Bước 4: Thêm các tính năng bảo mật nâng cao
- [ ] Rate limiting
- [ ] Security audit logging
- [ ] OTP security improvements
- [ ] Input validation

## Trạng thái hiện tại:
✅ Web hoạt động bình thường
✅ PasswordEncoder đã có
✅ JWT secret key đã cải thiện
❌ Chưa có JWT filter
❌ Mật khẩu chưa được hash
❌ Authentication chưa được enable

## Mức độ bảo mật hiện tại: 3/10
- Cải thiện từ 2/10 nhờ JWT secret key mạnh hơn
- Vẫn cần làm nhiều việc để đạt 8-9/10