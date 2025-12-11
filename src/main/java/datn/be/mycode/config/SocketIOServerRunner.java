package datn.be.mycode.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(SocketIOServer.class)
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    @Value("${socketio.enabled:true}")
    private Boolean socketioEnabled;

    @Autowired(required = false)
    public SocketIOServerRunner(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {
        if (socketIOServer == null) {
            System.out.println("⚠️ [Socket.IO] Server bean not created (socketio.enabled=false or missing)");
            return;
        }
        
        if (!socketioEnabled) {
            System.out.println("⚠️ [Socket.IO] Server disabled (socketio.enabled=false)");
            return;
        }
        
        try {
            socketIOServer.start();
            System.out.println("🚀 [Socket.IO] Server started successfully");
            System.out.println("🚀 [Socket.IO] Listening on " + socketIOServer.getConfiguration().getHostname() + 
                             ":" + socketIOServer.getConfiguration().getPort());
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
                fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"F\",\"location\":\"SocketIOServerRunner.java:run\",\"message\":\"Socket.IO server started\",\"data\":{\"host\":\"" + socketIOServer.getConfiguration().getHostname() + "\",\"port\":" + socketIOServer.getConfiguration().getPort() + "},\"timestamp\":" + System.currentTimeMillis() + "}\n");
                fw.close();
            } catch (Exception e) {}
            // #endregion
        } catch (Exception e) {
            System.err.println("❌ [Socket.IO] Failed to start server: " + e.getMessage());
            e.printStackTrace();
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("/Users/phamhaiphuc/Downloads/DATN/BE - Copy/ba_datn/.cursor/debug.log", true);
                fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"F\",\"location\":\"SocketIOServerRunner.java:run\",\"message\":\"Socket.IO server failed to start\",\"data\":{\"error\":\"" + e.getMessage() + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
                fw.close();
            } catch (Exception ex) {}
            // #endregion
        }
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (socketIOServer != null && socketioEnabled) {
            try {
                socketIOServer.stop();
                System.out.println("🛑 [Socket.IO] Server stopped");
            } catch (Exception e) {
                System.err.println("❌ [Socket.IO] Error stopping server: " + e.getMessage());
            }
        }
    }
}
