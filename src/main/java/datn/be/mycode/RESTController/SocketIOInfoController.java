package datn.be.mycode.RESTController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller để handle request đến /socket.io/ khi Socket.IO server bị disable
 * Trả về thông báo rõ ràng cho frontend
 */
@RestController
@RequestMapping("/socket.io")
public class SocketIOInfoController {
    
    @Value("${socketio.enabled:false}")
    private Boolean socketioEnabled;
    
    @Value("${socketio.port:9092}")
    private Integer socketioPort;
    
    /**
     * Handle tất cả request đến /socket.io/**
     * Trả về thông báo về trạng thái Socket.IO server
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    public ResponseEntity<?> handleSocketIORequest(@RequestHeader(value = "Origin", required = false) String origin) {
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"G\",\"location\":\"SocketIOInfoController.java:handleSocketIORequest\",\"message\":\"Socket.IO request received\",\"data\":{\"enabled\":" + socketioEnabled + ",\"port\":" + socketioPort + ",\"origin\":\"" + (origin != null ? origin : "null") + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        
        System.out.println("🔍 [SocketIOInfoController] Request to /socket.io/ received");
        System.out.println("🔍 [SocketIOInfoController] Socket.IO enabled: " + socketioEnabled);
        System.out.println("🔍 [SocketIOInfoController] Origin: " + origin);
        
        Map<String, Object> response = new HashMap<>();
        
        // Socket.IO is disabled on production (Render only exposes 1 port)
        // Return 200 with info message instead of error to avoid frontend retry
        response.put("code", socketioEnabled ? 400 : 503);
        response.put("message", socketioEnabled 
            ? "Socket.IO server runs on separate port " + socketioPort + ", not on HTTP port 8080. Please use STOMP WebSocket at /ws instead."
            : "Socket.IO server is disabled on production. Please use STOMP WebSocket at /ws instead.");
        response.put("socketioEnabled", socketioEnabled);
        response.put("alternative", Map.of(
            "type", "STOMP WebSocket",
            "endpoint", "/ws",
            "documentation", "See WEBSOCKET_AUTH_GUIDE.md for connection details"
        ));
        
        if (!socketioEnabled) {
            System.err.println("❌ [SocketIOInfoController] Socket.IO is disabled. Frontend should use STOMP WebSocket at /ws");
            // Return 200 to stop frontend from retrying
            return ResponseEntity.ok(response);
        } else {
            System.err.println("⚠️ [SocketIOInfoController] Socket.IO enabled but request came to HTTP port. Should connect to port " + socketioPort);
            // Return 200 to stop frontend from retrying
            return ResponseEntity.ok(response);
        }
    }
}

