# Hướng Dẫn Tích Hợp JWT Bảo Mật - Backend & Frontend

## 📋 Tổng Quan

Hệ thống JWT đã được thiết kế với các tính năng bảo mật cao:
- **Access Token**: Thời gian sống ngắn (24h)
- **Refresh Token**: Thời gian sống dài (7 ngày)
- **Role-based Authorization**: ADMIN, NHAN_VIEN, KHACH_HANG
- **Security Audit**: Ghi log tất cả hoạt động đăng nhập
- **Rate Limiting**: Giới hạn số lần thử đăng nhập

## 🔧 Backend Implementation

### 1. Cấu Trúc JWT Backend Hiện Tại

```
security-improvements/
├── JwtTokenProvider.java      # Tạo và validate JWT tokens
├── JwtAuthenticationFilter.java # Filter xử lý JWT trong requests
├── JwtAuthenticationEntryPoint.java # Xử lý lỗi authentication
├── AuthenticationService.java # Service xử lý đăng nhập
├── WebSecurityConfig.java    # Cấu hình Spring Security
├── PasswordService.java      # Mã hóa mật khẩu
├── SecurityAuditService.java # Audit log bảo mật
└── application.yml          # Cấu hình JWT
```

### 2. API Endpoints Cần Tích Hợp

#### 2.1 Authentication Controller
```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @PostMapping("/login/khach-hang")
    public ResponseEntity<?> loginKhachHang(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        return authenticationService.authenticateKhachHang(request, clientIp);
    }
    
    @PostMapping("/login/nhan-vien")
    public ResponseEntity<?> loginNhanVien(@RequestBody loginTest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        return authenticationService.authenticateNhanVien(request, clientIp);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.getRefreshToken());
    }
    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Implement token blacklist logic
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công"));
    }
}
```

#### 2.2 Protected Endpoints Examples
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // Admin only endpoints
}

@RestController
@RequestMapping("/api/nhan-vien")
@PreAuthorize("hasAnyRole('ADMIN', 'NHAN_VIEN')")
public class NhanVienController {
    // Staff endpoints
}

@RestController
@RequestMapping("/api/khach-hang")
@PreAuthorize("hasRole('KHACH_HANG')")
public class KhachHangController {
    // Customer endpoints
}
```

### 3. Response Format Chuẩn

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "hoTen": "Nguyễn Văn A",
    "email": "user@example.com",
    "taiKhoan": "username",
    "chucVu": {
      "id": 1,
      "ten": "ADMIN"
    },
    "trangThai": 1
  }
}
```

## 🌐 Frontend Implementation

### 1. JWT Service (Angular/React/Vue)

#### Angular Service Example
```typescript
// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface LoginRequest {
  username: string;
  pass: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: any;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Load user from localStorage on service init
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  // Đăng nhập khách hàng
  loginKhachHang(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login/khach-hang`, credentials)
      .pipe(
        map(response => {
          this.setAuthData(response);
          return response;
        })
      );
  }

  // Đăng nhập nhân viên
  loginNhanVien(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login/nhan-vien`, credentials)
      .pipe(
        map(response => {
          this.setAuthData(response);
          return response;
        })
      );
  }

  // Refresh token
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, { refreshToken })
      .pipe(
        map(response => {
          this.setAuthData(response);
          return response;
        })
      );
  }

  // Đăng xuất
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      map(() => {
        this.clearAuthData();
      })
    );
  }

  // Lưu thông tin authentication
  private setAuthData(authResponse: AuthResponse): void {
    localStorage.setItem('accessToken', authResponse.accessToken);
    localStorage.setItem('refreshToken', authResponse.refreshToken);
    localStorage.setItem('currentUser', JSON.stringify(authResponse.user));
    this.currentUserSubject.next(authResponse.user);
  }

  // Xóa thông tin authentication
  private clearAuthData(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  // Kiểm tra đăng nhập
  isLoggedIn(): boolean {
    const token = localStorage.getItem('accessToken');
    return token != null && !this.isTokenExpired(token);
  }

  // Kiểm tra token hết hạn
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }

  // Lấy access token
  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  // Lấy thông tin user hiện tại
  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  // Kiểm tra role
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    if (!user) return false;
    
    if (user.chucVu) {
      return user.chucVu.ten === role;
    }
    return false;
  }

  // Kiểm tra có một trong các roles
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }
}
```

### 2. HTTP Interceptor

```typescript
// jwt.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    // Thêm access token vào header
    const accessToken = this.authService.getAccessToken();
    if (accessToken) {
      req = this.addTokenToRequest(req, accessToken);
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Nếu lỗi 401 và có refresh token, thử refresh
        if (error.status === 401 && !req.url.includes('/auth/')) {
          return this.handle401Error(req, next);
        }
        return throwError(error);
      })
    );
  }

  private addTokenToRequest(req: HttpRequest<any>, token: string): HttpRequest<any> {
    return req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap((response: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(response.accessToken);
          return next.handle(this.addTokenToRequest(req, response.accessToken));
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.authService.logout();
          return throwError(error);
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => next.handle(this.addTokenToRequest(req, token)))
      );
    }
  }
}
```

### 3. Route Guards

```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    // Kiểm tra role nếu có yêu cầu
    const requiredRoles = route.data['roles'] as string[];
    if (requiredRoles && !this.authService.hasAnyRole(requiredRoles)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}
```

### 4. Login Component

```typescript
// login.component.ts
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div class="login-container">
      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
        <h2>Đăng Nhập</h2>
        
        <div class="form-group">
          <label>Tài khoản:</label>
          <input type="text" formControlName="username" class="form-control">
        </div>
        
        <div class="form-group">
          <label>Mật khẩu:</label>
          <input type="password" formControlName="password" class="form-control">
        </div>
        
        <div class="form-group">
          <label>Loại tài khoản:</label>
          <select formControlName="userType" class="form-control">
            <option value="khach-hang">Khách hàng</option>
            <option value="nhan-vien">Nhân viên</option>
          </select>
        </div>
        
        <button type="submit" [disabled]="loginForm.invalid || loading" class="btn btn-primary">
          {{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}
        </button>
        
        <div *ngIf="error" class="alert alert-danger">{{ error }}</div>
      </form>
    </div>
  `
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      userType: ['khach-hang', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.loading = true;
    this.error = '';

    const credentials = {
      username: this.loginForm.value.username,
      pass: this.loginForm.value.password
    };

    const loginMethod = this.loginForm.value.userType === 'khach-hang' 
      ? this.authService.loginKhachHang(credentials)
      : this.authService.loginNhanVien(credentials);

    loginMethod.subscribe({
      next: (response) => {
        this.loading = false;
        // Redirect based on user role
        const user = response.user;
        if (user.chucVu?.ten === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (user.chucVu?.ten === 'NHAN_VIEN') {
          this.router.navigate(['/nhan-vien']);
        } else {
          this.router.navigate(['/khach-hang']);
        }
      },
      error: (error) => {
        this.loading = false;
        this.error = error.error?.message || 'Đăng nhập thất bại';
      }
    });
  }
}
```

## 🔒 Security Best Practices

### 1. Token Storage
- **Access Token**: Lưu trong memory hoặc sessionStorage (không localStorage)
- **Refresh Token**: Lưu trong httpOnly cookie (khuyến nghị) hoặc localStorage
- **User Info**: Lưu trong localStorage/sessionStorage

### 2. Token Refresh Strategy
```typescript
// Tự động refresh token trước khi hết hạn
setInterval(() => {
  const token = this.authService.getAccessToken();
  if (token && this.isTokenExpiringSoon(token)) {
    this.authService.refreshToken().subscribe();
  }
}, 60000); // Check every minute

private isTokenExpiringSoon(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expiryTime = payload.exp * 1000;
    const currentTime = Date.now();
    const timeUntilExpiry = expiryTime - currentTime;
    return timeUntilExpiry < 5 * 60 * 1000; // 5 minutes
  } catch {
    return true;
  }
}
```

### 3. CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## 📱 Mobile Implementation (React Native)

### AsyncStorage for Token Management
```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';

class AuthService {
  async setTokens(accessToken, refreshToken) {
    await AsyncStorage.multiSet([
      ['accessToken', accessToken],
      ['refreshToken', refreshToken]
    ]);
  }

  async getAccessToken() {
    return await AsyncStorage.getItem('accessToken');
  }

  async clearTokens() {
    await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'currentUser']);
  }
}
```

## 🧪 Testing JWT Integration

### Backend Tests
```java
@Test
public void testJwtTokenGeneration() {
    UserLoginResponse user = createTestUser();
    String token = jwtTokenProvider.generateToken("testuser", "ADMIN", user);
    
    assertThat(token).isNotNull();
    assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("testuser");
}
```

### Frontend Tests
```typescript
describe('AuthService', () => {
  it('should login successfully', () => {
    const mockResponse = { accessToken: 'token', user: {} };
    spyOn(httpClient, 'post').and.returnValue(of(mockResponse));
    
    authService.loginKhachHang({ username: 'test', pass: 'test' }).subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(localStorage.getItem('accessToken')).toBe('token');
    });
  });
});
```

## 🚀 Deployment Checklist

### Backend
- [ ] Cấu hình JWT secret mạnh trong production
- [ ] Bật HTTPS
- [ ] Cấu hình CORS chính xác
- [ ] Enable security headers
- [ ] Cấu hình rate limiting
- [ ] Setup monitoring và logging

### Frontend
- [ ] Build production với environment variables
- [ ] Cấu hình API URL cho production
- [ ] Implement proper error handling
- [ ] Add loading states
- [ ] Test trên các browsers khác nhau
- [ ] Optimize bundle size

## 📚 Tài Liệu Tham Khảo

1. [JWT.io](https://jwt.io/) - JWT Debugger
2. [Spring Security JWT](https://spring.io/guides/tutorials/spring-boot-oauth2/)
3. [Angular JWT](https://github.com/auth0/angular2-jwt)
4. [React JWT](https://github.com/auth0/node-jsonwebtoken)

---

*Hướng dẫn này cung cấp implementation hoàn chỉnh cho việc tích hợp JWT bảo mật trong cả Backend và Frontend. Hãy tuân thủ các best practices để đảm bảo bảo mật tối đa.*