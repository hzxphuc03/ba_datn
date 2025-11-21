# ⚡ Cấu Hình Nhanh Render.com

## 🐳 Sử Dụng Docker (Khuyến nghị)

### Cấu hình:
- **Runtime**: Chọn `Docker`
- **Build Command**: Để trống (Render tự động build từ Dockerfile)
- **Start Command**: Để trống (Render tự động chạy từ Dockerfile)

✅ **Ưu điểm**: 
- Không cần lo về JAVA_HOME
- Môi trường build và runtime nhất quán
- Dễ dàng test local trước khi deploy

---

## ☕ Hoặc Sử Dụng Java Runtime

### Build Command:
```bash
chmod +x mvnw && ./mvnw clean package -DskipTests
```

### Start Command:
```bash
java -jar target/mycode-0.0.1-SNAPSHOT.jar
```

⚠️ **Lưu ý**: 
- **Runtime** phải là `Java` (không phải Node.js)
- Nếu thấy Build Command là `yarn` → đổi Runtime sang `Java` hoặc `Docker`

---

## 🔐 Environment Variables

Thêm các biến sau trong tab **"Environment"** của Render:

### Database (Bắt buộc)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname
SPRING_DATASOURCE_USERNAME=render
SPRING_DATASOURCE_PASSWORD=your_password
```

### JWT (Bắt buộc)
```
JWT_SECRET=your-secret-key-min-32-chars
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### Email (Nếu cần)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

### Spring Profile
```
SPRING_PROFILES_ACTIVE=prod
```

### Port
```
PORT=8080
```

### CORS (Nếu có frontend)
```
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

---

## 🚀 Các Bước Deploy

1. ✅ Đảm bảo file `render.yaml` đã được commit và push
2. ✅ Tạo Web Service trên Render
3. ✅ Chọn Runtime = **Java**
4. ✅ Set Build Command và Start Command (hoặc để Render tự đọc từ `render.yaml`)
5. ✅ Thêm Environment Variables
6. ✅ Click "Create Web Service" và chờ deploy

---

## ❌ Troubleshooting

### Lỗi: "JAVA_HOME is not defined"
→ **Giải pháp**: Đổi Runtime từ `Node` sang `Java`

### Lỗi: "Permission denied" với mvnw
→ **Giải pháp**: Build Command đã có `chmod +x mvnw`, nếu vẫn lỗi thì chạy:
```bash
git update-index --chmod=+x mvnw
git commit -m "Fix mvnw permissions"
git push
```

### Build Command hiển thị "yarn"
→ **Giải pháp**: Render đang dùng Node.js runtime. Đổi sang Java runtime.

