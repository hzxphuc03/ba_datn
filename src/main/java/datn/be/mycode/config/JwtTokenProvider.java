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
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT validation failed: " + e.getMessage());
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
     */
    private String mapRoleToSpringRole(String dbRole) {
        if (dbRole == null) return "ROLE_KHACH_HANG";
        
        String role = dbRole.trim();
        // Case-insensitive check
        String roleLower = role.toLowerCase();
        
        if (roleLower.contains("admin") || role.equals("ADMIN")) {
            return "ROLE_ADMIN";
        } else if (roleLower.contains("nhân viên") || roleLower.contains("nhan vien") 
                   || roleLower.contains("quản lý") || roleLower.contains("quan ly")
                   || role.equals("NHAN_VIEN")) {
            return "ROLE_NHAN_VIEN";
        } else if (roleLower.contains("khách hàng") || roleLower.contains("khach hang")
                   || role.equals("KHACH_HANG") || role.equals("KhachHang")) {
            return "ROLE_KHACH_HANG";
        }
        
        // Default fallback
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
            String role = claims.get("role", String.class);
            
            System.out.println("🔐 JWT getAuthentication - Username: " + username + ", Role from token: " + role);
            
            if (role != null) {
                String authority = mapRoleToSpringRole(role);
                System.out.println("🔐 Mapped authority: " + authority);
                List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(authority));
                return new UsernamePasswordAuthenticationToken(username, null, authorities);
            }
            
            System.out.println("⚠️ No role found in token, using empty authorities");
            return new UsernamePasswordAuthenticationToken(username, null, Arrays.asList());
        } catch (Exception e) {
            System.err.println("❌ Error in getAuthentication: " + e.getMessage());
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
