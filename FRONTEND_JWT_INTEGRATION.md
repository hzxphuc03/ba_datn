# 🔧 HƯỚNG DẪN TÍCH HỢP JWT VÀO FRONTEND

## 📋 TỔNG QUAN

Backend đã implement JWT bảo mật. Frontend cần:
1. ✅ Lưu token sau khi login
2. ✅ Gửi token trong mọi API request
3. ✅ Xử lý lỗi 401 (token hết hạn)
4. ✅ Redirect về login khi unauthorized

---

## 🎯 1. API RESPONSE TỪ BACKEND

### Login Response Format:
```typescript
// Backend trả về:
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIs..."
}

// Không phải:
// { "accessToken": "...", "refreshToken": "...", "user": {...} }
```

### Login Endpoints:
```
POST /api/auth/nhan_vien_login
POST /api/auth/khach_hang_login

Body: {
  "username": "admin",
  "pass": "password"
}
```

---

## 📝 2. IMPLEMENTATION CHO ANGULAR

### A. Auth Service (auth.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface LoginRequest {
  username: string;
  pass: string;
}

export interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUserFromToken();
  }

  /**
   * Đăng nhập nhân viên
   */
  loginNhanVien(credentials: LoginRequest): Observable<any> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/nhan_vien_login`, credentials)
      .pipe(
        map(response => {
          // Lưu token
          this.setToken(response.token);
          // Decode token để lấy user info
          this.loadUserFromToken();
          return response;
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(error);
        })
      );
  }

  /**
   * Đăng nhập khách hàng
   */
  loginKhachHang(credentials: LoginRequest): Observable<any> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/khach_hang_login`, credentials)
      .pipe(
        map(response => {
          this.setToken(response.token);
          this.loadUserFromToken();
          return response;
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(error);
        })
      );
  }

  /**
   * Lưu token vào localStorage
   */
  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  /**
   * Lấy token từ localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Kiểm tra đã đăng nhập chưa
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    // Kiểm tra token hết hạn chưa (optional - có thể check ở backend)
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiryTime = payload.exp * 1000; // Convert to milliseconds
      return Date.now() < expiryTime;
    } catch {
      return false;
    }
  }

  /**
   * Decode token để lấy user info
   */
  loadUserFromToken(): void {
    const token = this.getToken();
    if (!token) {
      this.currentUserSubject.next(null);
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.currentUserSubject.next({
        username: payload.sub,
        role: payload.role,
        userId: payload.userId,
        user: payload.user
      });
    } catch (e) {
      console.error('Error decoding token:', e);
      this.currentUserSubject.next(null);
    }
  }

  /**
   * Kiểm tra role
   */
  hasRole(role: string): boolean {
    const user = this.currentUserSubject.value;
    if (!user) return false;
    return user.role === role.toUpperCase();
  }

  /**
   * Kiểm tra có một trong các roles
   */
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  /**
   * Logout
   */
  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
  }

  /**
   * Lấy current user
   */
  getCurrentUser() {
    return this.currentUserSubject.value;
  }
}
```

### B. JWT Interceptor (jwt.interceptor.ts)

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
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Bỏ qua interceptor cho auth endpoints
    if (this.isAuthEndpoint(req.url)) {
      return next.handle(req);
    }

    // Lấy token
    const token = this.authService.getToken();

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
        // Xử lý lỗi 401 - Unauthorized
        if (error.status === 401) {
          // Token hết hạn hoặc không hợp lệ
          this.authService.logout();
          this.router.navigate(['/login'], {
            queryParams: { returnUrl: this.router.url }
          });
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

### C. Register Interceptor (app.module.ts hoặc app.config.ts)

```typescript
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';

// Nếu dùng Angular standalone
export const appConfig: ApplicationConfig = {
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    // ... other providers
  ]
};

// Hoặc nếu dùng NgModule
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

### D. Auth Guard (auth.guard.ts)

```typescript
import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    // Kiểm tra đã đăng nhập
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    // Kiểm tra role nếu route có yêu cầu
    const requiredRoles = route.data?.['roles'] as string[];
    if (requiredRoles && !this.authService.hasAnyRole(requiredRoles)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}
```

### E. Login Component (login.component.ts)

```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      pass: ['', Validators.required]
    });
  }

  login(isNhanVien: boolean = false) {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const credentials = this.loginForm.value;

    const loginObs = isNhanVien 
      ? this.authService.loginNhanVien(credentials)
      : this.authService.loginKhachHang(credentials);

    loginObs.subscribe({
      next: (response) => {
        // Token đã được lưu trong AuthService
        // Redirect về trang chủ hoặc returnUrl
        const returnUrl = this.router.parseUrl(this.router.url).queryParams['returnUrl'] || '/';
        this.router.navigate([returnUrl]);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Đăng nhập thất bại';
        console.error('Login error:', error);
      }
    });
  }
}
```

---

## 📝 3. SỬ DỤNG TRONG ROUTING

```typescript
const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'nhan-vien',
    component: NhanVienComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN', 'NHAN_VIEN'] }
  },
  {
    path: 'khach-hang',
    component: KhachHangComponent,
    canActivate: [AuthGuard],
    data: { roles: ['KHACH_HANG'] }
  }
];
```

---

## 🔧 4. CHECKLIST IMPLEMENTATION

- [ ] Tạo `AuthService` với methods login, getToken, isLoggedIn
- [ ] Tạo `JwtInterceptor` để thêm token vào headers
- [ ] Register interceptor trong app module/config
- [ ] Tạo `AuthGuard` để bảo vệ routes
- [ ] Update `LoginComponent` để lưu token sau khi login
- [ ] Xử lý logout (xóa token, redirect)
- [ ] Xử lý lỗi 401 (token hết hạn)
- [ ] Test với các API endpoints

---

## ⚠️ LƯU Ý QUAN TRỌNG

1. **Token format**: Backend trả về `token` (không phải `accessToken`)
2. **Login endpoints**: `/api/auth/nhan_vien_login` và `/api/auth/khach_hang_login`
3. **Token expiry**: 24 giờ - Frontend nên check và logout khi hết hạn
4. **Role format**: `ADMIN`, `NHAN_VIEN`, `KHACH_HANG` (uppercase)
5. **CORS**: Backend đã config CORS cho `http://localhost:4200`

---

## 🧪 TEST

```typescript
// 1. Test login
this.authService.loginNhanVien({ username: 'admin', pass: 'password' })
  .subscribe(response => console.log('Token:', response.token));

// 2. Test API call với token
this.http.get('/api/nhan_vien').subscribe();

// 3. Check current user
this.authService.getCurrentUser(); // { username, role, userId }
```

---

## 📚 TÀI LIỆU THAM KHẢO

- File example: `/frontend-examples/` (cần adjust theo API thực tế)
- Angular HTTP Interceptors: https://angular.io/api/common/http/HttpInterceptor

