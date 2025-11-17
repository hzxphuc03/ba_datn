package datn.be.mycode.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility để hash mật khẩu hiện có trong database
 * Chạy một lần để migrate từ plain text sang BCrypt
 */
public class PasswordMigrationUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    public static void main(String[] args) {
        // Hash các mật khẩu phổ biến
        System.out.println("Common password hashes:");
        System.out.println("123456: " + passwordEncoder.encode("123456"));
        System.out.println("password: " + passwordEncoder.encode("password"));
        System.out.println("admin: " + passwordEncoder.encode("admin"));
        System.out.println("user: " + passwordEncoder.encode("user"));
        System.out.println("123: " + passwordEncoder.encode("123"));
        System.out.println("qwerty: " + passwordEncoder.encode("qwerty"));
        
        // Test verification
        String plainPassword = "123456";
        String hashedPassword = passwordEncoder.encode(plainPassword);
        boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
        
        System.out.println("\nVerification test:");
        System.out.println("Plain: " + plainPassword);
        System.out.println("Hash: " + hashedPassword);
        System.out.println("Matches: " + matches);
    }
    
    /**
     * Hash một mật khẩu cụ thể
     */
    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Kiểm tra mật khẩu
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}