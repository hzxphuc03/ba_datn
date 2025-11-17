// Angular Login Component
import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService, LoginRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  loading = false;
  error = '';
  returnUrl = '';
  showPassword = false;
  
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.createLoginForm();
  }

  ngOnInit(): void {
    // Get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    
    // Clear any existing error messages
    this.error = '';
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Tạo form đăng nhập
   */
  private createLoginForm(): FormGroup {
    return this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(6)
      ]],
      userType: ['khach-hang', Validators.required],
      rememberMe: [false]
    });
  }

  /**
   * Xử lý submit form
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    this.loading = true;
    this.error = '';

    const credentials: LoginRequest = {
      username: this.loginForm.value.username.trim(),
      pass: this.loginForm.value.password
    };

    const userType = this.loginForm.value.userType;
    const loginMethod = userType === 'khach-hang' 
      ? this.authService.loginKhachHang(credentials)
      : this.authService.loginNhanVien(credentials);

    loginMethod.pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (response) => {
        this.loading = false;
        this.handleLoginSuccess(response);
      },
      error: (error) => {
        this.loading = false;
        this.handleLoginError(error);
      }
    });
  }

  /**
   * Xử lý đăng nhập thành công
   */
  private handleLoginSuccess(response: any): void {
    const user = response.user;
    
    // Redirect based on return URL or user role
    if (this.returnUrl && this.returnUrl !== '/') {
      this.router.navigate([this.returnUrl]);
    } else {
      this.redirectByRole(user);
    }
  }

  /**
   * Redirect theo role của user
   */
  private redirectByRole(user: any): void {
    if (user.chucVu?.ten === 'ADMIN') {
      this.router.navigate(['/admin/dashboard']);
    } else if (user.chucVu?.ten === 'NHAN_VIEN') {
      this.router.navigate(['/nhan-vien/dashboard']);
    } else {
      this.router.navigate(['/khach-hang/dashboard']);
    }
  }

  /**
   * Xử lý lỗi đăng nhập
   */
  private handleLoginError(error: any): void {
    if (typeof error === 'string') {
      this.error = error;
    } else if (error.error?.message) {
      this.error = error.error.message;
    } else {
      this.error = 'Đăng nhập thất bại. Vui lòng thử lại.';
    }
  }

  /**
   * Toggle hiển thị mật khẩu
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Đánh dấu tất cả field đã được touch
   */
  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * Kiểm tra field có lỗi không
   */
  hasError(fieldName: string, errorType?: string): boolean {
    const field = this.loginForm.get(fieldName);
    if (!field) return false;

    if (errorType) {
      return field.hasError(errorType) && (field.dirty || field.touched);
    }
    
    return field.invalid && (field.dirty || field.touched);
  }

  /**
   * Lấy thông báo lỗi cho field
   */
  getErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (!field || !field.errors) return '';

    const errors = field.errors;

    if (errors['required']) {
      return `${this.getFieldLabel(fieldName)} là bắt buộc`;
    }
    
    if (errors['minlength']) {
      const requiredLength = errors['minlength'].requiredLength;
      return `${this.getFieldLabel(fieldName)} phải có ít nhất ${requiredLength} ký tự`;
    }
    
    if (errors['maxlength']) {
      const requiredLength = errors['maxlength'].requiredLength;
      return `${this.getFieldLabel(fieldName)} không được vượt quá ${requiredLength} ký tự`;
    }

    return 'Dữ liệu không hợp lệ';
  }

  /**
   * Lấy label của field
   */
  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      username: 'Tài khoản',
      password: 'Mật khẩu',
      userType: 'Loại tài khoản'
    };
    
    return labels[fieldName] || fieldName;
  }

  /**
   * Chuyển đến trang quên mật khẩu
   */
  goToForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }

  /**
   * Chuyển đến trang đăng ký
   */
  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}