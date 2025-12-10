package datn.be.mycode.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = "datn.be.mycode")
public class SocketIOConfig {

    @Value("${socketio.host:0.0.0.0}")
    private String host;

    @Value("${socketio.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer(JwtTokenProvider jwtTokenProvider) {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        
        // Cho phép CORS - cho phép tất cả origins
        config.setOrigin("*");
        
        // Cho phép custom headers (để gửi JWT token)
        config.setAllowCustomRequests(true);
        
        // Tạo SocketIOServer
        SocketIOServer server = new SocketIOServer(config);
        
        // Thêm authentication listener
        server.addConnectListener(client -> {
            System.out.println("🔌 [Socket.IO] Client connecting: " + client.getSessionId());
            
            // Lấy token từ query parameter
            String token = client.getHandshakeData().getSingleUrlParam("token");
            
            // Nếu không có trong query, thử lấy từ headers
            if (token == null || token.isEmpty()) {
                String authHeader = client.getHandshakeData().getHttpHeaders().get("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
            
            System.out.println("🔐 [Socket.IO] Token received: " + (token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token));
            
            // Validate token
            if (token != null && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
                // Token hợp lệ - cho phép kết nối
                System.out.println("✅ [Socket.IO] Authentication successful for client: " + client.getSessionId());
                
                // Lưu thông tin user vào client session
                var authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    client.set("username", authentication.getName());
                    client.set("authorities", authentication.getAuthorities().toString());
                    System.out.println("✅ [Socket.IO] User: " + authentication.getName() + ", Authorities: " + authentication.getAuthorities());
                }
            } else {
                // Token không hợp lệ - reject connection
                System.err.println("❌ [Socket.IO] Invalid or missing token, rejecting connection");
                client.disconnect();
            }
        });
        
        // Log khi client disconnect
        server.addDisconnectListener(client -> {
            System.out.println("🔌 [Socket.IO] Client disconnected: " + client.getSessionId());
        });
        
        System.out.println("✅ [Socket.IO] Server configured on " + host + ":" + port);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
