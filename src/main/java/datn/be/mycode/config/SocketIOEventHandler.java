package datn.be.mycode.config;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SocketIOEventHandler {

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String username = client.get("username");
        String role = client.get("role");
        System.out.println("✅ [Socket.IO Event] Client connected: " + client.getSessionId() + 
                          (username != null ? " (user: " + username + ", role: " + role + ")" : ""));
        
        // Gửi welcome message
        client.sendEvent("welcome", Map.of(
            "message", "Connected successfully",
            "sessionId", client.getSessionId(),
            "username", username != null ? username : "unknown"
        ));
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String username = client.get("username");
        System.out.println("🔌 [Socket.IO Event] Client disconnected: " + client.getSessionId() + 
                          (username != null ? " (user: " + username + ")" : ""));
    }

    /**
     * Xử lý event "message" từ client
     * Client gửi: socket.emit("message", { content: "Hello" })
     */
    @OnEvent("message")
    public void onMessage(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        String username = client.get("username");
        String content = (String) data.get("content");
        
        System.out.println("📨 [Socket.IO Event] Message received from " + username + ": " + content);
        
        // Echo message back to client
        client.sendEvent("message", Map.of(
            "from", "server",
            "content", "Echo: " + content,
            "timestamp", System.currentTimeMillis()
        ));
        
        // Send acknowledgment nếu client yêu cầu
        if (ackRequest != null) {
            ackRequest.sendAckData(Map.of("status", "received"));
        }
    }

    /**
     * Xử lý event "join-room" từ client
     * Client gửi: socket.emit("join-room", { room: "room1" })
     */
    @OnEvent("join-room")
    public void onJoinRoom(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        String username = client.get("username");
        String room = (String) data.get("room");
        
        if (room != null) {
            client.joinRoom(room);
            System.out.println("🏠 [Socket.IO Event] User " + username + " joined room: " + room);
            
            // Broadcast to room
            client.getNamespace().getRoomOperations(room).sendEvent("room-joined", Map.of(
                "username", username,
                "room", room,
                "message", username + " joined the room"
            ));
            
            if (ackRequest != null) {
                ackRequest.sendAckData(Map.of("status", "joined", "room", room));
            }
        }
    }

    /**
     * Xử lý event "leave-room" từ client
     */
    @OnEvent("leave-room")
    public void onLeaveRoom(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        String username = client.get("username");
        String room = (String) data.get("room");
        
        if (room != null) {
            client.leaveRoom(room);
            System.out.println("🚪 [Socket.IO Event] User " + username + " left room: " + room);
            
            if (ackRequest != null) {
                ackRequest.sendAckData(Map.of("status", "left", "room", room));
            }
        }
    }

    /**
     * Xử lý event "notification" từ client
     */
    @OnEvent("notification")
    public void onNotification(SocketIOClient client, Map<String, Object> data, AckRequest ackRequest) {
        String username = client.get("username");
        String message = (String) data.get("message");
        
        System.out.println("🔔 [Socket.IO Event] Notification from " + username + ": " + message);
        
        // Broadcast notification to all connected clients
        client.getNamespace().getBroadcastOperations().sendEvent("notification", Map.of(
            "from", username,
            "message", message,
            "timestamp", System.currentTimeMillis()
        ));
        
        if (ackRequest != null) {
            ackRequest.sendAckData(Map.of("status", "broadcasted"));
        }
    }
}

