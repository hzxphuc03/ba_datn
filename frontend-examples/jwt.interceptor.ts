// Angular JWT HTTP Interceptor
import { Injectable } from '@angular/core';
import { 
  HttpInterceptor, 
  HttpRequest, 
  HttpHandler, 
  HttpEvent,
  HttpErrorResponse 
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take, finalize } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<any>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Skip interceptor for auth endpoints
    if (this.isAuthEndpoint(req.url)) {
      return next.handle(req);
    }

    // Add access token to request
    const accessToken = this.authService.getAccessToken();
    if (accessToken) {
      req = this.addTokenToRequest(req, accessToken);
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 Unauthorized errors
        if (error.status === 401 && accessToken) {
          return this.handle401Error(req, next);
        }
        
        // Handle other errors
        return throwError(error);
      })
    );
  }

  /**
   * Thêm token vào request header
   */
  private addTokenToRequest(req: HttpRequest<any>, token: string): HttpRequest<any> {
    return req.clone({
      setHeaders: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * Xử lý lỗi 401 - Token hết hạn
   */
  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = this.authService.getRefreshToken();
      if (!refreshToken) {
        return this.handleLogout();
      }

      return this.authService.refreshToken().pipe(
        switchMap((response: any) => {
          this.refreshTokenSubject.next(response.accessToken);
          return next.handle(this.addTokenToRequest(req, response.accessToken));
        }),
        catchError((error) => {
          return this.handleLogout();
        }),
        finalize(() => {
          this.isRefreshing = false;
        })
      );
    } else {
      // Đợi refresh token hoàn thành
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => next.handle(this.addTokenToRequest(req, token)))
      );
    }
  }

  /**
   * Xử lý logout khi refresh token thất bại
   */
  private handleLogout(): Observable<never> {
    this.authService.logout().subscribe();
    this.router.navigate(['/login']);
    return throwError('Session expired. Please login again.');
  }

  /**
   * Kiểm tra có phải auth endpoint không
   */
  private isAuthEndpoint(url: string): boolean {
    const authEndpoints = [
      '/api/auth/login',
      '/api/auth/refresh-token',
      '/api/auth/forgot-password',
      '/api/auth/reset-password'
    ];
    
    return authEndpoints.some(endpoint => url.includes(endpoint));
  }
}

/**
 * HTTP Error Interceptor - Xử lý lỗi chung
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  
  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Có lỗi xảy ra';

        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = `Lỗi: ${error.error.message}`;
        } else {
          // Server-side error
          switch (error.status) {
            case 400:
              errorMessage = error.error?.message || 'Dữ liệu không hợp lệ';
              break;
            case 401:
              errorMessage = 'Bạn không có quyền truy cập';
              break;
            case 403:
              errorMessage = 'Truy cập bị từ chối';
              break;
            case 404:
              errorMessage = 'Không tìm thấy tài nguyên';
              break;
            case 500:
              errorMessage = 'Lỗi máy chủ nội bộ';
              break;
            default:
              errorMessage = error.error?.message || `Lỗi ${error.status}: ${error.statusText}`;
          }
        }

        console.error('HTTP Error:', {
          status: error.status,
          message: errorMessage,
          url: req.url,
          error: error
        });

        return throwError({
          ...error,
          userMessage: errorMessage
        });
      })
    );
  }
}

/**
 * Loading Interceptor - Hiển thị loading indicator
 */
@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  private activeRequests = 0;
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Skip loading for certain endpoints
    if (this.shouldSkipLoading(req.url)) {
      return next.handle(req);
    }

    this.activeRequests++;
    this.updateLoadingStatus();

    return next.handle(req).pipe(
      finalize(() => {
        this.activeRequests--;
        this.updateLoadingStatus();
      })
    );
  }

  private updateLoadingStatus(): void {
    this.loadingSubject.next(this.activeRequests > 0);
  }

  private shouldSkipLoading(url: string): boolean {
    const skipEndpoints = [
      '/api/auth/refresh-token'
    ];
    
    return skipEndpoints.some(endpoint => url.includes(endpoint));
  }
}

/**
 * Cache Interceptor - Cache GET requests
 */
@Injectable()
export class CacheInterceptor implements HttpInterceptor {
  private cache = new Map<string, HttpEvent<any>>();

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Only cache GET requests
    if (req.method !== 'GET') {
      return next.handle(req);
    }

    // Skip cache for certain endpoints
    if (this.shouldSkipCache(req.url)) {
      return next.handle(req);
    }

    const cachedResponse = this.cache.get(req.url);
    if (cachedResponse) {
      return of(cachedResponse);
    }

    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          this.cache.set(req.url, event);
          
          // Clear cache after 5 minutes
          setTimeout(() => {
            this.cache.delete(req.url);
          }, 5 * 60 * 1000);
        }
      })
    );
  }

  private shouldSkipCache(url: string): boolean {
    const skipEndpoints = [
      '/api/auth/',
      '/api/real-time/'
    ];
    
    return skipEndpoints.some(endpoint => url.includes(endpoint));
  }

  clearCache(): void {
    this.cache.clear();
  }
}

// Additional imports
import { tap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';