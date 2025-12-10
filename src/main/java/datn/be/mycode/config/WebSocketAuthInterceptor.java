package datn.be.mycode.config;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketAuthInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Lấy token từ header
            List<String> tokenList = accessor.getNativeHeader("Authorization");
            
            if (tokenList != null && !tokenList.isEmpty()) {
                String token = tokenList.get(0);
                
                // Remove "Bearer " prefix nếu có
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                
                System.out.println("🔐 [WebSocket] Token received: " + (token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token));
                
                // Validate token
                if (token != null && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    
                    if (authentication != null) {
                        // Set authentication vào WebSocket session
                        accessor.setUser(authentication);
                        System.out.println("✅ [WebSocket] Authentication successful for user: " + authentication.getName());
                        System.out.println("✅ [WebSocket] User authorities: " + authentication.getAuthorities());
                    } else {
                        System.err.println("❌ [WebSocket] Authentication is null after token validation");
                    }
                } else {
                    System.err.println("❌ [WebSocket] Invalid or missing token");
                }
            } else {
                System.err.println("❌ [WebSocket] No Authorization header found in WebSocket connection");
            }
        }
        
        return message;
    }
}

