package datn.be.mycode.config;

import datn.be.mycode.response.UserLoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {
    // Đọc từ environment variable hoặc application.properties
    // Đọc từ app.jwt.secret trong application.properties
    @Value("${app.jwt.secret:myVerySecretKeyThatIsAtLeast256BitsLongForHS256AlgorithmSecurity2024}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;
    
    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        // Đảm bảo key đủ mạnh (ít nhất 256 bits)
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 characters)");
        }
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role, UserLoginResponse user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Map role từ database sang format chuẩn
        String normalizedRole = mapDbRoleToTokenRole(role);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", normalizedRole);
        if (user != null) {
            claims.put("userId", user.getId());
            claims.put("user", user);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate refresh token (expires in 7 days)
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("❌ [JWT] Token is null or empty");
            return false;
        }
        
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            // Check expiration
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                System.err.println("❌ [JWT] Token has expired. Expiration: " + expiration);
                return false;
            }
            
            System.out.println("✅ [JWT] Token validated successfully");
            System.out.println("✅ [JWT] Token subject: " + claims.getSubject());
            System.out.println("✅ [JWT] Token expiration: " + expiration);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("❌ [JWT] Token expired: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.err.println("❌ [JWT] Unsupported JWT token: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("❌ [JWT] Malformed JWT token: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("❌ [JWT] JWT token is empty or null: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.err.println("❌ [JWT] JWT validation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ [JWT] Unexpected error validating token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String getRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Map role từ database sang Spring Security role format
     * Handles both token roles (ADMIN, NHAN_VIEN, KHACH_HANG) and DB roles
     */
    private String mapRoleToSpringRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            System.out.println("⚠️ [JWT] Role is null or empty, defaulting to ROLE_KHACH_HANG");
            return "ROLE_KHACH_HANG";
        }
        
        String roleTrimmed = role.trim();
        String roleUpper = roleTrimmed.toUpperCase();
        String roleLower = roleTrimmed.toLowerCase();
        
        // First, check exact matches for token roles (most common case)
        if ("ADMIN".equals(roleUpper)) {
            return "ROLE_ADMIN";
        } else if ("NHAN_VIEN".equals(roleUpper) || "NHANVIEN".equals(roleUpper)) {
            return "ROLE_NHAN_VIEN";
        } else if ("KHACH_HANG".equals(roleUpper) || "KHACHHANG".equals(roleUpper)) {
            return "ROLE_KHACH_HANG";
        }
        
        // Then check for partial matches (for DB roles with Vietnamese text)
        if (roleLower.contains("admin")) {
            return "ROLE_ADMIN";
        } else if (roleLower.contains("nhân viên") || roleLower.contains("nhan vien") 
                   || roleLower.contains("quản lý") || roleLower.contains("quan ly")
                   || roleLower.contains("nhanvien")) {
            return "ROLE_NHAN_VIEN";
        } else if (roleLower.contains("khách hàng") || roleLower.contains("khach hang")
                   || roleLower.contains("khachhang")) {
            return "ROLE_KHACH_HANG";
        }
        
        // Default fallback
        System.out.println("⚠️ [JWT] Unknown role format: '" + roleTrimmed + "', defaulting to ROLE_KHACH_HANG");
        return "ROLE_KHACH_HANG";
    }

    /**
     * Map role từ DB sang format chuẩn để lưu trong token
     */
    public String mapDbRoleToTokenRole(String dbRole) {
        if (dbRole == null) return "KHACH_HANG";
        
        String role = dbRole.trim().toLowerCase();
        
        if (role.contains("admin")) {
            return "ADMIN";
        } else if (role.contains("nhân viên") || role.contains("nhan vien") 
                   || role.contains("quản lý") || role.contains("quan ly")) {
            return "NHAN_VIEN";
        } else {
            return "KHACH_HANG";
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            String username = claims.getSubject();
            if (username == null || username.isEmpty()) {
                System.err.println("❌ [JWT] Token has no subject (username)");
                return null;
            }
            
            String role = claims.get("role", String.class);
            
            System.out.println("🔐 [JWT] getAuthentication - Username: " + username + ", Role from token: " + role);
            
            // Always create authorities, even if role is null (default to KHACH_HANG)
            String authority = mapRoleToSpringRole(role);
            System.out.println("🔐 [JWT] Mapped authority: " + authority);
            
            List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(authority));
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            
            System.out.println("✅ [JWT] Authentication created successfully for user: " + username + " with role: " + authority);
            return auth;
        } catch (JwtException e) {
            System.err.println("❌ [JWT] JWT Exception in getAuthentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("❌ [JWT] Error in getAuthentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String getAuthorizationHeader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return "Bearer " + authentication.getCredentials();
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
