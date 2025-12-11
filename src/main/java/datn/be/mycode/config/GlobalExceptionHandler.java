package datn.be.mycode.config;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - Xử lý tất cả exceptions trong application
 * Đảm bảo API responses consistent và user-friendly
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý validation errors (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "Dữ liệu không hợp lệ");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());

        logger.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Xử lý EntityNotFoundException (404 Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Không tìm thấy tài nguyên");
        response.put("timestamp", LocalDateTime.now());

        logger.warn("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Xử lý AccessDeniedException (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Forbidden");
        response.put("message", "Bạn không có quyền truy cập tài nguyên này");
        response.put("timestamp", LocalDateTime.now());

        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Xử lý MethodArgumentTypeMismatchException (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "Tham số không hợp lệ: " + ex.getName());
        response.put("timestamp", LocalDateTime.now());

        logger.warn("Type mismatch: {} = {}", ex.getName(), ex.getValue());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Xử lý IllegalArgumentException (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Tham số không hợp lệ");
        response.put("timestamp", LocalDateTime.now());

        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Xử lý tất cả exceptions còn lại (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"H\",\"location\":\"GlobalExceptionHandler.java:handleGenericException\",\"message\":\"500 Internal Server Error\",\"data\":{\"exception\":\"" + ex.getClass().getName() + "\",\"message\":\"" + (ex.getMessage() != null ? ex.getMessage().replace("\"", "\\\"") : "null") + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        response.put("timestamp", LocalDateTime.now());
        // Đảm bảo có field 'data' để frontend không bị lỗi .length
        response.put("data", new java.util.ArrayList<>());

        // Log full exception với stack trace
        logger.error("Unexpected error occurred", ex);
        System.err.println("❌ [GlobalExceptionHandler] Exception: " + ex.getClass().getName());
        System.err.println("❌ [GlobalExceptionHandler] Message: " + ex.getMessage());
        ex.printStackTrace();

        // Trong production, không trả về stack trace cho client
        // Chỉ trả về message chung
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Note: erroResponse is not an Exception, so it cannot be handled by @ExceptionHandler
    // If you need to return erroResponse, do it directly in controllers
}

