package datn.be.mycode.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        this.cloudinary = new Cloudinary(config);
    }
    
    /**
     * Upload image to Cloudinary
     * @param file MultipartFile from request
     * @return URL of uploaded image
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 10MB");
        }
        
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "datn", // Folder trong Cloudinary
                    "resource_type", "auto" // Tự động detect type
                )
            );
            
            return (String) uploadResult.get("secure_url"); // Dùng secure_url (HTTPS)
        } catch (Exception e) {
            throw new IOException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Upload multiple images
     * @param files Array of MultipartFile
     * @return Array of URLs
     */
    public String[] uploadImages(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }
        
        String[] urls = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = uploadImage(files[i]);
        }
        return urls;
    }
    
    /**
     * Delete image from Cloudinary
     * @param imageUrl URL of image to delete
     */
    public void deleteImage(String imageUrl) throws IOException {
        try {
            // Extract public_id from URL
            // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{folder}/{public_id}.{format}
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            throw new IOException("Failed to delete image from Cloudinary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract public_id from Cloudinary URL
     */
    private String extractPublicId(String url) {
        if (url == null || !url.contains("cloudinary.com")) {
            return null;
        }
        
        try {
            // Extract path after /upload/
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }
            
            String path = url.substring(uploadIndex + "/upload/".length());
            // Remove version if present (v1234567890/)
            if (path.matches("^v\\d+/.*")) {
                path = path.substring(path.indexOf("/") + 1);
            }
            
            // Remove folder prefix if present
            if (path.startsWith("datn/")) {
                path = path.substring("datn/".length());
            }
            
            // Remove file extension
            int lastDot = path.lastIndexOf(".");
            if (lastDot != -1) {
                path = path.substring(0, lastDot);
            }
            
            return "datn/" + path;
        } catch (Exception e) {
            return null;
        }
    }
}

