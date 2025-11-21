# Fix: Unable to access jarfile /app.jar

## Vấn đề
Lỗi `Error: Unable to access jarfile /app.jar` xảy ra khi deploy trên Render.

## Nguyên nhân
1. JAR file không được tạo ra trong quá trình build
2. Tên file JAR không khớp với pattern trong Dockerfile
3. Quyền truy cập file bị hạn chế
4. Đường dẫn file không đúng

## Giải pháp đã áp dụng

### 1. Cập nhật Dockerfile
- Tìm và copy file JAR executable (loại trừ `*-plain.jar`)
- Đổi tên thành `app.jar` để dễ quản lý
- Verify file tồn tại trước khi chạy
- Set quyền truy cập đúng

### 2. Cập nhật pom.xml
- Cấu hình Spring Boot Maven plugin để chỉ tạo executable JAR
- Đảm bảo không tạo plain JAR

## Kiểm tra build

### Local test
```bash
# Build Docker image
docker build -t datn-app .

# Kiểm tra JAR file trong image
docker run --rm datn-app ls -lh /app/app.jar

# Test chạy container
docker run -p 8080:8080 datn-app
```

### Trên Render
1. Kiểm tra build logs để xem:
   - JAR file có được tạo ra không
   - Tên file JAR là gì
   - File có được copy vào stage 2 không

2. Nếu vẫn lỗi, thêm debug vào Dockerfile:
```dockerfile
# Trong build stage
RUN ls -la /app/target/

# Trong runtime stage  
RUN ls -la /app/
```

## Troubleshooting

### Nếu JAR không được tạo
- Kiểm tra build logs xem có lỗi compile không
- Kiểm tra Maven có chạy thành công không
- Kiểm tra `target/` directory có tồn tại không

### Nếu JAR được tạo nhưng không copy được
- Kiểm tra tên file JAR trong logs
- Đảm bảo pattern `mycode-*.jar` khớp với tên file
- Kiểm tra quyền truy cập file

### Nếu vẫn lỗi
1. Thử build local trước:
```bash
mvn clean package -DskipTests
ls -lh target/*.jar
```

2. Kiểm tra tên file JAR chính xác
3. Cập nhật Dockerfile với tên file cụ thể nếu cần

## Cấu trúc JAR file
Spring Boot Maven plugin tạo:
- `mycode-0.0.1-SNAPSHOT.jar` - Executable JAR (fat JAR)
- `mycode-0.0.1-SNAPSHOT-plain.jar` - Plain JAR (nếu có cấu hình)

Dockerfile sẽ tìm và copy executable JAR (không phải plain JAR).

