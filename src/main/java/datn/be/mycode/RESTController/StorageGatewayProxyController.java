package datn.be.mycode.RESTController;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy controller để forward upload request đến fsel-gateway
 * Chuyển đổi từ application/json sang multipart/form-data
 */
@RestController
@RequestMapping("/api/storage-gateway")
@CrossOrigin(origins = "*")
public class StorageGatewayProxyController {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GATEWAY_URL = "https://fsel-gateway-testing-api.fsel.edu.vn/storage-gateway/v1.1/file/Images";
    
    /**
     * Proxy upload image to fsel-gateway
     * POST /api/storage-gateway/upload
     * 
     * Accepts multipart/form-data và forward đến fsel-gateway
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        try {
            // Create multipart request
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            if (authorization != null) {
                headers.set("Authorization", authorization);
            }
            
            // Create multipart entity
            org.springframework.core.io.ByteArrayResource fileResource = 
                new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
            
            org.springframework.util.LinkedMultiValueMap<String, Object> body = 
                new org.springframework.util.LinkedMultiValueMap<>();
            body.add("file", fileResource);
            
            HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);
            
            // Forward request to fsel-gateway
            ResponseEntity<Map> response = restTemplate.exchange(
                GATEWAY_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
            
            // Normalize response để đảm bảo frontend nhận được format đúng
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            if (responseBody == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Empty response from storage gateway");
                error.put("data", new java.util.ArrayList<>()); // Đảm bảo có field data là array
                return ResponseEntity.status(500).body(error);
            }
            
            // Đảm bảo response có các field cần thiết mà frontend có thể expect
            Map<String, Object> normalizedResponse = new HashMap<>(responseBody);
            
            // Nếu response không có field 'data' hoặc 'data' là null, thêm field data rỗng
            if (!normalizedResponse.containsKey("data") || normalizedResponse.get("data") == null) {
                normalizedResponse.put("data", new java.util.ArrayList<>());
            }
            
            // Đảm bảo có field success
            if (!normalizedResponse.containsKey("success")) {
                normalizedResponse.put("success", true);
            }
            
            // Log response để debug
            System.out.println("✅ [StorageGatewayProxy] Upload successful. Response keys: " + normalizedResponse.keySet());
            System.out.println("✅ [StorageGatewayProxy] Response: " + normalizedResponse);
            
            return ResponseEntity.ok(normalizedResponse);
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ [StorageGatewayProxy] HttpClientErrorException: " + e.getMessage());
            System.err.println("❌ [StorageGatewayProxy] Status: " + e.getStatusCode());
            System.err.println("❌ [StorageGatewayProxy] Response body: " + e.getResponseBodyAsString());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("status", e.getStatusCode().value());
            error.put("message", "Upload failed: " + e.getMessage());
            
            // Try to parse error response from fsel-gateway
            try {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> gatewayError = mapper.readValue(responseBody, Map.class);
                    error.putAll(gatewayError);
                }
            } catch (Exception parseEx) {
                // Ignore parse error
            }
            
            return ResponseEntity.status(e.getStatusCode()).body(error);
        } catch (Exception e) {
            System.err.println("❌ [StorageGatewayProxy] Exception: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Alternative: Accept base64 image và convert to multipart
     * POST /api/storage-gateway/upload-base64
     */
    @PostMapping("/upload-base64")
    public ResponseEntity<?> uploadImageBase64(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        try {
            String base64Image = request.get("image");
            String filename = request.getOrDefault("filename", "image.jpg");
            
            if (base64Image == null || base64Image.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Base64 image is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Remove data URL prefix if present
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            
            // Decode base64 to bytes
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
            
            // Create multipart request
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            if (authorization != null) {
                headers.set("Authorization", authorization);
            }
            
            org.springframework.core.io.ByteArrayResource fileResource = 
                new org.springframework.core.io.ByteArrayResource(imageBytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                };
            
            org.springframework.util.LinkedMultiValueMap<String, Object> body = 
                new org.springframework.util.LinkedMultiValueMap<>();
            body.add("file", fileResource);
            
            HttpEntity<org.springframework.util.LinkedMultiValueMap<String, Object>> requestEntity = 
                new HttpEntity<>(body, headers);
            
            // Forward request to fsel-gateway
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                GATEWAY_URL,
                HttpMethod.POST,
                requestEntity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            // Normalize response để đảm bảo frontend nhận được format đúng
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Empty response from storage gateway");
                error.put("data", new java.util.ArrayList<>()); // Đảm bảo có field data là array
                return ResponseEntity.status(500).body(error);
            }
            
            // Đảm bảo response có các field cần thiết mà frontend có thể expect
            Map<String, Object> normalizedResponse = new HashMap<>(responseBody);
            
            // Nếu response không có field 'data' hoặc 'data' là null, thêm field data rỗng
            if (!normalizedResponse.containsKey("data") || normalizedResponse.get("data") == null) {
                normalizedResponse.put("data", new java.util.ArrayList<>());
            }
            
            // Đảm bảo có field success
            if (!normalizedResponse.containsKey("success")) {
                normalizedResponse.put("success", true);
            }
            
            // Log response để debug
            System.out.println("✅ [StorageGatewayProxy] Upload successful (base64). Response keys: " + normalizedResponse.keySet());
            System.out.println("✅ [StorageGatewayProxy] Response: " + normalizedResponse);
            
            return ResponseEntity.ok(normalizedResponse);
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ [StorageGatewayProxy] HttpClientErrorException (base64): " + e.getMessage());
            System.err.println("❌ [StorageGatewayProxy] Status: " + e.getStatusCode());
            System.err.println("❌ [StorageGatewayProxy] Response body: " + e.getResponseBodyAsString());
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("status", e.getStatusCode().value());
            error.put("message", "Upload failed: " + e.getMessage());
            
            // Try to parse error response from fsel-gateway
            try {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody != null && !responseBody.isEmpty()) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> gatewayError = mapper.readValue(responseBody, Map.class);
                    error.putAll(gatewayError);
                }
            } catch (Exception parseEx) {
                // Ignore parse error
            }
            
            return ResponseEntity.status(e.getStatusCode()).body(error);
        } catch (Exception e) {
            System.err.println("❌ [StorageGatewayProxy] Exception (base64): " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

