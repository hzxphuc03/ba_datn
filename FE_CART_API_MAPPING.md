# FE – Cart API Mapping (for AI-assisted fixes)

- Base URL: `http://localhost:8080`
- Auth: Most cart endpoints require JWT with role KHACH_HANG
  - Header: `Authorization: Bearer <token>`

## 1) Entities and IDs used by FE
- idKhachHang: number (customer id)
- idGioHangChiTiet: number (cart item id)
- idSanPhamChiTiet: number (SKU/variant id)

## 2) Endpoints used in Cart flows

### 2.1 Cart summary and items
- GET `/api/gio_hang/get_so_luong/{idKhachHang}`
  - Returns: number (total items)
  - Auth: required

- GET `/api/gio_hang_chi_tiet/{idKhachHang}`
  - Returns: `GioHangChiTiet[]`
  - Auth: required
  - Common FE bug: calling `/api/gio_hang_chi_tiet/undefined` → ensure `idKhachHang` is defined before calling.

### 2.2 Cart mutations
- POST `/api/gio_hang_chi_tiet/add`
  - Body:
    ```json
    {
      "idKhachhang": 2,
      "idSanPhamChiTiet": 123,
      "soLuong": 1
    }
    ```
  - Returns: `{ ...AddGioHangChiTietRes } | erroResponse`
  - Auth: required

- POST `/api/gio_hang_chi_tiet/add-so-luong`
  - Body:
    ```json
    {
      "idGioHangChiTiet": 456,
      "soLuong": 3
    }
    ```
  - Returns: `GioHangChiTiet | erroResponse`
  - Auth: required

- DELETE `/api/gio_hang_chi_tiet/delete-by-id/{idGioHangChiTiet}`
  - Returns: "Xóa thành công" | erroResponse
  - Auth: required

- DELETE `/api/gio_hang_chi_tiet/delete-All/{idKhachHang}`
  - Returns: "Xóa thành công" | erroResponse
  - Auth: required

### 2.3 Checkout-related on Cart
- PUT `/api/gio_hang/add-dia-chi`
  - Body: `GioHangDiaChi`
- PUT `/api/gio_hang/add-voucher`
  - Body: `GioHangVoucher`
- PUT `/api/gio_hang/add-phuong-thuc-thanh-toan`
  - Body: `GioHangPhuongThucThanhToan`
- PUT `/api/gio_hang/thanh-toan-khi-giao-hang`
  - Body: `ThanhToanKhiNhanHang`
- PUT `/api/gio_hang/thanh-toan-online`
  - Body: `ThanhToanKhiNhanHang`
- PUT `/api/gio_hang/thanh-toan-thanh-cong`
  - Body: `ThanhToanKhiNhanHang`

### 2.4 Related data used on cart screens
- GET `/api/dia_chi/get-by-idkh?idKhachHang={id}&page={n}` (Auth required)
- GET `/api/voucher_khach_hang/get-by-id/{idKhachHang}` (Auth required)
- GET `/api/thong_bao/get-count-thong-bao/{idKhachHang}` (Auth required)

## 3) Auth expectations (JWT)
- Backend requires JWT for cart endpoints → FE must send header:
  - `Authorization: Bearer ${token}`
- Recommended: Angular JWT Interceptor to auto-attach token
  - See: `JWT_INTERCEPTOR_CODE_FIX.md`

## 4) TypeScript contracts (suggested)
```ts
export interface GioHangChiTiet {
  id: number;
  soLuong: number;
  giaHienHanh: number;
  giaDaGiam?: number;
  sanPhamChiTiet: {
    id: number;
    ten: string;
    hinhAnh?: string;
  };
}

export interface AddGioHangChiTietReq {
  idKhachhang: number;
  idSanPhamChiTiet: number;
  soLuong: number;
}

export interface AddSoLuongReq {
  idGioHangChiTiet: number;
  soLuong: number;
}
```

## 5) Angular service examples
```ts
@Injectable({ providedIn: 'root' })
export class CartService {
  constructor(private http: HttpClient) {}

  getCount(idKhachHang: number) {
    return this.http.get<number>(`/api/gio_hang/get_so_luong/${idKhachHang}`);
  }

  getItems(idKhachHang: number) {
    return this.http.get<GioHangChiTiet[]>(`/api/gio_hang_chi_tiet/${idKhachHang}`);
  }

  addItem(req: AddGioHangChiTietReq) {
    return this.http.post(`/api/gio_hang_chi_tiet/add`, req);
  }

  updateQuantity(req: AddSoLuongReq) {
    return this.http.post(`/api/gio_hang_chi_tiet/add-so-luong`, req);
  }

  removeItem(idGioHangChiTiet: number) {
    return this.http.delete(`/api/gio_hang_chi_tiet/delete-by-id/${idGioHangChiTiet}`);
  }

  clearCart(idKhachHang: number) {
    return this.http.delete(`/api/gio_hang_chi_tiet/delete-All/${idKhachHang}`);
  }
}
```

## 6) Common FE mistakes causing errors
- Missing JWT header → 401 on all cart calls
- `idKhachHang` undefined → `/api/gio_hang_chi_tiet/undefined`  
  - Fix: wait for user state or route param before calling APIs
- Assuming array but backend returns object or null
  - Fix: always null-check and default to []
- Calling cart APIs before login/after logout
  - Fix: guard by `authService.isLoggedIn()`

## 7) Minimal guards in component
```ts
async loadCart() {
  const id = this.currentUser?.id; // ensure defined
  if (!id) return;

  try {
    const items = await firstValueFrom(this.cartService.getItems(id));
    this.items = Array.isArray(items) ? items : [];
    this.count = await firstValueFrom(this.cartService.getCount(id));
  } catch (e) {
    console.error('Cart load error', e);
    this.items = [];
    this.count = 0;
  }
}
```

## 8) Quick checklist
- FE sends `Authorization: Bearer <token>`
- `idKhachHang` is defined before calling cart APIs
- Handle 401 globally via interceptor
- Null/empty-safe UI bindings (e.g., `items?.length || 0`)
- Align body shapes with backend request models
