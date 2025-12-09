# 📚 TÀI LIỆU API - HỆ THỐNG BÁN HÀNG

## 🔗 Base URL

```
Production: https://your-domain.com
Development: http://localhost:8080
```

## 🔐 Xác thực (Authentication)

Hệ thống sử dụng **JWT (JSON Web Token)** để xác thực. Sau khi đăng nhập thành công, bạn sẽ nhận được:
- `accessToken`: Token để gửi kèm trong header mỗi request
- `refreshToken`: Token để làm mới accessToken khi hết hạn

### Cách sử dụng JWT Token

Thêm token vào header của mỗi request:

```
Authorization: Bearer {accessToken}
```

**Ví dụ:**
```javascript
headers: {
  'Authorization': 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...',
  'Content-Type': 'application/json'
}
```

### Thời gian hết hạn
- **Access Token**: 24 giờ
- **Refresh Token**: 7 ngày

---

## 📋 Mục lục

1. [Authentication](#1-authentication)
2. [Khách Hàng](#2-khách-hàng)
3. [Sản Phẩm](#3-sản-phẩm)
4. [Giỏ Hàng](#4-giỏ-hàng)
5. [Hóa Đơn](#5-hóa-đơn)
6. [Voucher](#6-voucher)
7. [Địa Chỉ](#7-địa-chỉ)
8. [Đánh Giá Sản Phẩm](#8-đánh-giá-sản-phẩm)
9. [Thống Kê](#9-thống-kê)
10. [Các API khác](#10-các-api-khác)

---

## 1. Authentication

### 1.1. Đăng nhập Khách hàng

**Endpoint:** `POST /api/auth/login/khach-hang`

**Không cần JWT**

**Request Body:**
```json
{
  "username": "string",
  "pass": "string"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "hoTen": "Nguyễn Văn A",
    "email": "user@example.com",
    "soDienThoai": "0123456789",
    "taiKhoan": "username",
    "role": "KHACH_HANG"
  }
}
```

**Response 401 (Sai thông tin đăng nhập):**
```json
{
  "message": "Tên đăng nhập hoặc mật khẩu không đúng"
}
```

---

### 1.2. Đăng nhập Nhân viên

**Endpoint:** `POST /api/auth/login/nhan-vien`

**Không cần JWT**

**Request Body:**
```json
{
  "username": "string",
  "pass": "string"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "hoTen": "Nguyễn Văn B",
    "email": "staff@example.com",
    "soDienThoai": "0987654321",
    "taiKhoan": "username",
    "role": "NHAN_VIEN",
    "chucVu": {
      "id": 1,
      "ten": "Nhân viên bán hàng"
    }
  }
}
```

---

### 1.3. Refresh Token

**Endpoint:** `POST /api/auth/refresh-token`

**Không cần JWT**

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 1.4. Đăng xuất

**Endpoint:** `POST /api/auth/logout`

**Cần JWT**

**Response 200:**
```json
{
  "message": "Đăng xuất thành công"
}
```

---

### 1.5. Lấy thông tin user hiện tại

**Endpoint:** `GET /api/auth/me`

**Cần JWT**

**Response 200:**
```json
{
  "message": "User authenticated"
}
```

---

## 2. Khách Hàng

### 2.1. Lấy danh sách khách hàng

**Endpoint:** `GET /api/khach_hang`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Query Parameters:**
- `page` (optional): Số trang (mặc định: 1)
- `pageSize` (optional): Số lượng mỗi trang (mặc định: 10)
- `keyWord` (optional): Từ khóa tìm kiếm
- `status` (optional): Trạng thái (0: Không hoạt động, 1: Hoạt động)

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "hoTen": "Nguyễn Văn A",
      "email": "user@example.com",
      "soDienThoai": "0123456789",
      "trangThai": 1,
      "ngayTao": "2024-01-01T00:00:00"
    }
  ],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

---

### 2.2. Lấy thông tin khách hàng theo ID

**Endpoint:** `GET /api/khach_hang/{id}`

**Không cần JWT (tạm thời public để test)**

**Response 200:**
```json
{
  "id": 1,
  "hoTen": "Nguyễn Văn A",
  "email": "user@example.com",
  "soDienThoai": "0123456789",
  "ngaySinh": "1990-01-01",
  "gioiTinh": 1,
  "trangThai": 1,
  "hinhAnh": "https://example.com/avatar.jpg"
}
```

---

### 2.3. Thêm khách hàng mới

**Endpoint:** `POST /api/khach_hang/add`

**Không cần JWT (tạm thời)**

**Request Body:**
```json
{
  "hoTen": "Nguyễn Văn A",
  "email": "user@example.com",
  "soDienThoai": "0123456789",
  "matKhau": "password123",
  "taiKhoan": "username",
  "ngaySinh": "1990-01-01",
  "gioiTinh": 1
}
```

**Response 200:**
```json
{
  "id": 1,
  "message": "Thêm khách hàng thành công"
}
```

---

### 2.4. Cập nhật thông tin khách hàng

**Endpoint:** `PUT /api/khach_hang/update-khach-hang`

**Cần JWT**

**Request Body:**
```json
{
  "id": 1,
  "hoTen": "Nguyễn Văn A Updated",
  "ngaySinh": "1990-01-01",
  "gioiTinh": 1
}
```

**Response 200:**
```json
{
  "message": "Cập nhật thành công"
}
```

---

### 2.5. Gửi mã OTP qua email

**Endpoint:** `POST /api/khach_hang/gui-ma-otp`

**Không cần JWT**

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response 200:**
```json
{
  "otp": "123456",
  "message": "OTP đã được gửi thành công."
}
```

**Lưu ý:** OTP được trả về trong response để test. Trong production, OTP chỉ được gửi qua email.

---

### 2.6. Cập nhật số điện thoại

**Endpoint:** `PUT /api/khach_hang/cap-nhat-so-dien-thoai`

**Cần JWT**

**Request Body:**
```json
{
  "idKhachHang": 1,
  "soDienThoai": "0987654321",
  "otp": "123456"
}
```

**Response 200:**
```json
{
  "message": "Cập nhật số điện thoại thành công"
}
```

---

### 2.7. Cập nhật email

**Endpoint:** `PUT /api/khach_hang/cap-nhat-email`

**Cần JWT**

**Request Body:**
```json
{
  "idKhachHang": 1,
  "email": "newemail@example.com",
  "otp": "123456"
}
```

**Response 200:**
```json
{
  "message": "Cập nhật email thành công"
}
```

---

## 3. Sản Phẩm

### 3.1. Lấy danh sách sản phẩm (có filter)

**Endpoint:** `GET /api/san_pham`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `keyWord` (optional): Từ khóa tìm kiếm
- `status` (optional): Trạng thái (0: Không hoạt động, 1: Hoạt động)
- `startDate` (optional): Ngày bắt đầu (format: yyyy-MM-ddTHH:mm:ss)
- `endDate` (optional): Ngày kết thúc
- `giamGia` (optional): ID giảm giá
- `thuongHieu` (optional): ID thương hiệu
- `theLoai` (optional): ID thể loại
- `loaiCo` (optional): ID loại cổ
- `vatLieu` (optional): ID vật liệu
- `loaiDe` (optional): ID loại đế
- `sort` (optional): Sắp xếp (0: Mặc định, 1: Giá tăng dần, 2: Giá giảm dần)

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "tenSanPham": "Áo sơ mi nam",
      "moTa": "Mô tả sản phẩm",
      "gia": 500000,
      "giaGiam": 400000,
      "hinhAnh": "https://example.com/image.jpg",
      "thuongHieu": {
        "id": 1,
        "ten": "Thương hiệu A"
      },
      "theLoai": {
        "id": 1,
        "ten": "Áo sơ mi"
      }
    }
  ],
  "total": 50,
  "page": 1,
  "pageSize": 10
}
```

---

### 3.2. Lấy danh sách sản phẩm (filter nâng cao)

**Endpoint:** `GET /api/san_pham/get-by-all`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang (mặc định: 1)
- `pageSize` (optional): Số lượng mỗi trang (mặc định: 10)
- `keyWord` (optional): Từ khóa tìm kiếm
- `giamGia` (optional): ID giảm giá
- `giaCaoNhat` (optional): Giá cao nhất
- `giaThapNhat` (optional): Giá thấp nhất
- `thuongHieu` (optional): ID thương hiệu
- `theLoai` (optional): ID thể loại
- `loaiCo` (optional): ID loại cổ
- `vatLieu` (optional): ID vật liệu
- `loaiDe` (optional): ID loại đế
- `sort` (optional): Sắp xếp (0: Mặc định, 1: Giá tăng, 2: Giá giảm)

**Response 200:** Tương tự endpoint trên

---

### 3.3. Lấy thông tin sản phẩm theo ID

**Endpoint:** `GET /api/san_pham/get-by-id?idSanPham={id}`

**Không cần JWT (GET public)**

**Response 200:**
```json
{
  "id": 1,
  "tenSanPham": "Áo sơ mi nam",
  "moTa": "Mô tả chi tiết",
  "gia": 500000,
  "giaGiam": 400000,
  "hinhAnh": "https://example.com/image.jpg",
  "thuongHieu": {
    "id": 1,
    "ten": "Thương hiệu A"
  },
  "theLoai": {
    "id": 1,
    "ten": "Áo sơ mi"
  },
  "sanPhamChiTiet": [
    {
      "id": 1,
      "kichCo": "M",
      "mauSac": "Trắng",
      "soLuong": 10
    }
  ]
}
```

---

### 3.4. Tìm kiếm sản phẩm

**Endpoint:** `GET /api/san_pham/tim-kiem/{tenSanPham}`

**Không cần JWT (GET public)**

**Response 200:**
```json
[
  {
    "id": 1,
    "tenSanPham": "Áo sơ mi nam",
    "gia": 500000,
    "giaGiam": 400000
  }
]
```

---

### 3.5. Top 5 sản phẩm bán chạy

**Endpoint:** `GET /api/san_pham/top5-ban-chay`

**Không cần JWT (GET public)**

**Response 200:**
```json
[
  {
    "id": 1,
    "tenSanPham": "Áo sơ mi nam",
    "gia": 500000,
    "giaGiam": 400000
  }
]
```

---

### 3.6. Top 5 sản phẩm giảm giá

**Endpoint:** `GET /api/san_pham/top5-giam-gia`

**Không cần JWT (GET public)**

---

### 3.7. Top 5 sản phẩm mới

**Endpoint:** `GET /api/san_pham/top5-moi`

**Không cần JWT (GET public)**

---

### 3.8. Top 3 sản phẩm giảm giá

**Endpoint:** `GET /api/san_pham/top3-giam-gia`

**Không cần JWT (GET public)**

---

### 3.9. Sản phẩm giảm giá sâu nhất

**Endpoint:** `GET /api/san_pham/giam-gia-sau-nhat`

**Không cần JWT (GET public)**

---

### 3.10. 12 sản phẩm cập nhật gần nhất

**Endpoint:** `GET /api/san_pham/12-san-pham-update-gan-nhat`

**Không cần JWT (GET public)**

---

### 3.11. Top 5 sản phẩm theo thể loại

**Endpoint:** `GET /api/san_pham/top5-san-pham-theo-the-loai?theLoaiId={id}`

**Không cần JWT (GET public)**

---

### 3.12. Lấy danh sách sản phẩm có đánh giá

**Endpoint:** `GET /api/san_pham/co-danh-gia`

**Không cần JWT (GET public)**

**Response 200:**
```json
[
  {
    "idSanPham": 1,
    "tenSanPham": "Áo sơ mi nam",
    "diemTrungBinh": 4.5,
    "soLuongDanhGia": 10
  }
]
```

---

### 3.13. Lấy danh sách đánh giá của sản phẩm

**Endpoint:** `GET /api/san_pham/san-pham/{idSanPham}/danh-gia`

**Không cần JWT (GET public)**

**Response 200:**
```json
[
  {
    "id": 1,
    "khachHang": {
      "id": 1,
      "hoTen": "Nguyễn Văn A"
    },
    "diem": 5,
    "noiDung": "Sản phẩm rất tốt",
    "ngayTao": "2024-01-01T00:00:00"
  }
]
```

---

### 3.14. Lấy sản phẩm chưa đánh giá của khách hàng

**Endpoint:** `GET /api/san_pham/san-pham-chua-danh-gia/{idKhachHang}`

**Không cần JWT (GET public)**

**Response 200:**
```json
[
  {
    "id": 1,
    "tenSanPham": "Áo sơ mi nam",
    "gia": 500000
  }
]
```

---

### 3.15. Thêm sản phẩm mới

**Endpoint:** `POST /api/san_pham/add`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "tenSanPham": "Áo sơ mi nam",
  "moTa": "Mô tả sản phẩm",
  "gia": 500000,
  "idThuongHieu": 1,
  "idTheLoai": 1,
  "idGiamGia": null,
  "trangThai": 1
}
```

---

### 3.16. Cập nhật sản phẩm

**Endpoint:** `PUT /api/san_pham/update`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:** Tương tự thêm sản phẩm, có thêm `id`

---

### 3.17. Cập nhật trạng thái sản phẩm

**Endpoint:** `PUT /api/san_pham/update-trang-thai/{id}?status={status}`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Query Parameters:**
- `status`: 0 (Không hoạt động) hoặc 1 (Hoạt động)

---

## 4. Giỏ Hàng

### 4.1. Lấy giỏ hàng của khách hàng

**Endpoint:** `GET /api/gio_hang/{idKhachHang}`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Response 200:**
```json
{
  "id": 1,
  "idKhachHang": 1,
  "tongTien": 1000000,
  "chiTiet": [
    {
      "id": 1,
      "sanPhamChiTiet": {
        "id": 1,
        "sanPham": {
          "id": 1,
          "tenSanPham": "Áo sơ mi nam"
        },
        "kichCo": "M",
        "mauSac": "Trắng"
      },
      "soLuong": 2,
      "gia": 500000,
      "thanhTien": 1000000
    }
  ]
}
```

---

### 4.2. Lấy số lượng sản phẩm trong giỏ hàng

**Endpoint:** `GET /api/gio_hang/get_so_luong/{idKhachHang}`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Response 200:**
```json
{
  "soLuong": 5
}
```

---

### 4.3. Cập nhật giỏ hàng

**Endpoint:** `PUT /api/gio_hang/update`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idKhachHang": 1,
  "chiTiet": [
    {
      "idSanPhamChiTiet": 1,
      "soLuong": 2
    }
  ]
}
```

---

### 4.4. Thêm địa chỉ vào giỏ hàng

**Endpoint:** `PUT /api/gio_hang/add-dia-chi`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idGioHang": 1,
  "idDiaChi": 1
}
```

---

### 4.5. Thêm voucher vào giỏ hàng

**Endpoint:** `PUT /api/gio_hang/add-voucher`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idGioHang": 1,
  "idVoucher": 1
}
```

---

### 4.6. Thêm phương thức thanh toán vào giỏ hàng

**Endpoint:** `PUT /api/gio_hang/add-phuong-thuc-thanh-toan`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idGioHang": 1,
  "idPhuongThucThanhToan": 1
}
```

---

### 4.7. Thanh toán khi nhận hàng

**Endpoint:** `PUT /api/gio_hang/thanh-toan-khi-giao-hang`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idGioHang": 1,
  "ghiChu": "Giao hàng vào buổi sáng"
}
```

**Response 200:**
```json
{
  "idHoaDon": 1,
  "message": "Đặt hàng thành công"
}
```

---

### 4.8. Thanh toán online

**Endpoint:** `PUT /api/gio_hang/thanh-toan-online`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idGioHang": 1
}
```

**Response 200:**
```json
{
  "paymentUrl": "https://vnpay.vn/payment?...",
  "idHoaDon": 1
}
```

---

### 4.9. Xác nhận thanh toán thành công

**Endpoint:** `PUT /api/gio_hang/thanh-toan-thanh-cong`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "paymentResponse": "..."
}
```

---

## 5. Hóa Đơn

### 5.1. Lấy danh sách hóa đơn

**Endpoint:** `GET /api/hoa_don`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `keyWord` (optional): Từ khóa tìm kiếm (mã hóa đơn)
- `startDate` (optional): Ngày bắt đầu
- `endDate` (optional): Ngày kết thúc
- `idKhachHang` (optional): ID khách hàng
- `idNhanVien` (optional): ID nhân viên
- `status` (optional): Trạng thái

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "maHoaDon": "HD001",
      "idKhachHang": 1,
      "tongTien": 1000000,
      "trangThai": 1,
      "ngayTao": "2024-01-01T00:00:00"
    }
  ],
  "total": 50,
  "page": 1,
  "pageSize": 10
}
```

---

### 5.2. Lấy tất cả hóa đơn (không filter trạng thái)

**Endpoint:** `GET /api/hoa_don/get-all`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Query Parameters:** Tương tự endpoint trên

---

### 5.3. Lấy hóa đơn theo ID

**Endpoint:** `GET /api/hoa_don/get-hoa-don-by-id-hoa-don/{idHoaDon}`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Response 200:**
```json
{
  "id": 1,
  "maHoaDon": "HD001",
  "idKhachHang": 1,
  "tongTien": 1000000,
  "tienGiam": 100000,
  "phiShip": 30000,
  "thanhTien": 930000,
  "trangThai": 1,
  "chiTiet": [
    {
      "id": 1,
      "sanPhamChiTiet": {
        "id": 1,
        "sanPham": {
          "tenSanPham": "Áo sơ mi nam"
        }
      },
      "soLuong": 2,
      "gia": 500000,
      "thanhTien": 1000000
    }
  ]
}
```

---

### 5.4. Lấy hóa đơn theo khách hàng

**Endpoint:** `GET /api/hoa_don/get-hoa-don-by-khach-hang`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `idKhachHang` (required): ID khách hàng

---

### 5.5. Lấy hóa đơn hoàn thành theo khách hàng

**Endpoint:** `GET /api/hoa_don/get-hoa-don-hoan-thanh-by-khach-hang`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Query Parameters:** Tương tự endpoint trên

---

### 5.6. Lấy chi tiết hóa đơn cho view khách hàng

**Endpoint:** `GET /api/hoa_don/get-hoa-don-chi-tiet-cho-view-khach-hang/{idHoaDon}`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

---

### 5.7. Thêm hóa đơn mới

**Endpoint:** `POST /api/hoa_don/add`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Request Body:**
```json
{
  "idKhachHang": 1,
  "idNhanVien": 1,
  "idPhuongThucThanhToan": 1,
  "idDiaChi": 1,
  "chiTiet": [
    {
      "idSanPhamChiTiet": 1,
      "soLuong": 2,
      "gia": 500000
    }
  ]
}
```

---

### 5.8. Thêm hóa đơn nhanh

**Endpoint:** `POST /api/hoa_don/add-nhanh?idNhanVien={id}`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

---

### 5.9. Cập nhật hóa đơn

**Endpoint:** `PUT /api/hoa_don/update`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Request Body:** Tương tự thêm hóa đơn, có thêm `id`

---

### 5.10. Thêm khách hàng vào hóa đơn

**Endpoint:** `PUT /api/hoa_don/add-khach-hang`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "idKhachHang": 1
}
```

---

### 5.11. Thêm phương thức thanh toán vào hóa đơn

**Endpoint:** `PUT /api/hoa_don/add-phuong-thuc-thanh-toan`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "idPhuongThucThanhToan": 1
}
```

---

### 5.12. Thêm địa chỉ vào hóa đơn

**Endpoint:** `PUT /api/hoa_don/add-dia-chi`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "idDiaChi": 1
}
```

---

### 5.13. Cập nhật trạng thái giao hàng

**Endpoint:** `PUT /api/hoa_don/update-trang-thai-giao-hang`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "isGiaoHang": true
}
```

---

### 5.14. Thanh toán tại quầy

**Endpoint:** `PUT /api/hoa_don/thanh-toan-tai-quay`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "tienKhachTra": 1000000,
  "tienThua": 70000
}
```

---

### 5.15. Xóa voucher khỏi hóa đơn

**Endpoint:** `POST /api/hoa_don/delete-voucher`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "idVoucher": 1
}
```

---

### 5.16. Thêm voucher vào hóa đơn

**Endpoint:** `PUT /api/hoa_don/add-voucher`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "idVoucher": 1
}
```

---

### 5.17. Chuyển bước tiếp theo (next step)

**Endpoint:** `PUT /api/hoa_don/next-step`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idHoaDon": 1
}
```

---

### 5.18. Cập nhật trạng thái hóa đơn

**Endpoint:** `PUT /api/hoa_don/update-trang-thai/{id}?status={status}`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Query Parameters:**
- `status`: Trạng thái mới

---

### 5.19. Hủy hóa đơn

**Endpoint:** `POST /api/hoa_don/huy-hoa-don`

**Cần JWT (ADMIN, NHAN_VIEN, hoặc KHACH_HANG)**

**Request Body:**
```json
{
  "idHoaDon": 1,
  "lyDo": "Khách hàng yêu cầu hủy"
}
```

---

### 5.20. Cập nhật trạng thái thanh toán online

**Endpoint:** `PUT /api/hoa_don/update-trang-thai-thanh_toan-online/{idHoaDon}`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

---

## 6. Voucher

### 6.1. Lấy danh sách voucher

**Endpoint:** `GET /api/voucher`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `keyWord` (optional): Từ khóa tìm kiếm
- `status` (optional): Trạng thái
- `theLoai` (optional): Loại voucher (1: Hóa đơn, 2: Ship)
- `startDate` (optional): Ngày bắt đầu
- `endDate` (optional): Ngày kết thúc
- `phanTramGiam` (optional): Phần trăm giảm

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "tenVoucher": "Giảm 10%",
      "maVoucher": "GIAM10",
      "phanTramGiam": 10,
      "giaTriGiamToiThieu": 100000,
      "giaTriGiamToiDa": 50000,
      "ngayBatDau": "2024-01-01T00:00:00",
      "ngayKetThuc": "2024-12-31T23:59:59",
      "trangThai": 1,
      "theLoai": 1
    }
  ],
  "total": 20,
  "page": 1,
  "pageSize": 10
}
```

---

### 6.2. Lấy voucher theo ID

**Endpoint:** `GET /api/voucher/get-by-id/{idVoucher}`

**Không cần JWT (GET public)**

---

### 6.3. Lấy voucher đang hoạt động

**Endpoint:** `GET /api/voucher/get-voucher-active`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `giaTriGiamToiThieu` (optional): Giá trị giảm tối thiểu
- `theLoai` (optional): Loại voucher

---

### 6.4. Lấy voucher hóa đơn

**Endpoint:** `GET /api/voucher/Voucher-Hoa-Don`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang

---

### 6.5. Lấy voucher ship

**Endpoint:** `GET /api/voucher/Voucher-Ship`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang

---

### 6.6. Lấy phiếu giảm giá sự kiện

**Endpoint:** `GET /api/voucher/get-phieu-giam-gia-su-kien`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang

---

### 6.7. Thêm voucher mới

**Endpoint:** `POST /api/voucher/add`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "tenVoucher": "Giảm 10%",
  "maVoucher": "GIAM10",
  "phanTramGiam": 10,
  "giaTriGiamToiThieu": 100000,
  "giaTriGiamToiDa": 50000,
  "ngayBatDau": "2024-01-01T00:00:00",
  "ngayKetThuc": "2024-12-31T23:59:59",
  "soLuong": 100,
  "theLoai": 1,
  "trangThai": 1
}
```

---

### 6.8. Thêm voucher sự kiện

**Endpoint:** `POST /api/voucher/add-voucher-su-kien`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idSuKien": 1,
  "vouchers": [
    {
      "tenVoucher": "Giảm 10%",
      "maVoucher": "GIAM10",
      "phanTramGiam": 10
    }
  ]
}
```

---

### 6.9. Cập nhật voucher

**Endpoint:** `PUT /api/voucher/update`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Request Body:** Tương tự thêm voucher, có thêm `id`

---

### 6.10. Cập nhật trạng thái voucher

**Endpoint:** `PUT /api/voucher/update-trang-thai/{id}?status={status}`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

---

### 6.11. Cập nhật trạng thái voucher tự động

**Endpoint:** `PUT /api/voucher/update-trangthai-auto`

**Không cần JWT (public - scheduled job)**

---

## 7. Địa Chỉ

### 7.1. Lấy danh sách địa chỉ

**Endpoint:** `GET /api/dia_chi`

**Cần JWT**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `keyWord` (optional): ID khách hàng
- `status` (optional): Trạng thái

---

### 7.2. Lấy địa chỉ theo khách hàng

**Endpoint:** `GET /api/dia_chi/get-by-idkh`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `idKhachHang` (required): ID khách hàng

**Response 200:**
```json
{
  "data": [
    {
      "id": 1,
      "idKhachHang": 1,
      "tenNguoiNhan": "Nguyễn Văn A",
      "soDienThoai": "0123456789",
      "diaChi": "123 Đường ABC",
      "phuongXa": "Phường 1",
      "quanHuyen": "Quận 1",
      "tinhThanh": "TP.HCM",
      "macDinh": true,
      "trangThai": 1
    }
  ],
  "total": 5,
  "page": 1,
  "pageSize": 10
}
```

---

### 7.3. Đặt địa chỉ mặc định

**Endpoint:** `GET /api/dia_chi/set-mac-dinh/{idDiaChi}`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

---

### 7.4. Thêm địa chỉ mới

**Endpoint:** `POST /api/dia_chi/add`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idKhachHang": 1,
  "tenNguoiNhan": "Nguyễn Văn A",
  "soDienThoai": "0123456789",
  "diaChi": "123 Đường ABC",
  "phuongXa": "Phường 1",
  "quanHuyen": "Quận 1",
  "tinhThanh": "TP.HCM",
  "macDinh": false
}
```

---

### 7.5. Cập nhật địa chỉ

**Endpoint:** `PUT /api/dia_chi/update`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:** Tương tự thêm địa chỉ, có thêm `id`

---

### 7.6. Cập nhật trạng thái địa chỉ

**Endpoint:** `PUT /api/dia_chi/update-trang-thai/{id}?status={status}`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

---

## 8. Đánh Giá Sản Phẩm

### 8.1. Lấy danh sách đánh giá

**Endpoint:** `GET /api/danh_gia_san_pham`

**Không cần JWT (GET public)**

**Query Parameters:**
- `page` (optional): Số trang
- `pageSize` (optional): Số lượng mỗi trang
- `idSanPham` (optional): ID sản phẩm
- `idKhachHang` (optional): ID khách hàng

---

### 8.2. Thêm đánh giá mới

**Endpoint:** `POST /api/danh_gia_san_pham/add`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:**
```json
{
  "idSanPham": 1,
  "idKhachHang": 1,
  "diem": 5,
  "noiDung": "Sản phẩm rất tốt",
  "hinhAnh": ["https://example.com/image1.jpg"]
}
```

---

### 8.3. Cập nhật đánh giá

**Endpoint:** `PUT /api/danh_gia_san_pham/update`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

**Request Body:** Tương tự thêm đánh giá, có thêm `id`

---

### 8.4. Xóa đánh giá

**Endpoint:** `DELETE /api/danh_gia_san_pham/delete/{id}`

**Cần JWT (KHACH_HANG, ADMIN, hoặc NHAN_VIEN)**

---

## 9. Thống Kê

### 9.1. Lấy thống kê tổng quan

**Endpoint:** `GET /api/thong-ke/tong-quan`

**Cần JWT (ADMIN hoặc NHAN_VIEN)**

**Response 200:**
```json
{
  "tongDoanhThu": 100000000,
  "tongDonHang": 1000,
  "tongKhachHang": 500,
  "tongSanPham": 200
}
```

---

## 10. Các API khác

### 10.1. Thể Loại

**Base URL:** `/api/the_loai`

- `GET /api/the_loai` - Lấy danh sách thể loại (public)
- `POST /api/the_loai/add` - Thêm thể loại (ADMIN/NHAN_VIEN)
- `PUT /api/the_loai/update` - Cập nhật thể loại (ADMIN/NHAN_VIEN)
- `PUT /api/the_loai/update-trang-thai/{id}?status={status}` - Cập nhật trạng thái (ADMIN/NHAN_VIEN)

---

### 10.2. Thương Hiệu

**Base URL:** `/api/thuong_hieu`

- `GET /api/thuong_hieu` - Lấy danh sách thương hiệu (public)
- `POST /api/thuong_hieu/add` - Thêm thương hiệu (ADMIN/NHAN_VIEN)
- `PUT /api/thuong_hieu/update` - Cập nhật thương hiệu (ADMIN/NHAN_VIEN)

---

### 10.3. Màu Sắc

**Base URL:** `/api/mau_sac`

- `GET /api/mau_sac` - Lấy danh sách màu sắc (public)
- `POST /api/mau_sac/add` - Thêm màu sắc (ADMIN/NHAN_VIEN)
- `PUT /api/mau_sac/update` - Cập nhật màu sắc (ADMIN/NHAN_VIEN)

---

### 10.4. Kích Cỡ

**Base URL:** `/api/kich_co`

- `GET /api/kich_co` - Lấy danh sách kích cỡ (ADMIN/NHAN_VIEN)
- `POST /api/kich_co/add` - Thêm kích cỡ (ADMIN/NHAN_VIEN)
- `PUT /api/kich_co/update` - Cập nhật kích cỡ (ADMIN/NHAN_VIEN)

---

### 10.5. Sản Phẩm Chi Tiết

**Base URL:** `/api/san_pham_chi_tiet`

- `GET /api/san_pham_chi_tiet` - Lấy danh sách sản phẩm chi tiết (public)
- `GET /api/san_pham_chi_tiet/get-by-id?idSanPhamChiTiet={id}` - Lấy theo ID (public)
- `POST /api/san_pham_chi_tiet/add` - Thêm sản phẩm chi tiết (ADMIN/NHAN_VIEN)
- `PUT /api/san_pham_chi_tiet/update` - Cập nhật sản phẩm chi tiết (ADMIN/NHAN_VIEN)

---

### 10.6. Nhân Viên

**Base URL:** `/api/nhan_vien`

- `GET /api/nhan_vien` - Lấy danh sách nhân viên (ADMIN/NHAN_VIEN)
- `GET /api/nhan_vien/{id}` - Lấy nhân viên theo ID (ADMIN/NHAN_VIEN)
- `POST /api/nhan_vien/add` - Thêm nhân viên (ADMIN)
- `PUT /api/nhan_vien/update` - Cập nhật nhân viên (ADMIN)

---

### 10.7. Phương Thức Thanh Toán

**Base URL:** `/api/phuong_thuc_thanh_toan`

- `GET /api/phuong_thuc_thanh_toan` - Lấy danh sách phương thức thanh toán

---

### 10.8. Sự Kiện

**Base URL:** `/api/su_kien`

- `GET /api/su_kien` - Lấy danh sách sự kiện (public)
- `POST /api/su_kien/add` - Thêm sự kiện (ADMIN/NHAN_VIEN)
- `PUT /api/su_kien/update` - Cập nhật sự kiện (ADMIN/NHAN_VIEN)

---

### 10.9. Giảm Giá

**Base URL:** `/api/giam_gia`

- `GET /api/giam_gia` - Lấy danh sách giảm giá (public)
- `POST /api/giam_gia/add` - Thêm giảm giá (ADMIN/NHAN_VIEN)
- `PUT /api/giam_gia/update` - Cập nhật giảm giá (ADMIN/NHAN_VIEN)

---

### 10.10. Đổi Trả

**Base URL:** `/api/doi-tra`

- `GET /api/doi-tra` - Lấy danh sách đổi trả (ADMIN/NHAN_VIEN/KHACH_HANG)
- `POST /api/doi-tra/add` - Tạo yêu cầu đổi trả (KHACH_HANG)
- `PUT /api/doi-tra/update` - Cập nhật đổi trả (ADMIN/NHAN_VIEN)

---

### 10.11. Thông Báo

**Base URL:** `/api/thong_bao`

- `GET /api/thong_bao` - Lấy danh sách thông báo (KHACH_HANG/ADMIN/NHAN_VIEN)
- `POST /api/thong_bao/add` - Thêm thông báo (ADMIN/NHAN_VIEN)
- `PUT /api/thong_bao/update` - Cập nhật thông báo (ADMIN/NHAN_VIEN)

---

### 10.12. Voucher Khách Hàng

**Base URL:** `/api/voucher_khach_hang`

- `GET /api/voucher_khach_hang` - Lấy voucher của khách hàng (KHACH_HANG/ADMIN/NHAN_VIEN)
- `POST /api/voucher_khach_hang/add` - Thêm voucher cho khách hàng (ADMIN/NHAN_VIEN)

---

### 10.13. Nhắn Tin / Chat

**Base URL:** `/api/nhan-tin`

- `GET /api/nhan-tin` - Lấy danh sách tin nhắn (ADMIN/NHAN_VIEN)
- `POST /api/nhan-tin/send` - Gửi tin nhắn (ADMIN/NHAN_VIEN)

---

## ⚠️ Xử lý lỗi

### Mã lỗi HTTP

- **200 OK**: Request thành công
- **201 Created**: Tạo mới thành công
- **400 Bad Request**: Dữ liệu request không hợp lệ
- **401 Unauthorized**: Chưa đăng nhập hoặc token hết hạn
- **403 Forbidden**: Không có quyền truy cập
- **404 Not Found**: Không tìm thấy resource
- **500 Internal Server Error**: Lỗi server

### Format Response lỗi

```json
{
  "message": "Mô tả lỗi",
  "error": "ERROR_CODE",
  "timestamp": "2024-01-01T00:00:00"
}
```

**Ví dụ:**
```json
{
  "message": "Tên đăng nhập hoặc mật khẩu không đúng",
  "error": "INVALID_CREDENTIALS",
  "timestamp": "2024-01-01T12:00:00"
}
```

---

## 📝 Ghi chú quan trọng

1. **JWT Token**: 
   - Lưu token vào localStorage hoặc sessionStorage
   - Gửi token trong header `Authorization: Bearer {token}` cho mọi request cần xác thực
   - Khi token hết hạn (401), gọi API refresh token để lấy token mới

2. **Pagination**: 
   - Hầu hết API list đều hỗ trợ pagination với `page` và `pageSize`
   - Mặc định: `page=1`, `pageSize=10`

3. **Date Format**: 
   - Sử dụng format ISO 8601: `yyyy-MM-ddTHH:mm:ss`
   - Ví dụ: `2024-01-01T00:00:00`

4. **Status Codes**:
   - `0`: Không hoạt động / Đã hủy
   - `1`: Hoạt động / Đang xử lý
   - Các mã khác tùy theo từng entity

5. **CORS**: 
   - Backend đã cấu hình CORS, cho phép tất cả origins trong development
   - Production sẽ chỉ cho phép specific origins

6. **Swagger UI**: 
   - Truy cập `/swagger-ui.html` để xem tài liệu API tương tác (nếu enabled)

---

## 🔗 Liên hệ

Nếu có thắc mắc hoặc cần hỗ trợ, vui lòng liên hệ team Backend.

**Chúc các bạn tích hợp API thành công! 🚀**

