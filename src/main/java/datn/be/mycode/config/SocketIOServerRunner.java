package datn.be.mycode.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer socketIOServer;

    @Autowired
    public SocketIOServerRunner(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {
        try {
            socketIOServer.start();
            System.out.println("🚀 [Socket.IO] Server started successfully");
            System.out.println("🚀 [Socket.IO] Listening on " + socketIOServer.getConfiguration().getHostname() + 
                             ":" + socketIOServer.getConfiguration().getPort());
        } catch (Exception e) {
            System.err.println("❌ [Socket.IO] Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            System.out.println("🛑 [Socket.IO] Server stopped");
        }
    }
}
