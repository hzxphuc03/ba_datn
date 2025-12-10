# Hướng dẫn kết nối WebSocket với JWT Authentication

## Vấn đề đã fix

✅ **Socket.io 401 Unauthorized** - Đã thêm JWT authentication cho WebSocket

## Backend đã cấu hình

Backend sử dụng **STOMP over WebSocket** (Spring WebSocket), không phải Socket.io.

- **Endpoint**: `/ws`
- **Yêu cầu**: JWT token trong header khi kết nối

## Cách Frontend kết nối

### Option 1: Sử dụng STOMP Client (Khuyến nghị)

#### Cài đặt:
```bash
npm install @stomp/stompjs sockjs-client
```

#### Code TypeScript/Angular:

```typescript
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

export class WebSocketService {
  private client: Client;
  private token: string;

  constructor() {
    // Lấy token từ localStorage hoặc auth service
    this.token = localStorage.getItem('accessToken') || '';
    
    this.client = new Client({
      webSocketFactory: () => {
        return new SockJS('https://ba-datn-1.onrender.com/ws');
      },
      connectHeaders: {
        Authorization: `Bearer ${this.token}` // QUAN TRỌNG: Gửi token trong header
      },
      debug: function (str) {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('✅ WebSocket Connected: ' + frame);
      
      // Subscribe to topics sau khi kết nối thành công
      this.client.subscribe('/topic/messages', (message) => {
        console.log('Message received: ' + message.body);
        // Xử lý message
      });
    };

    this.client.onStompError = (frame) => {
      console.error('❌ WebSocket Error: ' + frame.headers['message']);
      console.error('Error details: ' + frame.body);
    };

    this.client.onDisconnect = () => {
      console.log('WebSocket Disconnected');
    };
  }

  connect() {
    if (!this.client.active) {
      this.client.activate();
    }
  }

  disconnect() {
    if (this.client.active) {
      this.client.deactivate();
    }
  }

  sendMessage(destination: string, body: any) {
    if (this.client.active) {
      this.client.publish({
        destination: destination,
        body: JSON.stringify(body)
      });
    }
  }

  // Update token khi user đăng nhập lại
  updateToken(newToken: string) {
    this.token = newToken;
    this.disconnect();
    this.client.configure({
      connectHeaders: {
        Authorization: `Bearer ${this.token}`
      }
    });
    this.connect();
  }
}
```

### Option 2: Nếu Frontend đang dùng Socket.io

**Vấn đề**: Frontend đang cố kết nối đến `/socket.io/` nhưng backend chỉ có STOMP endpoint `/ws`.

**Giải pháp**:

#### A. Chuyển Frontend sang STOMP (Khuyến nghị)
- Sử dụng code ở Option 1

#### B. Thêm Socket.io Server vào Backend

1. **Uncomment dependency trong `pom.xml`**:
```xml
<dependency>
    <groupId>io.socket</groupId>
    <artifactId>socket.io-server</artifactId>
    <version>4.0.1</version>
</dependency>
```

2. **Tạo Socket.io Configuration** (tôi có thể tạo file này nếu cần)

## Kiểm tra Authentication

Khi kết nối thành công, bạn sẽ thấy log trong backend console:
```
🔐 [WebSocket] Token received: eyJhbGciOiJIUzI1NiIs...
✅ [WebSocket] Authentication successful for user: username
✅ [WebSocket] User authorities: [ROLE_ADMIN]
```

Nếu không có token hoặc token invalid:
```
❌ [WebSocket] No Authorization header found in WebSocket connection
❌ [WebSocket] Invalid or missing token
```

## Lưu ý quan trọng

1. **Token phải được gửi trong header `Authorization`** với format: `Bearer <token>`
2. **Token phải hợp lệ** (chưa hết hạn, đúng signature)
3. **Sau khi đăng nhập**, lưu token vào localStorage và dùng để kết nối WebSocket
4. **Khi token hết hạn**, cần refresh token và reconnect WebSocket

## Test kết nối

1. Đăng nhập và lấy `accessToken` từ response
2. Lưu token vào localStorage
3. Kết nối WebSocket với token trong header
4. Kiểm tra console log để xem authentication có thành công không

