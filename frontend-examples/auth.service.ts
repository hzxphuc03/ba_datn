// Angular JWT Authentication Service
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface LoginRequest {
  username: string;
  pass: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface User {
  id: number;
  hoTen: string;
  email: string;
  taiKhoan: string;
  chucVu?: {
    id: number;
    ten: string;
  };
  trangThai: number;
  hinhAnh?: string;
  soDienThoai?: string;
  gioiTinh?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private tokenRefreshInProgress = false;
  private tokenRefreshSubject = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {
    // Load user from localStorage on service init
    this.loadUserFromStorage();
  }

  /**
   * Đăng nhập khách hàng
   */
  loginKhachHang(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login/khach-hang`, credentials)
      .pipe(
        map(response => {
          this.setAuthData(response);
          return response;
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Đăng nhập nhân viên
   */
  loginNhanVien(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login/nhan-vien`, credentials)
      .pipe(
        map(response => {
          this.setAuthData(response);
          return response;
        }),
        catchError(this.handleError)
      );
  }

  /**
   * Refresh access token
   */
  refreshToken(): Observable<AuthResponse> {
    if (this.tokenRefreshInProgress) {
      return this.tokenRefreshSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => of({ accessToken: token } as AuthResponse))
      );
    }

    this.tokenRefreshInProgress = true;
    const refreshToken = this.getRefreshToken();
    
    if (!refreshToken) {
      this.logout();
      return throwError('No refresh token available');
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, { refreshToken })
      .pipe(
        map(response => {
          this.tokenRefreshInProgress = false;
          this.setAuthData(response);
          this.tokenRefreshSubject.next(response.accessToken);
          return response;
        }),
        catchError(error => {
          this.tokenRefreshInProgress = false;
          this.logout();
          return throwError(error);
        })
      );
  }

  /**
   * Đăng xuất
   */
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}).pipe(
      map(() => {
        this.clearAuthData();
      }),
      catchError(() => {
        // Even if logout fails on server, clear local data
        this.clearAuthData();
        return of(null);
      })
    );
  }

  /**
   * Đổi mật khẩu
   */
  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/change-password`, {
      currentPassword,
      newPassword
    }).pipe(catchError(this.handleError));
  }

  /**
   * Quên mật khẩu
   */
  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email })
      .pipe(catchError(this.handleError));
  }

  /**
   * Reset mật khẩu
   */
  resetPassword(email: string, otp: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, {
      email,
      otp,
      newPassword
    }).pipe(catchError(this.handleError));
  }

  /**
   * Lấy thông tin user hiện tại
   */
  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`)
      .pipe(catchError(this.handleError));
  }

  // ========== UTILITY METHODS ==========

  /**
   * Lưu thông tin authentication
   */
  private setAuthData(authResponse: AuthResponse): void {
    localStorage.setItem('accessToken', authResponse.accessToken);
    localStorage.setItem('refreshToken', authResponse.refreshToken);
    localStorage.setItem('currentUser', JSON.stringify(authResponse.user));
    this.currentUserSubject.next(authResponse.user);
  }

  /**
   * Xóa thông tin authentication
   */
  private clearAuthData(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  /**
   * Load user từ localStorage
   */
  private loadUserFromStorage(): void {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        this.currentUserSubject.next(user);
      } catch (error) {
        console.error('Error parsing stored user:', error);
        this.clearAuthData();
      }
    }
  }

  /**
   * Kiểm tra đăng nhập
   */
  isLoggedIn(): boolean {
    const token = this.getAccessToken();
    return token != null && !this.isTokenExpired(token);
  }

  /**
   * Kiểm tra token hết hạn
   */
  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      return payload.exp < currentTime;
    } catch (error) {
      console.error('Error parsing token:', error);
      return true;
    }
  }

  /**
   * Kiểm tra token sắp hết hạn (trong 5 phút)
   */
  isTokenExpiringSoon(token?: string): boolean {
    const accessToken = token || this.getAccessToken();
    if (!accessToken) return false;

    try {
      const payload = JSON.parse(atob(accessToken.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);
      const timeUntilExpiry = payload.exp - currentTime;
      return timeUntilExpiry < 300; // 5 minutes
    } catch (error) {
      return true;
    }
  }

  /**
   * Lấy access token
   */
  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  /**
   * Lấy refresh token
   */
  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  /**
   * Lấy thông tin user hiện tại từ BehaviorSubject
   */
  getCurrentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Kiểm tra role
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUserValue();
    if (!user || !user.chucVu) return false;
    return user.chucVu.ten === role;
  }

  /**
   * Kiểm tra có một trong các roles
   */
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  /**
   * Kiểm tra là admin
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Kiểm tra là nhân viên
   */
  isNhanVien(): boolean {
    return this.hasAnyRole(['ADMIN', 'NHAN_VIEN']);
  }

  /**
   * Kiểm tra là khách hàng
   */
  isKhachHang(): boolean {
    return this.hasRole('KHACH_HANG');
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: any): Observable<never> {
    let errorMessage = 'Có lỗi xảy ra';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    console.error('Auth Service Error:', error);
    return throwError(errorMessage);
  }
}

// Import statements for RxJS operators
import { filter, take, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';