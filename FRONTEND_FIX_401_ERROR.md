# 🔧 FIX LỖI 401 UNAUTHORIZED - FRONTEND

## ❌ VẤN ĐỀ
Sau khi login, tất cả các API requests bị lỗi **401 Unauthorized**:
- `/api/gio_hang/**`
- `/api/thong_bao/**`  
- `/api/dia_chi/**`
- `/api/danh_gia/**`
- `/api/voucher_khach_hang/**`

**Nguyên nhân**: Frontend chưa gửi JWT token trong header `Authorization`

---

## ✅ GIẢI PHÁP: THÊM JWT INTERCEPTOR

### **Bước 1: Tạo JWT Interceptor**

Tạo file: `src/app/core/interceptors/jwt.interceptor.ts`

```typescript
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Bỏ qua interceptor cho auth endpoints
    if (this.isAuthEndpoint(req.url)) {
      return next.handle(req);
    }

    // Lấy token từ localStorage
    const token = localStorage.getItem('token') || localStorage.getItem('auth_token');

    // Thêm token vào header nếu có
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Xử lý lỗi 401 - Token hết hạn hoặc không hợp lệ
        if (error.status === 401) {
          // Xóa token cũ
          localStorage.removeItem('token');
          localStorage.removeItem('auth_token');
          
          // Redirect về login (trừ khi đang ở trang login)
          if (!this.router.url.includes('/login')) {
            this.router.navigate(['/login'], {
              queryParams: { returnUrl: this.router.url }
            });
          }
        }

        return throwError(error);
      })
    );
  }

  /**
   * Kiểm tra có phải auth endpoint không
   */
  private isAuthEndpoint(url: string): boolean {
    return url.includes('/api/auth/');
  }
}
```

---

### **Bước 2: Register Interceptor**

#### Nếu dùng **Angular Standalone** (app.config.ts):

```typescript
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, HTTP_INTERCEPTORS, withInterceptors } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ]
};
```

#### Hoặc nếu dùng **NgModule** (app.module.ts):

```typescript
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

@NgModule({
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

---

### **Bước 3: Lưu Token Sau Khi Login**

Sửa file `login-client.component.ts`:

```typescript
// Sau khi login thành công
performLogin() {
  this.authService.loginKhachHang(credentials).subscribe({
    next: (response) => {
      // Lưu token vào localStorage
      if (response.token) {
        localStorage.setItem('token', response.token);
        // Hoặc localStorage.setItem('auth_token', response.token);
      }
      
      // Redirect hoặc reload data
      this.router.navigate(['/']);
      window.location.reload(); // Reload để load lại data với token
    },
    error: (error) => {
      console.error('Login failed:', error);
    }
  });
}
```

---

## 🔍 KIỂM TRA

### Test token có được gửi không:

1. Mở **DevTools** → Tab **Network**
2. Gọi một API (ví dụ: `/api/gio_hang/2`)
3. Xem **Request Headers** → Phải có:
   ```
   Authorization: Bearer eyJhbGc...
   ```
4. Nếu không có → Interceptor chưa hoạt động

---

## 🐛 TROUBLESHOOTING

### ❌ Vẫn bị 401 sau khi thêm interceptor?

**Kiểm tra:**
1. ✅ Token có được lưu sau login không?
   ```typescript
   console.log(localStorage.getItem('token')); // Phải có giá trị
   ```

2. ✅ Interceptor có được register không?
   - Check `app.module.ts` hoặc `app.config.ts`
   - Restart dev server sau khi thêm interceptor

3. ✅ Token có hợp lệ không?
   - Check token không bị expire
   - Check format: `Bearer <token>` (có dấu cách)

### ❌ Token key không đúng?

Backend trả về token với key là `"token"`:
```json
{
  "token": "eyJhbGc..."
}
```

Vậy localStorage key phải là:
```typescript
localStorage.setItem('token', response.token); // ✅ ĐÚNG
// KHÔNG phải:
localStorage.setItem('accessToken', response.token); // ❌ SAI
```

---

## 📝 CODE MẪU HOÀN CHỈNH

### Auth Service (nếu chưa có):

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) {}

  loginKhachHang(credentials: {username: string, pass: string}): Observable<any> {
    return this.http.post('/api/auth/khach_hang_login', credentials)
      .pipe(
        tap((response: any) => {
          // Lưu token ngay sau khi login thành công
          if (response.token) {
            localStorage.setItem('token', response.token);
            console.log('Token saved:', response.token);
          }
        }))
      );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
```

---

## ⚡ QUAN TRỌNG: Sửa ngay trong Login Component

Sửa file `login-client.component.ts` ở method `handleLoginSuccess`:

**TRƯỚC:**
```typescript
handleLoginSuccess(response) {
  // Chỉ redirect, không lưu token
  this.router.navigate(['/']);
}
```

**SAU:**
```typescript
handleLoginSuccess(response) {
  // ✅ LƯU TOKEN TRƯỚC
  if (response.token) {
    localStorage.setItem('token', response.token);
    console.log('Token saved:', response.token);
  }
  
  // Sau đó mới redirect
  this.router.navigate(['/']);
  // Hoặc reload để load lại data với token
  window.location.reload();
}
```

---

## ✅ SAU KHI FIX

**Trước:**
```
❌ GET /api/gio_hang/2 → 401 Unauthorized
❌ GET /api/thong_bao/... → 401 Unauthorized  
```

**Sau:**
```
✅ GET /api/gio_hang/2 
   Header: Authorization: Bearer <token>
   → 200 OK

✅ GET /api/thong_bao/...
   Header: Authorization: Bearer <token>
   → 200 OK
```

---

## 🚀 QUICK FIX (Tạm thời)

Nếu cần test nhanh, có thể thêm token thủ công trong mỗi request:

```typescript
// Trong service/component
const token = localStorage.getItem('token');
this.http.get('/api/gio_hang/2', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
}).subscribe();
```

**NHƯNG**: Cách này không khuyến khích. Nên dùng Interceptor để tự động hóa.

