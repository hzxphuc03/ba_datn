# Hướng dẫn kết nối Socket.IO từ Frontend với JWT Authentication

## Backend đã cấu hình

✅ **Socket.IO Server** đã được thêm vào backend với JWT authentication
- **Port**: 9092 (mặc định)
- **Host**: 0.0.0.0 (chấp nhận kết nối từ mọi nơi)
- **Yêu cầu**: JWT token để kết nối

## Cách Frontend kết nối

### 1. Cài đặt Socket.IO Client

```bash
npm install socket.io-client
```

### 2. Code TypeScript/Angular

```typescript
import { io, Socket } from 'socket.io-client';

export class SocketIOService {
  private socket: Socket | null = null;
  private token: string = '';

  constructor() {
    // Lấy token từ localStorage hoặc auth service
    this.token = localStorage.getItem('accessToken') || '';
  }

  connect() {
    if (this.socket?.connected) {
      console.log('Already connected');
      return;
    }

    // Kết nối với token trong query parameter
    this.socket = io('https://ba-datn-1.onrender.com:9092', {
      query: {
        token: this.token // Gửi token trong query parameter
      },
      // Hoặc có thể gửi trong auth object
      auth: {
        token: this.token
      },
      // Hoặc trong extraHeaders (nếu server hỗ trợ)
      extraHeaders: {
        Authorization: `Bearer ${this.token}`
      },
      transports: ['websocket', 'polling'], // Fallback transport
      reconnection: true,
      reconnectionDelay: 1000,
      reconnectionAttempts: 5
    });

    // Event listeners
    this.socket.on('connect', () => {
      console.log('✅ Socket.IO Connected:', this.socket?.id);
    });

    this.socket.on('welcome', (data) => {
      console.log('Welcome message:', data);
    });

    this.socket.on('connect_error', (error) => {
      console.error('❌ Connection error:', error);
      if (error.message.includes('401') || error.message.includes('Unauthorized')) {
        console.error('Token invalid or expired. Please login again.');
        // Redirect to login
      }
    });

    this.socket.on('disconnect', (reason) => {
      console.log('🔌 Disconnected:', reason);
    });

    // Listen for messages
    this.socket.on('message', (data) => {
      console.log('📨 Message received:', data);
    });

    // Listen for notifications
    this.socket.on('notification', (data) => {
      console.log('🔔 Notification:', data);
    });
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }

  // Gửi message
  sendMessage(content: string) {
    if (this.socket?.connected) {
      this.socket.emit('message', { content });
    }
  }

  // Join room
  joinRoom(room: string) {
    if (this.socket?.connected) {
      this.socket.emit('join-room', { room }, (response: any) => {
        console.log('Join room response:', response);
      });
    }
  }

  // Leave room
  leaveRoom(room: string) {
    if (this.socket?.connected) {
      this.socket.emit('leave-room', { room });
    }
  }

  // Update token khi user đăng nhập lại
  updateToken(newToken: string) {
    this.token = newToken;
    this.disconnect();
    this.connect();
  }

  // Check connection status
  isConnected(): boolean {
    return this.socket?.connected || false;
  }
}
```

### 3. Sử dụng trong Component

```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { SocketIOService } from './socketio.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html'
})
export class ChatComponent implements OnInit, OnDestroy {
  
  constructor(private socketService: SocketIOService) {}

  ngOnInit() {
    // Kết nối khi component khởi tạo
    this.socketService.connect();
  }

  ngOnDestroy() {
    // Ngắt kết nối khi component bị hủy
    this.socketService.disconnect();
  }

  sendMessage() {
    this.socketService.sendMessage('Hello from Angular!');
  }
}
```

## Các cách gửi Token

Backend hỗ trợ 3 cách gửi token:

### Cách 1: Query Parameter (Khuyến nghị)
```typescript
io('https://ba-datn-1.onrender.com:9092', {
  query: {
    token: 'your_jwt_token_here'
  }
});
```

### Cách 2: Authorization Header
```typescript
io('https://ba-datn-1.onrender.com:9092', {
  extraHeaders: {
    Authorization: 'Bearer your_jwt_token_here'
  }
});
```

### Cách 3: Auth Object
```typescript
io('https://ba-datn-1.onrender.com:9092', {
  auth: {
    token: 'your_jwt_token_here'
  }
});
```

## Events có sẵn

### Server gửi đến Client:

1. **`welcome`** - Khi kết nối thành công
   ```typescript
   socket.on('welcome', (data) => {
     // data: { message, sessionId, username }
   });
   ```

2. **`message`** - Nhận message từ server
   ```typescript
   socket.on('message', (data) => {
     // data: { from, content, timestamp }
   });
   ```

3. **`room-joined`** - Khi join room thành công
   ```typescript
   socket.on('room-joined', (data) => {
     // data: { username, room, message }
   });
   ```

4. **`notification`** - Nhận notification
   ```typescript
   socket.on('notification', (data) => {
     // data: { from, message, timestamp }
   });
   ```

### Client gửi đến Server:

1. **`message`** - Gửi message
   ```typescript
   socket.emit('message', { content: 'Hello' });
   ```

2. **`join-room`** - Join room
   ```typescript
   socket.emit('join-room', { room: 'room1' }, (response) => {
     console.log(response); // { status: 'joined', room: 'room1' }
   });
   ```

3. **`leave-room`** - Leave room
   ```typescript
   socket.emit('leave-room', { room: 'room1' });
   ```

4. **`notification`** - Gửi notification
   ```typescript
   socket.emit('notification', { message: 'Test notification' });
   ```

## Xử lý lỗi

### Token hết hạn
```typescript
socket.on('connect_error', (error) => {
  if (error.message.includes('401') || error.message.includes('Unauthorized')) {
    // Refresh token hoặc redirect to login
    this.refreshToken();
  }
});
```

### Reconnection
Socket.IO tự động reconnect nếu mất kết nối. Bạn có thể cấu hình:
```typescript
io('...', {
  reconnection: true,
  reconnectionDelay: 1000,
  reconnectionAttempts: 5
});
```

## Lưu ý quan trọng

1. **Token phải hợp lệ** - Nếu token hết hạn hoặc invalid, connection sẽ bị reject
2. **Port 9092** - Socket.IO chạy trên port riêng, không phải port HTTP (8080)
3. **CORS** - Backend đã cấu hình cho phép tất cả origins (`*`)
4. **Sau khi đăng nhập** - Lưu token và kết nối lại WebSocket
5. **Khi token refresh** - Cần disconnect và reconnect với token mới

## Test kết nối

1. Đăng nhập và lấy `accessToken`
2. Lưu token vào localStorage
3. Kết nối Socket.IO với token
4. Kiểm tra console để xem log:
   - `✅ Socket.IO Connected` - Kết nối thành công
   - `❌ Invalid or missing token` - Token không hợp lệ

## Troubleshooting

### Lỗi: Connection refused
- Kiểm tra port 9092 có được mở không
- Kiểm tra firewall/security group

### Lỗi: 401 Unauthorized
- Kiểm tra token có hợp lệ không
- Kiểm tra token có được gửi đúng cách không
- Thử refresh token và reconnect

### Lỗi: CORS
- Backend đã cấu hình `setOrigin("*")` nên không nên có lỗi CORS
- Nếu vẫn có, kiểm tra lại cấu hình

