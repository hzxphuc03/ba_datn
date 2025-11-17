package datn.be.mycode.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS = 3;
    
    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentHashMap<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static class OtpData {
        String otp;
        LocalDateTime expiryTime;
        int attempts;
        
        OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
            this.attempts = 0;
        }
    }
    
    /**
     * Tạo OTP bảo mật
     */
    public String generateOtp(String identifier) {
        // Tạo OTP 6 chữ số ngẫu nhiên
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        
        String otpCode = otp.toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Lưu OTP với thời gian hết hạn
        otpStorage.put(identifier, new OtpData(otpCode, expiryTime));
        
        return otpCode;
    }
    
    /**
     * Xác thực OTP
     */
    public boolean verifyOtp(String identifier, String inputOtp) {
        OtpData otpData = otpStorage.get(identifier);
        
        if (otpData == null) {
            return false; // OTP không tồn tại
        }
        
        // Kiểm tra số lần thử
        if (otpData.attempts >= MAX_OTP_ATTEMPTS) {
            otpStorage.remove(identifier);
            return false; // Quá số lần thử
        }
        
        // Kiểm tra thời gian hết hạn
        if (LocalDateTime.now().isAfter(otpData.expiryTime)) {
            otpStorage.remove(identifier);
            return false; // OTP đã hết hạn
        }
        
        // Tăng số lần thử
        otpData.attempts++;
        
        // Kiểm tra OTP
        if (otpData.otp.equals(inputOtp)) {
            otpStorage.remove(identifier); // Xóa OTP sau khi xác thực thành công
            return true;
        }
        
        return false; // OTP không đúng
    }
    
    /**
     * Xóa OTP
     */
    public void removeOtp(String identifier) {
        otpStorage.remove(identifier);
    }
    
    /**
     * Kiểm tra OTP có tồn tại không
     */
    public boolean hasValidOtp(String identifier) {
        OtpData otpData = otpStorage.get(identifier);
        return otpData != null && LocalDateTime.now().isBefore(otpData.expiryTime);
    }
    
    /**
     * Lấy thời gian còn lại của OTP (giây)
     */
    public long getOtpRemainingTime(String identifier) {
        OtpData otpData = otpStorage.get(identifier);
        if (otpData == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(otpData.expiryTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, otpData.expiryTime).getSeconds();
    }
    
    /**
     * Dọn dẹp OTP hết hạn (chạy định kỳ)
     */
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiryTime));
    }
}