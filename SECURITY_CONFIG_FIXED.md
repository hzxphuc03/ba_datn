# ✅ ĐÃ FIX: API TRẢ VỀ 401

## 🔧 Những gì đã sửa

### 1. Thêm Public Endpoints (không cần token)
- ✅ **GET `/api/the_loai/**`** - Xem thể loại
- ✅ **GET `/api/san_pham/**`** - Xem sản phẩm  
- ✅ **GET `/api/san_pham_chi_tiet/**`** - Xem chi tiết sản phẩm
- ✅ **GET `/api/su_kien/**`** - Xem sự kiện
- ✅ **GET `/api/thuong_hieu/**`** - Xem thương hiệu
- ✅ **GET `/api/mau_sac/**`** - Xem màu sắc
- ✅ **GET `/api/danh_gia_san_pham/**`** - Xem đánh giá
- ✅ **GET `/api/voucher/**`** - Xem voucher
- ✅ **GET `/api/giam_gia/**`** - Xem giảm giá
- ✅ **WebSocket `/ws/**`** - WebSocket connection

### 2. Endpoints cần token (sau khi login)

#### Khách hàng (cần token với role KHACH_HANG):
- ❌ `/api/gio_hang/**` - Giỏ hàng
- ❌ `/api/dia_chi/**` - Địa chỉ
- ❌ `/api/danh_gia/**` - Đánh giá sản phẩm
- ❌ `/api/voucher_khach_hang/**` - Voucher khách hàng
- ❌ `/api/thong_bao/**` - Thông báo
- ❌ `/api/hoa_don/**` - Hóa đơn

#### Admin/Nhân viên (cần token với role ADMIN/NHAN_VIEN):
- ❌ `/api/admin/**`
- ❌ `/api/nhan_vien/**`
- ❌ `/api/thong_ke/**`
- ❌ POST/PUT/DELETE `/api/san_pham/**`
- ❌ POST/PUT/DELETE `/api/the_loai/**`
- ❌ POST/PUT/DELETE `/api/voucher/**`

---

## ✅ CÁC API ĐÃ HOẠT ĐỘNG (KHÔNG CẦN TOKEN)

Bạn có thể test ngay:

```bash
# Xem sản phẩm - không cần token
GET http://localhost:8080/api/san_pham?page=1&pageSize=10

# Xem thể loại - không cần token
GET http://localhost:8080/api/the_loai

# Xem voucher - không cần token  
GET http://localhost:8080/api/voucher

# Xem đánh giá - không cần token
GET http://localhost:8080/api/danh_gia_san_pham/get-by-san-pham-id?idSanPham=1
```

---

## ❌ CÁC API CẦN TOKEN (SAU KHI LOGIN)

### Frontend cần làm:

1. **Login để lấy token:**
   ```bash
   POST /api/auth/khach_hang_login
   Body: { "username": "...", "pass": "..." }
   Response: { "token": "eyJhbGc..." }
   ```

2. **Lưu token sau khi login:**
   ```typescript
   localStorage.setItem('token', response.token);
   ```

3. **Gửi token trong mọi request:**
   ```typescript
   headers: {
     'Authorization': 'Bearer ' + localStorage.getItem('token')
   }
   ```

4. **Hoặc dùng JWT Interceptor** (tự động thêm token):
   - Xem file `JWT_INTERCEPTOR_CODE_FIX.md`

---

## 🎯 TÓM TẮT

✅ **Public APIs** (xem sản phẩm, thể loại, voucher...) → **Đã fix, không còn 401**

❌ **Protected APIs** (giỏ hàng, thông báo, hóa đơn...) → **Cần token, Frontend cần thêm JWT Interceptor**

---

## 📝 BƯỚC TIẾP THEO

1. ✅ Backend đã OK - Public endpoints hoạt động
2. ❌ Frontend cần:
   - Tạo JWT Interceptor
   - Lưu token sau login
   - Gửi token trong requests

**Xem chi tiết**: `JWT_INTERCEPTOR_CODE_FIX.md`

