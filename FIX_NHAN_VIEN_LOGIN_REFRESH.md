# 🔧 FIX: Đăng nhập nhân viên bị refresh

## ❌ Vấn đề

Sau khi đăng nhập nhân viên, trang bị refresh/reload liên tục.

## 🔍 Nguyên nhân

Có thể do:

1. **Frontend đang reload sau khi login:**
   ```typescript
   // Code có thể có:
   window.location.reload(); // ❌ Gây refresh
   ```

2. **Response backend trả về format khác:**
   - Backend trả: `{ "token": "..." }`
   - Frontend expect: `{ "accessToken": "..." }`

3. **Không lưu token đúng cách:**
   - Token không được lưu vào localStorage
   - Sau reload, token bị mất → redirect về login → reload lại

---

## ✅ Giải pháp

### **Bước 1: Kiểm tra Response Backend**

Backend trả về cho nhân viên:
```json
{
  "token": "eyJhbGc..."
}
```

### **Bước 2: Sửa Login Component (Frontend)**

**File**: `login-nhan-vien.component.ts` (hoặc tương tự)

Tìm method xử lý login và sửa:

```typescript
// ❌ SAI - Có reload:
handleLoginSuccess(response: any) {
  localStorage.setItem('token', response.token);
  this.router.navigate(['/admin']); // hoặc '/nhan-vien'
  window.location.reload(); // ❌ GÂY REFRESH
}

// ✅ ĐÚNG - Không reload:
handleLoginSuccess(response: any) {
  // Bước 1: Lưu token
  if (response && response.token) {
    localStorage.setItem('token', response.token);
    console.log('✅ Token saved for employee');
  } else {
    console.error('❌ No token in response');
    return;
  }
  
  // Bước 2: Redirect KHÔNG reload
  this.router.navigate(['/admin']).then(() => {
    // ✅ KHÔNG dùng window.location.reload()
    // Component sẽ tự load lại data nhờ JWT Interceptor
  });
}
```

### **Bước 3: Sửa nếu có vòng lặp redirect**

Nếu vẫn bị refresh, có thể do:

#### A. Auth Guard redirect loop

**File**: `auth.guard.ts`

```typescript
// ❌ SAI - Gây loop:
canActivate(): boolean {
  if (!localStorage.getItem('token')) {
    this.router.navigate(['/login']); // Redirect về login
    return false;
  }
  return true;
}
// → Login → Redirect → Check → Chưa có token (do chưa load) → Redirect lại login

// ✅ ĐÚNG:
canActivate(): boolean {
  const token = localStorage.getItem('token');
  if (!token) {
    this.router.navigate(['/login']);
    return false;
  }
  
  // Kiểm tra token có hợp lệ không
  if (this.isTokenExpired(token)) {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
    return false;
  }
  
  return true;
}

private isTokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp = payload.exp * 1000;
    return Date.now() >= exp;
  } catch {
    return true;
  }
}
```

#### B. Component OnInit gọi API → 401 → Interceptor redirect

**File**: `jwt.interceptor.ts`

```typescript
// ❌ SAI - Redirect ngay khi 401:
catchError((error: HttpErrorResponse) => {
  if (error.status === 401) {
    localStorage.removeItem('token');
    this.router.navigate(['/login']); // ❌ Gây refresh
  }
  return throwError(() => error);
})

// ✅ ĐÚNG - Chỉ redirect nếu KHÔNG đang ở login page:
catchError((error: HttpErrorResponse) => {
  if (error.status === 401) {
    localStorage.removeItem('token');
    
    // Chỉ redirect nếu không đang ở trang login
    const currentUrl = this.router.url;
    if (!currentUrl.includes('/login')) {
      this.router.navigate(['/login']);
    }
  }
  return throwError(() => error);
})
```

---

## 🎯 Code mẫu hoàn chỉnh - Login Nhân viên

```typescript
// login-nhan-vien.component.ts
export class LoginNhanVienComponent {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  onSubmit() {
    const credentials = {
      username: this.loginForm.value.username,
      pass: this.loginForm.value.password
    };
    
    this.authService.loginNhanVien(credentials).subscribe({
      next: (response) => {
        this.handleLoginSuccess(response);
      },
      error: (error) => {
        console.error('Login failed:', error);
        // Hiển thị lỗi
      }
    });
  }
  
  private handleLoginSuccess(response: any) {
    // ✅ Lưu token
    if (response.token) {
      localStorage.setItem('token', response.token);
      console.log('✅ Employee logged in');
    }
    
    // ✅ Redirect KHÔNG reload
    this.router.navigate(['/admin/dashboard']);
    // ❌ KHÔNG dùng: window.location.reload();
  }
}
```

---

## 🔍 Debug

Thêm console.log để debug:

```typescript
handleLoginSuccess(response: any) {
  console.log('1. Response:', response);
  
  if (response.token) {
    localStorage.setItem('token', response.token);
    console.log('2. Token saved');
    console.log('3. Current URL:', this.router.url);
    
    this.router.navigate(['/admin']).then(() => {
      console.log('4. Navigated to /admin');
      console.log('5. New URL:', this.router.url);
    });
  }
}
```

Kiểm tra console xem ở bước nào bị lặp.

---

## ✅ Checklist

- [ ] Login component KHÔNG có `window.location.reload()`
- [ ] Token được lưu vào localStorage
- [ ] Redirect dùng `router.navigate()`, không dùng `window.location.href`
- [ ] JWT Interceptor không redirect loop
- [ ] Auth Guard không redirect loop

---

## 🚀 Test

Sau khi sửa:

1. Login nhân viên
2. Check localStorage có token không
3. Không bị refresh/reload
4. Redirect đến trang admin đúng

