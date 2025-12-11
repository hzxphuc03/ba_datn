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
        
        if (!socketioEnabled) {
            response.put("status", 503);
            response.put("error", "Service Unavailable");
            response.put("message", "Socket.IO server is disabled on production. Please use STOMP WebSocket instead.");
            response.put("socketioEnabled", false);
            response.put("alternative", Map.of(
                "type", "STOMP WebSocket",
                "endpoint", "/ws",
                "documentation", "See WEBSOCKET_AUTH_GUIDE.md for connection details"
            ));
            
            System.err.println("❌ [SocketIOInfoController] Socket.IO is disabled. Frontend should use STOMP WebSocket at /ws");
            return ResponseEntity.status(503).body(response);
        } else {
            // Socket.IO enabled nhưng request đến port 8080 thay vì 9092
            response.put("status", 400);
            response.put("error", "Bad Request");
            response.put("message", "Socket.IO server runs on separate port " + socketioPort + ", not on HTTP port 8080");
            response.put("socketioEnabled", true);
            response.put("socketioPort", socketioPort);
            response.put("hint", "Connect to: ws://your-backend:" + socketioPort + " with token in query parameter or Authorization header");
            
            System.err.println("⚠️ [SocketIOInfoController] Socket.IO enabled but request came to HTTP port. Should connect to port " + socketioPort);
            return ResponseEntity.status(400).body(response);
        }
    }
}

