// Angular Route Guards for JWT Authentication
import { Injectable } from '@angular/core';
import { 
  CanActivate, 
  CanActivateChild,
  CanLoad,
  Router, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot,
  Route,
  UrlSegment
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Auth Guard - Kiểm tra đăng nhập
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild, CanLoad {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | Observable<boolean> {
    return this.checkAuth(route, state.url);
  }

  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | Observable<boolean> {
    return this.canActivate(route, state);
  }

  canLoad(
    route: Route,
    segments: UrlSegment[]
  ): boolean | Observable<boolean> {
    const url = segments.map(segment => segment.path).join('/');
    return this.checkAuth(route, `/${url}`);
  }

  private checkAuth(route: ActivatedRouteSnapshot | Route, url: string): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: url } 
      });
      return false;
    }

    // Kiểm tra role nếu có yêu cầu
    const requiredRoles = route.data?.['roles'] as string[];
    if (requiredRoles && !this.authService.hasAnyRole(requiredRoles)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

/**
 * Role Guard - Kiểm tra quyền truy cập theo role
 */
@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const requiredRoles = route.data['roles'] as string[];
    
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!this.authService.hasAnyRole(requiredRoles)) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

/**
 * Admin Guard - Chỉ cho phép Admin truy cập
 */
@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!this.authService.isAdmin()) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

/**
 * Staff Guard - Cho phép Admin và Nhân viên truy cập
 */
@Injectable({
  providedIn: 'root'
})
export class StaffGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!this.authService.isNhanVien()) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

/**
 * Customer Guard - Chỉ cho phép Khách hàng truy cập
 */
@Injectable({
  providedIn: 'root'
})
export class CustomerGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (!this.authService.isKhachHang()) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }
}

/**
 * Guest Guard - Chỉ cho phép user chưa đăng nhập truy cập
 */
@Injectable({
  providedIn: 'root'
})
export class GuestGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      // Redirect based on user role
      const user = this.authService.getCurrentUserValue();
      if (user?.chucVu?.ten === 'ADMIN') {
        this.router.navigate(['/admin']);
      } else if (user?.chucVu?.ten === 'NHAN_VIEN') {
        this.router.navigate(['/nhan-vien']);
      } else {
        this.router.navigate(['/khach-hang']);
      }
      return false;
    }

    return true;
  }
}

/**
 * Permission Guard - Kiểm tra quyền chi tiết
 */
@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredPermissions = route.data['permissions'] as string[];
    
    if (!requiredPermissions || requiredPermissions.length === 0) {
      return true;
    }

    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    // TODO: Implement permission checking logic
    // This would require adding permissions to user object
    const user = this.authService.getCurrentUserValue();
    
    // Example permission check
    const hasPermission = this.checkUserPermissions(user, requiredPermissions);
    
    if (!hasPermission) {
      this.router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  }

  private checkUserPermissions(user: any, requiredPermissions: string[]): boolean {
    // TODO: Implement actual permission checking logic
    // This is just an example
    
    if (user?.chucVu?.ten === 'ADMIN') {
      return true; // Admin has all permissions
    }

    // Check specific permissions for other roles
    // This would depend on your permission system design
    
    return false;
  }
}

/**
 * Route Configuration Examples
 */
export const ROUTE_EXAMPLES = {
  // Public routes
  public: {
    path: 'login',
    component: 'LoginComponent',
    canActivate: [GuestGuard]
  },

  // Protected routes with role checking
  admin: {
    path: 'admin',
    component: 'AdminComponent',
    canActivate: [AuthGuard, AdminGuard],
    data: { roles: ['ADMIN'] }
  },

  staff: {
    path: 'nhan-vien',
    component: 'StaffComponent',
    canActivate: [AuthGuard, StaffGuard],
    data: { roles: ['ADMIN', 'NHAN_VIEN'] }
  },

  customer: {
    path: 'khach-hang',
    component: 'CustomerComponent',
    canActivate: [AuthGuard, CustomerGuard],
    data: { roles: ['KHACH_HANG'] }
  },

  // Lazy loaded modules with guards
  adminModule: {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canLoad: [AuthGuard],
    data: { roles: ['ADMIN'] }
  }
};