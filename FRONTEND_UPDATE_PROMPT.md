# 🔧 PROMPT: Cập Nhật Frontend Sau Khi Backend Deploy

## 📋 Thông Tin Backend

- **Backend URL**: `https://ba-datn-1.onrender.com`
- **API Base URL**: `https://ba-datn-1.onrender.com/api`
- **Swagger UI**: `https://ba-datn-1.onrender.com/swagger-ui/index.html`

---

## ✅ Bước 1: Cập Nhật Environment Variables

### Angular: Tạo/Sửa `src/environments/environment.prod.ts`

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://ba-datn-1.onrender.com',
  apiBaseUrl: 'https://ba-datn-1.onrender.com/api',
  jwtTokenKey: 'token',
  requestTimeout: 30000
};
```

### Angular: Giữ `src/environments/environment.ts` cho development

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  apiBaseUrl: 'http://localhost:8080/api',
  jwtTokenKey: 'token',
  requestTimeout: 30000
};
```

### React/Vue: Tạo `.env.production`

```env
REACT_APP_API_URL=https://ba-datn-1.onrender.com
REACT_APP_API_BASE_URL=https://ba-datn-1.onrender.com/api
```

### React/Vue: Giữ `.env.development`

```env
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

---

## ✅ Bước 2: Đảm Bảo JWT Interceptor Hoạt Động

### File: `src/app/core/interceptors/jwt.interceptor.ts` (Angular)

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
import { environment } from '../../environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Bỏ qua interceptor cho auth endpoints
    if (req.url.includes('/api/auth/')) {
      return next.handle(req);
    }

    // Lấy token từ localStorage
    const token = localStorage.getItem(environment.jwtTokenKey || 'token');

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
        // Xử lý lỗi 401 (Unauthorized)
        if (error.status === 401) {
          // Xóa token
          localStorage.removeItem(environment.jwtTokenKey || 'token');
          
          // Redirect về login
          this.router.navigate(['/login']);
        }
        
        return throwError(() => error);
      })
    );
  }
}
```

### Đăng ký Interceptor trong `app.module.ts` hoặc `app.config.ts`

**NgModule:**
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

**Standalone (app.config.ts):**
```typescript
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, HTTP_INTERCEPTORS, withInterceptorsFromDi } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ]
};
```

---

## ✅ Bước 3: Cập Nhật API Service

### File: `src/app/services/api.service.ts` (Angular)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  // Ví dụ: Get sản phẩm
  getSanPham(): Observable<any> {
    return this.http.get(`${this.apiUrl}/san_pham`);
  }

  // Ví dụ: Get khách hàng
  getKhachHang(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/khach_hang/${id}`);
  }

  // Ví dụ: Get giỏ hàng (cần token)
  getGioHang(): Observable<any> {
    return this.http.get(`${this.apiUrl}/gio_hang`);
  }

  // Ví dụ: Login
  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/khach_hang_login`, {
      username: username,
      pass: password
    });
  }
}
```

---

## ✅ Bước 4: Cập Nhật Login Component

### File: `login.component.ts` hoặc `auth.service.ts`

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../services/api.service';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(
    private apiService: ApiService,
    private router: Router
  ) {}

  onLogin() {
    this.apiService.login(this.username, this.password).subscribe({
      next: (response: any) => {
        // ✅ LƯU TOKEN
        if (response && response.token) {
          localStorage.setItem(environment.jwtTokenKey || 'token', response.token);
          console.log('✅ Login successful! Token saved.');
          
          // Redirect
          this.router.navigate(['/']);
        } else {
          console.error('❌ No token in response');
          alert('Đăng nhập thất bại!');
        }
      },
      error: (error) => {
        console.error('❌ Login error:', error);
        alert('Sai tên đăng nhập hoặc mật khẩu!');
      }
    });
  }
}
```

---

## ✅ Bước 5: Tìm và Thay Thế Tất Cả Hardcoded URLs

### Tìm trong codebase:

```bash
# Tìm tất cả localhost:8080
grep -r "localhost:8080" src/

# Tìm tất cả http://
grep -r "http://" src/
```

### Thay thế:

**Trước:**
```typescript
this.http.get('http://localhost:8080/api/san_pham')
```

**Sau:**
```typescript
this.http.get(`${this.apiUrl}/san_pham`)
// hoặc
this.http.get(`${environment.apiBaseUrl}/san_pham`)
```

---

## ✅ Bước 6: Test API

### Test trong Browser Console:

```javascript
// Test API public (không cần token)
fetch('https://ba-datn-1.onrender.com/api/san_pham')
  .then(res => res.json())
  .then(data => console.log('✅ API works!', data))
  .catch(err => console.error('❌ Error:', err));

// Test login
fetch('https://ba-datn-1.onrender.com/api/auth/khach_hang_login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'your-username',
    pass: 'your-password'
  })
})
  .then(res => res.json())
  .then(data => {
    if (data.token) {
      localStorage.setItem('token', data.token);
      console.log('✅ Login successful! Token:', data.token);
    }
  })
  .catch(err => console.error('❌ Error:', err));

// Test API protected (cần token)
const token = localStorage.getItem('token');
fetch('https://ba-datn-1.onrender.com/api/gio_hang', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
  .then(res => res.json())
  .then(data => console.log('✅ Protected API works!', data))
  .catch(err => console.error('❌ Error:', err));
```

---

## ✅ Bước 7: Build Production

### Angular:

```bash
# Build production
ng build --configuration production

# Kiểm tra file build
ls -lh dist/
```

### React:

```bash
# Build production
npm run build

# Kiểm tra file build
ls -lh build/
```

### Vue:

```bash
# Build production
npm run build

# Kiểm tra file build
ls -lh dist/
```

---

## 🔍 Troubleshooting

### Lỗi: "Network Error" hoặc "Failed to fetch"

**Nguyên nhân**: Backend chưa chạy hoặc URL sai

**Giải pháp**:
1. Kiểm tra backend: https://ba-datn-1.onrender.com/api/san_pham
2. Kiểm tra URL trong code có đúng không
3. Kiểm tra CORS config trên backend

### Lỗi: "401 Unauthorized"

**Nguyên nhân**: Token không được gửi hoặc hết hạn

**Giải pháp**:
1. Kiểm tra token có trong localStorage: `localStorage.getItem('token')`
2. Kiểm tra JWT Interceptor có hoạt động không
3. Login lại để lấy token mới

### Lỗi: "CORS policy"

**Nguyên nhân**: Backend chưa cấu hình CORS cho domain frontend

**Giải pháp**:
1. Thêm domain frontend vào `CORS_ALLOWED_ORIGINS` trên Render
2. Restart backend service

---

## ✅ Checklist

Sau khi hoàn thành, kiểm tra:

- [ ] Đã tạo/sửa `environment.prod.ts` với URL production
- [ ] JWT Interceptor đã được đăng ký và hoạt động
- [ ] Tất cả hardcoded URLs đã được thay thế
- [ ] Login thành công và token được lưu
- [ ] API public hoạt động (sản phẩm, thể loại, voucher...)
- [ ] API protected hoạt động (giỏ hàng, hóa đơn...)
- [ ] Không có lỗi CORS
- [ ] Build production thành công
- [ ] Test trên production environment

---

## 📝 Tóm Tắt Thay Đổi

| Trước | Sau |
|-------|-----|
| `http://localhost:8080` | `https://ba-datn-1.onrender.com` |
| `http://localhost:8080/api` | `https://ba-datn-1.onrender.com/api` |
| Hardcoded URLs | Dùng `environment.apiBaseUrl` |

---

## 🚀 Quick Start

1. Copy code từ các bước trên
2. Paste vào file tương ứng trong project
3. Thay thế tất cả `localhost:8080` bằng `https://ba-datn-1.onrender.com`
4. Test API trong browser console
5. Build và deploy frontend

---

**Chúc bạn deploy thành công! 🎉**

