# 🚨 FIX NGAY LỖI 401 - CODE INTERCEPTOR ĐỂ COPY

## ⚡ CODE JWT INTERCEPTOR (COPY VÀO FRONTEND)

### File: `src/app/core/interceptors/jwt.interceptor.ts`

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
          
          // ⚠️ SỬA: Redirect đúng URL login
          const loginPath = '/login'; // Hoặc path login thực tế của bạn
          
          // Chỉ redirect nếu không đang ở trang login
          if (!this.router.url.includes('login')) {
            this.router.navigate([loginPath]).catch(() => {
              // Nếu navigate fail, dùng window.location
              window.location.href = loginPath;
            });
          }
        }

        return throwError(() => error);
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

## 🔧 REGISTER INTERCEPTOR

### Nếu dùng Angular Standalone (app.config.ts):

```typescript
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
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

### Nếu dùng NgModule (app.module.ts):

```typescript
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

@NgModule({
  // ...
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

## 🔧 SỬA LOGIN COMPONENT

### File: `login-client.component.ts`

Tìm method `handleLoginSuccess` và sửa:

```typescript
handleLoginSuccess(response: any) {
  // ✅ BƯỚC 1: Lưu token
  if (response && response.token) {
    localStorage.setItem('token', response.token);
    console.log('✅ Token saved:', response.token.substring(0, 20) + '...');
  } else {
    console.error('❌ No token in response:', response);
    return;
  }
  
  // ✅ BƯỚC 2: Redirect về trang chủ
  this.router.navigate(['/']).then(() => {
    // ✅ BƯỚC 3: Reload để load lại data với token
    window.location.reload();
  });
}
```

Hoặc nếu bạn đang dùng Observable:

```typescript
this.authService.loginKhachHang(credentials).subscribe({
  next: (response) => {
    // ✅ Lưu token
    if (response.token) {
      localStorage.setItem('token', response.token);
    }
    
    // Redirect
    this.router.navigate(['/']);
    window.location.reload();
  },
  error: (error) => {
    console.error('Login error:', error);
  }
});
```

---

## 🐛 FIX LỖI ROUTING 'g4/login'

Lỗi `Cannot match any routes. URL Segment: 'g4/login'` nghĩa là:
- Interceptor đang redirect sai URL
- URL login path không đúng

**Giải pháp**: Kiểm tra và sửa path login trong routing:

```typescript
// app-routing.module.ts
const routes: Routes = [
  {
    path: 'login',  // ✅ Đảm bảo path này đúng
    component: LoginClientComponent
  },
  // ...
];
```

Và sửa interceptor:
```typescript
// Thay vì:
this.router.navigate(['/login']);

// Dùng:
this.router.navigate(['/login']).catch(() => {
  window.location.href = '/login';
});
```

---

## ✅ KIỂM TRA SAU KHI FIX

1. **Check token có được lưu:**
   ```typescript
   console.log('Token:', localStorage.getItem('token'));
   ```

2. **Check Network tab:**
   - Mở DevTools → Network
   - Gọi API bất kỳ
   - Xem Request Headers → Phải có:
     ```
     Authorization: Bearer eyJhbGc...
     ```

3. **Test login flow:**
   - Login → Check token được lưu
   - Gọi API → Check có header Authorization
   - Không còn lỗi 401

---

## 🚀 QUICK TEST

Thêm vào console để test:
```typescript
// Test lấy token
localStorage.getItem('token')

// Test gửi request với token
fetch('http://localhost:8080/api/gio_hang/get_so_luong/2', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
}).then(r => r.json()).then(console.log)
```

