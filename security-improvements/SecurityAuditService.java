package datn.be.mycode.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SecurityAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 15;
    
    // Lưu trữ số lần đăng nhập thất bại theo IP
    private final ConcurrentHashMap<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> lockoutTime = new ConcurrentHashMap<>();
    
    public void logSuccessfulLogin(String username, String clientIp, String role) {
        // Reset failed attempts on successful login
        failedAttempts.remove(clientIp);
        lockoutTime.remove(clientIp);
        
        logger.info("SUCCESSFUL_LOGIN - Username: {}, IP: {}, Role: {}, Time: {}", 
                   username, clientIp, role, LocalDateTime.now());
    }
    
    public void logFailedLogin(String username, String clientIp, String reason) {
        // Increment failed attempts
        AtomicInteger attempts = failedAttempts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();
        
        logger.warn("FAILED_LOGIN - Username: {}, IP: {}, Reason: {}, Attempts: {}, Time: {}", 
                   username, clientIp, reason, currentAttempts, LocalDateTime.now());
        
        // Lock account if too many failed attempts
        if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
            lockoutTime.put(clientIp, LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
            logger.warn("IP_LOCKED - IP: {}, Lockout until: {}", 
                       clientIp, lockoutTime.get(clientIp));
        }
    }
    
    public boolean isIpLocked(String clientIp) {
        LocalDateTime lockUntil = lockoutTime.get(clientIp);
        if (lockUntil != null && LocalDateTime.now().isBefore(lockUntil)) {
            return true;
        }
        
        // Remove expired lockout
        if (lockUntil != null && LocalDateTime.now().isAfter(lockUntil)) {
            lockoutTime.remove(clientIp);
            failedAttempts.remove(clientIp);
        }
        
        return false;
    }
    
    public void logSecurityEvent(String event, String username, String clientIp, String details) {
        logger.warn("SECURITY_EVENT - Event: {}, Username: {}, IP: {}, Details: {}, Time: {}", 
                   event, username, clientIp, details, LocalDateTime.now());
    }
    
    public void logPasswordChange(String username, String clientIp) {
        logger.info("PASSWORD_CHANGED - Username: {}, IP: {}, Time: {}", 
                   username, clientIp, LocalDateTime.now());
    }
    
    public void logSuspiciousActivity(String activity, String username, String clientIp, String details) {
        logger.error("SUSPICIOUS_ACTIVITY - Activity: {}, Username: {}, IP: {}, Details: {}, Time: {}", 
                    activity, username, clientIp, details, LocalDateTime.now());
    }
}