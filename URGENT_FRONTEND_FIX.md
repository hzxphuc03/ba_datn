# 🚨 URGENT: Frontend cần implement JWT ngay!

## ❌ Vấn đề hiện tại

Mọi API trả về **401 Unauthorized** vì Frontend chưa gửi JWT token!

---

## ✅ Giải pháp: Copy code này vào Frontend

### **Bước 1: Tạo JWT Interceptor**

**File**: `src/app/core/interceptors/jwt.interceptor.ts`

```typescript
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Bỏ qua interceptor cho auth endpoints
    if (req.url.includes('/api/auth/')) {
      return next.handle(req);
    }

    // Lấy token từ localStorage
    const token = localStorage.getItem('token');

    // Thêm token vào header nếu có
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req);
  }
}
```

---

### **Bước 2: Register Interceptor**

#### Nếu dùng **NgModule** (app.module.ts):

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

#### Hoặc nếu dùng **Standalone** (app.config.ts):

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

---

### **Bước 3: Lưu token sau khi login**

**File**: `login-client.component.ts` (hoặc service login)

Tìm method `handleLoginSuccess` và sửa:

```typescript
handleLoginSuccess(response: any) {
  // ✅ LƯU TOKEN
  if (response && response.token) {
    localStorage.setItem('token', response.token);
    console.log('✅ Token saved!');
  } else {
    console.error('❌ No token in response');
    return;
  }
  
  // Redirect
  this.router.navigate(['/']);
  window.location.reload(); // Reload để load data với token
}
```

---

## 🔍 Kiểm tra

Sau khi làm xong, mở **DevTools** → **Network**:

1. Login → Check response có `token`
2. Check `localStorage.getItem('token')` có giá trị
3. Gọi API bất kỳ → Check **Request Headers** có:
   ```
   Authorization: Bearer eyJhbGc...
   ```

---

## ⚠️ Lưu ý

Backend đã tạm thời cho phép các API này **không cần token** để test:
- ✅ GET `/api/khach_hang/**`
- ✅ GET `/api/san_pham/**`
- ✅ GET `/api/the_loai/**`
- ✅ GET `/api/voucher/**`

**NHƯNG** các API này vẫn cần token:
- ❌ `/api/gio_hang/**`
- ❌ `/api/thong_bao/**`
- ❌ `/api/dia_chi/**`
- ❌ `/api/hoa_don/**`

---

## 📝 Test nhanh

Thêm vào console browser:
```javascript
// Check token
console.log('Token:', localStorage.getItem('token'));

// Test request với token
fetch('http://localhost:8080/api/khach_hang/2', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(r => r.json())
.then(console.log)
.catch(console.error);
```

---

## ✅ Sau khi fix

Tất cả API sẽ hoạt động bình thường:
- ✅ Login → Lưu token
- ✅ Mọi request tự động có header `Authorization: Bearer <token>`
- ✅ Không còn lỗi 401

