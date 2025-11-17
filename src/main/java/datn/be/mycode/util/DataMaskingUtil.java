package datn.be.mycode.util;

public class DataMaskingUtil {
    
    /**
     * Che giấu email, chỉ hiển thị 2 ký tự đầu và domain
     * Ví dụ: example@gmail.com -> ex****@gmail.com
     */
    public static String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return "***";
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }
        
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return "***@" + domain;
        }
        
        return username.substring(0, 2) + "****@" + domain;
    }
    
    /**
     * Che giấu số điện thoại, chỉ hiển thị 3 số cuối
     * Ví dụ: 0123456789 -> *******789
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        
        int length = phoneNumber.length();
        String masked = "*".repeat(length - 3);
        return masked + phoneNumber.substring(length - 3);
    }
    
    /**
     * Che giấu địa chỉ, chỉ hiển thị thành phố
     */
    public static String maskAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "***";
        }
        
        // Tách địa chỉ theo dấu phẩy và lấy phần cuối (thường là thành phố)
        String[] parts = address.split(",");
        if (parts.length > 1) {
            return "*** " + parts[parts.length - 1].trim();
        }
        
        return "***";
    }
    
    /**
     * Che giấu tên, chỉ hiển thị họ và ký tự đầu của tên
     * Ví dụ: Nguyễn Văn An -> Nguyễn V***
     */
    public static String maskName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "***";
        }
        
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].charAt(0) + "***";
        }
        
        StringBuilder masked = new StringBuilder();
        // Giữ nguyên họ
        masked.append(parts[0]);
        
        // Che giấu tên đệm và tên
        for (int i = 1; i < parts.length; i++) {
            masked.append(" ");
            if (i == parts.length - 1) {
                // Tên cuối: hiển thị ký tự đầu + ***
                masked.append(parts[i].charAt(0)).append("***");
            } else {
                // Tên đệm: chỉ hiển thị ký tự đầu
                masked.append(parts[i].charAt(0)).append("***");
            }
        }
        
        return masked.toString();
    }
}