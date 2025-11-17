// React JWT Authentication Hook
import { useState, useEffect, useContext, createContext, useCallback } from 'react';
import axios from 'axios';

// Auth Context
const AuthContext = createContext();

// Auth Provider Component
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Initialize auth state
  useEffect(() => {
    initializeAuth();
  }, []);

  const initializeAuth = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      const userData = localStorage.getItem('currentUser');
      
      if (token && userData && !isTokenExpired(token)) {
        setUser(JSON.parse(userData));
        setIsAuthenticated(true);
        setupAxiosInterceptors();
      } else {
        clearAuthData();
      }
    } catch (error) {
      console.error('Error initializing auth:', error);
      clearAuthData();
    } finally {
      setLoading(false);
    }
  };

  // Login functions
  const loginKhachHang = async (credentials) => {
    try {
      const response = await axios.post('/api/auth/login/khach-hang', credentials);
      handleAuthSuccess(response.data);
      return response.data;
    } catch (error) {
      throw handleAuthError(error);
    }
  };

  const loginNhanVien = async (credentials) => {
    try {
      const response = await axios.post('/api/auth/login/nhan-vien', credentials);
      handleAuthSuccess(response.data);
      return response.data;
    } catch (error) {
      throw handleAuthError(error);
    }
  };

  // Logout
  const logout = useCallback(async () => {
    try {
      await axios.post('/api/auth/logout');
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      clearAuthData();
    }
  }, []);

  // Refresh token
  const refreshToken = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('No refresh token');
      }

      const response = await axios.post('/api/auth/refresh-token', { refreshToken });
      handleAuthSuccess(response.data);
      return response.data;
    } catch (error) {
      logout();
      throw error;
    }
  };

  // Handle successful authentication
  const handleAuthSuccess = (authData) => {
    localStorage.setItem('accessToken', authData.accessToken);
    localStorage.setItem('refreshToken', authData.refreshToken);
    localStorage.setItem('currentUser', JSON.stringify(authData.user));
    
    setUser(authData.user);
    setIsAuthenticated(true);
    setupAxiosInterceptors();
  };

  // Handle authentication errors
  const handleAuthError = (error) => {
    const message = error.response?.data?.message || 'Authentication failed';
    return new Error(message);
  };

  // Clear authentication data
  const clearAuthData = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    setUser(null);
    setIsAuthenticated(false);
  };

  // Check if token is expired
  const isTokenExpired = (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  };

  // Setup Axios interceptors
  const setupAxiosInterceptors = () => {
    // Request interceptor
    axios.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor
    axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            await refreshToken();
            const token = localStorage.getItem('accessToken');
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return axios(originalRequest);
          } catch (refreshError) {
            logout();
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  };

  // Role checking functions
  const hasRole = (role) => {
    return user?.chucVu?.ten === role;
  };

  const hasAnyRole = (roles) => {
    return roles.some(role => hasRole(role));
  };

  const isAdmin = () => hasRole('ADMIN');
  const isNhanVien = () => hasAnyRole(['ADMIN', 'NHAN_VIEN']);
  const isKhachHang = () => hasRole('KHACH_HANG');

  const value = {
    user,
    loading,
    isAuthenticated,
    loginKhachHang,
    loginNhanVien,
    logout,
    refreshToken,
    hasRole,
    hasAnyRole,
    isAdmin,
    isNhanVien,
    isKhachHang
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use auth context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// Protected Route Component
export const ProtectedRoute = ({ children, roles = [] }) => {
  const { isAuthenticated, hasAnyRole, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles.length > 0 && !hasAnyRole(roles)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Login Component
export const LoginComponent = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    userType: 'khach-hang'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const { loginKhachHang, loginNhanVien } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const credentials = {
        username: formData.username,
        pass: formData.password
      };

      const loginMethod = formData.userType === 'khach-hang' 
        ? loginKhachHang 
        : loginNhanVien;

      const response = await loginMethod(credentials);
      
      // Redirect based on user role
      const user = response.user;
      if (user.chucVu?.ten === 'ADMIN') {
        navigate('/admin');
      } else if (user.chucVu?.ten === 'NHAN_VIEN') {
        navigate('/nhan-vien');
      } else {
        navigate('/khach-hang');
      }
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="login-container">
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Đăng Nhập</h2>
        
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <div className="form-group">
          <label>Loại tài khoản:</label>
          <select
            name="userType"
            value={formData.userType}
            onChange={handleChange}
            required
          >
            <option value="khach-hang">Khách hàng</option>
            <option value="nhan-vien">Nhân viên</option>
          </select>
        </div>

        <div className="form-group">
          <label>Tài khoản:</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Mật khẩu:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>
      </form>
    </div>
  );
};

// App Component with routing
export const App = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginComponent />} />
          
          <Route path="/admin/*" element={
            <ProtectedRoute roles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/nhan-vien/*" element={
            <ProtectedRoute roles={['ADMIN', 'NHAN_VIEN']}>
              <StaffDashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/khach-hang/*" element={
            <ProtectedRoute roles={['KHACH_HANG']}>
              <CustomerDashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

// Required imports (add to your package.json)
import { Navigate, useNavigate, BrowserRouter, Routes, Route } from 'react-router-dom';